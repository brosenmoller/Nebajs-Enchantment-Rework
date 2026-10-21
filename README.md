# Nebaj's Enchantment Rework

A Fabric mod for Minecraft 26.3 that reworks the enchanting system. It replaces the enchanting table interface, moves treasure enchantments to specific structures, adds new enchantments, changes several existing ones, and changes how experience is earned and spent.

## Requirements

- Minecraft 26.3
- Fabric Loader 0.19.5 or newer
- Fabric API
- Java 25

The mod must be installed on both the server and the client.

## Enchanting table

Right-clicking an enchanting table opens a new interface instead of the vanilla one.

- Every enchantment that can be applied to the item in the slot is listed in a scrollable list.
- Enchantments are bought one level at a time. Each level costs experience levels and a material, and some levels also require a minimum number of bookshelves around the table.
- The cost of the next level is always shown until the enchantment reaches its maximum level.
- Entries that cannot be bought show the reason: maximum level reached, or incompatible with an enchantment already on the item.

### Materials

Lapis lazuli is the default material. Some enchantments use a different one:

| Enchantment            | Material                                                         |
| ---------------------- | ---------------------------------------------------------------- |
| Protection, Unbreaking | The item's repair material                                       |
| Mending, Impaling      | The item's repair material, or lapis lazuli if the item has none |
| Feather Falling        | Feather                                                          |
| Fire Aspect, Flame     | Fire charge                                                      |
| Frost Walker           | Blue ice                                                         |
| Crust Walker           | Basalt                                                           |
| Channeling             | Copper ingot                                                     |
| Thorns                 | Prismarine shard                                                 |
| Aqua Affinity          | Heart of the Sea                                                 |
| Swift Sneak, Keeping   | Echo shard                                                       |
| Wind Burst             | Wind charge                                                      |

### Treasure enchantments and chiseled bookshelves

Treasure enchantments have to be taught to the table before they can be bought. To teach one, place a chiseled bookshelf in a valid bookshelf position around the table and put an enchanted book with that enchantment in it.

- Treasure enchantments that have not been taught are shown as `?????`. Hovering over the entry explains the bookshelf mechanic and gives a clue to where the book can be found.
- A chiseled bookshelf counts towards the table's bookshelf total if it holds at least one enchanted book.

The following enchantments are treasure enchantments, some were already, some are turned into treasure enchantments: Mending, Respiration, Thorns, Depth Strider, Frost Walker, Luck of the Sea, Swift Sneak, Wind Burst, Soul Speed, Multishot, Flame, Channeling, Infinity, Beheading, Keeping, Magma Strider, Crust Walker, Tempering and Air Affinity.

## Treasure enchantment locations

Treasure enchantments no longer appear in general loot. They are removed from random enchanted loot, enchant-with-levels loot, villager trades and mob equipment, and only come from the locations below.

| Enchantment            | Location                                          | Chance per chest             |
| ---------------------- | ------------------------------------------------- | ---------------------------- |
| Respiration            | Ocean ruins                                       | 30% (big), 10% (small)       |
| Depth Strider          | Buried treasure                                   | 50%                          |
| Luck of the Sea        | Shipwrecks                                        | 30% (treasure), 10% (supply) |
| Frost Walker           | Igloos, snowy village houses                      | 50% (igloo), 15% (house)     |
| Keeping                | Ancient cities (not in hardcore worlds)           | 30%                          |
| Magma Strider or Flame | Nether fortresses                                 | 20%                          |
| Crust Walker           | Ruined portals                                    | 20%                          |
| Beheading or Mending   | End cities                                        | 20%                          |
| Tempering              | Village armorer, toolsmith and weaponsmith chests | 40%, 50%, 40%                |
| Multishot              | Pillager outposts                                 | 60%                          |
| Air Affinity           | Jungle temples                                    | 90%                          |
| Channeling             | Woodland mansions                                 | 60%                          |
| Infinity               | Stronghold libraries                              | 40%                          |
| Wind Burst             | Trial chamber ominous vaults                      | Part of the rare reward pool |
| Thorns                 | Elder Guardian (killed by a player)               | 100%                         |

Swift Sneak and Soul Speed keep their vanilla sources (ancient cities and bastions).

## New enchantments

| Enchantment      | Applies to               | Max level | Effect                                                                                                                                                                                           |
| ---------------- | ------------------------ | --------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Beheading        | Swords, axes             | III       | Chance for a mob killed by a player to drop its head: 40%, 60%, 80%. Works on zombies, skeletons, creepers, wither skeletons, piglins and the Ender Dragon.                                      |
| Tempering        | Any item with durability | III       | Each repair material in the anvil restores more durability: 1/3 at level I, 1/2 at level II, full durability at level III (vanilla restores 1/4). Incompatible with Mending.                     |
| Keeping          | Any item with durability | III       | The item stays in your inventory when you die, and loses one level of Keeping. Incompatible with Curse of Vanishing.                                                                             |
| Magma Strider    | Boots                    | III       | Faster movement in lava, the lava equivalent of Depth Strider. Incompatible with Depth Strider and Crust Walker.                                                                                 |
| Crust Walker     | Boots                    | II        | Walking over lava turns it into temporary Crusted Basalt that melts back into lava, the lava equivalent of Frost Walker. Also prevents damage from magma blocks. Incompatible with Frost Walker. |
| Melee Protection | Armor                    | IV        | Reduces melee damage, including melee attacks from players, more than Protection does. Incompatible with the other protection enchantments.                                                      |
| Air Affinity     | Helmets                  | I         | Removes most of the mining speed penalty while not on the ground, including while riding. Mining speed in the air is 80% instead of 20%. Incompatible with Aqua Affinity.                        |

Changes to existing enchantments

- **Bane of Arthropods** is renamed to **Bane of Beasts**. In addition to arthropods, it now affects creepers, slimes, magma cubes, blazes, breezes, ghasts, endermen, hoglins, ravagers, vexes, shulkers, wardens and creakings.
- **Fire Protection:** wearing Fire Protection IV on all four armor pieces prevents all fire and lava damage. Damage that bypasses invulnerability is not affected.
- **Channeling** works in any weather. It still requires the target to be under open sky.
- **Wind Burst** on a mace: right-clicking fires a wind charge. This has a 4 second cooldown and does not use any wind charge items.

### New item and enchantment combinations

These apply both at the enchanting table and in the anvil.

| Item     | Newly allowed enchantments                 |
| -------- | ------------------------------------------ |
| Axe      | Looting, Fire Aspect, Sharpness, Beheading |
| Trident  | Sharpness, Looting                         |
| Mace     | Looting                                    |
| Crossbow | Infinity, Flame                            |
| Bow      | Piercing                                   |

## Items and repair

- Elytra can be repaired with leather, in addition to phantom membrane.
- Tridents can be repaired with prismarine shards.
- Netherite tools and armor are repaired with diamonds instead of netherite ingots.
- Drowned holding a trident drop it 25% of the time when killed by a player (vanilla is 8.5%).

## Anvil

- Repairing and renaming no longer cost experience. Only combining enchantments costs experience.
- The "Too Expensive!" limit is removed.

## Experience

- Every level requires the same amount of experience: 20 points per level.
- On death, a player drops 50% of their total experience. Vanilla's cap of 7 levels per level held (maximum 100 points) is removed.
- The grindstone refunds 50% of the experience it would cost to buy the removed enchantments at the enchanting table. The refund is no longer random.
- Fishing gives 3 to 10 experience per catch.
- Harvesting sweet berries and glow berries gives 0 to 1 experience.
- Breaking fully grown crops gives experience: wheat, carrots, potatoes, beetroots, nether wart and cocoa give 0 to 1, pitcher crops give 1 to 2. Breaking an amethyst cluster gives 1 to 3.

## Configuration

Most values are data driven and can be changed with a datapack.

- **Experience settings:** `data/nebajs-enchantment-rework/xp_settings/xp_settings.json` controls experience per level, the death drop share, the grindstone refund share, and the experience from fishing, berries and block breaking.
- **Enchanting table costs:** `data/<namespace>/enchantment_menu_data/<enchantment>.json` controls the experience cost, material, material amount and required bookshelves for each level of an enchantment, its position in the list, and its clue text. Enchantments without a file use `data/nebajs-enchantment-rework/enchantment_menu_data/default.json`.

Changes to enchanting table costs also change grindstone refunds, since the refund is based on them.

## Building

```
./gradlew build
```

The mod jar is written to `build/libs`. Other useful tasks:

- `./gradlew runClient` starts a development client.
- `./gradlew runServer` starts a development server.
- `./gradlew runDatagen` regenerates the generated data in `src/main/generated`.

## Credits

This mod takes heavy inspiration from [Mallarad's Enchanting Rework](https://modrinth.com/mod/mallarads-enchanting-rework). That project has not been updated for newer Minecraft versions, and there were several things I wanted to do differently, so this mod was written as a separate rework rather than a port.

Inspiration was also taken from [Enchancement](https://modrinth.com/mod/enchancement).

I really appreciate the work that went into both of these mods, and I want to thank their authors for making them.

## License

MIT, as declared in `fabric.mod.json`.
