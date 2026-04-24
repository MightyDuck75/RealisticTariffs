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
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.impl.campaign.DModManager;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;

import org.apache.log4j.Logger;
import java.awt.Color;
import java.util.*;

import static com.fs.starfarer.api.impl.campaign.ids.Submarkets.SUBMARKET_STORAGE;

public class FactionShipBuyBack implements EveryFrameScript{
    private boolean wasInMarket = false;
    private MarketAPI currentMarket = null;
    private float exoticShipRepGain = 0f, factionShipRepGain = 0f;
    private float exoticShipRepLoss = 0f, factionShipRepLoss = 0f;

    private float reputation = 0f;
    // Tracks the state of the player's fleet to safely detect sold ships
    private final Map<String, FleetMemberAPI> currentFleetThatEntersMarket = new HashMap<>();

    // Aggregators for the current transaction session
    private float rareFactionalShipBonusCredits = 0f, regularFactionShipBonusCredits = 0f;
    private final Map<String, Float> rareFactionalShipBuybackRepChanges = new HashMap<>();
    private final Map<String, Float> regularFactionalShipBuybackRepChanges = new HashMap<>();
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
            // 1. DIALOG OPENS: Take Snapshot
            currentMarket = dialog.getInteractionTarget().getMarket();
            resetSessionData();
            updateTrackedFleet(); // Fills currentSessionFleet
            wasInMarket = true;

        } else if (!inMarket && wasInMarket) {
            exoticShipRepGain = RTConfig.exoticShipSaleReputationGain;
            exoticShipRepLoss = RTConfig.exoticShipSaleReputationLoss;
            factionShipRepGain = RTConfig.factionShipSaleReputationGain;
            factionShipRepLoss = RTConfig.factionShipSaleReputationLoss;

            // 2. DIALOG CLOSES: Compare and Calculate
            // Build a quick HashSet of the player's CURRENT fleet IDs for instant lookup
            Set<String> postTradeFleetIds = new HashSet<>();
            for (FleetMemberAPI member : Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy()) {
                postTradeFleetIds.add(member.getId());
            }

            // Check our original snapshot against the new fleet
            for (Map.Entry<String, FleetMemberAPI> entry : currentFleetThatEntersMarket.entrySet()) {
                String shipId = entry.getKey();
                FleetMemberAPI ship = entry.getValue();

                // If the ship is NOT in the new fleet, and NOT in storage, it was permanently sold.
                if (!postTradeFleetIds.contains(shipId) && !isShipInStorage(shipId, currentMarket)) {
                    processSoldShip(ship, currentMarket);
                }
            }

            // Apply rewards and wipe the memory for the next visit
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
        String exoticFactionId = getFactionOfRareFactionalShip(ship);

        // --- 1. EXOTIC SHIPS LOGIC ---
        if (exoticFactionId != null) {
            boolean isEnemyMarket = market.getFaction().isHostileTo(exoticFactionId);
            boolean isPlayerHostile = market.getFaction().isHostileTo(Factions.PLAYER);

            if (marketFactionId.equals(exoticFactionId)) {
                // Sold to SAME faction market
                if (!isPlayerHostile) {
                    rareFactionalShipBonusCredits += baseSellValue * RTConfig.shipMultipleWarsPricesBonus;
                    accumulateRep(rareFactionalShipBuybackRepChanges, exoticFactionId, RTConfig.exoticShipSaleReputationGain);
                } else {
                    rareFactionalShipBonusCredits += baseSellValue * RTConfig.shipWarPricesBonus;
                    accumulateRep(rareFactionalShipBuybackRepChanges, exoticFactionId, RTConfig.exoticShipSaleReputationGain);
                }
            } else {
                // Sold to OTHER faction market
                if (isEnemyMarket) {
                    accumulateRep(rareFactionalShipBuybackRepChanges, exoticFactionId, RTConfig.exoticShipSaleReputationLoss);
                } else {
                    accumulateRep(rareFactionalShipBuybackRepChanges, exoticFactionId, RTConfig.exoticShipSaleReputationLoss);
                }
            }
            return; // Ship processed as exotic, skip regular rules
        }

        // --- 2. REGULAR SHIPS LOGIC ---
        String regularFactionId = getFactionOfRegularFactionalShip(ship);
        if (regularFactionId != null) {
            boolean isEnemyMarket = market.getFaction().isHostileTo(regularFactionId);
            boolean isPlayerHostile = market.getFaction().isHostileTo(Factions.PLAYER);

            if (marketFactionId.equals(regularFactionId)) {
                // Sold to SAME faction market
                if (!isPlayerHostile) {
                    regularFactionShipBonusCredits += baseSellValue * RTConfig.factionShipSellBonus;
                    accumulateRep(regularFactionalShipBuybackRepChanges, regularFactionId, RTConfig.factionShipSaleReputationGain);
                } else {
                    // +1 rep ONLY (NO credits bonus)
                    accumulateRep(regularFactionalShipBuybackRepChanges, regularFactionId, RTConfig.factionShipSaleReputationGain);
                }
            } else {
                // Sold to OTHER faction market
                if (isEnemyMarket) {
                    accumulateRep(regularFactionalShipBuybackRepChanges, regularFactionId, RTConfig.factionShipSaleReputationLoss);
                }
            }
        }
    }

    private void applyAndShowRewards() {
        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();

        // 1. Calculate Totals
        float totalBonusCredits = regularFactionShipBonusCredits + rareFactionalShipBonusCredits;

        // Aggregate rep specifically for the current market's faction for the Intel display
        String marketFactionId = currentMarket.getFaction().getId();
        float marketFactionRepChange = 0f;
        if (regularFactionalShipBuybackRepChanges.containsKey(marketFactionId)) {
            marketFactionRepChange += regularFactionalShipBuybackRepChanges.get(marketFactionId);
        }
        if (rareFactionalShipBuybackRepChanges.containsKey(marketFactionId)) {
            marketFactionRepChange += rareFactionalShipBuybackRepChanges.get(marketFactionId);
        }

        // 2. Check if we have any rewards worth notifying
        if (totalBonusCredits > 0 || marketFactionRepChange != 0) {

            // Apply Credits to player
            if (totalBonusCredits > 0) {
                playerFleet.getCargo().getCredits().add(totalBonusCredits);
            }

            // Prepare strings using the Credit Symbol icon
            String creditsStr = "+" + Misc.getDGSCredits(totalBonusCredits);
            String repStr = (marketFactionRepChange >= 0 ? "+" : "") + (int)(marketFactionRepChange * 100);

            List<ExpandedParagraphForIntel> details = new ArrayList<>();
            details.add(new ExpandedParagraphForIntel("Faction: " + currentMarket.getFaction().getDisplayName(),
                    currentMarket.getFaction().getDisplayName(), currentMarket.getFaction().getBaseUIColor()));

            if (totalBonusCredits > 0) {
                details.add(new ExpandedParagraphForIntel("Bonus Credits: " + creditsStr, creditsStr, Misc.getHighlightColor()));
            }
            if (marketFactionRepChange != 0) {
                Color repColor = marketFactionRepChange > 0 ? Misc.getPositiveHighlightColor() : Misc.getNegativeHighlightColor();
                details.add(new ExpandedParagraphForIntel("Reputation Change: " + repStr, repStr, repColor));
            }

            String sectionHeadingText = "The " + currentMarket.getFaction().getDisplayName() +
                    " recognizes your contribution and compensates you for assisting their war effort.";

            // 3. Trigger Intel Notification FIRST
            // This ensures the popup is created before the text messages hit the queue
            IntelMessageNotification intel = new IntelMessageNotification(
                    "Ship Buyback Program",
                    creditsStr + " transferred as compensation",
                    new String[]{creditsStr},
                    new Color[]{Misc.getHighlightColor()},
                    "Ship Buyback Program",
                    details,
                    marketFactionId,
                    currentMarket,
                    sectionHeadingText,
                    Misc.getGrayColor(),
                    "Transaction Details",
                    "icons",
                    "rt_rebate_icon",
                    Tags.INTEL_LOCAL
            );

            Global.getSector().getIntelManager().addIntel(intel, false);
            intel.endAfterDelay(4f);
        }

        // 4. Apply all Reputation changes AFTER the Intel is added
        // This causes the vanilla "Relationship improved" messages to appear SECOND
        for (Map.Entry<String, Float> entry : regularFactionalShipBuybackRepChanges.entrySet()) {
            applyReputation(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Float> entry : rareFactionalShipBuybackRepChanges.entrySet()) {
            applyReputation(entry.getKey(), entry.getValue());
        }
    }

    private void applyReputation(String factionId, float repAmount) {
        if (repAmount == 0f) return;

        CustomRepImpact impact = new CustomRepImpact();
        reputation = repAmount;
        impact.delta = repAmount; // Starsector rep scales from -1.0 to 1.0 internally

        Global.getSector().adjustPlayerReputation(
                new RepActionEnvelope(RepActions.CUSTOM, impact, null, null, false),
                factionId
        );
    }

    private String getFactionOfRareFactionalShip(FleetMemberAPI ship) {
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

    private String getFactionOfRegularFactionalShip(FleetMemberAPI ship) {
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
            currentFleetThatEntersMarket.put(member.getId(), member);
        }
    }

    private boolean isShipInStorage(String shipId, MarketAPI market) {
        //SubmarketAPI storage = market.getSubmarket("storage_player");
        SubmarketAPI storage = market.getSubmarket(SUBMARKET_STORAGE);
        if (storage != null && storage.getCargo() != null && storage.getCargo().getMothballedShips() != null) {
            for (FleetMemberAPI member : storage.getCargo().getMothballedShips().getMembersListCopy()) {
                if (member.getId().equals(shipId)) return true;
            }
        }

        return false;
    }

    private void accumulateRep(Map<String, Float> map, String factionId, float amount) {
        float current = map.containsKey(factionId) ? map.get(factionId) : 0f;
        map.put(factionId, current + amount);
    }

    private void resetSessionData() {
        currentFleetThatEntersMarket.clear();
        rareFactionalShipBuybackRepChanges.clear();
        regularFactionalShipBuybackRepChanges.clear();
        rareFactionalShipBonusCredits = 0f;
        regularFactionShipBonusCredits = 0f;
    }
}