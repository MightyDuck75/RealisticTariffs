package com.mightyduck.realistictariffs;

import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;

public class ShipWeaponsDemandEconomy {
    public static float getShipDemandSellMultiplier(MarketAPI market) {
        CommodityOnMarketAPI ships = market.getCommodityData("ships");
        if (ships == null) return 0f;

        int deficit = ships.getMaxDemand() - ships.getAvailable();
        float demandBonus;

        if(deficit < 1)
            demandBonus = 0f;
        else {
            if (deficit >= 3)
                demandBonus = RTConfig.ShipsDemandHighSellPriceBoost;
            else if (deficit == 1)
                demandBonus = RTConfig.ShipsDemandMinorSellPriceBoost;
            else
                demandBonus = RTConfig.ShipsDemandModerateSellPriceBoost;
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
                demandBonus = RTConfig.ShipsDemandHighBuyPriceBoost;
            else if (deficit == 1)
                demandBonus = RTConfig.ShipsDemandMinorBuyPriceBoost;
            else
                demandBonus = RTConfig.ShipsDemandModerateBuyPriceBoost;
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
                demandBonus =  RTConfig.WeaponsDemandHighSellPriceBoost;
            else if (deficit == 1)
                demandBonus = RTConfig.WeaponsDemandMinorSellPriceBoost;
            else
                demandBonus = RTConfig.WeaponsDemandModerateSellPriceBoost;
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
                demandBonus =  RTConfig.WeaponsDemandHighBuyPriceBoost;
            else if (deficit == 1)
                demandBonus = RTConfig.WeaponsDemandMinorBuyPriceBoost;
            else
                demandBonus = RTConfig.WeaponsDemandModerateBuyPriceBoost;
        }
        return demandBonus;
    }
}
