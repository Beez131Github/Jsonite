# Jsonite

MOD IS IN VERY EARLY STAGES
Create custom modded features on Quilt using resource/data packs. Currently only supports basic items and blocks (blocks are pre-alpha-0.02) but will be gettings lots of updates in the future.

How it works: Jsonite acts as a middleman between the Java code for creating items and the resource/datapacks json files. It translates the basic JSON data and applys the rest of the code the item requires.

Limitations: Due to how mods work u can only turn on/off the defualt behavoirs of resource/data packs. To remove or use a pack a game restart is needed. If a modded resource/data pack is not "used" but is still in the resourcepacks folder all modded aspects apply, but things like item name, textures etc do not apply until it moved to the "used" section.

Example pack: https://modrinth.com/datapack/jsonite-modded-resource-pack-example

Mod req:
Quilt 0.28-beta6
Fabric API
