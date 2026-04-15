package com.mightyduck.realistictariffs;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import org.apache.log4j.Logger;

public class WarPriceModifier {

    public static float getShipWarMultiplier(MarketAPI market) {
        FactionAPI faction = market.getFaction();

        int wars = countFactionWarsMinusPiratesAndTerrorists(faction);

        if (wars <= 0) return 0f;

        if(wars == 1)
            return RTConfig.ShipWarPricesBonus;
        else
            return RTConfig.ShipMultipleWarsPricesBonus;
    }

    public static float getWeaponsWarMultiplier(MarketAPI market) {
        FactionAPI faction = market.getFaction();

        int wars = countFactionWarsMinusPiratesAndTerrorists(faction);

        if (wars <= 0) return 0f;

        if(wars == 1)
            return RTConfig.WeaponsWarPricesBonus;
        else
            return RTConfig.WeaponsMultipleWarsPricesBonus;
    }

    private static int countFactionWarsMinusPiratesAndTerrorists(FactionAPI faction){
        int wars = 0;

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
                wars++;
            }
        };

        return wars;
    }
}
