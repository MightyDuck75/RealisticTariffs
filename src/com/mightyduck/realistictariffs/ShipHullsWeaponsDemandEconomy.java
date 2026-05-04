package com.mightyduck.realistictariffs;

import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;

public class ShipHullsWeaponsDemandEconomy {
    private static final int highDemandTrigger = 3;
    private static final int minorDemandTrigger = 1;

    public static float getShipDemandSellMultiplier(MarketAPI market) {
        CommodityOnMarketAPI ships = market.getCommodityData("ships");
        if (ships == null) return 0f;

        int deficit = ships.getMaxDemand() - ships.getAvailable();
        float demandBonus;

        if(deficit < minorDemandTrigger)
            demandBonus = 0f;
        else {
            if (deficit >= highDemandTrigger)
                demandBonus = RTConfig.shipsDemandHighSellPriceBoost;
            else if (deficit == minorDemandTrigger)
                demandBonus = RTConfig.shipsDemandMinorSellPriceBoost;
            else
                demandBonus = RTConfig.shipsDemandModerateSellPriceBoost;
        }
        return demandBonus;
    }

    public static float getShipDemandBuyMultiplier(MarketAPI market) {
        CommodityOnMarketAPI ships = market.getCommodityData("ships");
        if (ships == null) return 0f;

        int deficit = ships.getMaxDemand() - ships.getAvailable();
        float demandBonus;

        if(deficit < minorDemandTrigger)
            demandBonus = 0f;
        else {
            if (deficit >= highDemandTrigger)
                demandBonus = RTConfig.shipsDemandHighBuyPriceBoost;
            else if (deficit == minorDemandTrigger)
                demandBonus = RTConfig.shipsDemandMinorBuyPriceBoost;
            else
                demandBonus = RTConfig.shipsDemandModerateBuyPriceBoost;
        }
        return demandBonus;
    }

    public static float getWeaponDemandSellMultiplier(MarketAPI market) {
        CommodityOnMarketAPI ships = market.getCommodityData("ships");
        if (ships == null) return 0f;

        int deficit = ships.getMaxDemand() - ships.getAvailable();
        float demandBonus;

        if(deficit < minorDemandTrigger)
            demandBonus = 0f;
        else {
            if(deficit >= highDemandTrigger)
                demandBonus =  RTConfig.weaponsDemandHighSellPriceBoost;
            else if (deficit == minorDemandTrigger)
                demandBonus = RTConfig.weaponsDemandMinorSellPriceBoost;
            else
                demandBonus = RTConfig.weaponsDemandModerateSellPriceBoost;
        }
        return demandBonus;
    }

    public static float getWeaponDemandBuyMultiplier(MarketAPI market) {
        CommodityOnMarketAPI ships = market.getCommodityData("ships");
        if (ships == null) return 0f;

        float demandBonus;
        int deficit = ships.getMaxDemand() - ships.getAvailable();

        if(deficit < minorDemandTrigger)
            demandBonus = 0f;
        else {
            if(deficit >= highDemandTrigger)
                demandBonus =  RTConfig.weaponsDemandHighBuyPriceBoost;
            else if (deficit == minorDemandTrigger)
                demandBonus = RTConfig.weaponsDemandMinorBuyPriceBoost;
            else
                demandBonus = RTConfig.weaponsDemandModerateBuyPriceBoost;
        }
        return demandBonus;
    }
}
