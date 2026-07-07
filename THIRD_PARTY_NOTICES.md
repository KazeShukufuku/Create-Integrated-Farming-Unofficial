# Third-Party Notices

This file documents third-party material that is incorporated into, bundled
with, or directly referenced by this project. Ordinary runtime dependencies
declared in build.gradle are not listed here unless their files or license
obligations are carried in this distribution.

## Upstream Sources

### Create: Integrated Farming

* Project: https://github.com/DragonsPlusMinecraft/CreateIntegratedFarming
* Original authors: DragonsPlus
* Port maintainer: KazeShukufuku
* Upstream version referenced for this port: Minecraft 1.21.1 branch, mod
  version 1.2.6.
* Use: this repository is a Minecraft 1.20.1 port derived from the upstream
  Create: Integrated Farming project.
* License: GNU Lesser General Public License version 3 or later.
* License text: `LICENSE.txt`

### Create Dragons Plus

* Project: https://github.com/DragonsPlusMinecraft/CreateDragonsPlus
* Original authors: DragonsPlus
* Upstream version referenced by Create: Integrated Farming 1.21.1:
  `create_dragons_plus_version = 1.11.2`.
* Use: selected compatibility/helper code is incorporated under
  `src/main/java/plus/dragons/createdragonsplus/` because this port targets
  Minecraft 1.20.1 instead of using the upstream 1.21.1 dependency directly.
* License: GNU Lesser General Public License version 3 or later.
* License text: `LICENSE.txt`

## Bundled Libraries

### MixinExtras

* Project: https://github.com/LlamaLad7/MixinExtras
* Coordinates: `io.github.llamalad7:mixinextras-forge:0.4.1`
* Use: bundled into the published jar via Forge jarJar.
* License: MIT License
* License text: `licenses/MIXINEXTRAS_LICENSE.txt`

### Conditional Mixin

* Project: https://github.com/Fallen-Breath/conditional-mixin
* Coordinates: `me.fallenbreath:conditional-mixin-forge:0.6.4`
* Use: bundled into the published jar via Forge jarJar.
* License: GNU Lesser General Public License version 3
* License text: `licenses/CONDITIONAL_MIXIN_LICENSE.txt`

## Included Assets

### Farmer's Delight

* Project: https://github.com/vectorwing/FarmersDelight
* Use: texture assets under
  `src/main/resources/assets/create_integrated_farming/textures/block/farmersdelight/`.
* License: MIT License
* License text: `licenses/FARMERS_DELIGHT_LICENSE.txt`

The same license text is also kept next to the included texture files at
`src/main/resources/assets/create_integrated_farming/textures/block/farmersdelight/FARMERS_DELIGHT_LICENSE.txt`
so the attribution remains close to the assets.

## Code References

### Create

* Project: https://github.com/Creators-of-Create/Create
* Use: code reference marked with `@CodeReference` in
  `plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour`.
  The referenced implementation is Create's `BlockHelper#destroyBlockAs`.
* License: MIT License for Create code. Create assets are separately licensed
  as All Rights Reserved by the Create project and are not incorporated by this
  notice.
* License text: `licenses/CREATE_LICENSE.md`
