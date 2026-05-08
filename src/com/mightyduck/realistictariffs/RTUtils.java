package com.mightyduck.realistictariffs;

import com.fs.starfarer.api.campaign.econ.MarketAPI;

public class RTUtils {
    public static boolean isValidPrimaryMarket(MarketAPI market) {
        if (market == null || market.isHidden() || market.getFaction().isPlayerFaction()) return false;
        if (market.getPrimaryEntity() == null) return false;

        // Ensures we only process the main market object for a location
        return market.getPrimaryEntity().getMarket() == market;
    }
}