# Realistic Tariffs & Weapons Salesman
#### Created by **MightyDuck75**

A small gameplay and economy mod which is vanilla friendly it replaces static global tariffs with a more **reactive & realistic system** meant to add to the immersion of the game economy. 
Local governments now actively lower trade barriers and provide rebates for commodity exports.

Additionally, Ship & Weapons buy & sell prices now increase if there are deficits in the Ship Hulls & Weapons commodity.

To add some extra flavor selling faction design ships to their factions provides reputation and boost in credits when these factions are at war, like selling XIV battlegroup ships to the hegemony.

---
## Why did I make this mod ?
First as of 2026 in vanilla **Starsector** you don't get a notification when there is significant demand for commodities, secondly tariffs are not immersive or logical the main purpose 
of tariffs is to control imports & is usually done in a per commodity basis.
Since it's impossible or extremely complex to apply a tariff per commodity, I did the second-best thing it came to mind... when markets cant meet the demand of multiple commodities and have these in
deficit these progressively lower the tariffs on that market, as a normal government would do.

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

##
![](https://raw.githubusercontent.com/MightyDuck75/RealisticTariffs/refs/heads/main/Readme_Imgs/TariffsAndCommoditiesDeficit.jpg)

---


### Ships and weapons prices boost

Currently, in-game you have an economic demand for ship hulls and weapons, this can get into deficit as any normal commodity but doesn't affect ship or weapons prices in any way.

As I was addressing the tariff values and intel, I also decided to boost ship and weapon prices based on market demand and any ongoing factional wars (excluding terrorists and pirates). 
Selling ships and weapons doesn't reduce the deficit, though. I haven't tested this enough to determine whether the price boost gives the player too many credits. 
However, since selling a ship in-game often doesn't provide a big economic incentive, the impact still shouldn't be too significant. 
The values can always be adjusted by the player in rt_settings.json.



|                                                                                | Buying Ships Price | Selling Ships Price | Buying Weapons Price | Selling Weapons Price |
|--------------------------------------------------------------------------------|--------------------|---------------------|----------------------|-----------------------|
| Minor Deficit                                                                  | 30%                | 20%                 | 15%                  | 10%                   |
| Medium Deficit                                                                 | 65%                | 50%                 | 30%                  | 20%                   |
| High Deficit                                                                   | 145%               | 125%                | 50%                  | 40%                   |
| Single Factional War                                                           | 15%                | 15%                 | 10%                   | 10%                    |
| Multiple Factional Wars                                                        | 30%                | 30%                 | 20%                   | 20%                    |
| Max Total Boost                                                                | 165%               | 155%                | 70%                  | 60%                   |
| Factional Design                                                               | 10%                | 10%                 | -                    | -                     |
| Rare/Exotic Faction Ship | 20%                | 20%                 | -                     | -                      |
| Max Total Boost                         | 185%               | 175%                | -                     | -                      |

### Ship & Weapons new intel notifications:

![](https://raw.githubusercontent.com/MightyDuck75/RealisticTariffs/refs/heads/main/Readme_Imgs/ShipsAndWeaponBoosts.png)

## 💰 Key Features

* **The Exporter's Rebate:**
    * Exporting goods are sales that bring new money to an economy, so it doesn't generally make sense to have exports tariffs.
    * This mod tracks the **exact amount** of tariffs paid during trades and returns to the players effectively disabling their 
  gameplay impact.
    * Upon leaving the trade menu, the government issues a **full credit refund** directly to the player.
    * *Note: Rebates do not apply to illegal goods (Drugs/Organs).*

![](https://raw.githubusercontent.com/MightyDuck75/RealisticTariffs/refs/heads/main/Readme_Imgs/OnScreenExporterRebate.jpg)

* **Ship Market Overhaul:**
    * **Ship Hulls & Weapons Deficit Impacts Prices**: ships & weapons selling & buying prices now fluctuate based on the local market ship hulls & weapons 
deficit.
    * **Faction Wars Impacts Prices:** If a faction is currently **at war** with 1 other faction, this increases the prices for ships(+15%) and weapons by (+15%),
if the faction instead has 2 or more wars prices increase by (+30%)
    * **Ship Buyback Program:** If a faction is at war and this faction has faction ship designs, players who sell these ships in these
faction markets receive a credit boost of 10% and +1 reputation, if XIV battlegroup ships are sold to the hegemony you receive an extra 
20% of the base value and +4 reputation. (If the player instead sells to a different market he will not receive the extra credits
and also lose -1 rep or -8 for XIV ships)

![](https://raw.githubusercontent.com/MightyDuck75/RealisticTariffs/refs/heads/main/Readme_Imgs/ShipFactionBuybacks.png)

* **New Intel Notifications:**
    * Custom Intel entries appear in your log for any market with **shortages**, if there aren't many commodities missing you will find these
in the trade section, while markets with more missing goods will be placed in the important section.
    * Custom Intel entries for ship buyback programs and which markets had a surge in ship and weapons prices due to factional wars.

  
---

## Final Thoughts 
I’m pretty picky with mods. I usually stick to the developer’s original vision, since mods can easily mess with the overall feel of the game. <br>Anything that breaks immersion is a dealbreaker for me.
That said, I’m really happy with this one. It fits naturally into the vanilla experience and improves it without getting in the way.<br>
The exporter rebate is a workaround, but a necessary one. It might seem odd at first, but compared to how extreme the vanilla export tariffs are, it actually feels more balanced.<br>
If you have feedback, I’m open to it and will consider changes that make sense. I’ve also got a few more mod ideas in mind to improve some bad gameplay designs and stars and support are always appreciated.



## 📥 Installation
1.  Download the latest release.
2.  Extract into your `Starsector/mods` folder, the folder structure should be "..\StarSector.v0.9.8a-RC8\game\mods\RealisticTariffs\(files)"
3.  Enable **Realistic Tariffs** in the launcher.

## F.A.Q
**1 - Does this work with Nexerelin ?**

Yes, from my limited testing it works as intended due to telling in mod_info.json to run after nexerelin, nexerelin also modifies tariffs but as this mod runs after it nullfies nexerelin mod changes.

**2 - Can I disable Tariff Rebates and Factional ship buybacks?**

Yes, just go to the mod folder -> data -> config -> rt_settings.json and set the "isExportRebateActive" to false and the same for "isFactionBuybackProgramActive" ..

**3 - Can I change tariffs values and other settings in this mod?**

Yes, there is a number of settings you can change just go to mod folder -> data -> config -> rt_settings.json


## Scope of the implementation / Q & A Testing
- A known market with deficits that reduce tariffs to under 9% is put in the important section while the others can be found in the trade section
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
- Factions who have ship design types (hegemony, Tritachyon..) when at war issue a Ship Buyback Program that further boosts prices(+10%) & gives reputation(+1)
  - Selling factional ships to other factions when these are engaged in factional wars reduces player reputation(-1) with the design faction
  - The Ship Buyback Program credit boost is calculated on the base sale value if in pristine condition, if the ship has D-mods it adjusts it with "hullWithDModsSellPriceMult"
  - Factions without ship designs do not generate a Ship buyback program event
- Rare/Exotic Factional Ships like XIV Battleground and Lion's Guard Ships also provide a boost in credits(+20%) and a bigger reputation reward(+4), if sold to another faction it damages reputation(-8)
- Users can change some values of this mod by going to \RealisticTariffs\data\config\rt_settings.json like:
  - the tariff impact from commodities, 
  - disable rebates & factional ship buybacks, 
  - the default & min tariff amount, 
  - boost to Ships and Weapons price changes..