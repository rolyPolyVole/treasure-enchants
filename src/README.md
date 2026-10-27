# Treasure Enchants

This mod adds unobtainable enchanted books as rare loot all across the world.
It's a great endgame feature for SMPs or Vanilla+ Servers.

The mod functions as a server-side only mod, but also works for singleplayer.

---

## Enchantments

The books and where they can be found are 100% customisable.<br />Here are some examples from the default config:

| Enchanted Book          | Found In                | Chance |
|-------------------------|-------------------------|--------|
| Sharpness VI            | End City chests         | 2.5%   |
| Power VI                | Woodland Mansion chests | 5.0%   |
| Protection V            | Ancient City chests     | 1.5%   |
| Impaling VI             | Elder Guardian drop     | 3.0%   |
| Multishot & Piercing IV | Pillager Outpost chests | 0.5%   |

The book is added to the original loot, so the original loot is not affected.

<details>
<summary>Full default list</summary>

---

| Found In                   | Enchanted Book          | Chance |
|----------------------------|-------------------------|--------|
| Dungeon chests             | Unbreaking IV           | 2.5%   |
| Mineshaft chests           | Efficiency VI           | 0.5%   |
| Desert Pyramid chests      | Smite VI                | 2.0%   |
|                            | Thorns IV               | 2.0%   |
| Jungle Temple chests       | Smite VI                | 15.0%  |
| Igloo chests               | Frost Walker III        | 1.0%   |
| Pillager Outpost chests    | Quick Charge IV         | 2.5%   |
|                            | Multishot & Piercing IV | 0.5%   |
| Woodland Mansion chests    | Power VI                | 5.0%   |
|                            | Infinity & Mending      | 2.5%   |
| Buried Treasure chests     | Looting IV              | 1.0%   |
| Ocean Ruin chests          | Luck of the Sea VI      | 0.1%   |
|                            | Lure IV                 | 0.1%   |
| Fishing (treasure catches) | Luck of the Sea VI      | 2.0%   |
|                            | Lure IV                 | 2.0%   |
| Stronghold Library chests  | Protection V            | 5.0%   |
|                            | Efficiency VI           | 5.0%   |
| Ancient City chests        | Protection V            | 1.5%   |
| Ancient City ice box       | Frost Walker III        | 1.0%   |
| Ruined Portal chests       | Fire Protection V       | 0.5%   |
| Nether Fortress chests     | Fire Protection V       | 1.5%   |
| Bastion Treasure chests    | Sharpness VI            | 1.0%   |
| Other Bastion chests       | Soul Speed IV           | 0.5%   |
| End City chests            | Sharpness VI            | 2.5%   |
|                            | Feather Falling V       | 2.5%   |
|                            | Fortune IV              | 1.0%   |
| Elder Guardian drop        | Impaling VI             | 3.0%   |
|                            | Riptide IV              | 1.0%   |
| Warden drop                | Swift Sneak IV          | 5.0%   |

---

</details>

---

## Anvils

The anvil mechanics had to be changed to support the treasure enchants. There is also a config option to increase the level cost cap of the anvil.

Note: Players without the mod will see the "Too expensive!" text, but the items can be combined anyway.

---

## Configuration

The config is generated at `config/treasure_enchants.json`. All values can be updated with `/reload`.

```json
{
    "anvilCostLimit": 40,
    "enchants": [
        {
            "lootKey": "chests/end_city_treasure",
            "chance": 2.5, "luckFactor": 0.5,
            "enchantments": [ { "enchantKey": "sharpness", "level": 6 } ]
        },
        {
            "lootKey": "chests/woodland_mansion",
            "chance": 2.5, "luckFactor": 0.5,
            "enchantments": [ { "enchantKey": "infinity", "level": 1 }, { "enchantKey": "mending", "level": 1 } ]
        }
    ]
}
```

`lootKey` is the resource location of the loot table.<br />
`chance` is the probability of the book to appear, in percentage, with 1 decimal precision.<br />
`luckFactor` is multiplied by the player's luck of the sea enchantment (or luck effect) and added to `chance`.<br /><br />

The `minecraft:` namespace is optional, but for custom enchants/loottables, you need to add the namespace of the mod/datapack: `my-mod:my_cool_enchant`

---

## Installation Notes

Requires Fabric API and Fabric Language Kotlin.<br />

Only the server needs the mod. Players can join with a vanilla client. For singleplayer, install it like any other mod.<br />

The mod works in existing worlds. Chest loot is generated the first time a chest is opened.<br />

## For Developers

Contributions are welcome.

## Notes

Credit in the form of a link would be appreciated. Not required though.
