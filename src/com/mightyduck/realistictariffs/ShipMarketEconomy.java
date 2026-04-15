package com.mightyduck.realistictariffs;

import com.fs.starfarer.api.campaign.econ.MarketAPI;

public class ShipMarketEconomy {

    public static float getShipBuyMultiplier(MarketAPI market) {
        float demand = ShipWeaponsDemandEconomy.getShipDemandBuyMultiplier(market);
        float war = WarPriceModifier.getShipWarMultiplier(market);

        float result = demand + war;
        if (result > RTConfig.MaxShipBuyPriceMult) {
            result = RTConfig.MaxShipBuyPriceMult;
        }

        return result;
    }

    public static float getShipSellMultiplier(MarketAPI market) {
        float demand = ShipWeaponsDemandEconomy.getShipDemandSellMultiplier(market);
        float war = WarPriceModifier.getShipWarMultiplier(market);

        float result = demand + war;
        if (result > RTConfig.MaxShipSellPriceMult) {
            result = RTConfig.MaxShipSellPriceMult;
        }

        return result;
    }

    public static float getWeaponBuyMultiplier(MarketAPI market) {
        float demand = ShipWeaponsDemandEconomy.getWeaponDemandBuyMultiplier(market);
        float war = WarPriceModifier.getWeaponsWarMultiplier(market);

        float result = demand + war;
        if (result > RTConfig.MaxWeaponBuyPriceMult) {
            result = RTConfig.MaxWeaponBuyPriceMult;
        }

        return result;
    }
    public static float getWeaponSellMultiplier(MarketAPI market) {
        float demand = ShipWeaponsDemandEconomy.getWeaponDemandSellMultiplier(market);
        float war = WarPriceModifier.getWeaponsWarMultiplier(market);

        float result = demand + war;
        if (result > RTConfig.MaxWeaponSellPriceMult) {
            result = RTConfig.MaxWeaponSellPriceMult;
        }

        return result;
    }
}
