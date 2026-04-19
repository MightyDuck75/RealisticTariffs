package com.mightyduck.realistictariffs;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.comm.IntelManagerAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class RealisticTariffPlugin extends BaseModPlugin {

    private static float originalWeaponBuyMult, originalWeaponSellMult;
    private static float originalShipSellPriceMult, originalShipBuyPriceMult;
    // A temporary list to hold our intel while the game is saving
    private transient List<ShipHullsWeaponsMarketIntel> backUpShipWeaponsIntelForSaveCompatibility = new ArrayList<>();
    private transient List<DeficitTariffMarketIntel> backUpDeficitTariffMarketIntelForGameCompatibility = new ArrayList<>();
    public static final String MOD_ID = "realistictariffs";
    private static final Logger log = Global.getLogger(RealisticTariffPlugin.class);

    private void saveOriginalPriceVariablesFromSettings(){
        originalShipSellPriceMult = Global.getSettings().getFloat("shipSellPriceMult");
        originalShipBuyPriceMult = Global.getSettings().getFloat("shipBuyPriceMult");
        originalWeaponSellMult = Global.getSettings().getFloat("shipWeaponSellPriceMult");
        originalWeaponBuyMult = Global.getSettings().getFloat("shipWeaponBuyPriceMult");
    }

    private void setNormalTariffsAccordingToRTConfig(){
        //Set all submarket global tariffs to the normal value as define in the RTConfig

        float targetTariff = RTConfig.normalTariff;

        for (MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
            // 3. Clean up any old modifiers from this mod to get a clean reading
            market.getTariff().unmodify(MOD_ID);

            // 4. Calculate how much we need to add/subtract to reach 0.18
            float currentVanilla = market.getTariff().getModifiedValue();
            float differenceNeeded = targetTariff - currentVanilla;
            // 5. Apply the modifier
            market.getTariff().modifyFlat(MOD_ID, differenceNeeded, "Normal Tariffs Adjusted");

            //float finalTariff = market.getTariff().getModifiedValue();
            //int displayPercent = Math.round(finalTariff * 100f);
            //RealisticTariffPlugin.log.info("Market : "+market.getName()+" SubtractionValue: "+ displayPercent + "%%");
        }
    }

    @Override
    public void onGameLoad(boolean newGame) {
        //setNormalTariffsAccordingToRTConfig();

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

    @Override
    public void beforeGameSave() {
        // 2. The game is about to save, hide the custom intel
        IntelManagerAPI intelManager = Global.getSector().getIntelManager();

        if (backUpShipWeaponsIntelForSaveCompatibility == null)
            backUpShipWeaponsIntelForSaveCompatibility = new ArrayList<>();
        if (backUpDeficitTariffMarketIntelForGameCompatibility == null)
            backUpDeficitTariffMarketIntelForGameCompatibility = new ArrayList<>();

        backUpShipWeaponsIntelForSaveCompatibility.clear(); // Ensure our temporary list is empty
        backUpDeficitTariffMarketIntelForGameCompatibility.clear(); // Ensure our temporary list is empty

        // Find all our custom intel items currently active in the player's log
        for (IntelInfoPlugin plugin : intelManager.getIntel(ShipHullsWeaponsMarketIntel.class)) {
            backUpShipWeaponsIntelForSaveCompatibility.add((ShipHullsWeaponsMarketIntel) plugin);
        }

        for (IntelInfoPlugin plugin : intelManager.getIntel(DeficitTariffMarketIntel.class)) {
            backUpDeficitTariffMarketIntelForGameCompatibility.add((DeficitTariffMarketIntel) plugin);
        }

        // Remove them from the game's official manager so they don't get saved
        for (ShipHullsWeaponsMarketIntel intelShipWeapons : backUpShipWeaponsIntelForSaveCompatibility) {
            intelManager.removeIntel(intelShipWeapons);
        }

        for (DeficitTariffMarketIntel intelDeficit : backUpDeficitTariffMarketIntelForGameCompatibility) {
            intelManager.removeIntel(intelDeficit);
        }

        Global.getSettings().setFloat("shipBuyPriceMult",originalShipBuyPriceMult);
        Global.getSettings().setFloat("shipSellPriceMult", originalShipSellPriceMult);
        Global.getSettings().setFloat("shipWeaponSellPriceMult", originalWeaponSellMult);
        Global.getSettings().setFloat("shipWeaponBuyPriceMult", originalWeaponBuyMult);
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
        // This runs the moment the player boots up Starsector
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
                intelManager.addIntel(intelArmament, true);
            }
            backUpShipWeaponsIntelForSaveCompatibility.clear();
        }

        if (backUpDeficitTariffMarketIntelForGameCompatibility != null) {
            for (DeficitTariffMarketIntel deficitTariffMarketIntel : backUpDeficitTariffMarketIntelForGameCompatibility) {
                // Re-add quietly so it doesn't trigger the "New Intel" sound effect again
                intelManager.addIntel(deficitTariffMarketIntel, true);
            }
            backUpDeficitTariffMarketIntelForGameCompatibility.clear();
        }

        // Clear the list to free up memory
        backUpShipWeaponsIntelForSaveCompatibility.clear();
        backUpDeficitTariffMarketIntelForGameCompatibility.clear();
    }
}