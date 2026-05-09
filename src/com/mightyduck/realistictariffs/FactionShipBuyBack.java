package com.mightyduck.realistictariffs;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
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

import java.awt.Color;
import java.util.*;

public class FactionShipBuyBack implements EveryFrameScript {
    private boolean wasInMarket = false;
    private MarketAPI currentMarket = null;
    private final Map<String, FleetMemberAPI> trackedFleet = new HashMap<>();
    private float rareShipCreditReward = 0f;
    private float regularShipCreditReward = 0f;
    private final Map<String, Float> rareShipRep = new HashMap<>();
    private final Map<String, Float> regularShipRep = new HashMap<>();
    private static final String PERSISTENT_KEY = "md_ship_buyback_tracker";

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return true;
    }

    @Override
    public void advance(float amount) {
        InteractionDialogAPI dialog = Global.getSector().getCampaignUI().getCurrentInteractionDialog();
        boolean inMarket = isInMarket(dialog);

        if (inMarket && !wasInMarket)
            onMarketOpened(dialog);
        else if (!inMarket && wasInMarket)
            onMarketClosed();
    }

    private boolean isInMarket(InteractionDialogAPI dialog) {
        return dialog != null &&
                dialog.getInteractionTarget() != null &&
                dialog.getInteractionTarget().getMarket() != null;
    }

    private void onMarketOpened(InteractionDialogAPI dialog) {
        currentMarket = dialog.getInteractionTarget().getMarket();
        resetSession();
        snapshotFleet();
        wasInMarket = true;
    }

    private void onMarketClosed() {
        Set<String> currentFleetIds = getCurrentFleetIds();
        confirmSaleShipSubjectToBuyback(currentFleetIds);

        applyAndShowRewards();

        resetSession();
        wasInMarket = false;
        currentMarket = null;
    }

    // FLEET TRACKING
    private void snapshotFleet() {
        for (FleetMemberAPI member : Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy()) {
            trackedFleet.put(member.getId(), member);
        }
    }

    private Set<String> getCurrentFleetIds() {
        Set<String> ids = new HashSet<>();
        for (FleetMemberAPI member : Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy()) {
            ids.add(member.getId());
        }
        return ids;
    }

    private void confirmSaleShipSubjectToBuyback(Set<String> currentFleetIds) {
        for (Map.Entry<String, FleetMemberAPI> entry : trackedFleet.entrySet()) {

            String shipId = entry.getKey();
            FleetMemberAPI ship = entry.getValue();

            if (!currentFleetIds.contains(shipId) && !isShipInStorage(shipId, currentMarket))
                processSoldShip(ship);
        }
    }

    private void processSoldShip(FleetMemberAPI ship) {
        Set<String> history = getHistory();
        if (history.contains(ship.getId())) return;

        float value = calculateBaseSellValue(ship);

        String exoticFaction = getFactionOfRareFactionalShip(ship);
        if (exoticFaction != null) {
            handleExoticShip(ship, exoticFaction, value);
            history.add(ship.getId());
            return;
        }

        String regularFaction = getFactionOfRegularFactionalShip(ship);
        if (regularFaction != null) {
            handleRegularShip(ship, regularFaction, value);
            history.add(ship.getId());
        }
    }

    private float calculateBaseSellValue(FleetMemberAPI ship) {
        int dmods = DModManager.getNumDMods(ship.getVariant());

        float mult = dmods > 0
                ? Global.getSettings().getFloat("hullWithDModsSellPriceMult")
                : Global.getSettings().getFloat("shipSellPriceMult");

        return ship.getBaseValue() * mult;
    }

    private void handleExoticShip(FleetMemberAPI ship, String factionId, float value) {
        boolean sameFaction = currentMarket.getFaction().getId().equals(factionId);
        boolean playerHostile = currentMarket.getFaction().isHostileTo(Factions.PLAYER);

        if (sameFaction) {
            float bonus = playerHostile
                    ? RTConfig.shipWarPricesBonus
                    : RTConfig.shipMultipleWarsPricesBonus;

            rareShipCreditReward += value * bonus;

            accumulateRep(rareShipRep, factionId, RTConfig.exoticShipSaleReputationGain);
        } else
            accumulateRep(rareShipRep, factionId, RTConfig.exoticShipSaleReputationLoss);
    }

    private void handleRegularShip(FleetMemberAPI ship, String factionId, float value) {
        boolean sameFaction = currentMarket.getFaction().getId().equals(factionId);
        boolean playerHostile = currentMarket.getFaction().isHostileTo(Factions.PLAYER);

        if (sameFaction) {
            if (!playerHostile)
                regularShipCreditReward += value * RTConfig.factionShipSellBonus;

            accumulateRep(regularShipRep, factionId, RTConfig.factionShipSaleReputationGain);
        }
        else if (currentMarket.getFaction().isHostileTo(factionId))
            accumulateRep(regularShipRep, factionId, RTConfig.factionShipSaleReputationLoss);
    }

    private void applyAndShowRewards() {
        float totalCredits = rareShipCreditReward + regularShipCreditReward;
        float repChange = getMarketRepChange();

        if (totalCredits <= 0 && repChange == 0) return;

        applyCredits(totalCredits);
        showIntel(totalCredits, repChange);
        applyAllReputation();
    }

    private float getMarketRepChange() {
        String factionId = currentMarket.getFaction().getId();

        return rareShipRep.getOrDefault(factionId, 0f)
                + regularShipRep.getOrDefault(factionId, 0f);
    }

    private void applyCredits(float credits) {
        if (credits > 0)
            Global.getSector().getPlayerFleet().getCargo().getCredits().add(credits);
    }

    private void showIntel(float credits, float repChange) {
        String creditsStr = "+" + Misc.getDGSCredits(credits);
        String repStr = (repChange >= 0 ? "+" : "") + (int) (repChange * 100);

        List<ExpandedParagraphForIntel> details = new ArrayList<>();

        details.add(new ExpandedParagraphForIntel(
                "Faction: " + currentMarket.getFaction().getDisplayName(),
                currentMarket.getFaction().getDisplayName(),
                currentMarket.getFaction().getBaseUIColor()
        ));

        if (credits > 0) {
            details.add(new ExpandedParagraphForIntel(
                    "Bonus Credits: " + creditsStr,
                    creditsStr,
                    Misc.getHighlightColor()
            ));
        }

        if (repChange != 0) {
            Color repColor = repChange > 0
                    ? Misc.getPositiveHighlightColor()
                    : Misc.getNegativeHighlightColor();

            details.add(new ExpandedParagraphForIntel(
                    "Reputation Change: " + repStr,
                    repStr,
                    repColor
            ));
        }

        IntelMessageNotification intel = new IntelMessageNotification(
                "Ship Buyback Program",
                creditsStr + " transferred as compensation",
                new String[]{creditsStr},
                new Color[]{Misc.getHighlightColor()},
                "Ship Buyback Program",
                details,
                currentMarket.getFaction().getId(),
                currentMarket.getId(),
                "Compensation issued for supporting war effort.",
                "Transaction Details",
                "icons",
                "ship_buyback_icon",
                Tags.INTEL_LOCAL
        );

        Global.getSector().getIntelManager().addIntel(intel, false);
        Global.getSector().addScript(intel);
        intel.endAfterDelay(1f);
    }

    private void applyAllReputation() {
        for (Map.Entry<String, Float> entry : regularShipRep.entrySet()) {
            applyReputation(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, Float> entry : rareShipRep.entrySet()) {
            applyReputation(entry.getKey(), entry.getValue());
        }
    }

    private void applyReputation(String factionId, float amount) {
        if (amount == 0f) return;

        CustomRepImpact impact = new CustomRepImpact();
        impact.delta = amount;

        Global.getSector().adjustPlayerReputation(
                new RepActionEnvelope(RepActions.CUSTOM, impact, null, null, false),
                factionId
        );
    }

    // HELPERS
    private boolean isShipInStorage(String shipId, MarketAPI market) {
        SubmarketAPI storage = market.getSubmarket(Submarkets.SUBMARKET_STORAGE);

        if (storage != null && storage.getCargo() != null) {
            for (FleetMemberAPI member : storage.getCargo().getMothballedShips().getMembersListCopy()) {
                if (member.getId().equals(shipId)) return true;
            }
        }
        return false;
    }

    private void accumulateRep(Map<String, Float> map, String factionId, float amount) {
        map.put(factionId, map.getOrDefault(factionId, 0f) + amount);
    }

    private void resetSession() {
        trackedFleet.clear();
        rareShipRep.clear();
        regularShipRep.clear();
        rareShipCreditReward = 0f;
        regularShipCreditReward = 0f;
    }

    private Set<String> getHistory() {
        Map<String, Object> data = Global.getSector().getPersistentData();
        if (!data.containsKey(PERSISTENT_KEY)) {
            data.put(PERSISTENT_KEY, new HashSet<String>());
        }
        return (Set<String>) data.get(PERSISTENT_KEY);
    }

    private String getFactionOfRareFactionalShip(FleetMemberAPI ship) {
        String name = ship.getHullSpec().getHullName().toLowerCase();
        if (name.contains("xiv")) return Factions.HEGEMONY;
        if (name.contains("lion")) return Factions.DIKTAT;
        return null;
    }

    private String getFactionOfRegularFactionalShip(FleetMemberAPI ship) {
        String m = ship.getHullSpec().getManufacturer().toLowerCase();

        if (m.contains("hegemony")) return Factions.HEGEMONY;
        if (m.contains("diktat")) return Factions.DIKTAT;
        if (m.contains("tri-tachyon")) return Factions.TRITACHYON;
        if (m.contains("luddic church")) return Factions.LUDDIC_CHURCH;
        if (m.contains("luddic path")) return Factions.LUDDIC_PATH;
        if (m.contains("persean")) return Factions.PERSEAN;
        if (m.contains("pirate")) return Factions.PIRATES;

        return null;
    }
}