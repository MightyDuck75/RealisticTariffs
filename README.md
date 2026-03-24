# Realistic Tariffs
### Created by **MightyDuck75**

A small gameplay and economy mod which is vanilla friendly it replaces static global tariffs with a more **reactive system** meant to add to the immersion of the game economy. Local governments now actively lower trade barriers and provide rebates for commodity exports.

---
## Why did I made this mod ?
First as of 2026 in vanilla starsector you dont get a notification when there is significant demand for commodities, secondly tariffs are not immersive or logical the main purpose of tariffs is to control imports & is usually done in a per commodity basis.
Since it's impossible or extremely complex to apply a tariff per commodity, I try the second best thing I made it so that submarkets that cant meet the demand of multiple commodities progressely lower these, as a normal goverment would do.

## 📉 Dynamic Tariff Scaling
The mod monitors every market in the sector. When a colony is in crisis, the local authorities will slash tariffs to attract independent traders.

| Severity | Tariff Rate | Description |
| :--- | :--- | :--- |
| **No Shortages** | **18%** | Standard government operation. |
| **1 Commodity** | **14%** | Minor incentives for relief efforts. |
| **2 Commodities** | **9%** | Severe crisis; significant tax breaks applied. |
| **3+ Commodities** | **3%** | Emergency status; essentially a "Free Port" rate. |

---

## 💰 Key Features

* **The Exporter's Rebate:**
    * Fulfilling shortages shouldn't be penalized by the "Global Tariff."
    * This mod tracks the **exact amount** of tariffs paid during trades at a market with shortages.
    * Upon leaving the trade menu, the government issues a **full credit refund** directly to the player.
    * *Note: Rebates do not apply to illegal goods (Drugs/Organs).*

* **Ship Market Overhaul:**
    * **Demand-Based Pricing:** Ship hull prices now fluctuate based on local market needs.
    * **War Profiteering:** If a faction is currently **at war**, the demand for military hulls spikes, significantly increasing their market value.

* **Smart Intel Notifications:**
    * Custom Intel entries appear in your log for any market with **Severe Shortages**.
    * The UI displays the **current adjusted tariff** in gold text so you know where the best deals are.

---

## 🛠️ Technical Details

* **Performance Focused:** Uses optimized scripts that only run once per day and when the users opens the trading screen.
* **Compatibility:** Untested assume it doesnt support I did to run after *Nexerelin*.

---

## 📥 Installation
1.  Download the latest release.
2.  Extract into your `Starsector/mods` folder.
3.  Enable **Realistic Tariffs** in the launcher.

## Scope of the implementation
- A submarket with demand in only 1 to 2 commodity goes into the trade intel
- A submarket with demand for 3 or more commodities is also present in the important intel
- All demand intel is tracked on map and uses the icon of the faction that currently owns that submarket
- All demand intel is not updated if there is not connection to the core systems
- All demand intel uses the intel colors, light blue for the title, grey for the text, gold/yellow for highlights, faction color for submarkets, white for description of the intel
- Hidden submarkets are ignored
- Illicit Commodities when cant meet demand create a trade intel notification
- Illicit commodities like Drugs and Organs dont lower global tariffs
- A 100% tariff rebate is given for the total commodities purchased in submarket, player gets notified by a message & audio after exiting the trade screen
- Ships prices increase when there is demand for the commodity ships & hulls (selling ships does not reduce demand)
- Ships increases in prices when a submarket faction is at war
