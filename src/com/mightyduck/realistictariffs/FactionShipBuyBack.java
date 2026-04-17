package com.mightyduck.realistictariffs;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.impl.campaign.CoreReputationPlugin.CustomRepImpact;
import com.fs.starfarer.api.impl.campaign.CoreReputationPlugin.RepActionEnvelope;
import com.fs.starfarer.api.impl.campaign.CoreReputationPlugin.RepActions;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.impl.campaign.DModManager;

import org.apache.log4j.Logger;
import java.awt.Color;
import java.util.*;

public class FactionShipBuyBack implements EveryFrameScript{
    private boolean wasInMarket = false;
    private MarketAPI currentMarket = null;

    // Tracks the state of the player's fleet to safely detect sold ships
    private final Map<String, FleetMemberAPI> currentSessionFleet = new HashMap<>();

    // Aggregators for the current transaction session
    private float exoticBonusCredits = 0f;
    private float regularBonusCredits = 0f;
    private final Map<String, Float> exoticRepChanges = new HashMap<>();
    private final Map<String, Float> regularRepChanges = new HashMap<>();
    private static final Logger log = Global.getLogger(FactionShipBuyBack.class);

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return true; // Must run while paused since trade UI pauses the game
    }

    @Override
    public void advance(float amount) {
        if (Global.getSector().getCampaignUI() == null) return;

        InteractionDialogAPI dialog = Global.getSector().getCampaignUI().getCurrentInteractionDialog();
        boolean inMarket = dialog != null
                && dialog.getInteractionTarget() != null
                && dialog.getInteractionTarget().getMarket() != null;

        if (inMarket && !wasInMarket) {
            // Player just opened a market dialog
            currentMarket = dialog.getInteractionTarget().getMarket();
            resetSessionData();
            updateTrackedFleet();
            wasInMarket = true;

        } else if (inMarket) {
            // Player is actively in the market dialog.
            // Check if any tracked ships went missing (sold).
            List<String> toRemove = new ArrayList<>();
            for (Map.Entry<String, FleetMemberAPI> entry : currentSessionFleet.entrySet()) {
                String shipId = entry.getKey();
                if (!playerHasShip(shipId) && !isShipInStorage(shipId, currentMarket)) {
                    // Ship is gone from fleet and NOT in storage -> It was sold!
                    processSoldShip(entry.getValue(), currentMarket);
                    toRemove.add(shipId);
                }
            }

            // Remove sold ships from tracking
            for (String id : toRemove) {
                currentSessionFleet.remove(id);
            }

            // Re-sync fleet (in case player bought a ship or pulled one out of storage)
            updateTrackedFleet();
            wasInMarket = true;

        } else if (!inMarket && wasInMarket) {
            // Player just closed the market dialog
            applyAndShowRewards();
            resetSessionData();
            wasInMarket = false;
            currentMarket = null;
        }
    }

    private void processSoldShip(FleetMemberAPI ship, MarketAPI market) {
        String marketFactionId = market.getFaction().getId();

        // 1. Get the exact number of D-Mods on the ship
        int dmods = DModManager.getNumDMods(ship.getVariant());

        // 2. Determine the correct vanilla sell multiplier
        float sellPriceMult;
        if (dmods > 0) {
            // If it has ANY D-Mods, Starsector uses this specific, lower multiplier
            sellPriceMult = Global.getSettings().getFloat("hullWithDModsSellPriceMult");
        } else {
            // If it's pristine, use the standard multiplier
            sellPriceMult = Global.getSettings().getFloat("shipSellPriceMult");
        }

        // 3. Calculate the true base sell value based on the D-Mod state
        float baseSellValue = ship.getBaseValue() * sellPriceMult;
        String exoticFactionId = getExoticFaction(ship);

        // --- 1. EXOTIC SHIPS LOGIC ---
        if (exoticFactionId != null) {
            boolean isEnemyMarket = market.getFaction().isHostileTo(exoticFactionId);
            boolean isPlayerHostile = market.getFaction().isHostileTo(Factions.PLAYER);

            if (marketFactionId.equals(exoticFactionId)) {
                // Sold to SAME faction market
                if (!isPlayerHostile) {
                    exoticBonusCredits += baseSellValue * RTConfig.ShipMultipleWarsPricesBonus;
                    accumulateRep(exoticRepChanges, exoticFactionId, RTConfig.ExoticShipSaleReputationGain);
                } else {
                    exoticBonusCredits += baseSellValue * RTConfig.ShipWarPricesBonus;
                    accumulateRep(exoticRepChanges, exoticFactionId, RTConfig.ExoticShipSaleReputationGain);
                }
            } else {
                // Sold to OTHER faction market
                if (isEnemyMarket) {
                    accumulateRep(exoticRepChanges, exoticFactionId, RTConfig.ExoticShipSaleReputationLoss);
                } else {
                    accumulateRep(exoticRepChanges, exoticFactionId, RTConfig.ExoticShipSaleReputationLoss);
                }
            }
            return; // Ship processed as exotic, skip regular rules
        }

        // --- 2. REGULAR SHIPS LOGIC ---
        String regularFactionId = getRegularFaction(ship);
        if (regularFactionId != null) {
            boolean isEnemyMarket = market.getFaction().isHostileTo(regularFactionId);
            boolean isPlayerHostile = market.getFaction().isHostileTo(Factions.PLAYER);

            if (marketFactionId.equals(regularFactionId)) {
                // Sold to SAME faction market
                if (!isPlayerHostile) {
                    regularBonusCredits += baseSellValue * RTConfig.FactionShipSellBonus;
                    accumulateRep(regularRepChanges, regularFactionId, RTConfig.FactionShipSaleReputationGain);
                    FactionShipBuyBack.log.info("isPlayerHostile : " + isPlayerHostile + " regularBonusCredits: " + regularBonusCredits);
                    //RealisticTariffPlugin.log.info("Market : "+market.getName()+" SubtractionValue: "+ displayPercent + "%%");
                } else {
                    // +1 rep ONLY (NO credits bonus)
                    FactionShipBuyBack.log.info("isPlayerHostile : " + isPlayerHostile);
                    accumulateRep(regularRepChanges, regularFactionId, RTConfig.FactionShipSaleReputationGain);
                }
            } else {
                // Sold to OTHER faction market
                FactionShipBuyBack.log.info("marketFactionId: " + marketFactionId + " Ship Design Faction(regularFactionId): " + regularFactionId);

                if (isEnemyMarket) {
                    accumulateRep(regularRepChanges, regularFactionId, RTConfig.FactionShipSaleReputationLoss);
                }
            }
        }
    }

    private void applyAndShowRewards() {
        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        Color highlightColor = Misc.getHighlightColor();
        Color textBaseColor = Misc.getTextColor();

        // Exotic/Rare Ships like XIV Battlegroup
        if (exoticBonusCredits > 0 || !exoticRepChanges.isEmpty()) {
            if (exoticBonusCredits > 0) {
                playerFleet.getCargo().getCredits().add(exoticBonusCredits);
            }
            for (Map.Entry<String, Float> entry : exoticRepChanges.entrySet()) {
                applyReputation(entry.getKey(), entry.getValue());
            }

            String creditsStr = Misc.getDGSCredits(exoticBonusCredits);
            Global.getSector().getCampaignUI().getMessageDisplay().addMessage(
                    "Ship Buyback Program: The faction thanks you for returning advance ships and rewards you with " + creditsStr + " bonus credits.",
                    textBaseColor, creditsStr, highlightColor
            );
        }

        // --- Process Regular Rewards ---
        if (regularBonusCredits > 0 || !regularRepChanges.isEmpty()) {
            if (regularBonusCredits > 0) {
                playerFleet.getCargo().getCredits().add(regularBonusCredits);
            }
            for (Map.Entry<String, Float> entry : regularRepChanges.entrySet()) {
                applyReputation(entry.getKey(), entry.getValue());
            }

            // Only show credit message if credits were actually awarded (since sometimes it's rep only)
            if (regularBonusCredits > 0) {
                String creditsStr = Misc.getDGSCredits(regularBonusCredits);
                Global.getSector().getCampaignUI().addMessage("Ship Buyback Program: Local authorities issued a rebate of " + creditsStr + " credits for selling faction hulls.", textBaseColor );

            } else {
                Global.getSector().getCampaignUI().getMessageDisplay().addMessage(
                        "Local government thanks you for conducting faction ship sales inside their markets.",
                        textBaseColor
                );
            }
        }
    }

    private void applyReputation(String factionId, float repAmount) {
        if (repAmount == 0f) return;

        CustomRepImpact impact = new CustomRepImpact();
        impact.delta = repAmount; // Starsector rep scales from -1.0 to 1.0 internally

        Global.getSector().adjustPlayerReputation(
                new RepActionEnvelope(RepActions.CUSTOM, impact, null, null, true),
                factionId
        );
    }

    private String getExoticFaction(FleetMemberAPI ship) {
        String designation = ship.getHullSpec().getDesignation().toLowerCase();
        String name = ship.getHullSpec().getHullName().toLowerCase();
        Set<String> tags = ship.getHullSpec().getTags();

        if (designation.contains("xiv") || name.contains("xiv") || tags.contains("xiv_bp")) {
            return Factions.HEGEMONY;
        }
        if (designation.contains("lion's guard") || name.contains("lion's guard") || tags.contains("lions_guard")) {
            return Factions.DIKTAT;
        }
        return null;
    }

    private String getRegularFaction(FleetMemberAPI ship) {
        String manufacturer = ship.getHullSpec().getManufacturer();
        if (manufacturer == null || manufacturer.isEmpty()) return null;

        manufacturer = manufacturer.toLowerCase();

        if (manufacturer.contains("hegemony")) return Factions.HEGEMONY;
        if (manufacturer.contains("sindrian") || manufacturer.contains("diktat")) return Factions.DIKTAT;
        if (manufacturer.contains("tri-tachyon")) return Factions.TRITACHYON;
        if (manufacturer.contains("luddic church")) return Factions.LUDDIC_CHURCH;
        if (manufacturer.contains("luddic path")) return Factions.LUDDIC_PATH;
        if (manufacturer.contains("persean")) return Factions.PERSEAN;
        if (manufacturer.contains("pirate")) return Factions.PIRATES;

        return null;
    }

    // Tracking Helpers & Memory Management
    private void updateTrackedFleet() {
        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        for (FleetMemberAPI member : playerFleet.getFleetData().getMembersListCopy()) {
            currentSessionFleet.put(member.getId(), member);
        }
    }

    private boolean playerHasShip(String shipId) {
        for (FleetMemberAPI member : Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy()) {
            if (member.getId().equals(shipId)) return true;
        }
        return false;
    }

    private boolean isShipInStorage(String shipId, MarketAPI market) {
        SubmarketAPI storage = market.getSubmarket("storage_player");
        if (storage != null && storage.getCargo() != null && storage.getCargo().getMothballedShips() != null) {
            for (FleetMemberAPI member : storage.getCargo().getMothballedShips().getMembersListCopy()) {
                if (member.getId().equals(shipId)) return true;
            }
        }

        return false;
    }

    private void accumulateRep(Map<String, Float> map, String factionId, float amount) {
        map.put(factionId, amount);
    }

    private void resetSessionData() {
        currentSessionFleet.clear();
        exoticRepChanges.clear();
        regularRepChanges.clear();
        exoticBonusCredits = 0f;
        regularBonusCredits = 0f;
    }
}