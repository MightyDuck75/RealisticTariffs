package com.mightyduck.realistictariffs;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.comm.IntelManagerAPI;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class RealisticTariffPlugin extends BaseModPlugin {

    private static float originalWeaponBuyMult, originalWeaponSellMult;
    private static float originalShipSellPriceMult, originalShipBuyPriceMult;
    // A temporary list to hold our intel while the game is saving
    private transient List<ShipHullsWeaponsMarketIntel> backUpShipWeaponsIntelForSaveCompatibility = new ArrayList<>();
    private transient List<DeficitTariffMarketIntel> backUpDeficitTariffMarketIntelForGameCompatibility = new ArrayList<>();
    private static final Logger log = Global.getLogger(RealisticTariffPlugin.class);

    private void saveOriginalPriceVariablesFromSettings(){
        originalShipSellPriceMult = Global.getSettings().getFloat("shipSellPriceMult");
        originalShipBuyPriceMult = Global.getSettings().getFloat("shipBuyPriceMult");
        originalWeaponSellMult = Global.getSettings().getFloat("shipWeaponSellPriceMult");
        originalWeaponBuyMult = Global.getSettings().getFloat("shipWeaponBuyPriceMult");
    }

    @Override
    public void onGameLoad(boolean newGame) {

        backUpShipWeaponsIntelForSaveCompatibility = new ArrayList<>();
        backUpDeficitTariffMarketIntelForGameCompatibility = new ArrayList<>();

        if(RTConfig.isExportRebateActive) {
            Global.getSector().addTransientListener(new RebateManager(false));
        }

        if(RTConfig.isFactionBuybackProgramActive) {
            Global.getSector().addTransientScript(new FactionShipBuyBack());
        }

        Global.getSector().addTransientScript(new TariffUpdater());
        Global.getSector().addTransientScript(new ShipTradeUIHook());
        Global.getSector().addTransientScript(new WeaponPriceUIHook());
        Global.getSector().addTransientScript(new ShipHullsWeaponIntelManager());
    }

    public static float getOriginalWeaponSellMult(){
        return originalWeaponSellMult;
    }

    public static float getOriginalWeaponBuyMult(){
        return originalWeaponBuyMult;
    }

    public static float getOriginalShipBuyMult(){
        return originalShipBuyPriceMult;
    }

    public static float getOriginalShipSellMult(){
        return originalShipSellPriceMult;
    }

    @Override
    public void onApplicationLoad() throws Exception {
        saveOriginalPriceVariablesFromSettings();
        RTConfig.loadSettings();
    }

    @Override
    public void afterGameSave() {
        // 3. The save is complete. Put the intel back so the player doesn't notice!
        IntelManagerAPI intelManager = Global.getSector().getIntelManager();

        if (backUpShipWeaponsIntelForSaveCompatibility != null) {
            for (ShipHullsWeaponsMarketIntel intelArmament : backUpShipWeaponsIntelForSaveCompatibility) {
                // Re-add quietly so it doesn't trigger the "New Intel" sound effect again
                intelManager.addIntel(intelArmament, false);
            }

            backUpShipWeaponsIntelForSaveCompatibility.clear();
        }

        if (backUpDeficitTariffMarketIntelForGameCompatibility != null) {
            for (DeficitTariffMarketIntel deficitTariffMarketIntel : backUpDeficitTariffMarketIntelForGameCompatibility) {
                intelManager.addIntel(deficitTariffMarketIntel, false);
            }

            backUpDeficitTariffMarketIntelForGameCompatibility.clear();
        }

        // Clear the list to free up memory
        backUpShipWeaponsIntelForSaveCompatibility.clear();
        backUpDeficitTariffMarketIntelForGameCompatibility.clear();
    }
}