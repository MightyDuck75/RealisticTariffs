# Realistic Tariffs & Weapons Salesman
#### Created by **MightyDuck75**

A small gameplay and economy mod which is vanilla friendly it replaces static global tariffs with a more **reactive & realistic system** meant to add to the immersion of the game economy. 
Local governments now actively lower trade barriers and provide rebates for commodity exports.

Additionally, Ship & Weapons buy & sell prices now increase if there are deficits in the Ship Hulls & Weapons commodity.

To add some extra flavor selling faction design ships to their factions provides reputation and boost in credits when these factions are at war, in particular to XIV battlegroup ships.

---
## Why did I make this mod ?
First as of 2026 in vanilla **Starsector** you don't get a notification when there is significant demand for commodities, secondly tariffs are not immersive or logical the main purpose 
of tariffs is to control imports & is usually done in a per commodity basis.
Since it's impossible or extremely complex to apply a tariff per commodity, I did the second-best thing it came to mind... I make it so that markets that cant meet the demand of multiple commodities 
progressively lower these, as a normal government would do.

![](https://raw.githubusercontent.com/MightyDuck75/RealisticTariffs/refs/heads/main/Readme_Imgs/VanillaTariffs.jpg)

## 📉 Dynamic Tariff Scaling
The mod monitors every market(planet, station..) known to the player, when a market has deficit in a commodity, the local 
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

### Why did I set these values ?
I basically wanted an important intel notification to pop up whenever there were about 2 to 3 deficits in commodities. 
The rest should feel immersive like, a planet running out of food or essential goods would create real 
political pressures, unlike just having a deficit in Marines or something. I haven't really tested these 
values extensively or consider numerous combinations...

### Global Tariffs Range:

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

![](https://raw.githubusercontent.com/MightyDuck75/RealisticTariffs/refs/heads/main/Readme_Imgs/TariffsRebate.jpg)

* **Ship Market Overhaul:**
    * **Ship Hulls & Weapons Deficit Impacts Prices** Ships & weapons selling/buying prices now fluctuate based on the local market ship hulls & weapons 
deficit.
    * **Faction Wars Impacts Prices:** If a faction is currently **at war** with 1 other faction, this increases the prices for ships(+15%) and weapons by (+15%),
if the faction instead has 2 or more wars prices increase by (+30%)
    * **Ship Buyback Program:** If a faction is at war and this faction has faction ship designs, players who sell these ships in these
faction markets receive a credit boost of 10% and +1 reputation, if XIV battlegroup ships are sold to the hegemony you receive an extra 
20% of the base value and +4 reputation. (If the player instead sells to a different market he will not receive the extra credits
and also lose -1 rep or -8 for XIV ships)


* **Smart Intel Notifications:**
    * Custom Intel entries appear in your log for any market with **shortages**, if there aren't many commodities missing you will find this
on the trade section, while more missing good will add intel messages to the important section.
    * Custom Intel entries for ship buyback programs and information which markets are currently involved in faction wars.

![](https://raw.githubusercontent.com/MightyDuck75/RealisticTariffs/refs/heads/main/Readme_Imgs/NewIntel.jpg)

---

## Final Thoughts 
I'm pretty particular about mods. I usually prefer the developer’s original vision because I feel like it's easy for mods
to damage that unified feel & vision. Secondly, anything that breaks my immersion or suspension of disbelief really
puts me off. That said, I’m really happy with this mod. As a personal user, it feels like a perfectly fitted
shirt, it just enhances the vanilla experience without getting in the way. The exporter rebate was unfortunately a necessary
workaround. On paper, it might have been off-putting to me, but when I compare it to vanilla which is x100 time more egregious with
the immersion-breaking high export tariffs, it actually feels like sweet justice getting that rebate! :P
If you have any feedback that can improve this mod I will read it and if it makes sense to me, I will try to implement it. I also 
have a few more ideas for mods to fix some bad game design mechanics & others that improve the game, so likes, stars,.. are
always welcomed!

## 📥 Installation
1.  Download the latest release.
2.  Extract into your `Starsector/mods` folder, the folder structure should be "..\StarSector.v0.9.8a-RC8\game\mods\RealisticTariffs\"
3.  Enable **Realistic Tariffs** in the launcher.

## Scope of the implementation
- A known markets with deficits that reduce tariffs to under 9% are put in the important section while the others are can be found in the trade section
  - Markets with deficits in just illegal commodities are found in trade section
  - Illicit commodities like Drugs and Organs don't lower global tariffs but generate an intel entry about the demand from illicit goods
- All demand intel is trackable on map and uses the icon of the faction that currently owns that market
- All demand intel is not updated if there is no connection to the core systems
- All demand intel uses the intel colors, light blue for the title, grey for the text, gold/yellow for highlights, faction color for markets, white for description of the intel
- A 100% tariff rebate is given for the total commodities purchased in market, player gets notified by a message & audio notification after exiting the trade screen
  - Removing commodities from storage don't influence the rebate amount
- Ships & Weapons prices increase when there is demand for the commodity ships & hulls (selling ships does not reduce demand)
- Ships & Weapons prices increase when a market faction is at war, 15% with 1 faction war & 30% if at war with more than 1 faction
  - Pirates & Luddic Path don't count as factional wars.
  - An Intel entry is generated for a faction engaged in 1 factional war and another for when it's engaged in more than 1 factional war
- Factions who have ship design types (pirates, hegemony..) when at war issue a Ship Buyback Program that further boosts prices(+10%) & gives reputation(+1)
  - Selling factional ships to other factions when these are engaged in factional wars reduces player reputation(-1) with the design faction
  - The Ship Buyback Program credit boost is calculated on the base sale value if in pristine condition, or if the ship has D-mods it adjusts it with "hullWithDModsSellPriceMult"
  - Factions without ship designs do not generate a Ship buyback program event
- Rare/Exotic Factional Ships like XIV Battleground and Lion's Guard Ships also provide a boost in credits(+20%) and a bigger reputation reward(+4), if sold to another faction it damages reputation(-8)
- Users can change some values of this mod by going to \RealisticTariffs\data\config\rt_settings.json like:
  - the tariff impact from commodities, 
  - disable rebates & factional ship buybacks, 
  - the default & min tariff amount, 
  - boost to Ships and Weapons price changes..

