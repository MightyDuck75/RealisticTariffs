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
            return RTConfig.shipWarPricesBonus;
        else
            return RTConfig.shipMultipleWarsPricesBonus;
    }

    public static float getWeaponsWarMultiplier(MarketAPI market) {
        FactionAPI faction = market.getFaction();

        int wars = countFactionWarsMinusPiratesAndTerrorists(faction);

        if (wars <= 0) return 0f;

        if(wars == 1)
            return RTConfig.weaponsWarPricesBonus;
        else
            return RTConfig.weaponsMultipleWarsPricesBonus;
    }

    private static int countFactionWarsMinusPiratesAndTerrorists(FactionAPI faction){
        int wars = 0;

        for (FactionAPI other : Global.getSector().getAllFactions()) {
            //Safety: Don't count yourself as an enemy
            if (other == faction) continue;

            // This automatically filters out Remnants, Derelicts, Omega, and Guardians.
            if (!other.isShowInIntelTab()) continue;

            // Maybe remove player faction? I think yes if player fleets attack and damages the faction markets
            if (other.isPlayerFaction() || other.isNeutralFaction()) continue;

            String id = other.getId();
            if (id.equals(Factions.PIRATES) || id.equals(Factions.LUDDIC_PATH)) continue;

            if (faction.isHostileTo(other)) {
                wars++;
            }
        }

        return wars;
    }
}
