## Create: Integrated Farming 1.4.3-1.20.1

Ported applicable changes from upstream commits `56ab765`–`1f63780` (release 1.4.3) to Minecraft 1.20.1 Forge.

### Feature

* Combined crop farming, animal husbandry, and fishing Ponder tutorials under Farming Appliances.
* Occupied Roost variants are grouped in the farming and Mechanical Arm target categories via Create: Dragons Plus 1.11.9's Ponder tag groups. Scroll through a group to select an animal and open its tutorial. All duck and goose Roost variants now have tutorials.

### Fix

* Added Glow Berry harvesting that leaves the cave vines intact.

### Compatibility

* Expanded Vacuum Harvester and Mechanical Harvester support for Atmospheric oranges, blood oranges, passion fruit, dragon fruit, yucca fruit bundles, and hanging currants.
* Added harvesting for Neapolitan banana bundles, vanilla vines, and adzuki sprouts. Replanting vanilla retains its growing end and support.
* Added Jaden's Nether Expansion Cerebrage and Sorrowsquash harvesting, matching the 1.20.1 pick-up drops.
* Added Upgrade Aquatic 6.x mulberry harvesting, preserving single and double vines.
* Added Ender's Delight 1.1.x Amberveil, Chorus Flame, Ethereal Saffron, and Voidpepper harvesting. Picked fruit can grow again; Voidpepper retains its spreading origin.
* Disable harvester replanting to collect Ender's Delight Amberveil and Ethereal Saffron as whole plants; otherwise their drops are used to replant them.

### Skipped

* Cultural Delights 0.18.1+ corn, beans, and mint harvesting: no 0.18.1+ build exists for 1.20.1.
* Cook's Collection 0.5.8+ lemons: the lemon tree is bundled with Cultural Delights 0.18.1 support upstream and its 1.20.1 build differs.

### Requirements

* Requires Create: Dragons Plus 1.11.9 or newer.

## Create: Integrated Farming 1.4.2-1.20.1

Ported applicable fixes from upstream commit `0ff12d2` to Minecraft 1.20.1 Forge.

* Area harvesting preserves the fluid at the harvested block. Farmer's Delight rice waits for harvestable panicles and preserves the submerged plant.
* Autumnity foul berries validate both halves and harvest each plant only once per area scan. Young single-block bushes remain untouched.
* My Nether's Delight powdery crops yield ripe peppers without uprooting the plant, using the crop properties available in 1.20.1.
* In ultra-warm dimensions, each 250 mB of lava advances Leteos Compost one stage; ten applications complete fresh compost. Insufficient fluid, invalid catalysts, and replaced targets consume no fluid. Custom catalysts use `#mynethersdelight:leteos_booster` (the 1.20.1 data path is `tags/fluids`).
* Updated the compost Ponder demonstration and translations.

### Optional harvesting integrations

* Added Neapolitan 5.1.0 mint harvesting: pick mature leaves while preserving sprout density.
* Added Atmospheric 6.1.1 aloe harvesting, including mature double-height plants and soil-dependent growth.
* Added Supplementaries 1.20-3.1.42+ flax harvesting with whole-plant validation and seed-consuming replanting.
* Added Haunted Harvest 1.20-3.2.0 corn harvesting with whole-plant maturity checks and kernel-consuming replanting.
* Added Jaden's Nether Expansion 2.3.5 warped wart harvesting: validate both hanging halves, harvest once, and consume one wart to replant. Ported the immature hanging plant neighbor-update fix.
* These integrations support both mechanical and Vacuum Harvesters and remain optional. Neapolitan/Atmospheric require Blueprint; Supplementaries/Haunted Harvest require Moonlight Lib; Jaden's Nether Expansion requires Elysium API 1.1.3 or newer.

The Confluence model fix does not apply because this port has no Confluence integration.
