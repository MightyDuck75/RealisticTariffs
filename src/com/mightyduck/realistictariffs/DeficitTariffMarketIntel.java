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

import static com.mightyduck.realistictariffs.RTConfig.ECONOMIC_LEGAL_COMMODITIES;

public class DeficitTariffMarketIntel extends BaseMarketIntel {
    private final Color blueIntelTitleColor, goldHighlightColor, grayTextColor;

    private List<String> ecoCommodities = Arrays.asList(
            Commodities.SHIPS, Commodities.CREW, Commodities.DOMESTIC_GOODS, Commodities.FOOD,
            Commodities.FUEL, Commodities.HAND_WEAPONS, Commodities.HEAVY_MACHINERY, Commodities.LOBSTER,
            Commodities.LUXURY_GOODS, Commodities.MARINES, Commodities.METALS, Commodities.ORE,
            Commodities.ORGANICS, Commodities.RARE_METALS, Commodities.RARE_ORE, Commodities.SUPPLIES,
            Commodities.VOLATILES
    );

    private CommoditiesDeficitLevel currentSeverity = CommoditiesDeficitLevel.NONE;

    public DeficitTariffMarketIntel(MarketAPI market, CommoditiesDeficitLevel initialSeverity) {
        super(market);
        this.blueIntelTitleColor = Misc.getBasePlayerColor();
        this.goldHighlightColor = Misc.getHighlightColor();
        this.grayTextColor = Misc.getGrayColor();

        this.currentSeverity = initialSeverity;
    }

    @Override
    public String getName() {
        String mName = market.getName();

        switch (currentSeverity) {
            case ILLICIT_ONLY: return "Shortages of illicit goods in " + mName;
            case CRITICAL:     return "Critical Shortages in " + mName;
            case SEVERE:       return "Severe Shortages in " + mName;
            case MINOR:        return "Shortages in " + mName;
            default:           return "";
        }
    }

    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        if (currentSeverity == CommoditiesDeficitLevel.NONE) return;

        info.addPara(getName(), 0f, blueIntelTitleColor, market.getTextColorForFactionOrPlanet());

        float pad = 3f;
        if (currentSeverity == CommoditiesDeficitLevel.ILLICIT_ONLY)
            info.addPara("Profitable opportunities for those dealing in illegal goods", grayTextColor, pad);
        else {
            int tariffPercent = (int)(market.getTariff().getModifiedValue() * 100f);
            info.addPara("Local government lowered tariffs to %s%%", pad, grayTextColor, goldHighlightColor, String.valueOf(tariffPercent));
        }
    }

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        if (currentSeverity == CommoditiesDeficitLevel.NONE) return;

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
        if (currentSeverity == CommoditiesDeficitLevel.ILLICIT_ONLY) {
            info.addPara("This market has seen an increase in demand for illicit goods.", opad);
        } else {
            info.addPara("Local authorities have slashed trade tariffs on essential goods to incentivize independent merchants.", opad);
        }
    }

    @Override
    public Set<String> getIntelTags(SectorMapAPI map) {
        Set<String> tags = super.getIntelTags(map);

        if (currentSeverity == CommoditiesDeficitLevel.CRITICAL)
            tags.add(Tags.INTEL_IMPORTANT);

        if (currentSeverity != CommoditiesDeficitLevel.NONE)
            tags.add(Tags.INTEL_TRADE);

        return tags;
    }

    public void updateSeverity(CommoditiesDeficitLevel newSeverity) {
        this.currentSeverity = newSeverity;
    }
}