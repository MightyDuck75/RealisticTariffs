package com.mightyduck.realistictariffs;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import org.json.JSONObject;
import java.util.*;

public class RTConfig {

    // --- Tariffs ---
    public static boolean IsExportRebateActive = true;
    public static float NormalTariff = 0.18f;
    public static float LowestPossibleTariff = 0.03f;
    public static float CommoditiesSHIPSTariffImpact = 0.04f, CommoditiesCREWTariffImpact = 0.01f, CommoditiesDOMESTIC_GOODSTariffImpact = 0.05f,
            CommoditiesFOODTariffImpact = 0.05f, CommoditiesFUELTariffImpact = 0.05f, CommoditiesHAND_WEAPONSTariffImpact = 0.03f, CommoditiesHEAVY_MACHINERYTariffImpact = 0.04f,
            CommoditiesLOBSTERTariffImpact = 0.01f, CommoditiesLUXURY_GOODSTariffImpact = 0.03f, CommoditiesMARINESTariffImpact = 0.01f, CommoditiesMETALSTariffImpact = 0.04f,
            CommoditiesORETariffImpact = 0.03f, CommoditiesORGANICSTariffImpact = 0.05f, CommoditiesRARE_METALSTariffImpact = 0.03f, CommoditiesRARE_ORETariffImpact = 0.3f,
            CommoditiesSUPPLIESTariffImpact = 0.05f, CommoditiesVOLATILESTariffImpact = 0.03f;

    // --- Ships ---
    public static boolean IsFactionBuybackProgramActive = true;
    public static float ShipsDemandMinorSellPriceBoost = 0.2f;
    public static float ShipsDemandModerateSellPriceBoost = 0.5f;
    public static float ShipsDemandHighSellPriceBoost = 1.0f;
    public static float ShipsDemandMinorBuyPriceBoost = 0.25f;
    public static float ShipsDemandModerateBuyPriceBoost = 0.6f;
    public static float ShipsDemandHighBuyPriceBoost = 1.1f;
    public static float ExoticShipSellPriceBonus = 0.20f;
    public static float ExoticShipSaleReputationGain = 0.04f;
    public static float ExoticShipSaleReputationLoss = -0.08f;
    public static float FactionShipSellBonus = 0.10f;
    public static float FactionShipSaleReputationGain = 0.01f;
    public static float FactionShipSaleReputationLoss = -0.01f;
    public static float WeaponsDemandMinorSellPriceBoost = 0.10f;
    public static float WeaponsDemandModerateSellPriceBoost = 0.20f;
    public static float WeaponsDemandHighSellPriceBoost = 0.40f;
    public static float WeaponsDemandMinorBuyPriceBoost = 0.15f;
    public static float WeaponsDemandModerateBuyPriceBoost = 0.25f;
    public static float WeaponsDemandHighBuyPriceBoost = 0.45f;
    // --- Wars ---
    public static float ShipWarPricesBonus = 0.10f;
    public static float WeaponsWarPricesBonus = 0.10f;
    public static float ShipMultipleWarsPricesBonus = 0.20f;
    public static float WeaponsMultipleWarsPricesBonus = 0.20f;

    public static float MaxShipSellPriceMult = 1.3f, MaxShipBuyPriceMult = 1.3f,
            MaxWeaponSellPriceMult = 1.3f, MaxWeaponBuyPriceMult = 1.3f;

    public static Map<String, Float> mapCommoditiesTariffImpact = new HashMap<>();

    public static void loadSettings() {
        try {
            // Load the JSON file from your mod's data/config folder
            JSONObject settings = Global.getSettings().loadJSON("data/config/rt_settings.json");

            // Extract the sub-objects
            JSONObject tariffs = settings.getJSONObject("Tariff Commodity Demand Values");
            JSONObject armaments = settings.getJSONObject("Ship & Armaments Values");

            // Parse Tariffs (optDouble prevents crashes if a player deletes a line by mistake)
            IsExportRebateActive = tariffs.optBoolean("IsExportRebateActive", true);

            NormalTariff = (float) tariffs.optDouble("NormalTariff", 0.18);
            LowestPossibleTariff = (float) tariffs.optDouble("LowestPossibleTariff", 0.03);

            CommoditiesSHIPSTariffImpact  = (float) tariffs.optDouble("CommoditiesSHIPSTariffImpact", 0.04);
            CommoditiesCREWTariffImpact  = (float) tariffs.optDouble("CommoditiesCREWTariffImpact", 0.02);
            CommoditiesDOMESTIC_GOODSTariffImpact = (float) tariffs.optDouble("CommoditiesDOMESTIC_GOODSTariffImpact", 0.05);
            CommoditiesFOODTariffImpact = (float) tariffs.optDouble("CommoditiesFOODTariffImpact", 0.05);
            CommoditiesFUELTariffImpact = (float) tariffs.optDouble("CommoditiesFUELTariffImpact", 0.05);
            CommoditiesHAND_WEAPONSTariffImpact = (float) tariffs.optDouble("CommoditiesHAND_WEAPONSTariffImpact", 0.03);
            CommoditiesHEAVY_MACHINERYTariffImpact = (float) tariffs.optDouble("CommoditiesHEAVY_MACHINERYTariffImpact", 0.04);
            CommoditiesLOBSTERTariffImpact = (float) tariffs.optDouble("CommoditiesLOBSTERTariffImpact", 0.01);
            CommoditiesLUXURY_GOODSTariffImpact = (float) tariffs.optDouble("CommoditiesLUXURY_GOODSTariffImpact", 0.03);
            CommoditiesMARINESTariffImpact = (float) tariffs.optDouble("CommoditiesMARINESTariffImpact", 0.01);
            CommoditiesMETALSTariffImpact = (float) tariffs.optDouble("CommoditiesMETALSTariffImpact", 0.04);
            CommoditiesORETariffImpact = (float) tariffs.optDouble("CommoditiesORETariffImpact", 0.03);
            CommoditiesORGANICSTariffImpact = (float) tariffs.optDouble("CommoditiesORGANICSTariffImpact", 0.04);
            CommoditiesRARE_METALSTariffImpact = (float) tariffs.optDouble("CommoditiesRARE_METALSTariffImpact", 0.3);
            CommoditiesRARE_ORETariffImpact = (float) tariffs.optDouble("CommoditiesRARE_ORETariffImpact", 0.03);
            CommoditiesSUPPLIESTariffImpact = (float) tariffs.optDouble("CommoditiesSUPPLIESTariffImpact", 0.05);
            CommoditiesVOLATILESTariffImpact = (float) tariffs.optDouble("CommoditiesVOLATILESTariffImpact", 0.03);

            // Ships Price Boosts
            ShipsDemandMinorSellPriceBoost = (float) armaments.optDouble("ShipsDemandMinorSellPriceBoost", 0.20);
            ShipsDemandModerateSellPriceBoost = (float) armaments.optDouble("ShipsDemandModerateSellPriceBoost", 0.6);
            ShipsDemandHighSellPriceBoost = (float) armaments.optDouble("ShipsDemandHighSellPriceBoost", 1.0);
            ShipsDemandMinorBuyPriceBoost = (float) armaments.optDouble("ShipsDemandMinorBuyPriceBoost", 0.25);
            ShipsDemandModerateBuyPriceBoost = (float) armaments.optDouble("ShipsDemandModerateBuyPriceBoost", 0.65);
            ShipsDemandHighBuyPriceBoost = (float) armaments.optDouble("ShipsDemandHighBuyPriceBoost", 1.05);

            // Small Credit Bonus for selling exotic and faction design type ships for parent factions
            IsFactionBuybackProgramActive = armaments.optBoolean("IsFactionBuybackProgramActive", true);
            // Exotic Ships Price Boosts Design Type
            ExoticShipSellPriceBonus = (float) armaments.optDouble("ExoticShipSellPriceBonus", 0.20);
            ExoticShipSaleReputationGain = (float)armaments.optDouble("ExoticShipSaleReputationGain", 0.04);
            ExoticShipSaleReputationLoss = (float)armaments.optDouble("ExoticShipSaleReputationLoss", -0.08);
            // Faction Ships Price Boosts Design Type
            FactionShipSellBonus = (float) armaments.optDouble("FactionShipSellBonus", 0.10);
            FactionShipSaleReputationGain = (float)armaments.optDouble("FactionShipSaleReputationGain", 0.01);
            FactionShipSaleReputationLoss = (float)armaments.optDouble("FactionShipSaleReputationLoss", -0.01);

            // Weapons Selling Price boost
            WeaponsDemandMinorSellPriceBoost = (float) armaments.optDouble("WeaponsDemandMinorSellPriceBoost", 0.10);
            WeaponsDemandModerateSellPriceBoost = (float) armaments.optDouble("WeaponsDemandModerateSellPriceBoost", 0.20);
            WeaponsDemandHighSellPriceBoost = (float) armaments.optDouble("WeaponsDemandHighSellPriceBoost", 0.40);
            // Weapons Buying Price boost
            WeaponsDemandMinorBuyPriceBoost = (float) armaments.optDouble("WeaponsDemandMinorBuyPriceBoost", 0.15);
            WeaponsDemandModerateBuyPriceBoost = (float) armaments.optDouble("WeaponsDemandModerateBuyPriceBoost", 0.25);
            WeaponsDemandHighBuyPriceBoost = (float) armaments.optDouble("WeaponsDemandHighBuyPriceBoost", 0.45);

            // War boost
            ShipWarPricesBonus = (float) armaments.optDouble("ShipWarPricesBonus",0.10);
            WeaponsWarPricesBonus = (float) armaments.optDouble("WeaponsWarPricesBonus", 0.10);

            // Multiple war boost
            ShipMultipleWarsPricesBonus = (float) armaments.optDouble("ShipMultipleWarsPricesBonus", 0.20);
            WeaponsMultipleWarsPricesBonus = (float) armaments.optDouble("WeaponsMultipleWarsPricesBonus", 0.20);
            //Ship & weapons Prices Max Multiplier over Base Price
            MaxShipSellPriceMult = (float) armaments.optDouble("MaxShipSellPriceMult", 1.3);
            MaxShipBuyPriceMult = (float) armaments.optDouble("MaxShipBuyPriceMult", 1.3);
            MaxWeaponSellPriceMult = (float) armaments.optDouble("MaxWeaponSellPriceMult", 1.3);
            MaxWeaponBuyPriceMult = (float) armaments.optDouble("MaxWeaponBuyPriceMult", 1.3);

            mapCommoditiesAndTheirTariffImpact();

            Global.getLogger(RTConfig.class).info("Realistic Tariffs settings loaded successfully.");
        } catch (Exception e) {
            Global.getLogger(RTConfig.class).error("Failed to load rt_settings.json! Using default values.", e);
        }
    }

    private static void mapCommoditiesAndTheirTariffImpact(){
        mapCommoditiesTariffImpact.clear();
        mapCommoditiesTariffImpact = new HashMap<>();
        mapCommoditiesTariffImpact.put(Commodities.SHIPS,CommoditiesSHIPSTariffImpact);
        mapCommoditiesTariffImpact.put(Commodities.CREW,CommoditiesCREWTariffImpact);
        mapCommoditiesTariffImpact.put(Commodities.DOMESTIC_GOODS,CommoditiesDOMESTIC_GOODSTariffImpact);
        mapCommoditiesTariffImpact.put(Commodities.FOOD,CommoditiesFOODTariffImpact);
        mapCommoditiesTariffImpact.put(Commodities.FUEL,CommoditiesFUELTariffImpact);
        mapCommoditiesTariffImpact.put(Commodities.HAND_WEAPONS,CommoditiesHAND_WEAPONSTariffImpact);
        mapCommoditiesTariffImpact.put(Commodities.HEAVY_MACHINERY,CommoditiesHEAVY_MACHINERYTariffImpact);
        mapCommoditiesTariffImpact.put(Commodities.LOBSTER,CommoditiesLOBSTERTariffImpact);
        mapCommoditiesTariffImpact.put(Commodities.LUXURY_GOODS,CommoditiesLUXURY_GOODSTariffImpact);
        mapCommoditiesTariffImpact.put(Commodities.MARINES,CommoditiesMARINESTariffImpact);
        mapCommoditiesTariffImpact.put(Commodities.METALS,CommoditiesMETALSTariffImpact);
        mapCommoditiesTariffImpact.put(Commodities.ORE,CommoditiesORETariffImpact);
        mapCommoditiesTariffImpact.put(Commodities.ORGANICS,CommoditiesORGANICSTariffImpact);
        mapCommoditiesTariffImpact.put(Commodities.RARE_METALS,CommoditiesRARE_METALSTariffImpact);
        mapCommoditiesTariffImpact.put(Commodities.RARE_ORE,CommoditiesRARE_ORETariffImpact);
        mapCommoditiesTariffImpact.put(Commodities.SUPPLIES,CommoditiesSUPPLIESTariffImpact);
        mapCommoditiesTariffImpact.put(Commodities.VOLATILES,CommoditiesVOLATILESTariffImpact);
    };
}