package com.mightyduck.realistictariffs;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.Color;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IntelMessageNotification extends BaseIntelPlugin{
    private final String notificationTitle, text;
    private final String[] highlights;
    private final Color[] highlightColors;
    private final String title;
    private final List<ExpandedParagraphForIntel> expandedParagraphs;
    private final String sectionHeadingText;  // optional
    private final String sectionHeadingLabel;  // optional
    private final String folder, icon;  // optional
    private final String factionId, marketId;
    private final Boolean hasSecundarySectionHeading;
    private final Set<String> intelTags = new HashSet<>();

    public IntelMessageNotification(
            String notificationTitle,
            String text,
            String[] highlights,
            Color[] highlightColors,
            String title,
            List<ExpandedParagraphForIntel> detailLines,
            String factionId,
            String marketId,
            String sectionHeadingText,
            String sectionHeadingLabel,
            String folder,
            String icon,
            String intelSectionTag
    ) {
        this.notificationTitle = notificationTitle;
        this.text = text;
        this.highlights = highlights;
        this.highlightColors = highlightColors;
        this.title = title;
        this.expandedParagraphs = detailLines;
        this.hasSecundarySectionHeading = true;
        this.factionId = factionId;
        this.marketId = marketId;
        this.sectionHeadingText = sectionHeadingText;
        this.sectionHeadingLabel = sectionHeadingLabel;
        this.folder = folder;
        this.icon = icon;

        if (intelSectionTag != null) {
            this.intelTags.add(intelSectionTag);
        }
    }

    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        info.addPara(notificationTitle, Misc.getBasePlayerColor(),0f);
        LabelAPI label = info.addPara(text, Misc.getGrayColor(), 0f);

        label.setHighlight(highlights);
        label.setHighlightColors(highlightColors);
    }

    @Override
    public Set<String> getIntelTags(SectorMapAPI map) {
        Set<String> tags = super.getIntelTags(map);
        tags.addAll(this.intelTags);
        return tags;
    }

    public MarketAPI getMarket() {
        if (marketId == null) return null;
        return Global.getSector().getEconomy().getMarket(marketId);
    }

    @Override
    public boolean isHidden() {
        return isEnded();
    }

    @Override
    public String getIcon() {
        if (folder != null && !folder.isEmpty())
            return Global.getSettings().getSpriteName(folder, icon);

        return Global.getSector()
                .getFaction(factionId)
                .getCrest();
    }

    @Override
    public String getName() {
        return title;
    }

    @Override
    public Color getTitleColor(ListInfoMode mode) {
        return Misc.getBasePlayerColor();
    }

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        float opad = 10f;
        info.addPara(sectionHeadingText, opad);

        if(hasSecundarySectionHeading)
            info.addSectionHeading(sectionHeadingLabel, Alignment.MID, opad);

        for (ExpandedParagraphForIntel para : expandedParagraphs) {
            info.addPara(para.text, opad, Misc.getTextColor(), para.colors, para.highlights);
        }
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
    }
}
