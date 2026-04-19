package com.mightyduck.realistictariffs;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;

import org.apache.log4j.Logger;

public class ShipTradeUIHook implements EveryFrameScript {

    private static final Logger log = Global.getLogger(ShipTradeUIHook.class);
    private boolean isModified = false;
    //I should run this on economy changes rather than Every Frame
    public void advance(float amount) {
        InteractionDialogAPI dialog = Global.getSector().getCampaignUI().getCurrentInteractionDialog();

        // 1. Check if the player is in a dialog with a valid market
        if (dialog != null && dialog.getInteractionTarget() != null && dialog.getInteractionTarget().getMarket() != null) {

            // If we haven't applied our custom prices yet
            if (!isModified) {
                MarketAPI market = dialog.getInteractionTarget().getMarket();

                // Calculate your custom multiplier
                float customSellMult = ShipMarketEconomy.getShipSellMultiplier(market);
                float customBuyMult = ShipMarketEconomy.getShipBuyMultiplier(market);

                // 2. Overwrite the global setting in memory BEFORE the TradeUI is opened.
                Global.getSettings().setFloat("shipSellPriceMult", RealisticTariffPlugin.getOriginalShipSellMult() + customSellMult);
                Global.getSettings().setFloat("shipBuyPriceMult", RealisticTariffPlugin.getOriginalShipBuyMult() + customBuyMult);

                isModified = true; // Flag to prevent running this every single frame
            }
        } else {
            // We Delete safe keeping going to another market with an old setting
            // 3. Once the dialog is closed, reset the setting so it doesn't bleed into other interactions
            if (isModified) {
                Global.getSettings().setFloat("shipSellPriceMult", RealisticTariffPlugin.getOriginalShipSellMult());
                Global.getSettings().setFloat("shipBuyPriceMult", RealisticTariffPlugin.getOriginalShipBuyMult());

                isModified = false;
            }
        }
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return true;
    }
}
