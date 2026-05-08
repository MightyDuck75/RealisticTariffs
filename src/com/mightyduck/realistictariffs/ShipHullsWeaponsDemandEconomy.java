package com.mightyduck.realistictariffs;

import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;

public class ShipHullsWeaponsDemandEconomy {
    private static final String SHIPS_COMMODITY = Commodities.SHIPS; // use constant!

    public static float getWeaponDemandBuyMultiplier(MarketAPI market) {

        return getDemandMultiplier(market, ShipsHullWeaponsDeficitLevel.evaluate(getShipDeficit(market)),
                RTConfig.weaponsDemandMinorBuyPriceBoost,
                RTConfig.weaponsDemandModerateBuyPriceBoost,
                RTConfig.weaponsDemandHighBuyPriceBoost);
    }

    public static float getWeaponDemandSellMultiplier(MarketAPI market) {

        return getDemandMultiplier(market, ShipsHullWeaponsDeficitLevel.evaluate(getShipDeficit(market)),
                RTConfig.weaponsDemandMinorSellPriceBoost,
                RTConfig.weaponsDemandModerateSellPriceBoost,
                RTConfig.weaponsDemandHighSellPriceBoost);
    }

    public static float getShipDemandBuyMultiplier(MarketAPI market) {

        return getDemandMultiplier(market, ShipsHullWeaponsDeficitLevel.evaluate(getShipDeficit(market)),
                RTConfig.shipsDemandMinorBuyPriceBoost,
                RTConfig.shipsDemandModerateBuyPriceBoost,
                RTConfig.shipsDemandHighBuyPriceBoost);
    }

    public static float getShipDemandSellMultiplier(MarketAPI market) {

        return getDemandMultiplier(market, ShipsHullWeaponsDeficitLevel.evaluate(getShipDeficit(market)),
                RTConfig.shipsDemandMinorSellPriceBoost,
                RTConfig.shipsDemandModerateSellPriceBoost,
                RTConfig.shipsDemandHighSellPriceBoost);
    }

    private static float getDemandMultiplier(MarketAPI market, ShipsHullWeaponsDeficitLevel level,
                                             float minor, float severe, float high) {
        return switch (level) {
            case NONE -> 0f;
            case MINOR -> minor;
            case SEVERE -> severe;
            case CRITICAL -> high;
        };
    }


    private static int getShipDeficit(MarketAPI market) {
        CommodityOnMarketAPI ships = market.getCommodityData(SHIPS_COMMODITY);
        return ships == null ? 0 : ships.getMaxDemand() - ships.getAvailable();
    }
}
