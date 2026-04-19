package com.mightyduck.realistictariffs;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.util.IntervalUtil;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.List;

public class TariffUpdater implements EveryFrameScript {
    public static final String MOD_ID = "realistictariffs";

    private static final Logger log = Global.getLogger(TariffUpdater.class);

    private final IntervalUtil interval = new IntervalUtil(0.8f, 1.2f);
    private final Set<String> economicCommodities = new HashSet<>(Arrays.asList(
            Commodities.SHIPS,Commodities.CREW, Commodities.DOMESTIC_GOODS, Commodities.FOOD,
            Commodities.FUEL, Commodities.HAND_WEAPONS, Commodities.HEAVY_MACHINERY, Commodities.LOBSTER,
            Commodities.LUXURY_GOODS, Commodities.MARINES, Commodities.METALS, Commodities.ORE,
            Commodities.ORGANICS, Commodities.RARE_METALS, Commodities.RARE_ORE, Commodities.SUPPLIES,
            Commodities.VOLATILES, Commodities.DRUGS,Commodities.ORGANS
    ));
    @Override
    public boolean isDone() { return false; }

    @Override
    public boolean runWhilePaused() { return false; }

    @Override
    public void advance(float amount) {
        interval.advance(amount);
        if (!interval.intervalElapsed()) return;

        // Optimization: Get the list of intel once per day
        List<IntelInfoPlugin> activeIntelList = Global.getSector().getIntelManager().getIntel(DeficitTariffMarketIntel.class);

        for (MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
            if (!isValidMarket(market)) continue;

            int totalShortages = 0;
            float sumTariffReduction = 0;

            for (CommodityOnMarketAPI commodity : market.getCommoditiesCopy()) {
                if(economicCommodities.contains(commodity.getId())) {
                    // Check if it's an "essential" good (not illegal)
                    String id = commodity.getId();
                    if (commodity.getMaxDemand() > commodity.getAvailable()){
                        if (!id.equals(Commodities.DRUGS) && !id.equals(Commodities.ORGANS)) {
                            sumTariffReduction += RTConfig.tariffImpacts.getOrDefault(commodity.getId(), 0f);
                        }
                        totalShortages++;
                    }
                }
            }

            // --- 2. MANAGE INTEL ---
            handleIntel(market, totalShortages, activeIntelList);

            // --- 3. APPLY TARIFFS ---
            applyTariffChanges(market, sumTariffReduction);
        }
    }

    private boolean isValidMarket(MarketAPI market) {
        if (market.getPrimaryEntity() == null) {
            return false;
        }
        // Use Misc.getStationMarket to avoid duplicating logic on stations vs planets
        return market.getId().equals(market.getPrimaryEntity().getMarket().getId());
    }

    private void handleIntel(MarketAPI market, int totalShortages, List<IntelInfoPlugin> activeIntelList) {
        DeficitTariffMarketIntel existingIntel = null;

        for (IntelInfoPlugin plugin : activeIntelList) {
            DeficitTariffMarketIntel intel = (DeficitTariffMarketIntel) plugin;
            if (intel.getMarket() != null && intel.getMarket().getId().equals(market.getId())) {
                if (!intel.isEnded() && !intel.isEnding()) {
                    existingIntel = intel;
                    break;
                }
            }
        }

        if (totalShortages >= RTConfig.intelTriggerThreshold && existingIntel == null) {
            // Intel doesn't exist yet, create it.
            Global.getSector().getIntelManager().addIntel(new DeficitTariffMarketIntel(market));

        } else if (totalShortages < RTConfig.intelTriggerThreshold && existingIntel != null) {
            // FIX: If deficits are solved, this instantly deletes the intel.
            existingIntel.endAfterDelay(0f);

        } else if (existingIntel != null) {
            // FIX: Intel exists and there are still shortages.
            // Force the intel to re-evaluate its stats so it can change titles/tabs!
            existingIntel.refreshShortageStats();
        }
    }

    private void applyTariffChanges(MarketAPI market, float tariffReduction) {
        // 3. Clean up any old modifiers from this mod to get a clean reading
        float setTariffsToNormal = 0f;

        market.getTariff().unmodify(MOD_ID);

        if(market.getTariff().getModifiedValue() != RTConfig.normalTariff)
            setTariffsToNormal = RTConfig.normalTariff - market.getTariff().getModifiedValue();

        // 4. Calculate how much we need to add/subtract to reach 0.18
        float current = market.getTariff().getModifiedValue();

        // 2. Calculate the "Potential" new tariff
        float potentialTariff = current - (Math.abs(setTariffsToNormal) + tariffReduction) ;

        // 3. Apply the Floor (The Math)
        // Math.max returns the LARGER of the two numbers.
        float finalTargetTariff = Math.max(potentialTariff, RTConfig.lowestPossibleTariff);

        // 4. Calculate the required Flat Modifier
        float finalAdjustment = finalTargetTariff - current;

        // 5. Apply the change
        // We only apply it if the adjustment isn't 0 (to keep the UI clean)
        if (finalAdjustment != 0) {
            market.getTariff().modifyFlat(MOD_ID, finalAdjustment, "Realistic Tariffs Adjustment");
        }
    }
}