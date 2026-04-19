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

public class ShipsHullWeaponIntelManager implements EveryFrameScript {
    private final IntervalUtil tracker = new IntervalUtil(1f, 1f); // Check once per day

    // Define which factions have specific/elite ships for the buyback programs
    private final List<String> FACTION_SHIP_OWNERS = Arrays.asList(
            Factions.HEGEMONY, Factions.DIKTAT, Factions.TRITACHYON,
            Factions.PERSEAN, Factions.LUDDIC_CHURCH, Factions.LUDDIC_PATH, Factions.PIRATES
    );

    @Override
    public void advance(float amount) {
        float days = Global.getSector().getClock().convertToDays(amount);
        tracker.advance(days);

        if (tracker.intervalElapsed()) {
            evaluateSectorConditions();
        }
    }

    private void evaluateSectorConditions() {
// 1. Identify the 'Representative' market for every faction (the largest one)
        // This ensures Faction-wide intel only appears ONCE per faction.
        Map<String, MarketAPI> representativeMarkets = new HashMap<>();
        for (MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
            if (market.isHidden()) continue;
            String fId = market.getFactionId();

            if (!representativeMarkets.containsKey(fId) ||
                    market.getSize() > representativeMarkets.get(fId).getSize()) {
                representativeMarkets.put(fId, market);
            }
        }

        // 2. Loop through all markets and determine which intel they should have
        for (MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
            if (market.isHidden() || market.getFaction().isPlayerFaction()) continue;

            boolean isRepresentative = (market == representativeMarkets.get(market.getFactionId()));
            List<ShipHullsWeaponsIntelMarketCondition> conditionsForThisMarket = determineConditions(market, isRepresentative);

            syncIntelForMarket(market, conditionsForThisMarket);
        }
    }

    private List<ShipHullsWeaponsIntelMarketCondition> determineConditions(MarketAPI market, boolean isRepresentative) {
        List<ShipHullsWeaponsIntelMarketCondition> conditions = new ArrayList<>();
        FactionAPI faction = market.getFaction();
        String fId = faction.getId();
        CommodityOnMarketAPI ships = market.getCommodityData(Commodities.SHIPS);

        // 1. Ship Deficit Logic
        int shipDeficit = ships.getMaxDemand() - ships.getAvailable();

        if (shipDeficit >= 3) conditions.add(ShipHullsWeaponsIntelMarketCondition.DEMAND_CRITICAL);
        else if (shipDeficit == 2) conditions.add(ShipHullsWeaponsIntelMarketCondition.DEMAND_MODERATE);
        else if (shipDeficit == 1) conditions.add(ShipHullsWeaponsIntelMarketCondition.DEMAND_MINOR);

        // 2. War Logic
        if (isRepresentative && !fId.equals(Factions.PIRATES) && !fId.equals(Factions.LUDDIC_PATH)){
            int activeWars = getActiveWarCount(market.getFaction());

            if (activeWars > 1) {
                conditions.add(ShipHullsWeaponsIntelMarketCondition.WAR_MULTIPLE);
            } else if (activeWars == 1) {
                conditions.add(ShipHullsWeaponsIntelMarketCondition.WAR_SINGLE);
            }

            // Buyback program
            if (activeWars > 0 && (!fId.equals(Factions.PERSEAN) || !fId.equals(Factions.DIKTAT))) {
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
        // Find all existing intel for this market currently displayed to the player
        List<ShipHullsWeaponsIntel> existingIntel = new ArrayList<>();
        for (IntelInfoPlugin plugin : Global.getSector().getIntelManager().getIntel(ShipHullsWeaponsIntel.class)) {
            ShipHullsWeaponsIntel intel = (ShipHullsWeaponsIntel) plugin;
            if (intel.getMarket() == market && !intel.isEnding() && !intel.isEnded()) {
                existingIntel.add(intel);
            }
        }

        // Clean up conditions that are no longer true
        for (ShipHullsWeaponsIntel intel : existingIntel) {
            if (!activeConditions.contains(intel.getCondition())) {
                intel.endAfterDelay(); // Gracefully archives the intel before removal
            }
        }

        // Create new intel for conditions that just started
        for (ShipHullsWeaponsIntelMarketCondition condition : activeConditions) {
            boolean exists = false;
            for (ShipHullsWeaponsIntel intel : existingIntel) {
                if (intel.getCondition() == condition) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                Global.getSector().getIntelManager().addIntel(new ShipHullsWeaponsIntel(market, condition));
            }
        }
    }

    private int getActiveWarCount(FactionAPI faction) {
        int count = 0;
        for (FactionAPI other : Global.getSector().getAllFactions()) {
            // 1. Safety: Don't count yourself as an enemy
            if (other == faction) continue;

            // 2. The Golden Rule: Only count factions that show in the Intel/Diplomacy tab.
            // This automatically filters out Remnants, Derelicts, Omega, and Guardians.
            if (!other.isShowInIntelTab()) continue;

            // 3. Keep your existing exclusions for players and neutral entities
            if (other.isPlayerFaction() || other.isNeutralFaction()) continue;

            // 4. Specifically ignore the 'Chaos' factions as you intended
            String id = other.getId();
            if (id.equals(Factions.PIRATES) || id.equals(Factions.LUDDIC_PATH)) continue;

            // 5. Count hostiles
            if (faction.isHostileTo(other)) {
                count++;
            }
        }

        return count;
    }

    @Override public boolean isDone() { return false; }
    @Override public boolean runWhilePaused() { return false; }
}