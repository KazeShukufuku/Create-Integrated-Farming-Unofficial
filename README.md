# Create: Integrated Farming

Create: Integrated Farming is a Create add-on for farming automation.

This repository is a Minecraft 1.20.1 Forge port of the upstream Minecraft
1.21.1 Create: Integrated Farming project by DragonsPlus. The port is
maintained by KazeShukufuku.

This is an unofficial port. Please report issues with this Minecraft 1.20.1
port to this project, not to the upstream Create: Integrated Farming project.

Upstream project links:

* CurseForge: https://www.curseforge.com/minecraft/mc-mods/create-integrated-farming
* GitHub: https://github.com/DragonsPlusMinecraft/CreateIntegratedFarming

## Features

### Fishing Net

The Fishing Net is a contraption actor for automated fishing. Mount it on a
Create contraption and move it through valid water to collect fishing loot.

When Nether's Depths Upgrade is installed, this port also adds the Lava Fishing
Net for lava fishing automation.

### Roosts

Roost blocks automate poultry production.

* Capture chickens in a Roost to create a Chicken Roost.
* Feed roosted poultry with configured item or fluid foods.
* Collect poultry products from roost loot tables.
* Use Create Mechanical Arms and Spouts with supported roost workflows.

When Untitled Duck Mod is installed, duck and goose roost variants are also
enabled.

### Create Integration

This port keeps the integration-focused design of the upstream mod:

* Mechanical Arm interaction points for supported farming and ranching blocks.
* Spout interactions for compatible compost-style blocks.
* Ponder scenes for the main workflows and supported integrations.
* Custom harvest behaviour hooks used by selected integrations.

Current optional integrations include Farmer's Delight, My Nether's Delight,
Nether's Depths Upgrade, Crabber's Delight, Untitled Duck Mod, Delight O' Flight,
Twilight Forest / Twilight's Flavors & Delight, Create Crafts & Additions, and
Create Enchantable Machinery.

Optional crop harvesting also supports Neapolitan 5.1.0 (mint), Atmospheric
6.1.1 (aloe), Supplementaries 1.20-3.1.42 or newer in the 1.20-3.x line (flax), and Haunted Harvest
1.20-3.2.0 (corn), and Jaden's Nether Expansion 2.3.5 (warped wart).
These work with mechanical and Vacuum Harvesters.
Install Blueprint for Neapolitan/Atmospheric, and Moonlight Lib for
Supplementaries/Haunted Harvest. Jaden's Nether Expansion requires Elysium API
1.1.3 or newer. None of these mods is required by this port.

Fruits Delight 1.1.3 fruit trees, single and double berry bushes, durians,
and pineapples also support mechanical and Vacuum Harvesters. Fruit picking
uses the mod's L2 Harvester API to preserve growing plants and synchronize
both halves of tall bushes. Pineapple replanting consumes a pineapple sapling
from the harvester's inventory when replanting is enabled.

Northeast China Delight 3.2.0 support includes wood ear crops, hazelnut bushes,
and corn. Wood ears retain their attachment direction and regrow from age 1;
corn picking resets both the crop and its stalk without duplicate drops.
These integrations are optional.

Both mods and Fruits Delight's mapped L2 libraries are included in the
development runtime. Use the normal `runClient` task to test them in game.

Collector's Reap 1.5.5 and Northstar Redux 0.6.6 (Create 6 build) are also
included in the development runtime and remain optional for players.
Mechanical and Vacuum Harvesters support:

* Collector's Reap lime and pomegranate bushes, preserving both halves when
  picking fruit, including the mod's special pomegranate rules.
* Dragon fruit vines, preserving their ropes and neighbouring segments when
  picking. Budding dragon fruit is left to grow unless partial harvesting is enabled.
* Bulbous rose and heartpetals crops, and mature damselflower, moontear and
  skull lily flowers. Mature flowers drop no seeds; replanting requires the
  corresponding seeds in the harvester inventory. Portobello colonies keep
  using the existing Farmer's Delight integration.
* Northstar Mars tulips, palms, sprouts and Martian strawberries. Replanting
  consumes the actual planting item from drops first, then from inventory,
  and resets the crop to age 0. Flower/block items are not used as seeds.

These adapters respect replanting and partial-harvesting settings. Northstar's
decorative tall sprouts, fungi and Venus vines are not treated as seed crops.
The Collector's Reap integration requires version 1.5.5 or newer in the 1.x line.

## 1.20.1 Port Notes

Not every upstream 1.21.1 feature is duplicated in this port.

In particular, the general Harvester compatibility features from the upstream
1.21.1 version were not ported here, because Create: Central Kitchen already
implements the relevant 1.20.1 Harvester compatibility work. This project avoids
shipping a second implementation of that compatibility layer for Minecraft
1.20.1.

Mechanical Arm interactions with Farmer's Delight baskets were also not ported,
because Create: Central Kitchen already includes that integration for Minecraft
1.20.1.

Integration code that is still needed by this port, such as selected custom
harvest behaviours for supported add-ons, is kept where it remains useful.

## Dependencies

Required:

* Minecraft 1.20.1
* Minecraft Forge 47.4.10 or newer in the 47.x line
* Create 6.0.8 or newer
* Create: Dragons Plus 1.11.6 or newer in the 1.11.x line

Some features require optional integration mods. See `build.gradle` for the
exact development dependency list used by this repository.

## Authorship And Attribution

Original upstream project by DragonsPlus:
https://github.com/DragonsPlusMinecraft/CreateIntegratedFarming

Minecraft 1.20.1 port maintained by KazeShukufuku.

This port depends on the official Create Dragons Plus release:
https://github.com/DragonsPlusMinecraft/CreateDragonsPlus

See `NOTICE.md`, `THIRD_PARTY_NOTICES.md`, and `licenses/` for license and
third-party attribution details.

## AI Assistance

This Minecraft 1.20.1 port was developed with AI assistance. AI tools were used
to help with code adaptation, documentation, and repository maintenance. Final
review, integration decisions, and project maintenance are handled by the port
maintainer.

## License

Create: Integrated Farming is licensed under the GNU Lesser General Public
License version 3 or later. See `LICENSE.txt` for the full license text.
