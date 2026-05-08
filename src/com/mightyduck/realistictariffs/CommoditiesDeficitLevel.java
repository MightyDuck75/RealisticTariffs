package com.mightyduck.realistictariffs;

import com.fs.starfarer.api.campaign.econ.MarketAPI;

public enum CommoditiesDeficitLevel {
    NONE,
    ILLICIT_ONLY,
    MINOR,
    SEVERE,
    CRITICAL;


    public static CommoditiesDeficitLevel evaluate(MarketAPI market, int numberOfShortages, int illicitShortages) {
        boolean meetsNormal = numberOfShortages >= RTConfig.intelTriggerThreshold;
        boolean meetsIllicit = illicitShortages >= RTConfig.intelIllicitTriggerThreshold;

        if (!meetsNormal && !meetsIllicit) return NONE;
        if (!meetsNormal && meetsIllicit) return ILLICIT_ONLY;

        float tariff = market.getTariff().getModifiedValue();
        if (tariff <= RTConfig.criticalTariffThreshold) return CRITICAL;
        if (tariff <= RTConfig.severeTariffThreshold) return SEVERE;

        return MINOR;
    }
}
