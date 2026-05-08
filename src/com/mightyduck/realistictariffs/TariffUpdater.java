package com.mightyduck.realistictariffs;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.util.IntervalUtil;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.List;

public class TariffUpdater implements EveryFrameScript {
    public static final String MOD_ID = "realistictariffs";
    private static final Logger log = Global.getLogger(TariffUpdater.class);
    private final IntervalUtil interval = new IntervalUtil(2f, 3f);

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

        List<IntelInfoPlugin> activeIntelList = Global.getSector().getIntelManager().getIntel(DeficitTariffMarketIntel.class);

        for (MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
            if (!isValidMarket(market)) continue;

            int normalShortages = 0;
            int illicitShortages = 0;
            float sumTariffReduction = 0;

            for (CommodityOnMarketAPI commodity : market.getCommoditiesCopy()) {
                if(economicCommodities.contains(commodity.getId())) {

                    if (commodity.getMaxDemand() > commodity.getAvailable()){
                        String id = commodity.getId();

                        // Split the counting logic here
                        if (id.equals(Commodities.DRUGS) || id.equals(Commodities.ORGANS)) {
                            illicitShortages++;
                        } else {
                            sumTariffReduction += RTConfig.tariffImpacts.getOrDefault(id, 0f);
                            normalShortages++;
                        }
                    }
                }
            }

            applyTariffChanges(market, sumTariffReduction);

            CommoditiesDeficitLevel severity = CommoditiesDeficitLevel.evaluate(market, normalShortages, illicitShortages);

            handleIntel(market, severity, activeIntelList);
        }
    }

    private void handleIntel(MarketAPI market, CommoditiesDeficitLevel severity, List<IntelInfoPlugin> activeIntelList) {
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

        if (severity != CommoditiesDeficitLevel.NONE && existingIntel == null) {
            Global.getSector().getIntelManager().addIntel(new DeficitTariffMarketIntel(market));

        } else if (severity == CommoditiesDeficitLevel.NONE && existingIntel != null) {
            existingIntel.endAfterDelay(0f);

        } else if (existingIntel != null) {
            existingIntel.refreshShortageStats();
        }
    }

    private boolean isValidMarket(MarketAPI market) {
        if (market.getPrimaryEntity() == null)
            return false;

        return market.getId().equals(market.getPrimaryEntity().getMarket().getId());
    }

    private void applyTariffChanges(MarketAPI market, float tariffReduction) {
        //Remove previous adjustments to see the "clean" vanilla tariff
        market.getTariff().unmodify(MOD_ID);

        float cleanTariff = market.getTariff().getModifiedValue();

        //Calculate what our goal tariff is, start at our "Normal" and subtract the reduction from shortages
        float desiredTariff = RTConfig.normalTariff - tariffReduction;

        //This ensures not to go below as defined in the settings
        float finalTargetTariff = Math.max(desiredTariff, RTConfig.lowestPossibleTariff);

        float requiredAdjustment = finalTargetTariff - cleanTariff;

        //Plus epsilon due to Java rounding floating accuracy
        float finalAdjustment = requiredAdjustment + 0.0001f;

        //Apply the modifier only if there is a change
        if (Math.abs(finalAdjustment) > 0.0001f)
            market.getTariff().modifyFlat(MOD_ID, finalAdjustment, "Realistic Tariffs Adjustment");
    }
}