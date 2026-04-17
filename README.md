# Realistic Tariffs & Weapons Salesman
#### Created by **MightyDuck75**

A small gameplay and economy mod which is vanilla friendly it replaces static global tariffs with a more **reactive & realistic system** meant to add to the immersion of the game economy. 
Local governments now actively lower trade barriers and provide rebates for commodity exports.

Additionally, Ship & Weapons buy & sell prices now increase if there are deficits in the Ship Hulls & Weapons commodity.

To add some extra flavor selling faction design ships to their factions provides reputation and boost in credits when these factions are at war, in particular to XIV battlegroup ships.

---
## Why did I made this mod ?
First as of 2026 in vanilla **Starsector** you don't get a notification when there is significant demand for commodities, secondly tariffs are not immersive or logical the main purpose 
of tariffs is to control imports & is usually done in a per commodity basis.
Since it's impossible or extremely complex to apply a tariff per commodity, I try the second-best thing, I make it so that submarkets that cant meet the demand of multiple commodities 
progressively lower these, as a normal government would do.

## 📉 Dynamic Tariff Scaling
The mod monitors every submarket(planet, station..) known to the player, when a market has deficit in a commodity, the local 
authorities will slash tariffs to attract independent traders.
The table bellow shows the impact that each commodity has on the market tariffs when on deficit, they subtract from the default of 18%
until they reach the minimum value of 3%. 


| Commodities                                                              | Tariff Impact |
|--------------------------------------------------------------------------|---------------|
| **Food, Fuel, Domestic Goods, Supplies**                                 | **-5%**       |
| **Ships, Heavy Machinery, Metals, Organics**                             | **-4%**       |
| **Heavy Armaments, Luxury Goods, Ore, Rare Ore, Rare Metals, Volatiles** | **-3%**       |
| **Crew**                                                                 | **-2%**       |
| **Marines, Lobster**                                                     | **-1%**       |

#### Global Tariffs Range:

| Min Tariffs | Max Tariffs (Default) |
|-------------|-----------------------|
| 3%          | 18%                   |


---

## 💰 Key Features

* **The Exporter's Rebate:**
    * Exporting goods are sales that bring new money to an economy, so it doesn't generally make sense to have exports tariffs.
    * This mod tracks the **exact amount** of tariffs paid during trades and returns to the players effectively disabling their 
  gameplay impact.
    * Upon leaving the trade menu, the government issues a **full credit refund** directly to the player.
    * *Note: Rebates do not apply to illegal goods (Drugs/Organs).*

* **Ship Market Overhaul:**
    * **Ships Hulls & Weapons Deficit Impacts Prices** Ships & weapons selling/buying prices now fluctuate based on the local market ship hulls & weapons 
deficit.
    * **Faction Wars Impacts Prices:** If a faction is currently **at war** with 1 other faction, this increases the prices for ships(+15%) and weapons by (+15%),
if the faction instead has 2 or more wars prices increase by (+30%)
    * **Ship Buyback Program:** If a faction is at war and this faction has faction ship designs, players who sell these ships in these
faction markets receive a credit boost of 10% and +1 reputation, if XIV battlegroup ships are sold to the hegemony you receive an extra 
20% of the base value and +4 reputation. (If the player insteads sells to a different market he will not receive the extra credits
and also lose -1 rep or -8 for XIV ships)

* **Smart Intel Notifications:**
    * Custom Intel entries appear in your log for any market with **Shortages**, if there arent many commodities missing you will find this
on the trade section, while more missing good will add intel messages to the important section.
    * Custom Intel entries for ship buyback programs and information which markets are currently involved in faction wars.

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
