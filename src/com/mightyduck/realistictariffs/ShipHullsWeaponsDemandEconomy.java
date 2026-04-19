package com.mightyduck.realistictariffs;

import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;

public class ShipHullsWeaponsDemandEconomy {
    public static float getShipDemandSellMultiplier(MarketAPI market) {
        CommodityOnMarketAPI ships = market.getCommodityData("ships");
        if (ships == null) return 0f;

        int deficit = ships.getMaxDemand() - ships.getAvailable();
        float demandBonus;

        if(deficit < 1)
            demandBonus = 0f;
        else {
            if (deficit >= 3)
                demandBonus = RTConfig.shipsDemandHighSellPriceBoost;
            else if (deficit == 1)
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

        if(deficit < 1)
            demandBonus = 0f;
        else {
            if (deficit >= 3)
                demandBonus = RTConfig.shipsDemandHighBuyPriceBoost;
            else if (deficit == 1)
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

        if(deficit < 1)
            demandBonus = 0f;
        else {
            if(deficit>=3)
                demandBonus =  RTConfig.weaponsDemandHighSellPriceBoost;
            else if (deficit == 1)
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

        if(deficit < 1)
            demandBonus = 0f;
        else {
            if(deficit>=3)
                demandBonus =  RTConfig.weaponsDemandHighBuyPriceBoost;
            else if (deficit == 1)
                demandBonus = RTConfig.weaponsDemandMinorBuyPriceBoost;
            else
                demandBonus = RTConfig.weaponsDemandModerateBuyPriceBoost;
        }
        return demandBonus;
    }
}
