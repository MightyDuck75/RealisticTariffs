package com.mightyduck.realistictariffs;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCampaignEventListener;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.PlayerMarketTransaction;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.util.Misc;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

public class RebateManager extends BaseCampaignEventListener {
    public RebateManager(boolean permaRegister) {
        super(permaRegister);
    }

    @Override
    public void reportPlayerMarketTransaction(PlayerMarketTransaction transaction) {
        SubmarketAPI submarket = transaction.getSubmarket();
        MarketAPI market = transaction.getMarket();

        // 1. Safety checks
        if (submarket == null || market == null) return;

        // Exclude storage and backmarket for getting rebates
        String submarketId = submarket.getSpecId();
        if (submarketId.equals(Submarkets.SUBMARKET_BLACK) ||
                submarketId.equals(Submarkets.SUBMARKET_STORAGE)) {
            return;
        }

        // 3. Get the market tariff
        float currentTariff = market.getTariff().getModifiedValue();
        if (currentTariff <= 0f) return;

        float estimatedGrossSpent = 0f;

        // 4. Calculate based ONLY on what was bought in this specific transaction
        // transaction.getBought() returns a precise CargoAPI of just the purchased goods
        for (CargoStackAPI stack : transaction.getBought().getStacksCopy()) {
            if (stack.isCommodityStack()) {
                String commodityId = stack.getCommodityId();
                float qty = stack.getSize();

                float basePrice = Global.getSettings().getCommoditySpec(commodityId).getBasePrice();
                float priceWithTariff = basePrice * (1f + currentTariff);

                estimatedGrossSpent += (priceWithTariff * qty);
            }
        }

        // 5. Apply the rebate
        if (estimatedGrossSpent > 0f) {
            float taxPaid = estimatedGrossSpent * (currentTariff / (1f + currentTariff));

            if (taxPaid > 1f) {
                Global.getSector().getPlayerFleet().getCargo().getCredits().add(taxPaid);
                sendRebateNotification(taxPaid, currentTariff, market, estimatedGrossSpent);
            }
        }
    }

    private void sendRebateNotification(float rebateAmount, float currentTariff, MarketAPI market, float transactionTotal) {
        int creditsInt = (int) rebateAmount;
        int tariffInt = (int) (currentTariff * 100);

        String header = "Exporter Rebate";
        String creditsStr ="+" + Misc.getDGSCredits(creditsInt);
        String marketName = market.getName();
        String tariffSmall = String.valueOf(tariffInt);

        String fullText = String.format("%s returned from %s trade authorities",
                creditsStr, marketName);

        String sectionHeadingText = "As a trader, you are entitled to commodity export rebates. " +
                "These rebates are issued to incentivize legal trade and are processed " +
                "automatically upon transaction completion at authorized markets.";
        String sectionHeadingLabel = "Transaction Details";
        String transactionTotalValue = "+" + Misc.getDGSCredits((int)transactionTotal);

        List<ExpandedParagraphForIntel> details = Arrays.asList(
                new ExpandedParagraphForIntel("Market: "+ market.getName(), market.getName(), market.getFaction().getBaseUIColor()),
                new ExpandedParagraphForIntel("Faction: "+ market.getFaction().getDisplayName(), market.getFaction().getDisplayName(), market.getFaction().getBaseUIColor()),
                new ExpandedParagraphForIntel("Transaction Value: "+ transactionTotalValue, "" + transactionTotalValue, Misc.getHighlightColor()),
                new ExpandedParagraphForIntel("Tariff Rate: " + ""+ tariffSmall + "%%", ""+tariffSmall + "%%", Misc.getHighlightColor()),
                new ExpandedParagraphForIntel("Total Rebate: "+ creditsStr, ""+creditsStr, Misc.getHighlightColor())
        );

        IntelMessageNotification intel = new IntelMessageNotification(
                header,
                fullText,
                new String[]{ creditsStr, market.getName()},
                new Color[]{Misc.getHighlightColor(), market.getFaction().getBaseUIColor()},
                "Tariff Rebate Issued",
                details,
                market.getFaction().getId(),
                market.getId(),
                sectionHeadingText,
                sectionHeadingLabel,
                "icons",
                "rt_rebate_icon",
                Tags.INTEL_LOCAL
        );

        Global.getSector().getIntelManager().addIntel(intel, false);
        Global.getSector().addScript(intel);
        intel.endAfterDelay(1f);

        try {
            Global.getSoundPlayer().playUISound("rt_exporters_rebate", 1f, 1f);
        } catch (Exception e) {
            Global.getLogger(RebateManager.class).error("Failed to play rebate UI sound!", e);
        }
    }
}