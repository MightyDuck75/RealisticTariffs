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

public class DeficitIntel extends BaseMarketIntel {
    private int countIllegalGoods = 0, countNormalGoods = 0;
    private Boolean hasOnlyIllegalGoodsDemand;
    private final Color intelTitleBlueColor, goldColor, grayColor;

    private List<String> mapLegalCommoditiesDeficit = new ArrayList<>();
    private final List<String> mapIllegalCommoditiesDeficit = new ArrayList<>();

    private final List<String> Eco_Commodities = Arrays.asList(
            Commodities.SHIPS, Commodities.CREW, Commodities.DOMESTIC_GOODS, Commodities.FOOD,
            Commodities.FUEL, Commodities.HAND_WEAPONS, Commodities.HEAVY_MACHINERY, Commodities.LOBSTER,
            Commodities.LUXURY_GOODS, Commodities.MARINES, Commodities.METALS, Commodities.ORE,
            Commodities.ORGANICS, Commodities.RARE_METALS, Commodities.RARE_ORE, Commodities.SUPPLIES,
            Commodities.VOLATILES
    );

    public DeficitIntel(MarketAPI market) {
        super(market);
        intelTitleBlueColor = Misc.getBasePlayerColor();
        goldColor = Misc.getHighlightColor();
        grayColor = Misc.getGrayColor();
        hasOnlyIllegalGoodsDemand = false; // Reset here
        refreshShortageStats(); // Calculate once on start
    }

    @Override
    public String getName() {
        // Only illegal goods
        if (hasOnlyIllegalGoodsDemand) {
            return "Shortages of illicit goods in " + market.getName();
        }

        float tariff = market.getTariff().getModifiedValue();

        if (tariff <= 0.09f) {
            return "Critical Shortages in " + market.getName();
        }
        if (tariff > 0.09f && tariff <= 0.14f ) {
            return "Severe Shortages in " + market.getName();
        }

        return "Shortages in " + market.getName();
    }

    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        if (countNormalGoods > 0 || countIllegalGoods > 0) {
            String intelTitleMsg = getName();
            info.addPara(intelTitleMsg, 0f, intelTitleBlueColor, market.getTextColorForFactionOrPlanet());
            if (intelTitleMsg.startsWith("Shortages of illicit goods in ")) {
                info.addPara("Profitable opportunities for those dealing in illegal goods", grayColor, 3f);
            } else {
                String highlightText = (int)(market.getTariff().getModifiedValue()*100f) + "%";
                String fullText = "Local government lowered tariffs to " + (int)(market.getTariff().getModifiedValue()*100f) + "%%";
                info.addPara(fullText, 3f, Misc.getGrayColor(), goldColor, highlightText);
            }
        }
    }

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        if (countNormalGoods > 0 || countIllegalGoods > 0) {
            info.addImage(getIcon(), width, 128f, 10f);

            info.addPara("There are severe resource shortages in %s. The following commodities are in critical deficit:",
                    10f, market.getTextColorForFactionOrPlanet(), market.getName());

            // EVERYTHING that is in demand for maximum profit info.
            for (CommodityOnMarketAPI commMkrt : market.getCommoditiesCopy()) {
                if (!commMkrt.isNonEcon() && commMkrt.getMaxDemand() > commMkrt.getAvailable()) {
                    if (commMkrt.getId().equals(Commodities.DRUGS) || commMkrt.getId().equals(Commodities.ORGANS)) {
                        info.addPara("- " + commMkrt.getCommodity().getName(), Color.RED, 3f);
                    } else {
                        info.addPara("- " + commMkrt.getCommodity().getName(), Misc.getNegativeHighlightColor(), 3f);
                    }
                }
            }

            if (hasOnlyIllegalGoodsDemand) {
                info.addPara("This market has seen a increase in demand for illicit goods.", 10f);
            } else {
                info.addPara("Local authorities have slashed trade tariffs on essential goods to incentivize independent merchants.", 10f);
            }
        }
    }

    @Override
    public Set<String> getIntelTags(SectorMapAPI map) {
        Set<String> tags = super.getIntelTags(map);

        float tariff = market.getTariff().getModifiedValue();

        if (tariff <= 0.09f && !hasOnlyIllegalGoodsDemand)
            tags.add(Tags.INTEL_IMPORTANT);
        if (tariff > 0.09f || countIllegalGoods >= 1)
            tags.add(Tags.INTEL_TRADE);

        return tags;
    }

    public void refreshShortageStats() {
        countIllegalGoods = 0;
        countNormalGoods = 0;
        hasOnlyIllegalGoodsDemand = false; // Reset here
        mapLegalCommoditiesDeficit.clear();
        mapIllegalCommoditiesDeficit.clear();

        for (CommodityOnMarketAPI commMkrt : market.getCommoditiesCopy()) {
            if (commMkrt.getMaxDemand() > commMkrt.getAvailable()) {
                if (Eco_Commodities.contains(commMkrt.getId())) {
                    mapLegalCommoditiesDeficit.add(commMkrt.getId());
                    countNormalGoods++;
                } else if(commMkrt.getId().equals(Commodities.ORGANS) || commMkrt.getId().equals(Commodities.DRUGS)){
                    countIllegalGoods++;
                    mapIllegalCommoditiesDeficit.add(commMkrt.getId());
                }
            }
        }
        if (countNormalGoods == 0 && countIllegalGoods > 0)
            hasOnlyIllegalGoodsDemand = true;
    }
}