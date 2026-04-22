package com.mightyduck.realistictariffs;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.PlanetSearchData;
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

    private final String factionId; // safer than MarketAPI
    private final MarketAPI market;  // optional
    private final String sectionHeadingText;  // optional
    private final Color sectionHeadingTextColor;
    private final String sectionHeadingLabel;  // optional

    private final String folder, icon;  // optional

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
            MarketAPI market
    ) {
        this.notificationTitle = notificationTitle;
        this.text = text;
        this.highlights = highlights;
        this.highlightColors = highlightColors;
        this.title = title;
        this.expandedParagraphs = detailLines;
        this.factionId = factionId;
        this.market = market;

        this.hasSecundarySectionHeading = false;
        this.sectionHeadingText = "";
        this.sectionHeadingTextColor = Color.BLACK;
        this.sectionHeadingLabel = "";
        this.folder = "";
        this.icon = "";
    }

    public IntelMessageNotification(
            String notificationTitle,
            String text,
            String[] highlights,
            Color[] highlightColors,
            String title,
            List<ExpandedParagraphForIntel> detailLines,
            String factionId,
            MarketAPI market,
            String sectionHeadingText,
            Color sectionHeadingTextColor,
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
        this.factionId = factionId;
        this.market = market;
        this.hasSecundarySectionHeading = true;
        this.sectionHeadingText = sectionHeadingText;
        this.sectionHeadingTextColor = sectionHeadingTextColor;
        this.sectionHeadingLabel = sectionHeadingLabel;
        this.folder = folder;
        this.icon = icon;

        if (intelSectionTag != null) {
            this.intelTags.add(intelSectionTag);
        }
    }

    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        // This is what actually draws the text in the notification area
        // We use Misc.getGrayColor() as the base for "returned from" etc.
        info.addPara(notificationTitle,Misc.getBasePlayerColor(),0f);
        LabelAPI label = info.addPara(text, Misc.getGrayColor(), 0f);

        // 2. Apply the highlights and their corresponding colors to the label object.
        label.setHighlight(highlights);
        label.setHighlightColors(highlightColors);
    }

    @Override
    public Set<String> getIntelTags(SectorMapAPI map) {
        Set<String> tags = super.getIntelTags(map);
        tags.addAll(this.intelTags);
        return tags;
    }

    @Override
    public boolean isHidden() {
        return isEnded();
    }

    @Override
    public String getIcon() {
        if(folder != "")
            return Global.getSettings().getSpriteName(folder, icon);

        return market.getFaction().getCrest();
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

        if(hasSecundarySectionHeading){
            info.addSectionHeading(sectionHeadingLabel, Alignment.MID, opad);
        }

        for (ExpandedParagraphForIntel para : expandedParagraphs) {
            info.addPara(para.text, opad, Misc.getTextColor(), para.colors, para.highlights);
        }
    }
}
