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

Some features require optional integration mods. See `build.gradle` for the
exact development dependency list used by this repository.

## Authorship And Attribution

Original upstream project by DragonsPlus:
https://github.com/DragonsPlusMinecraft/CreateIntegratedFarming

Minecraft 1.20.1 port maintained by KazeShukufuku.

Some helper code from Create Dragons Plus is incorporated for this port:
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
