package com.mightyduck.realistictariffs;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCampaignEventListener;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.PlayerMarketTransaction;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class RebateManager extends BaseCampaignEventListener {
    public RebateManager(boolean permaRegister) {
        super(permaRegister);
    }

    // This method fires automatically whenever the player confirms a trade
    @Override
    public void reportPlayerMarketTransaction(PlayerMarketTransaction transaction) {
        SubmarketAPI submarket = transaction.getSubmarket();
        MarketAPI market = transaction.getMarket();

        // 1. Safety checks
        if (submarket == null || market == null) return;

        // 2. EXCLUDE specific submarkets!
        String submarketId = submarket.getSpecId();
        if (submarketId.equals(Submarkets.SUBMARKET_BLACK) ||
                submarketId.equals(Submarkets.SUBMARKET_STORAGE)) {
            return; // Abort the rebate entirely if it's the Black Market or Storage
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
                sendRebateNotification(taxPaid, currentTariff, market.getName());
            }
        }
    }

    private void sendRebateNotification(float rebateAmount, float currentTariff, String marketName) {
        String message = String.format("Exporter Rebate: %d credits returned from %s (Tariff: %d%%)",
                (int) rebateAmount,
                marketName,
                (int) (currentTariff * 100));

        Global.getSector().getCampaignUI().addMessage(message, Color.GREEN);

        try {
            Global.getSoundPlayer().playUISound("rt_exporters_rebate", 1f, 1f);
        } catch (Exception e) {
            // If the sound fails, the game won't crash. We just log it so you know to fix it later.
            Global.getLogger(RebateManager.class).error("Failed to play rebate UI sound!", e);
        }
    }
}