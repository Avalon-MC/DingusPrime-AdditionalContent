# DingusPrime Additional Content Mod - [MIT Licence](https://github.com/Avalon-MC/DingusPrime-AdditionalContent/blob/main/LICENSE)
> A Forge 1.18.2 mod. Requires Calemi's Economy, OpenLoader, KubeJS. Created by Peter Cashel (pacas00) and contributers.
>> PLEASE NOTE: DingusPrime ACM is in Alpha and is being actively developed and tested.

THIS MOD CONTAINS NO COPYRIGHTED MATERIAL. WE DO NOT DISTRIBUTE COPYRIGHTED MATERIAL. WE WILL NOT HELP YOU FIND ANY COPYRIGHTED MATERIAL. 

## What is this

DingusPrime ACM is a mod to house additional custom content / code / any required additions for the DingusPrime modpack and server.
 We intend to use this alongside our other mod ContentSync to allow us to update KubeJS scripts for this mod, without needing major pack updates.


It currently contains: 
* A custom type for KubeJS to create handheld GameBoy like items
* A custom type for KubeJS to create handheld cartridges for the use with the handhelds
* A custom registry for ROMs to map between cart items and files
* A GBStudio made 'game' (defaultrom.gb) to serve as a fallback when the game rom isnt loadable (missing file, not registered, no cart in handheld)
* * Simple one screen 'game' that says there is no cart.

* A Clone of the KubeJS 6 cardinal block type. This is not availible in KubeJS 5.5 for 1.18.2 and we need it for custom blocks.

* A set of 18 Villager Trade NPCs for use in custom shops. They don't move around and have most of their AI removed. They can only be hurt by creative players (to prevent zombies killing them).
* * Configurable using KubeJS scripts
* * Intergrates into Calemi's Economy.
* * Max of 3 items random available each 'day'.
* * Always available items supported.
* * Cycles available items every 24 hours or every server restart.


## Configuration - Aka, How to use this? Where's the spawn eggs? Can I change the shop names? Why are my shops empty? How do I use all these new block types!

### Shop NPCs
Whats Configurable: Shop Names, Trades, Villager Skins.

##### Shop Names (Requires Resource Pack)
Shop names can be changed by extracting and editing the mods `en_us.json` language file and packing the modified copy into your own custom resource pack at the same path.
The file can be found in the mod jar at following path. `assets/dingusprimeacm/lang/`

##### Villager Skins (Requires Resource Pack)
Villager Skins can be changed by extracting and editing the textures and packing the modified copy into your own custom resource pack at the same path.
The skins can be found in the mod jar at following path. `assets/dingusprimeacm/textures/entity/villager/`

##### Trades (Requires KubeJS Startup Script)
Trades are configured at startup using KubeJS. You need to be familar with KubeJS before attempting this.

All calls are required (except `always()`). Below is a list of methods with thee parameters in ALL_CAPS with a brief description of what they are
* `event.create('NAME')`          - takes a unique registration name per trade.
* `shopType('SHOPTYPE')`          - Which NPC shop the trade is for. See below example for Shop Type list
* `result('kubejs:tombstone_1')`  - A resource location to point to the block or item to give. e.g. `minecraft:cobblestone` or `minecraft:stick` or `mycoolmodhere:someblockname`
* `shopResultType('block')`  - `block` or `item`. Set this depending on if the result (bought item/block) is a block or item.
* `count(3)`  - How many is sold. Either set it to 1 for a single item, or for example, if you want a 3 pack, set it to 3.
* `cost(5)`  - How many Calemi's Economy Copper Coins it will cost.
* `always()` - If the trade is always available.


##### Script Examples - These go in a KubeJS Startup Script.
```
onEvent('shoptrade_registry', event => {
	// Register new Shop Trades here

 //Example of random trade
 event.create('tombstone_1').shopType('furniture').result('kubejs:tombstone_1').shopResultType('block').count(3).cost(5)

 //Example of always available trade
 event.create('tombstone_2').shopType('furniture').result('kubejs:tombstone_2').shopResultType('block').count(2).cost(2).always().
 
})
```
##### Shop Types
```
general,
furniture,

weapons,
armor,
tools,

seeds,
trees,
plants,

cosmetic,
hats,
shirts,
pants,
shoes,

curios,

custom1,
custom2,
custom3,
custom4,
```

##### Spawn Eggs?
There are no spawn eggs.
Instead, Stand where you want the NPC and use the summon command.
Start by typing `/summon dingusprimeacm:shopkeeper_` into chat and append the shop type to the end
Ignore `dingusprimeacm:shopkeeper`, is is the base npc for the shopkeepers and defaults to furniture.



### New Block and Item Types
This information is relevent to all of the ones below.
Each type below will be annotated with any relevent information. Is the facing blockstate supported, required assets, etc.

You will need to setup Blockstates (Block Only), Models and Textures (B, M and T) for each block and item you add. If you dont specify a namespace for a block, then its B,M and T need to go under `assets/kubejs/` in your resource pack, However, Keep reading. 

You will need to deploy these assets to `KubeJS`'s assets folder. (Server/Client) resource packs and OpenLoader do not work as KubeJS won't pickup on the files. I've not found any other method of getting these recognised.
The two currently known methods of doing this is:

* Modpack Updates - Your usual pack update and adding the new files to `KubeJS`'s assets folder obviously works.

* ContentSync - If you are using our other mod ContentSync for your server's modpack, then it supports automatically deploying the contents of `assets/kubejs/` to the `KubeJS`'s assets folder.

Note: Support for non kubejs namespaced content is not implemented in ContentSync, so if you want to use CS for this issue, do not specify a namespace for the blocks / items. 


### Common new builder methods
All DingusPrime ACM added block types support a new method.
* `addBox` - Works almost like the box method. Except it's designed to take the position and size from BlockBench and do the math for you. Finally, the true/false is for flipping the box 180 if the model is backwards.
* * Example `addBox('posX','posY','posZ','sizeX','sizeY','sizeZ', (true/false))`


### Cardinal Blocks (port from KJS 6 to 5.5) (Facing:Yes) (B,M,T)
type: `customcardinal` - Supports facing any horizontal direction.


### Shelves (Facing:Yes) (B,M,T)
type: `shelf` - A 16 slot (4x4) shelf that displays items. Included in the mod is a B,M & T for 'gameshelf'. Use this as a reference.

### Cabinet(Facing:Yes) (B,M,T)
type: `cabinet` - A 16 slot (4x4) shelf that doesn't display items. 

### Chair (Facing:Yes) (B,M,T)
type: `chair` - Make a custom chair.

The Y height the player sits at is adjustable with a custom method.
* `sitOffset` - Numerical parameter. (eg `-0.5` or `1.5`) Higher values move you higher up the block. You start centered in the block.


### Lamp (Tops and Posts) (Facing:No) (B,M,T)
type: `lamp_top` - The Light Generating part. Activates on Right Click. Use for custom lights

type: `lamp_post` - The optional stand part. Activates a lamp_top up to 8 blocks above this. Must be connected via other lamp_post blocks.

### Flatpack Crates (Facing:No) (~~B,M,T~~)
type: `flatpack` - It's a bundle and a block. Automatically uses the included model and textures. Can be used with the shopkeeper.

Add items to a custom box with the custom methods
* `AddItem` - Takes a registry name of a block or item and a quantity and adds it to the box.
* *  Will automatically search Items before Blocks for a valid item. Will also search the `kubejs` namespace after the default `minecraft` namespace if no namespace is supplied.
* * Example `AddItem('fancy_lamp_post', 2)` or `AddItem('kubejs:fancy_lamp_post', 2)`



### GameBoy Color Emulator
Included is a GBC emulator, which is a modified version of https://github.com/trekawek/coffee-gb/. All credit for the excelent Java based GBC emulator goes to trekawek.

Work in Progress.
TODO: Add info on the following
* Registering Roms to the Rom Registry
* Creating gbcart Cartridge items and linking them to roms
* Creating gameboy Handheld items to use the carts in.
* where to put the rom files in the resource pack.


```

```
