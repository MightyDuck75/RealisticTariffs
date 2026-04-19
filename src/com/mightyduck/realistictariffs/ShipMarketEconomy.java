package com.mightyduck.realistictariffs;

import com.fs.starfarer.api.campaign.econ.MarketAPI;

public class ShipMarketEconomy {

    public static float getShipBuyMultiplier(MarketAPI market) {
        float demand = ShipHullsWeaponsDemandEconomy.getShipDemandBuyMultiplier(market);
        float war = WarPriceModifier.getShipWarMultiplier(market);

        float result = demand + war;
        if (result > RTConfig.maxShipBuyPriceMult) {
            result = RTConfig.maxShipBuyPriceMult;
        }

        return result;
    }

    public static float getShipSellMultiplier(MarketAPI market) {
        float demand = ShipHullsWeaponsDemandEconomy.getShipDemandSellMultiplier(market);
        float war = WarPriceModifier.getShipWarMultiplier(market);

        float result = demand + war;
        if (result > RTConfig.maxShipSellPriceMult) {
            result = RTConfig.maxShipSellPriceMult;
        }

        return result;
    }

    public static float getWeaponBuyMultiplier(MarketAPI market) {
        float demand = ShipHullsWeaponsDemandEconomy.getWeaponDemandBuyMultiplier(market);
        float war = WarPriceModifier.getWeaponsWarMultiplier(market);

        float result = demand + war;
        if (result > RTConfig.maxWeaponBuyPriceMult) {
            result = RTConfig.maxWeaponBuyPriceMult;
        }

        return result;
    }
    public static float getWeaponSellMultiplier(MarketAPI market) {
        float demand = ShipHullsWeaponsDemandEconomy.getWeaponDemandSellMultiplier(market);
        float war = WarPriceModifier.getWeaponsWarMultiplier(market);

        float result = demand + war;
        if (result > RTConfig.maxWeaponSellPriceMult) {
            result = RTConfig.maxWeaponSellPriceMult;
        }

        return result;
    }
}
