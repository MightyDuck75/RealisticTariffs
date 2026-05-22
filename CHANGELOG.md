## [0.9.2] - 2026-05-22

### 🐛 Bug Fixes

- The images were not going to expected parent folder

### 🚜 Refactor

- Removed unused dependencies from mod_info.json


## [0.9.1] - 2026-05-19

### 🚀 Features

- Tariffs now change per market according to the number of commodities in deficit
- Added a Faction Ship Buyback program selling faction design ships now give reputation & credits
- Ships and weapons sell and buy prices are now impact by deficit in ship hulls & weapons
- Added Intel messages for increased prices due to deficits in Ship Hull & Weapons
- Added rt_settings.json so that other users can modified the economic values and enable or disable ship buybacks and tariff rebates
- Intel deficit messages can now be limited by illicit goods trigger threshold
- Added sound for critical shortages intel notification

### 🐛 Bug Fixes

- Tariff related intel in the important and trade section was not updating correctly
- Resolve crash on startup
- Bug on the RTConfig blocking functionality of tariffs
- On screen intel notifications were showing tariffs not updated
- Notification of credits now shows before reputation gain
- Fixed selling and buying the same ship to get infinite reputation
- Intel no longer spams on loading the game
- Intel from rebate and ships buybacks was not being deleted
- Intel about shortages in ships and weapons was being grey out when it was still active
- Added epsilon due to float accuracy so that the user can set 1% tariffs
- Bad calculation on tariff rebates
- Made rebates more accurate by rounding to nearest
- Potential fix for a bug that generated 2 intel messages about ships and weapons shortage
- Missing configuration shiphulls deficit thresholds in rt_settings
- Increase the buy price from ships so that it's not economical to exploit the buybacks

### 🚜 Refactor

- Better naming and clean some code for Tariffs & Ship Weapons Intel
- Clean up code added a few more enums but general logic is the same
- Cleaned some code, add some constants and utils to not repeat code
- Removed unused debugging logging

### 🎨 Styling

- Improved UI visuals on tariff rebates notifications & ship buybacks
- Improve intel notification and descriptions for ship buybacks and rebates

### ⚙️ Miscellaneous Tasks

- Modified .gitignore to ignore artifacts
- Remove build artifacts from repository
- Updated readme and uploaded relevant imgs
- Added images to readme
- Improved the readme & some light refactor
- Refactoring and removing magic numbers
- Improved the README.md and more refactoring & following naming conventions
- Extracted logic to private methods for better readability
- Removing obvious comments and removing some magic numbers
- Further removal of used comments
- Future proof any possible custom icon for buybacks
- Added new picture to be used in the readme
- Improved the readme
