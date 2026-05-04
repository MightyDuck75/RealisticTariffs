package com.mightyduck.realistictariffs;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.ui.SectorMapAPI;

public abstract class BaseMarketIntel extends BaseIntelPlugin {

    protected final MarketAPI market;

    public BaseMarketIntel(MarketAPI market) {
        this.market = market;
    }

    public MarketAPI getMarket() {
        return market;
    }

    @Override
    public String getIcon() {
        return market.getFaction().getCrest();
    }

    @Override
    public SectorEntityToken getMapLocation(SectorMapAPI map) {
        return market.getPrimaryEntity();
    }

    @Override
    public boolean isHidden() {
        if (super.isHidden()) return true;
        if (market.getFaction().isPlayerFaction()) return false;
        return !Global.getSector().getIntelManager().isPlayerInRangeOfCommRelay();
    }
}