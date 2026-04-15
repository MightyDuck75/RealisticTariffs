package com.mightyduck.realistictariffs;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.util.IntervalUtil;
import org.apache.log4j.Logger;

import java.awt.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class TariffUpdater implements EveryFrameScript {
    public static final String MOD_ID = "realistictariffs";

    // --- BALANCING CONSTANTS ---
    private static final int INTEL_TRIGGER_THRESHOLD = 1;
    private static final Logger log = Global.getLogger(TariffUpdater.class);

    private final IntervalUtil interval = new IntervalUtil(0.8f, 1.2f);
    private final List<String> Eco_Commodities = Arrays.asList(
            Commodities.SHIPS,Commodities.CREW, Commodities.DOMESTIC_GOODS, Commodities.FOOD,
            Commodities.FUEL, Commodities.HAND_WEAPONS, Commodities.HEAVY_MACHINERY, Commodities.LOBSTER,
            Commodities.LUXURY_GOODS, Commodities.MARINES, Commodities.METALS, Commodities.ORE,
            Commodities.ORGANICS, Commodities.RARE_METALS, Commodities.RARE_ORE, Commodities.SUPPLIES,
            Commodities.VOLATILES, Commodities.DRUGS,Commodities.ORGANS
    );
    @Override
    public boolean isDone() { return false; }

    @Override
    public boolean runWhilePaused() { return false; }

    @Override
    public void advance(float amount) {
        interval.advance(amount);
        if (!interval.intervalElapsed()) return;

        // Optimization: Get the list of intel once per day
        List<IntelInfoPlugin> activeIntelList = Global.getSector().getIntelManager().getIntel(DeficitIntel.class);

        for (MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
            if (!isValidMarket(market)) continue;
            // BUG MAybe around here
            // --- 1. CALCULATE SHORTAGES ---
            int totalShortages = 0;
            float sumTariffReduction = 0;

            for (CommodityOnMarketAPI commodity : market.getCommoditiesCopy()) {
                if(Eco_Commodities.contains(commodity.getId())) {
                    // Check if it's an "essential" good (not illegal)
                    String id = commodity.getId();
                    if (commodity.getMaxDemand() > commodity.getAvailable()){
                        if (!id.equals(Commodities.DRUGS) && !id.equals(Commodities.ORGANS)) {
                            switch (commodity.getId()) {
                                case Commodities.SHIPS:
                                sumTariffReduction += RTConfig.CommoditiesSHIPSTariffImpact;
                                //TariffUpdater.log.info("TariffUpdater Market : " + market.getName() + " Commodities.SHIPS sumTariffReduction: "+ sumTariffReduction + "%" +" Tariff ModifiedValue: "+ (int)(market.getTariff().getModifiedValue() * 100f) +" %");
                                break;
                                case Commodities.CREW:
                                    sumTariffReduction += RTConfig.CommoditiesCREWTariffImpact;
                                    //TariffUpdater.log.info("TariffUpdater Market : " + market.getName() + " Commodities.CREW sumTariffReduction: "+ sumTariffReduction + "%" +" Tariff ModifiedValue: "+ (int)(market.getTariff().getModifiedValue() * 100f) +" %");
                                    break;
                                case Commodities.DOMESTIC_GOODS:
                                    sumTariffReduction += RTConfig.CommoditiesDOMESTIC_GOODSTariffImpact;
                                    //TariffUpdater.log.info("TariffUpdater Market : " + market.getName() + " Commodities.DOMESTIC_GOODS sumTariffReduction: "+ sumTariffReduction + "%" +" Tariff ModifiedValue: "+ (int)(market.getTariff().getModifiedValue() * 100f) +" %");
                                    break;
                                case Commodities.FOOD:
                                    sumTariffReduction += RTConfig.CommoditiesFOODTariffImpact;
                                    //TariffUpdater.log.info("TariffUpdater Market : " + market.getName() + " Commodities.FOOD sumTariffReduction: "+ sumTariffReduction + "%" +" Tariff ModifiedValue: "+ (int)(market.getTariff().getModifiedValue() * 100f) +" %");
                                    break;
                                case Commodities.FUEL:
                                    sumTariffReduction += RTConfig.CommoditiesFUELTariffImpact;
                                    //TariffUpdater.log.info("TariffUpdater Market : " + market.getName() + " Commodities.FUEL sumTariffReduction: "+ sumTariffReduction + "%" +" Tariff ModifiedValue: "+ (int)(market.getTariff().getModifiedValue() * 100f) +" %");
                                    break;
                                case Commodities.HAND_WEAPONS:
                                    sumTariffReduction += RTConfig.CommoditiesHAND_WEAPONSTariffImpact;
                                    //TariffUpdater.log.info("TariffUpdater Market : " + market.getName() + " Commodities.HAND_WEAPONS sumTariffReduction: "+ sumTariffReduction + "%" +" Tariff ModifiedValue: "+ (int)(market.getTariff().getModifiedValue() * 100f) +" %");
                                    break;
                                case Commodities.HEAVY_MACHINERY:
                                    sumTariffReduction += RTConfig.CommoditiesHEAVY_MACHINERYTariffImpact;
                                    //TariffUpdater.log.info("TariffUpdater Market : " + market.getName() + " Commodities.HEAVY_MACHINERY "+ sumTariffReduction + "%" +" Tariff ModifiedValue: "+ (int)(market.getTariff().getModifiedValue() * 100f) +" %");
                                    break;
                                case Commodities.LOBSTER:
                                    sumTariffReduction += RTConfig.CommoditiesLOBSTERTariffImpact;
                                    //TariffUpdater.log.info("TariffUpdater Market : " + market.getName() + " Commodities.LOBSTER sumTariffReduction: "+ sumTariffReduction + "%" +" Tariff ModifiedValue: "+ (int)(market.getTariff().getModifiedValue() * 100f) +" %");
                                    break;
                                case Commodities.LUXURY_GOODS:
                                    sumTariffReduction += RTConfig.CommoditiesLUXURY_GOODSTariffImpact;
                                    //TariffUpdater.log.info("TariffUpdater Market : " + market.getName() + " Commodities.LUXURY_GOODS sumTariffReduction: "+ sumTariffReduction + "%" +" Tariff ModifiedValue: "+ (int)(market.getTariff().getModifiedValue() * 100f) +" %");
                                    break;
                                case Commodities.MARINES:
                                    sumTariffReduction += RTConfig.CommoditiesMARINESTariffImpact;
                                    //TariffUpdater.log.info("TariffUpdater Market : " + market.getName() + " Commodities.MARINES sumTariffReduction: "+ sumTariffReduction + "%" +" Tariff ModifiedValue: "+ (int)(market.getTariff().getModifiedValue() * 100f) +" %");
                                    break;
                                case Commodities.METALS:
                                    sumTariffReduction += RTConfig.CommoditiesMETALSTariffImpact;
                                    //TariffUpdater.log.info("TariffUpdater Market : " + market.getName() + " Commodities.METALS sumTariffReduction: "+ sumTariffReduction + "%" +" Tariff ModifiedValue: "+ (int)(market.getTariff().getModifiedValue() * 100f) +" %");
                                    break;
                                case Commodities.RARE_METALS:
                                    sumTariffReduction += RTConfig.CommoditiesRARE_METALSTariffImpact;
                                    //TariffUpdater.log.info("TariffUpdater Market : " + market.getName() + " Commodities.RARE_METALS sumTariffReduction: "+ sumTariffReduction + "%" +" Tariff ModifiedValue: "+ (int)(market.getTariff().getModifiedValue() * 100f) +" %");
                                    break;
                                case Commodities.RARE_ORE:
                                    sumTariffReduction += RTConfig.CommoditiesRARE_ORETariffImpact;
                                    //TariffUpdater.log.info("TariffUpdater Market : " + market.getName() + " Commodities.RARE_ORE sumTariffReduction: "+ sumTariffReduction + "%" +" Tariff ModifiedValue: "+ (int)(market.getTariff().getModifiedValue() * 100f) +" %");
                                    break;
                                case Commodities.SUPPLIES:
                                    sumTariffReduction += RTConfig.CommoditiesSUPPLIESTariffImpact;
                                    //TariffUpdater.log.info("TariffUpdater Market : " + market.getName() + " Commodities.SUPPLIES sumTariffReduction: "+ sumTariffReduction + "%" +" Tariff ModifiedValue: "+ (int)(market.getTariff().getModifiedValue() * 100f) +" %");
                                    break;
                                case Commodities.VOLATILES:
                                    sumTariffReduction += RTConfig.CommoditiesVOLATILESTariffImpact;
                                    //TariffUpdater.log.info("TariffUpdater Market : " + market.getName() + " Commodities.VOLATILES sumTariffReduction: "+ sumTariffReduction + "%" +" Tariff ModifiedValue: "+ (int)(market.getTariff().getModifiedValue() * 100f) +" %");
                                    break;
                            }
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
        DeficitIntel existingIntel = null;

        for (IntelInfoPlugin plugin : activeIntelList) {
            DeficitIntel intel = (DeficitIntel) plugin;
            if (intel.getMarket() != null && intel.getMarket().getId().equals(market.getId())) {
                if (!intel.isEnded() && !intel.isEnding()) {
                    existingIntel = intel;
                    break;
                }
            }
        }

        if (totalShortages >= INTEL_TRIGGER_THRESHOLD && existingIntel == null) {
            Global.getSector().getIntelManager().addIntel(new DeficitIntel(market));
        } else if (totalShortages < INTEL_TRIGGER_THRESHOLD && existingIntel != null) {
            existingIntel.endAfterDelay(0.1f);
        }
    }

    private void applyTariffChanges(MarketAPI market, float tariffReduction) {
        // 3. Clean up any old modifiers from this mod to get a clean reading
        float setTariffsToNormal = 0f;

        market.getTariff().unmodify(MOD_ID);

        if(market.getTariff().getModifiedValue() != RTConfig.NormalTariff)
            setTariffsToNormal = RTConfig.NormalTariff - market.getTariff().getModifiedValue();

        // 4. Calculate how much we need to add/subtract to reach 0.18
        float current = market.getTariff().getModifiedValue();

        // 2. Calculate the "Potential" new tariff
        float potentialTariff = current - (Math.abs(setTariffsToNormal) + tariffReduction) ;

        // 3. Apply the Floor (The Math)
        // Math.max returns the LARGER of the two numbers.
        float finalTargetTariff = Math.max(potentialTariff, RTConfig.LowestPossibleTariff);

        // 4. Calculate the required Flat Modifier
        float finalAdjustment = finalTargetTariff - current;

        // 5. Apply the change
        // We only apply it if the adjustment isn't 0 (to keep the UI clean)
        if (finalAdjustment != 0) {
            market.getTariff().modifyFlat(MOD_ID, finalAdjustment, "Realistic Tariffs Adjustment");
            float finalTariff = market.getTariff().getModifiedValue();
        }
    }
}