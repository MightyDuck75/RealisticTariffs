package com.mightyduck.realistictariffs;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
public class IntelMessageNotification extends BaseIntelPlugin{
    private final String text;
    private final String[] highlights;
    private final Color[] highlightColors;
    private MarketAPI factionMarket;

    private final String credits;

    private final String tariff;
    private final float totalTransaction;

    public IntelMessageNotification(String text, String[] highlights, Color[] highlightColors, MarketAPI factionMarket, String credits, String tariff, float totalTransaction) {
        this.text = text;
        this.highlights = highlights;
        this.highlightColors = highlightColors;
        this.factionMarket = factionMarket;
        this.credits = credits;
        this.tariff = tariff;
        this.totalTransaction = totalTransaction;
    }

    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        // This is what actually draws the text in the notification area
        // We use Misc.getGrayColor() as the base for "returned from" etc.
        LabelAPI label = info.addPara(text, Misc.getGrayColor(), 0f);

        // 2. Apply the highlights and their corresponding colors to the label object.
        label.setHighlight(highlights);
        label.setHighlightColors(highlightColors);
    }

    @Override
    public boolean isHidden() {
        return isEnded();
    }

    @Override
    public String getIcon() {
        return Global.getSettings().getSpriteName("icons", "rt_rebate_icon");
    }

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        float opad = 10f;

        info.addPara("As a trader, you are entitled to commodity export rebates.", opad);

        info.addPara("These rebates are issued to incentivize legal trade and are processed " +
                        "automatically upon transaction completion at authorized markets.",
                Misc.getGrayColor(), opad);

        info.addSectionHeading("Transaction Details", Alignment.MID, opad);

        info.addPara("Market: " + factionMarket.getName(), opad,
                factionMarket.getFaction().getBaseUIColor(), factionMarket.getName());

        info.addPara("Faction: " + factionMarket.getFaction().getDisplayName(), opad,
                factionMarket.getFaction().getBaseUIColor(), factionMarket.getFaction().getDisplayName());

        // Cleaned up the integer conversion to look a bit neater
        String transValueStr = String.valueOf((int)totalTransaction);
        info.addPara("Transaction Value: " + transValueStr, opad,
                Misc.getHighlightColor(), transValueStr);

        // THE FIX: Base text uses %% to prevent the crash. Highlight text uses % to match the screen.
        info.addPara("Tariff Rate: " + tariff + "%%", opad,
                Misc.getHighlightColor(), tariff + "%");

        info.addPara("Total Rebate credited: " + credits, opad,
                Misc.getHighlightColor(), credits);
    }
}
