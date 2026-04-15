package com.mightyduck.realistictariffs;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;

import org.apache.log4j.Logger;

public class WeaponPriceUIHook implements EveryFrameScript {
    // Store original vanilla values to reset them later
    private float originalWeaponSellMult, originalWeaponBuyMult;
    private boolean initialized = false;
    private static final Logger log = Global.getLogger(WeaponPriceUIHook.class);

    @Override
    public void advance(float amount) {
        // 1. Initialize original values once
        if (!initialized) {
            originalWeaponSellMult = Global.getSettings().getFloat("shipWeaponSellPriceMult");
            originalWeaponBuyMult = Global.getSettings().getFloat("shipWeaponBuyPriceMult");
            initialized = true;
        }

        InteractionDialogAPI dialog = Global.getSector().getCampaignUI().getCurrentInteractionDialog();

        // 2. If the player is interacting with a market, apply the "War Surcharge"
        if (dialog != null && dialog.getInteractionTarget() != null && dialog.getInteractionTarget().getMarket() != null) {
            MarketAPI market = dialog.getInteractionTarget().getMarket();

            float customSellWpMult = ShipMarketEconomy.getWeaponSellMultiplier(market);
            float customBuyWpMult = ShipMarketEconomy.getWeaponBuyMultiplier(market);

            applyWarPricing(market, customBuyWpMult, customSellWpMult);
        } else {
            resetToVanilla();
        }
    }

    private void applyWarPricing(MarketAPI market, float buyWeaponMult, float sellWeaponMult ) {
        float currentWeaponBuyPriceMul = Global.getSettings().getFloat("shipWeaponBuyPriceMult");
        float currentWeaponSellPriceMul = Global.getSettings().getFloat("shipWeaponSellPriceMult");

        if ( currentWeaponBuyPriceMul != buyWeaponMult) {
            Global.getSettings().setFloat("shipWeaponBuyPriceMult", RealisticTariffPlugin.getOriginalWeaponBuyMult() + buyWeaponMult);
        }
        if ( currentWeaponSellPriceMul != sellWeaponMult)
            // Apply the boost to the global settings temporarily
            Global.getSettings().setFloat("shipWeaponSellPriceMult", RealisticTariffPlugin.getOriginalWeaponSellMult() + sellWeaponMult);


//        WeaponPriceUIHook.log.info("WeaponPriceUIHook: shipWeaponBuyPriceMult : " + (RealisticTariffPlugin.getOriginalWeaponBuyMult() + buyWeaponMult) +
//                " shipWeaponSellPriceMult : "+ (RealisticTariffPlugin.getOriginalWeaponSellMult() + sellWeaponMult) +
//                " buyWeaponMult  "+ buyWeaponMult +
//                " sellWeaponMult  "+ sellWeaponMult +
//                " current WeaponBuy" + Global.getSettings().getFloat("shipWeaponBuyPriceMult") +
//                " current WeaponSell" +  Global.getSettings().getFloat("shipWeaponSellPriceMult") +
//                " Market " + market.getId()
//        );
    }

    private void resetToVanilla() {
        Global.getSettings().setFloat("shipWeaponSellPriceMult", RealisticTariffPlugin.getOriginalWeaponSellMult());
        Global.getSettings().setFloat("shipWeaponBuyPriceMult", RealisticTariffPlugin.getOriginalWeaponBuyMult());
    }

    @Override public boolean isDone() { return false; }
    @Override public boolean runWhilePaused() { return true; }
}
