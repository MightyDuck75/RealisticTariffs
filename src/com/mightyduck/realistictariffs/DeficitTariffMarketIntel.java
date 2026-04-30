package com.mightyduck.realistictariffs;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.*;
import java.util.List;

public class DeficitTariffMarketIntel extends BaseMarketIntel {
    private int countIllegalGoods = 0;
    private int countNormalGoods = 0;
    private boolean hasOnlyIllegalGoodsDemand = false;
    private final Color blueIntelTitleColor;
    private final Color goldHighlightColor;
    private final Color grayTextColor;

    private List<String> ecoCommodities = Arrays.asList(
            Commodities.SHIPS, Commodities.CREW, Commodities.DOMESTIC_GOODS, Commodities.FOOD,
            Commodities.FUEL, Commodities.HAND_WEAPONS, Commodities.HEAVY_MACHINERY, Commodities.LOBSTER,
            Commodities.LUXURY_GOODS, Commodities.MARINES, Commodities.METALS, Commodities.ORE,
            Commodities.ORGANICS, Commodities.RARE_METALS, Commodities.RARE_ORE, Commodities.SUPPLIES,
            Commodities.VOLATILES
    );

    public DeficitTariffMarketIntel(MarketAPI market) {
        super(market);
        this.blueIntelTitleColor = Misc.getBasePlayerColor();
        this.goldHighlightColor = Misc.getHighlightColor();
        this.grayTextColor = Misc.getGrayColor();

        refreshShortageStats(); // Calculate once on start
    }

    @Override
    public String getName() {
        if (hasOnlyIllegalGoodsDemand)
            return "Shortages of illicit goods in " + market.getName();

        float tariff = market.getTariff().getModifiedValue();

        if (tariff <= RTConfig.criticalTariffThreshold)
            return "Critical Shortages in " + market.getName();
        else if (tariff <= RTConfig.severeTariffThreshold)
            return "Severe Shortages in " + market.getName();

        return "Shortages in " + market.getName();
    }

    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        // If there are no shortages, don't draw anything
        if (countNormalGoods == 0 && countIllegalGoods == 0) return;

        info.addPara(getName(), 0f, blueIntelTitleColor, market.getTextColorForFactionOrPlanet());

        float pad = 3f;
        if (hasOnlyIllegalGoodsDemand)
            info.addPara("Profitable opportunities for those dealing in illegal goods", grayTextColor, pad);
        else {
            // Clean percentage calculation and display
            int tariffPercent = (int)(market.getTariff().getModifiedValue() * 100f);
            String highlightText = tariffPercent + "%";
            String fullText = "Local government lowered tariffs to " + tariffPercent + "%%";
            info.addPara(fullText, pad, grayTextColor, goldHighlightColor, highlightText);
        }
    }

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        if (countNormalGoods == 0 && countIllegalGoods == 0) return;

        float opad = 10f;
        info.addImage(getIcon(), width, 128f, opad);

        info.addPara("There are severe resource shortages in %s. The following commodities are in critical deficit:",
                opad, market.getTextColorForFactionOrPlanet(), market.getName());

        addCommodityShortageList(info);
        addClosingTextToTheSmallDescription(info, opad);
    }

    private void addCommodityShortageList(TooltipMakerAPI info) {
        float pad = 3f;

        for (CommodityOnMarketAPI commMkrt : market.getCommoditiesCopy()) {
            if (commMkrt.isNonEcon() || commMkrt.getMaxDemand() <= commMkrt.getAvailable())
                continue; // Skip non-economic items or items with no deficit

            String commId = commMkrt.getId();
            String bulletText = "- " + commMkrt.getCommodity().getName();

            if (commId.equals(Commodities.DRUGS) || commId.equals(Commodities.ORGANS))
                info.addPara(bulletText, Color.RED, pad);
            else
                info.addPara(bulletText, Misc.getNegativeHighlightColor(), pad);
        }
    }

    private void addClosingTextToTheSmallDescription(TooltipMakerAPI info, float opad) {
        if (hasOnlyIllegalGoodsDemand) {
            info.addPara("This market has seen an increase in demand for illicit goods.", opad);
        } else {
            info.addPara("Local authorities have slashed trade tariffs on essential goods to incentivize independent merchants.", opad);
        }
    }

    @Override
    public Set<String> getIntelTags(SectorMapAPI map) {
        Set<String> tags = super.getIntelTags(map);
        float tariff = market.getTariff().getModifiedValue();

        if (tariff <= RTConfig.criticalTariffThreshold && !hasOnlyIllegalGoodsDemand) {
            tags.add(Tags.INTEL_IMPORTANT);
        }
        if (tariff > RTConfig.criticalTariffThreshold || countIllegalGoods >= 1) {
            tags.add(Tags.INTEL_TRADE);
        }

        return tags;
    }

    public void refreshShortageStats() {
        countIllegalGoods = 0;
        countNormalGoods = 0;

        for (CommodityOnMarketAPI commMkrt : market.getCommoditiesCopy()) {
            if (commMkrt.getMaxDemand() > commMkrt.getAvailable()) {
                String id = commMkrt.getId();

                if (ecoCommodities.contains(id))
                    countNormalGoods++;
                else if (id.equals(Commodities.ORGANS) || id.equals(Commodities.DRUGS))
                    countIllegalGoods++;
            }
        }

        hasOnlyIllegalGoodsDemand = (countNormalGoods == 0 && countIllegalGoods > 0);
    }
}