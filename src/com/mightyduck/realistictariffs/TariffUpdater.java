package com.mightyduck.realistictariffs;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.util.IntervalUtil;

import java.util.List;

import static com.mightyduck.realistictariffs.RTConfig.ECONOMIC_COMMODITIES;

public class TariffUpdater implements EveryFrameScript {
    public static final String MOD_ID = "realistictariffs";
    private final IntervalUtil interval = new IntervalUtil(2f, 3f);

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
                if(ECONOMIC_COMMODITIES.contains(commodity.getId())) {

                    if (commodity.getMaxDemand() > commodity.getAvailable()){
                        String id = commodity.getId();

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
            Global.getSector().getIntelManager().addIntel(new DeficitTariffMarketIntel(market, severity));

        } else if (severity == CommoditiesDeficitLevel.NONE && existingIntel != null) {
            existingIntel.endAfterDelay(0f);

        } else if (existingIntel != null) {
            existingIntel.updateSeverity(severity);
        }
    }

    private boolean isValidMarket(MarketAPI market) {
        if (market.getPrimaryEntity() == null)
            return false;

        return market.getId().equals(market.getPrimaryEntity().getMarket().getId());
    }

    private void applyTariffChanges(MarketAPI market, float tariffReduction) {
        float currentTotalTariff = market.getTariff().getModifiedValue();
        float ourCurrentModValue = 0f;

        // Check if our modifier already exists to subtract it accurately
        if (market.getTariff().getFlatStatMod(MOD_ID) != null)
            ourCurrentModValue = market.getTariff().getFlatStatMod(MOD_ID).value;

        float cleanTariff = currentTotalTariff - ourCurrentModValue;

        // Calculate the target
        float desiredTariff = RTConfig.normalTariff - tariffReduction;
        float finalTargetTariff = Math.max(desiredTariff, RTConfig.lowestPossibleTariff);

        // We check if the difference is significant to avoid unnecessary recalculations
        if (Math.abs(currentTotalTariff - finalTargetTariff) > 0.001f) {
            market.getTariff().unmodify(MOD_ID);

            float requiredAdjustment = finalTargetTariff - cleanTariff;

            market.getTariff().modifyFlat(MOD_ID, requiredAdjustment, "Local economic conditions");
        }
    }
}