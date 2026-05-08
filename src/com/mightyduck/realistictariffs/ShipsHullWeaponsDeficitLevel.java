package com.mightyduck.realistictariffs;

public enum ShipsHullWeaponsDeficitLevel {
    NONE(0),
    MINOR(RTConfig.minorShipDeficitThreshold),
    SEVERE(RTConfig.severeShipDeficitThreshold),
    CRITICAL(RTConfig.criticalShipDeficitThreshold);

    public final int threshold;

    ShipsHullWeaponsDeficitLevel(int threshold) {
        this.threshold = threshold;
    }

    public static ShipsHullWeaponsDeficitLevel evaluate(int deficit) {
        if (deficit <= 0) return NONE;

        if (deficit >= RTConfig.criticalShipDeficitThreshold) return CRITICAL;

        if (deficit >= RTConfig.severeShipDeficitThreshold) return SEVERE;

        return MINOR;
    }
}
