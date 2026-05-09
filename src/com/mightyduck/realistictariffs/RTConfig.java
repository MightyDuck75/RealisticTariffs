package com.mightyduck.realistictariffs;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import org.json.JSONObject;
import java.util.*;

public class RTConfig {

    //Constants
    public static final Set<String> ECONOMIC_COMMODITIES = new HashSet<>(Arrays.asList(
            Commodities.SHIPS,Commodities.CREW, Commodities.DOMESTIC_GOODS, Commodities.FOOD,
            Commodities.FUEL, Commodities.HAND_WEAPONS, Commodities.HEAVY_MACHINERY, Commodities.LOBSTER,
            Commodities.LUXURY_GOODS, Commodities.MARINES, Commodities.METALS, Commodities.ORE,
            Commodities.ORGANICS, Commodities.RARE_METALS, Commodities.RARE_ORE, Commodities.SUPPLIES,
            Commodities.VOLATILES, Commodities.DRUGS,Commodities.ORGANS));

    // --- Tariffs ---
    public static boolean isExportRebateActive = true;
    public static float normalTariff = 0.18f;
    public static float lowestPossibleTariff = 0.03f;
    public static float criticalTariffThreshold = 0.09f;
    public static float severeTariffThreshold = 0.14f;
    public static int intelTriggerThreshold = 1;
    public static int intelIllicitTriggerThreshold = 1;


    public static float commoditiesSHIPSTariffImpact = 0.04f, commoditiesCREWTariffImpact = 0.02f, commoditiesDOMESTIC_GOODSTariffImpact = 0.05f,
            commoditiesFOODTariffImpact = 0.05f, commoditiesFUELTariffImpact = 0.05f, commoditiesHAND_WEAPONSTariffImpact = 0.03f,
            commoditiesHEAVY_MACHINERYTariffImpact = 0.04f,
            commoditiesLOBSTERTariffImpact = 0.01f, commoditiesLUXURY_GOODSTariffImpact = 0.03f, commoditiesMARINESTariffImpact = 0.01f,
            commoditiesMETALSTariffImpact = 0.04f,
            commoditiesORETariffImpact = 0.03f, commoditiesORGANICSTariffImpact = 0.04f, commoditiesRARE_METALSTariffImpact = 0.03f,
            commoditiesRARE_ORETariffImpact = 0.3f,
            commoditiesSUPPLIESTariffImpact = 0.05f, commoditiesVOLATILESTariffImpact = 0.03f;

    // --- Ships ---
    public static boolean isFactionBuybackProgramActive = true;
    public static float shipsDemandMinorSellPriceBoost = 0.2f;
    public static float shipsDemandModerateSellPriceBoost = 0.5f;
    public static float shipsDemandHighSellPriceBoost = 1.0f;
    public static float shipsDemandMinorBuyPriceBoost = 0.25f;
    public static float shipsDemandModerateBuyPriceBoost = 0.6f;
    public static float shipsDemandHighBuyPriceBoost = 1.1f;
    public static float exoticShipSellPriceBonus = 0.20f;
    public static float exoticShipSaleReputationGain = 0.04f;
    public static float exoticShipSaleReputationLoss = -0.08f;
    public static float factionShipSellBonus = 0.10f;
    public static float factionShipSaleReputationGain = 0.01f;
    public static float factionShipSaleReputationLoss = -0.01f;
    public static int minorShipDeficitThreshold = 1;
    public static int severeShipDeficitThreshold = 2;
    public static int criticalShipDeficitThreshold = 3;
    public static float weaponsDemandMinorSellPriceBoost = 0.10f;
    public static float weaponsDemandModerateSellPriceBoost = 0.20f;
    public static float weaponsDemandHighSellPriceBoost = 0.40f;
    public static float weaponsDemandMinorBuyPriceBoost = 0.15f;
    public static float weaponsDemandModerateBuyPriceBoost = 0.25f;
    public static float weaponsDemandHighBuyPriceBoost = 0.45f;

    // --- Wars ---
    public static float shipWarPricesBonus = 0.10f;
    public static float weaponsWarPricesBonus = 0.10f;
    public static float shipMultipleWarsPricesBonus = 0.20f;
    public static float weaponsMultipleWarsPricesBonus = 0.20f;
    public static float maxShipSellPriceMult = 1.3f, maxShipBuyPriceMult = 1.3f,
            maxWeaponSellPriceMult = 1.3f, maxWeaponBuyPriceMult = 1.3f;

    public static Map<String, Float> tariffImpacts = new HashMap<>();

    public static void loadSettings() {
        try {
            JSONObject settings = Global.getSettings().loadJSON("data/config/rt_settings.json");

            JSONObject tariffs = settings.getJSONObject("Tariff Commodity Demand Values");
            JSONObject armaments = settings.getJSONObject("Ship & Armaments Values");

            // Parse Tariffs (optDouble prevents crashes if a player deletes a line by mistake)
            isExportRebateActive = tariffs.optBoolean("isExportRebateActive", true);

            normalTariff = (float) tariffs.optDouble("normalTariff", 0.18);
            lowestPossibleTariff = (float) tariffs.optDouble("lowestPossibleTariff", 0.03);
            if (lowestPossibleTariff < 0f)
                lowestPossibleTariff = 0.03f;

            criticalTariffThreshold = (float) tariffs.optDouble("criticalTariffThreshold", 0.09);
            severeTariffThreshold = (float) tariffs.optDouble("severeTariffThreshold", 0.14);
            intelTriggerThreshold = tariffs.optInt("numberOfCommoditiesInDeficitToTriggerIntel", 1);
            intelIllicitTriggerThreshold = tariffs.optInt("numberOfIllegalCommoditiesInDeficitToTriggerIntel", 1);

            commoditiesSHIPSTariffImpact = (float) tariffs.optDouble("commoditiesSHIPSTariffImpact", 0.04);
            commoditiesCREWTariffImpact  = (float) tariffs.optDouble("commoditiesCREWTariffImpact", 0.02);
            commoditiesDOMESTIC_GOODSTariffImpact = (float) tariffs.optDouble("commoditiesDOMESTIC_GOODSTariffImpact", 0.05);
            commoditiesFOODTariffImpact = (float) tariffs.optDouble("commoditiesFOODTariffImpact", 0.05);
            commoditiesFUELTariffImpact = (float) tariffs.optDouble("commoditiesFUELTariffImpact", 0.05);
            commoditiesHAND_WEAPONSTariffImpact = (float) tariffs.optDouble("commoditiesHAND_WEAPONSTariffImpact", 0.03);
            commoditiesHEAVY_MACHINERYTariffImpact = (float) tariffs.optDouble("commoditiesHEAVY_MACHINERYTariffImpact", 0.04);
            commoditiesLOBSTERTariffImpact = (float) tariffs.optDouble("commoditiesLOBSTERTariffImpact", 0.01);
            commoditiesLUXURY_GOODSTariffImpact = (float) tariffs.optDouble("commoditiesLUXURY_GOODSTariffImpact", 0.03);
            commoditiesMARINESTariffImpact = (float) tariffs.optDouble("commoditiesMARINESTariffImpact", 0.01);
            commoditiesMETALSTariffImpact = (float) tariffs.optDouble("commoditiesMETALSTariffImpact", 0.04);
            commoditiesORETariffImpact = (float) tariffs.optDouble("commoditiesORETariffImpact", 0.03);
            commoditiesORGANICSTariffImpact = (float) tariffs.optDouble("commoditiesORGANICSTariffImpact", 0.04);
            commoditiesRARE_METALSTariffImpact = (float) tariffs.optDouble("commoditiesRARE_METALSTariffImpact", 0.3);
            commoditiesRARE_ORETariffImpact = (float) tariffs.optDouble("commoditiesRARE_ORETariffImpact", 0.03);
            commoditiesSUPPLIESTariffImpact = (float) tariffs.optDouble("commoditiesSUPPLIESTariffImpact", 0.05);
            commoditiesVOLATILESTariffImpact = (float) tariffs.optDouble("commoditiesVOLATILESTariffImpact", 0.03);

            // Ships Price Boosts
            shipsDemandMinorSellPriceBoost = (float) armaments.optDouble("shipsDemandMinorSellPriceBoost", 0.20);
            shipsDemandModerateSellPriceBoost = (float) armaments.optDouble("shipsDemandModerateSellPriceBoost", 0.6);
            shipsDemandHighSellPriceBoost = (float) armaments.optDouble("shipsDemandHighSellPriceBoost", 1.0);
            shipsDemandMinorBuyPriceBoost = (float) armaments.optDouble("shipsDemandMinorBuyPriceBoost", 0.25);
            shipsDemandModerateBuyPriceBoost = (float) armaments.optDouble("shipsDemandModerateBuyPriceBoost", 0.65);
            shipsDemandHighBuyPriceBoost = (float) armaments.optDouble("shipsDemandHighBuyPriceBoost", 1.05);

            // Small Credit Bonus for selling exotic and faction design type ships for parent factions
            isFactionBuybackProgramActive = armaments.optBoolean("isFactionBuybackProgramActive", true);
            // Exotic Ships Price Boosts Design Type
            exoticShipSellPriceBonus = (float) armaments.optDouble("exoticShipSellPriceBonus", 0.20);
            exoticShipSaleReputationGain = (float)armaments.optDouble("exoticShipSaleReputationGain", 0.04);
            exoticShipSaleReputationLoss = (float)armaments.optDouble("exoticShipSaleReputationLoss", -0.08);
            // Faction Ships Price Boosts Design Type
            factionShipSellBonus = (float) armaments.optDouble("factionShipSellBonus", 0.10);
            factionShipSaleReputationGain = (float)armaments.optDouble("factionShipSaleReputationGain", 0.01);
            factionShipSaleReputationLoss = (float)armaments.optDouble("factionShipSaleReputationLoss", -0.01);

            minorShipDeficitThreshold = tariffs.optInt("minorShipHullsWeaponsDeficitThreshold", 1);
            severeShipDeficitThreshold = tariffs.optInt("severeShipHullsWeaponsDeficitThreshold", 2);
            criticalShipDeficitThreshold = tariffs.optInt("criticalShipHullsWeaponsDeficitThreshold", 3);

            // Weapons Selling Price boost
            weaponsDemandMinorSellPriceBoost = (float) armaments.optDouble("weaponsDemandMinorSellPriceBoost", 0.10);
            weaponsDemandModerateSellPriceBoost = (float) armaments.optDouble("weaponsDemandModerateSellPriceBoost", 0.20);
            weaponsDemandHighSellPriceBoost = (float) armaments.optDouble("weaponsDemandHighSellPriceBoost", 0.40);
            // Weapons Buying Price boost
            weaponsDemandMinorBuyPriceBoost = (float) armaments.optDouble("weaponsDemandMinorBuyPriceBoost", 0.15);
            weaponsDemandModerateBuyPriceBoost = (float) armaments.optDouble("weaponsDemandModerateBuyPriceBoost", 0.25);
            weaponsDemandHighBuyPriceBoost = (float) armaments.optDouble("weaponsDemandHighBuyPriceBoost", 0.45);

            // War boost
            shipWarPricesBonus = (float) armaments.optDouble("shipWarPricesBonus",0.10);
            weaponsWarPricesBonus = (float) armaments.optDouble("weaponsWarPricesBonus", 0.10);

            // Multiple war boost
            shipMultipleWarsPricesBonus = (float) armaments.optDouble("shipMultipleWarsPricesBonus", 0.20);
            weaponsMultipleWarsPricesBonus = (float) armaments.optDouble("weaponsMultipleWarsPricesBonus", 0.20);

            //Ship & weapons Prices Max Multiplier over Base Price
            maxShipSellPriceMult = (float) armaments.optDouble("maxShipSellPriceMult", 1.3);
            maxShipBuyPriceMult = (float) armaments.optDouble("maxShipBuyPriceMult", 1.3);
            maxWeaponSellPriceMult = (float) armaments.optDouble("maxWeaponSellPriceMult", 1.3);
            maxWeaponBuyPriceMult = (float) armaments.optDouble("maxWeaponBuyPriceMult", 1.3);

            mapCommoditiesAndTheirTariffImpact();

            Global.getLogger(RTConfig.class).info("Realistic Tariffs settings loaded successfully.");
        } catch (Exception e) {
            Global.getLogger(RTConfig.class).error("Failed to load rt_settings.json! Using default values.", e);
        }
    }

    private static void mapCommoditiesAndTheirTariffImpact(){
        tariffImpacts.clear();
        tariffImpacts.put(Commodities.SHIPS, commoditiesSHIPSTariffImpact);
        tariffImpacts.put(Commodities.CREW,commoditiesCREWTariffImpact);
        tariffImpacts.put(Commodities.DOMESTIC_GOODS,commoditiesDOMESTIC_GOODSTariffImpact);
        tariffImpacts.put(Commodities.FOOD,commoditiesFOODTariffImpact);
        tariffImpacts.put(Commodities.FUEL,commoditiesFUELTariffImpact);
        tariffImpacts.put(Commodities.HAND_WEAPONS,commoditiesHAND_WEAPONSTariffImpact);
        tariffImpacts.put(Commodities.HEAVY_MACHINERY,commoditiesHEAVY_MACHINERYTariffImpact);
        tariffImpacts.put(Commodities.LOBSTER,commoditiesLOBSTERTariffImpact);
        tariffImpacts.put(Commodities.LUXURY_GOODS,commoditiesLUXURY_GOODSTariffImpact);
        tariffImpacts.put(Commodities.MARINES,commoditiesMARINESTariffImpact);
        tariffImpacts.put(Commodities.METALS,commoditiesMETALSTariffImpact);
        tariffImpacts.put(Commodities.ORE,commoditiesORETariffImpact);
        tariffImpacts.put(Commodities.ORGANICS,commoditiesORGANICSTariffImpact);
        tariffImpacts.put(Commodities.RARE_METALS,commoditiesRARE_METALSTariffImpact);
        tariffImpacts.put(Commodities.RARE_ORE,commoditiesRARE_ORETariffImpact);
        tariffImpacts.put(Commodities.SUPPLIES,commoditiesSUPPLIESTariffImpact);
        tariffImpacts.put(Commodities.VOLATILES,commoditiesVOLATILESTariffImpact);
    }
}