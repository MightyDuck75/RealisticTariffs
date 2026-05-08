package com.mightyduck.realistictariffs;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.util.IntervalUtil;

import java.util.*;
import java.util.List;


public class ShipHullsWeaponIntelManager implements EveryFrameScript {
    private final IntervalUtil tracker = new IntervalUtil(1f, 1f); // Check once per day
    private org.apache.log4j.Logger log = Global.getLogger(ShipHullsWeaponIntelManager.class);
    // Define which factions have faction design ships for the buyback programs
    private final List<String> FACTION_SHIP_OWNERS = Arrays.asList(
            Factions.HEGEMONY, Factions.DIKTAT, Factions.TRITACHYON,
            Factions.PERSEAN, Factions.LUDDIC_CHURCH, Factions.LUDDIC_PATH, Factions.PIRATES
    );

    private final int minorDeficitTrigger = 1, moderateDeficitTrigger = 2, highDeficitTrigger = 3;

    @Override
    public void advance(float amount) {
        float days = Global.getSector().getClock().convertToDays(amount);
        tracker.advance(days);

        if (tracker.intervalElapsed()) {
            evaluateSectorConditions();
        }
    }

    private void evaluateSectorConditions() {
        Map<String, MarketAPI> representativeMarkets = new HashMap<>();

        for (MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
            if (market.isHidden()) continue;
            String fId = market.getFactionId();

            if (!representativeMarkets.containsKey(fId) ||
                    market.getSize() > representativeMarkets.get(fId).getSize()) {
                representativeMarkets.put(fId, market);
            }
        }

        // Loop through all markets and determine which intel they should have
        for (MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
            if (market.isHidden() || market.getFaction().isPlayerFaction()) continue;

            // AI says this could be the reason for a bug counting a station market and the planet market
            if (market.getPrimaryEntity() == null ||
                    market.getPrimaryEntity().getMarket() != market) {
                continue;
            }

            boolean isRepresentative = (market == representativeMarkets.get(market.getFactionId()));
            List<ShipHullsWeaponsIntelMarketCondition> conditionsForThisMarket = determineConditionsCreateIntel(market, isRepresentative);

            syncIntelForMarket(market, conditionsForThisMarket);
        }
    }

    private List<ShipHullsWeaponsIntelMarketCondition> determineConditionsCreateIntel(MarketAPI market, boolean isRepresentative) {
        List<ShipHullsWeaponsIntelMarketCondition> conditions = new ArrayList<>();
        FactionAPI faction = market.getFaction();
        String fId = faction.getId();
        CommodityOnMarketAPI ships = market.getCommodityData(Commodities.SHIPS);

        // 1. Ship Deficit Logic
        int shipDeficit = ships.getMaxDemand() - ships.getAvailable();

        if (shipDeficit >= highDeficitTrigger) conditions.add(ShipHullsWeaponsIntelMarketCondition.DEMAND_CRITICAL);
        else if (shipDeficit == moderateDeficitTrigger) conditions.add(ShipHullsWeaponsIntelMarketCondition.DEMAND_MODERATE);
        else if (shipDeficit == minorDeficitTrigger) conditions.add(ShipHullsWeaponsIntelMarketCondition.DEMAND_MINOR);

        // 2. War Logic
        if (isRepresentative && !fId.equals(Factions.PIRATES) && !fId.equals(Factions.LUDDIC_PATH)){
            int activeWars = WarPriceModifier.getActiveFactionalWars(market.getFaction());

            if (activeWars > 1)
                conditions.add(ShipHullsWeaponsIntelMarketCondition.WAR_MULTIPLE);
            else if (activeWars == 1)
                conditions.add(ShipHullsWeaponsIntelMarketCondition.WAR_SINGLE);

            // Buyback program
            if (activeWars > 0 && (!fId.equals(Factions.PERSEAN) && !fId.equals(Factions.DIKTAT))) {
                switch(fId) {
                    case Factions.HEGEMONY:
                        if (FACTION_SHIP_OWNERS.contains(fId)) conditions.add(ShipHullsWeaponsIntelMarketCondition.FACTION_BUYBACK_HEGEMONY);
                        break;
                    case Factions.LUDDIC_CHURCH:
                        if (FACTION_SHIP_OWNERS.contains(fId)) conditions.add(ShipHullsWeaponsIntelMarketCondition.FACTION_BUYBACK_LUDDICCHURCH);
                        break;
                    case Factions.TRITACHYON:
                        if (FACTION_SHIP_OWNERS.contains(fId)) conditions.add(ShipHullsWeaponsIntelMarketCondition.FACTION_BUYBACK_TRITACHYON);
                        break;
                }
            }
        }

        return conditions;
    }

    private void syncIntelForMarket(MarketAPI market, List<ShipHullsWeaponsIntelMarketCondition> activeConditions) {
        List<ShipHullsWeaponsMarketIntel> existingIntel = new ArrayList<>();
        for (IntelInfoPlugin plugin : Global.getSector().getIntelManager().getIntel(ShipHullsWeaponsMarketIntel.class)) {
            ShipHullsWeaponsMarketIntel intel = (ShipHullsWeaponsMarketIntel) plugin;

            if (intel.getMarket() != null && intel.getMarket().getId().equals(market.getId())) {
                if (!intel.isEnding() && !intel.isEnded()) {
                    existingIntel.add(intel);
                }
            }
        }

        for (ShipHullsWeaponsMarketIntel intel : existingIntel) {
            if (!activeConditions.contains(intel.getCondition()))
                intel.endAfterDelay(0f);
        }

        //The Update: Create intel for any condition that doesn't have a report yet
        for (ShipHullsWeaponsIntelMarketCondition condition : activeConditions) {
            boolean exists = false;
            for (ShipHullsWeaponsMarketIntel intel : existingIntel) {
                if (intel.getCondition() == condition) {
                    exists = true;
                    break;
                }
            }

            if (!exists)
                Global.getSector().getIntelManager().addIntel(new ShipHullsWeaponsMarketIntel(market, condition));
        }
    }

    @Override public boolean isDone() { return false; }
    @Override public boolean runWhilePaused() { return false; }
}