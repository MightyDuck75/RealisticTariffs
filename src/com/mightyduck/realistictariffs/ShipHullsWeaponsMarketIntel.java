package com.mightyduck.realistictariffs;

import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.Color;
import java.util.Set;

public class ShipHullsWeaponsMarketIntel extends BaseMarketIntel {
    private final ShipHullsWeaponsIntelMarketCondition condition;

    public ShipHullsWeaponsMarketIntel(MarketAPI market, ShipHullsWeaponsIntelMarketCondition condition) {
        super(market);
        this.condition = condition;
    }

    public ShipHullsWeaponsIntelMarketCondition getCondition() {
        return condition;
    }

    @Override
    public String getName() {
        String fName = market.getFaction().getDisplayName();
        String mName = market.getName();

        switch (condition) {
            case DEMAND_CRITICAL: return "Critical Shortage of Ships & Armaments in " + mName;
            case DEMAND_MODERATE: return "Moderate Shortage of Ships & Armaments in " + mName;
            case DEMAND_MINOR:    return "Minor Shortage of Ships & Armaments in " + mName;
            case WAR_MULTIPLE:    return "Multi-Front War Drives Military Prices";
            case WAR_SINGLE:      return "War-Driven Military Price Inflation";
            case FACTION_BUYBACK_HEGEMONY:
            case FACTION_BUYBACK_LUDDICPATH:
            case FACTION_BUYBACK_LUDDICCHURCH:
            case FACTION_BUYBACK_TRITACHYON:
                return fName + " Ship Buyback Initiative";
            default: return "Market Update: " + mName;
        }
    }

    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        info.addPara(getName(), getTitleColor(mode), 0f);

        float pad = 3f;
        Color gray = Misc.getGrayColor();
        Color highlight = Misc.getHighlightColor();
        String fName = market.getFaction().getDisplayName();

        switch (condition) {
            case DEMAND_CRITICAL:
                info.addPara("Ship sale prices have surged to %s and weapon prices to %s", pad, gray, highlight,
                        formatTotal(RTConfig.shipsDemandHighSellPriceBoost), formatTotal(RTConfig.weaponsDemandHighSellPriceBoost));
                break;
            case DEMAND_MODERATE:
                info.addPara("Ship sale prices have risen to %s and weapon prices to %s", pad, gray, highlight,
                        formatTotal(RTConfig.shipsDemandModerateSellPriceBoost), formatTotal(RTConfig.weaponsDemandModerateSellPriceBoost));
                break;
            case DEMAND_MINOR:
                info.addPara("Ship sale prices have increased by %s and weapon prices by %s", pad, gray, highlight,
                        formatBoost(RTConfig.shipsDemandMinorSellPriceBoost), formatBoost(RTConfig.weaponsDemandMinorSellPriceBoost));
                break;
            case WAR_MULTIPLE:
                info.addPara("The expansion of %s's conflicts has further increased military prices", pad, gray, market.getFaction().getBaseUIColor(), fName);
                break;
            case WAR_SINGLE:
                info.addPara("Ongoing conflict has increased ship and weapon prices across faction markets", gray, pad);
                break;
            case FACTION_BUYBACK_HEGEMONY:
            case FACTION_BUYBACK_LUDDICPATH:
            case FACTION_BUYBACK_LUDDICCHURCH:
            case FACTION_BUYBACK_TRITACHYON:
                info.addPara("%s is offering bonuses for returning faction ships", pad, gray,  market.getFaction().getBaseUIColor(), fName);
                break;
        }
    }

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        float opad = 10f;
        FactionAPI faction = market.getFaction();

        info.addImages(width, 128, opad, opad, faction.getCrest());

        generateNarrativeDescription(info, faction, opad);

        generateStatBullets(info, faction);
    }

    private void generateNarrativeDescription(TooltipMakerAPI info, FactionAPI faction, float opad) {
        String mName = market.getName();
        String fName = faction.getDisplayName();
        Color fColor = faction.getBaseUIColor();

        switch (condition) {
            case DEMAND_CRITICAL:
                info.addPara("%s is experiencing a critical shortage of both ships and military-grade weaponry. Fleet losses, " +
                        "logistical strain, and heightened demand have driven prices sharply upward, creating highly profitable opportunities " +
                        "for independent traders willing to supply the market.", opad, fColor, mName);
                break;
            case DEMAND_MODERATE:
                info.addPara("%s is currently facing a notable deficit in ships and ship-mounted weaponry. Ongoing demand has begun to outpace " +
                        "supply, pushing market prices upward and creating favorable conditions for traders supplying military assets.", opad, fColor, mName);
                break;
            case DEMAND_MINOR:
                info.addPara("%s is experiencing a mild shortage of ships and associated weaponry. While not critical, the reduced " +
                        "supply has led to a modest increase in both selling and purchasing prices, offering limited but reliable trade opportunities.", opad, fColor, mName);
                break;
            case WAR_MULTIPLE:
                info.addPara("%s is now engaged across multiple fronts, stretching its logistical and industrial capacity to the limit. The escalating " +
                        "demand for ships and weapon systems has driven military market prices to unprecedented levels.", opad, fColor, fName);
                break;
            case WAR_SINGLE:
                info.addPara("The escalating conflict between %s and its adversary has begun to strain military supply chains. As demand for ships " +
                        "and weapons intensifies, market prices have risen accordingly across affected systems.", opad, fColor, fName);
                break;
            case FACTION_BUYBACK_HEGEMONY:
                info.addPara("With war placing increasing pressure on fleet strength, %s has initiated a ship buyback program to recover and redeploy " +
                        "its own designs. Captains willing to sell compatible vessels will receive additional compensation as part of this effort, in " +
                        "particular for those belonging to the XIV Battlegroup.", opad, fColor, fName);
                break;
            case FACTION_BUYBACK_LUDDICPATH:
            case FACTION_BUYBACK_LUDDICCHURCH:
            case FACTION_BUYBACK_TRITACHYON:
                info.addPara("With war placing increasing pressure on fleet strength, %s has initiated a ship buyback program to recover and redeploy" +
                        " its own designs. Captains willing to sell compatible vessels will receive additional compensation as part of this effort.", opad, fColor, fName);
                break;
        }
    }

    private void generateStatBullets(TooltipMakerAPI info, FactionAPI faction) {
        float pad = 3f;
        Color h = Misc.getHighlightColor();

        switch (condition) {
            case DEMAND_CRITICAL:
                addTradeBulletListToIntelSmallDescription(info, pad, h, RTConfig.shipsDemandHighSellPriceBoost, RTConfig.weaponsDemandHighSellPriceBoost, RTConfig.shipsDemandHighBuyPriceBoost, RTConfig.weaponsDemandHighBuyPriceBoost);
                break;
            case DEMAND_MODERATE:
                addTradeBulletListToIntelSmallDescription(info, pad, h, RTConfig.shipsDemandModerateSellPriceBoost, RTConfig.weaponsDemandModerateSellPriceBoost, RTConfig.shipsDemandModerateBuyPriceBoost, RTConfig.weaponsDemandModerateBuyPriceBoost);
                break;
            case DEMAND_MINOR:
                addTradeBulletListToIntelSmallDescription(info, pad, h, RTConfig.shipsDemandMinorSellPriceBoost, RTConfig.weaponsDemandMinorSellPriceBoost, RTConfig.shipsDemandMinorBuyPriceBoost, RTConfig.weaponsDemandMinorBuyPriceBoost);
                break;
            case WAR_MULTIPLE:
                addTradeBulletListToIntelSmallDescription(info, pad, h, RTConfig.shipMultipleWarsPricesBonus, RTConfig.weaponsMultipleWarsPricesBonus, RTConfig.shipMultipleWarsPricesBonus, RTConfig.weaponsMultipleWarsPricesBonus);
                break;
            case WAR_SINGLE:
                addTradeBulletListToIntelSmallDescription(info, pad, h, RTConfig.shipWarPricesBonus, RTConfig.weaponsWarPricesBonus, RTConfig.shipWarPricesBonus, RTConfig.weaponsWarPricesBonus);
                break;
            case FACTION_BUYBACK_HEGEMONY:
                info.addPara(" %s Selling Faction Ships ", 10f, h, formatBoost(RTConfig.factionShipSellBonus));
                info.addPara(" %s Selling XIV Battlegroup Ships", 0f, h, formatBoost(RTConfig.exoticShipSellPriceBonus));
                break;
            case FACTION_BUYBACK_LUDDICPATH:
            case FACTION_BUYBACK_LUDDICCHURCH:
            case FACTION_BUYBACK_TRITACHYON:
                info.addPara(" %s Selling Faction Ships ", 10f, h, formatBoost(RTConfig.factionShipSellBonus));
                if (Factions.DIKTAT.equals(faction.getId())) {
                    info.addPara(" %s Selling Lion's Guard Ships", 0f, h, formatBoost(RTConfig.exoticShipSellPriceBonus));
                }
                break;
        }
    }

    private void addTradeBulletListToIntelSmallDescription(TooltipMakerAPI info, float pad, Color h, float sellShip, float sellWeap, float buyShip, float buyWeap) {
        info.addPara(" %s Selling Ships", pad, h, formatBoost(sellShip));
        info.addPara(" %s Selling Weapons", 0f, h, formatBoost(sellWeap));
        info.addPara(" %s Buying Ships", 0f, h, formatBoost(buyShip));
        info.addPara(" %s Buying Weapons", 0f, h, formatBoost(buyWeap));
    }

    @Override
    public Set<String> getIntelTags(SectorMapAPI map) {
        Set<String> tags = super.getIntelTags(map);
        tags.add(market.getFaction().getId());

        if (isImportant()) {
            tags.add(Tags.INTEL_IMPORTANT);
        }

        tags.add(Tags.INTEL_TRADE);
        return tags;
    }

    @Override
    public boolean isImportant() {
        return condition == ShipHullsWeaponsIntelMarketCondition.DEMAND_CRITICAL ||
                condition == ShipHullsWeaponsIntelMarketCondition.DEMAND_MODERATE ||
                condition == ShipHullsWeaponsIntelMarketCondition.WAR_MULTIPLE;
    }

    // --- Format Helpers ---
    private String formatBoost(float mult) { return "+" + (int)(mult * 100f) + "%"; }
    private String formatTotal(float mult) { return (int)(mult * 100f) + "%"; }
}