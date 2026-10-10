# Surging and Momentum rework: summary

I've reworked Surging and Momentum and the build passes, but none of it is tested in-game yet. I also never pinned down why Momentum did nothing, so watch that one first in `./gradlew runClient`.

**Momentum**
- **Bar:** a blue XP-style bar now shows build-up while you hold a Momentum tool, and it empties on reset. It's drawn from the same value that sets the speed, so it doubles as a diagnostic:
  - If the bar fills but mining doesn't speed up, the speed bonus isn't reaching your client. Run `/attribute @s minecraft:block_break_speed get` and tell me the number.
  - If the bar doesn't fill, block breaks aren't being counted.
- **Why the switch to a multiplier:** instamining obsidian needs a total break speed of 1500. The old bonus (`mining_efficiency`) is additive and capped at 1024, so even at its cap a diamond pickaxe would fall short. Momentum now multiplies `block_break_speed` instead.
- **Ramp:** the multiplier grows exponentially, so each block speeds you up by the same factor. A straight-line ramp would hit instamine after only a block or two.

| Level | Blocks to max | Max multiplier |
|---|---|---|
| I | 40 | ×3 |
| II | 20 | ×8 (instamines stone) |
| III | 14 | ×25 |
| IV | 10 | ×70 |
| V | 8 | ×190 (diamond reaches 1520, so obsidian instamines) |

- **Reset:** after 2 seconds without mining. Holding left-click counts as mining, so a slow block like obsidian won't reset you mid-dig.

**Surging**
- **Per pickaxe:** each pickaxe gets an ID the first time you hold it, and its charge is stored on you under that ID.
  - I couldn't store the charge on the pickaxe itself: the client restarts block-breaking whenever the held item's data changes, so mining would never finish.
  - Cooldowns survive relogging and death.
- **Instamining now drains:** each block broken costs 1/15 of the bar. Blocks with zero hardness, like grass and torches, stay free.
- **Every surge triggers a cooldown:** half a second after you stop mining, the bar turns orange and refills, and there's no bonus until it's full. The half-second bridges vanilla's short pause between blocks, so continuous mining counts as one surge. Mining during the cooldown pauses the refill.
- **Colours:** both bars use the XP bar's background, with new flat blue and orange fills: `mining_bar_blue.png` and `mining_bar_orange.png` under `textures/gui/sprites/hud/`.

**Things you might notice**
- The first time you hold a Surging pickaxe, the hand does one re-equip bob as it gets its ID.
- If you pass a Surging pickaxe to another player, it arrives fully charged for them, because charge is stored per player.
- All the numbers are constants at the top of `enchantment/MiningSpeedEnchantments.java`. That file is used by both the server and the HUD, so the bar always matches the actual bonus. The server logic is in `event/MiningSpeedEnchantmentEvents.java`.

**What to check in `runClient`**
1. Momentum V on the stone cube: watch the bar and the speed.
2. Then 8 obsidian with a diamond pickaxe: it should instamine after that.
3. Two Surging pickaxes: their bars should stay independent.
4. Surging after a relog: the cooldown should still be there.

---

# Test commands

Here are the commands. Most of the treasure books only drop some of the time, so run each one several times (up-arrow, Enter) and `/clear @s` now and then, because whole chest tables fill your inventory fast.

**The four new enchantments**
```
/loot give @s loot minecraft:chests/bastion_treasure
/loot give @s loot minecraft:chests/bastion_other
/loot give @s loot minecraft:chests/desert_pyramid
/loot give @s loot minecraft:chests/jungle_temple
/loot give @s loot minecraft:chests/woodland_mansion
```
| Table | Book | Chance |
|---|---|---|
| bastion_treasure | Momentum | 30% |
| bastion_other | Momentum | 10% |
| desert_pyramid | Flurry | 30% |
| jungle_temple | Air Affinity | 40% |
| woodland_mansion | Surging | 40% |

**Treasure locations from earlier work**
```
/loot give @s loot minecraft:chests/underwater_ruin_big
/loot give @s loot minecraft:chests/underwater_ruin_small
/loot give @s loot minecraft:chests/buried_treasure
/loot give @s loot minecraft:chests/shipwreck_treasure
/loot give @s loot minecraft:chests/shipwreck_supply
/loot give @s loot minecraft:chests/igloo_chest
/loot give @s loot minecraft:chests/village/village_snowy_house
/loot give @s loot minecraft:chests/ancient_city
/loot give @s loot minecraft:chests/nether_bridge
/loot give @s loot minecraft:chests/ruined_portal
/loot give @s loot minecraft:chests/end_city_treasure
/loot give @s loot minecraft:chests/village/village_armorer
/loot give @s loot minecraft:chests/village/village_toolsmith
/loot give @s loot minecraft:chests/village/village_weaponsmith
/loot give @s loot minecraft:chests/pillager_outpost
/loot give @s loot minecraft:chests/trial_chambers/reward_rare
/loot give @s loot minecraft:chests/trial_chambers/reward_ominous_rare
```
- **Underwater ruins:** Respiration
- **Buried treasure:** Depth Strider
- **Shipwrecks:** Luck of the Sea
- **Igloo and snowy village houses:** Frost Walker
- **Ancient cities:** Keeping. It should never drop in a hardcore world.
- **Nether fortresses:** Magma Strider or Flame
- **Ruined portals:** Crust Walker
- **End cities:** Beheading or Mending
- **Village armorer, toolsmith and weaponsmith chests:** Tempering
- **Pillager outposts:** Multishot
- **Trial chamber reward vaults:** your edited tables. The ominous one has the Wind Burst book.

**Elder Guardian (Thorns, 100% when a player kills it)**

Spawn one, then roll its drops without fighting it:
```
/summon minecraft:elder_guardian ~ ~ ~5 {NoAI:1b}
/loot give @s kill @e[type=minecraft:elder_guardian,limit=1,sort=nearest]
```

**Restricted books shouldn't appear in normal loot**

Roll ordinary chests a few dozen times. Enchanted books there should never carry the treasure enchantments.
```
/loot give @s loot minecraft:chests/simple_dungeon
/loot give @s loot minecraft:chests/abandoned_mineshaft
```

**Beheading head drops**

Kill zombies, skeletons, creepers, wither skeletons or piglins with a Beheading III sword:
```
/give @s minecraft:diamond_sword[enchantments={"nebajs-enchantment-rework:beheading":3}]
/summon minecraft:zombie ~ ~ ~3
```

**Testing the enchantments themselves without looting**

Get tools directly:
```
/give @s minecraft:diamond_pickaxe[enchantments={"nebajs-enchantment-rework:momentum":5}]
/give @s minecraft:diamond_pickaxe[enchantments={"nebajs-enchantment-rework:surging":3}]
/give @s minecraft:diamond_pickaxe[enchantments={"nebajs-enchantment-rework:surging":3}]
/give @s minecraft:diamond_sword[enchantments={"nebajs-enchantment-rework:flurry":5}]
/give @s minecraft:diamond_helmet[enchantments={"nebajs-enchantment-rework:air_affinity":1}]
/give @s minecraft:obsidian 64
/attribute @s minecraft:block_break_speed get
/attribute @s minecraft:mining_efficiency get
```
The second Surging pickaxe is for checking that each pickaxe has its own bar.

To check that the table unlocks these and shows their clues:
```
/give @s minecraft:enchanted_book[stored_enchantments={"nebajs-enchantment-rework:momentum":1}]
/give @s minecraft:enchanted_book[stored_enchantments={"nebajs-enchantment-rework:surging":1}]
/give @s minecraft:enchanted_book[stored_enchantments={"nebajs-enchantment-rework:flurry":1}]
/give @s minecraft:enchanted_book[stored_enchantments={"nebajs-enchantment-rework:air_affinity":1}]
/give @s minecraft:chiseled_bookshelf 4
/give @s minecraft:enchanting_table
```
1. Before placing any books, open the table with a pickaxe, sword or helmet in the slot. The new enchantments should show as `?????` with their clue on hover.
2. Place the bookshelves around the table and put the books in them. The `?????` entries should now be purchasable.

---

# Changes to existing enchantments

None of this has been tested in-game yet. The build and datagen pass, but the Drowned mixin and the vanilla enchantment JSON overrides (`src/main/resources/data/minecraft/enchantment/`) are only checked when the game loads, so first make sure the log has no mixin or "Failed to parse" errors.

**New items in the enchanting table**
```
/give @s minecraft:enchanting_table
/give @s minecraft:lapis_lazuli 64
/xp add @s 100 levels
/give @s minecraft:diamond_axe
/give @s minecraft:trident
/give @s minecraft:mace
/give @s minecraft:crossbow
/give @s minecraft:bow
```
Put each item in the table. The list should now include:

| Item | Newly available |
|---|---|
| Axe | Looting, Fire Aspect, Sharpness, Beheading (`?????` until taught) |
| Trident | Sharpness, Looting |
| Mace | Looting |
| Crossbow | Infinity, Flame (`?????` until taught) |
| Bow | Piercing |

Axes already took Sharpness through the anvil in vanilla, but not at the table. Mace Fire Aspect stays anvil-only, as in vanilla.

To unlock Beheading and Flame:
```
/give @s minecraft:enchanted_book[stored_enchantments={"nebajs-enchantment-rework:beheading":1}]
/give @s minecraft:enchanted_book[stored_enchantments={"minecraft:flame":1}]
/give @s minecraft:chiseled_bookshelf 2
```

**Same items in the anvil**
```
/give @s minecraft:anvil
/give @s minecraft:enchanted_book[stored_enchantments={"minecraft:looting":3}]
/give @s minecraft:enchanted_book[stored_enchantments={"minecraft:fire_aspect":2}]
/give @s minecraft:enchanted_book[stored_enchantments={"minecraft:sharpness":5}]
/give @s minecraft:enchanted_book[stored_enchantments={"minecraft:infinity":1}]
/give @s minecraft:enchanted_book[stored_enchantments={"minecraft:piercing":4}]
```
1. Each book should combine onto the items from the table above.
2. A crossbow should still refuse Power and Punch, and a bow should still refuse Multishot and Quick Charge. Only the three listed enchantments were opened up.
3. Infinity on a crossbow: load it with a single arrow. The arrow shouldn't be used up.
4. Flame on a crossbow: arrows should fly out burning.
5. Piercing on a bow: arrows should pass through a line of mobs.

**Bane of Beasts**
```
/give @s minecraft:diamond_sword[enchantments={"minecraft:bane_of_arthropods":5}]
/summon minecraft:creeper ~ ~ ~3 {NoAI:1b}
/summon minecraft:enderman ~ ~ ~3 {NoAI:1b}
/summon minecraft:blaze ~ ~ ~3 {NoAI:1b}
/summon minecraft:vex ~ ~ ~3 {NoAI:1b}
/summon minecraft:zombie ~ ~ ~3 {NoAI:1b}
/summon minecraft:witch ~ ~ ~3 {NoAI:1b}
```
1. The tooltip and the table both say "Bane of Beasts".
2. Hitting a creeper, enderman, blaze or vex applies Slowness IV, which is the visible sign that the bonus damage applies too.
3. Zombies and witches get no Slowness: undead fall under Smite, and witches and illagers were left out on purpose.
4. Also covered: slime, magma cube, breeze, ghast, hoglin, ravager, shulker, warden, creaking, plus vanilla's arthropods. The list is in `datagen/ModEntityTypeTagsProvider.java`.

**Fire Protection IV full set**
```
/give @s minecraft:diamond_helmet[enchantments={"minecraft:fire_protection":4}]
/give @s minecraft:diamond_chestplate[enchantments={"minecraft:fire_protection":4}]
/give @s minecraft:diamond_leggings[enchantments={"minecraft:fire_protection":4}]
/give @s minecraft:diamond_boots[enchantments={"minecraft:fire_protection":4}]
/give @s minecraft:diamond_helmet[enchantments={"minecraft:fire_protection":3}]
/gamemode survival
/setblock ~2 ~ ~ minecraft:lava
/setblock ~-2 ~-1 ~ minecraft:magma_block
/damage @s 5 minecraft:lava
```
1. Wearing all four Fire Prot IV pieces: lava, fire, magma blocks and `/damage ... minecraft:lava` all do nothing. You'll still look like you're on fire, but it doesn't hurt.
2. Swap the helmet for the Fire Prot III one: damage comes back (reduced as normal).
3. Things that bypass invulnerability (`/kill`, the void) still go through.

**Mace Wind Burst right-click**
```
/give @s minecraft:mace[enchantments={"minecraft:wind_burst":3}]
/give @s minecraft:mace
```
1. Right-clicking the Wind Burst mace fires a wind charge where you're looking, and the hotbar shows a 4-second cooldown sweep.
2. No wind charges are used up (you don't need any).
3. The plain mace does nothing on right-click.
4. Combo: shoot at your feet, get launched, and smash on the way down. Melee attacks are allowed during the cooldown.

**Elytra and trident repair**
```
/give @s minecraft:elytra[damage=300]
/give @s minecraft:leather 4
/give @s minecraft:phantom_membrane 4
/give @s minecraft:trident[damage=200]
/give @s minecraft:prismarine_shard 8
```
1. An elytra plus leather in the anvil repairs it. Phantom membrane still works too.
2. A trident plus prismarine shards repairs it.

**Channeling in any weather**
```
/weather clear
/give @s minecraft:trident[enchantments={"minecraft:channeling":1}]
/summon minecraft:cow ~ ~ ~8
/give @s minecraft:lightning_rod
```
1. In clear weather, a thrown trident that hits the cow calls down lightning.
2. Under a roof (no sky view) there's no lightning, same as vanilla.
3. A trident hitting a lightning rod under open sky also calls lightning.

**Trident drops from drowned**

Drop chance is now 25%, up from vanilla's 8.5%. The rate at which drowned spawn holding tridents (about 6%) is unchanged. A plain `/summon` goes through the normal spawn equipment roll, so summon a batch and kill the ones holding tridents:
```
/summon minecraft:drowned ~ ~ ~3
/kill @e[type=minecraft:drowned,nbt=!{equipment:{mainhand:{id:"minecraft:trident"}}}]
```
Kill the trident drowned yourself (it only drops on player kills). About 1 in 4 should drop the trident. If you summon one with the trident set in its NBT, it skips the spawn roll and keeps vanilla's 8.5%.

**Things you might notice (not bugs)**
- Looting on a trident only counts for melee kills. Loot reads Looting from what you're holding, so thrown-trident kills get none (vanilla works the same way).
- Channeling now works in clear weather, along with lightning's usual side effects: fires, charged creepers, and villagers turning into witches.
- The copied vanilla enchantment files need re-checking after a Minecraft version bump.
