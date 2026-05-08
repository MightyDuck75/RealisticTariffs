package com.mightyduck.realistictariffs;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;

import java.util.HashMap;
import java.util.Map;

public class WarPriceModifier {
    private static final Map<String, Integer> warCache = new HashMap<>();
    private static long lastCacheTime = 0;
    private static final long CACHE_DURATION = 86400L; // 1 game day

    public static float getShipWarMultiplier(MarketAPI market) {
        FactionAPI faction = market.getFaction();

        int wars = getActiveFactionalWars(faction);

        if (wars <= 0) return 0f;

        if(wars == 1)
            return RTConfig.shipWarPricesBonus;
        else
            return RTConfig.shipMultipleWarsPricesBonus;
    }

    public static float getWeaponsWarMultiplier(MarketAPI market) {
        FactionAPI faction = market.getFaction();

        int wars = getActiveFactionalWars(faction);

        if (wars <= 0) return 0f;

        if (wars == 1)
            return RTConfig.weaponsWarPricesBonus;
        else
            return RTConfig.weaponsMultipleWarsPricesBonus;
    }

    public static int getActiveFactionalWars(FactionAPI faction){
        long now = Global.getSector().getClock().getTimestamp();
        if (now - lastCacheTime > CACHE_DURATION) { // once per game day
            warCache.clear();
            lastCacheTime = now;
        }

        return warCache.computeIfAbsent(faction.getId(),
                id -> countFactionWarsMinusPiratesAndTerroristsInternal(faction));
    }

    private static int countFactionWarsMinusPiratesAndTerroristsInternal(FactionAPI faction) {
        int wars = 0;
        for (FactionAPI other : Global.getSector().getAllFactions()) {
            //Safety: Don't count yourself as an enemy
            if (other == faction) continue;

            //Filters out Remnants, Derelicts, Omega, and Guardians.
            if (!other.isShowInIntelTab()) continue;

            if (other.isNeutralFaction()) continue;

            String id = other.getId();
            if (id.equals(Factions.PIRATES) || id.equals(Factions.LUDDIC_PATH)) continue;

            if (faction.isHostileTo(other))
                wars++;
        }

        return wars;
    }
}
