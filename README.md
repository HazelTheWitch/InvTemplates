# InvTemplates

## Description

Highly customizable inventory sorting templates with the click of a button. To sort the player's inventory
either middle mouse click anywhere not over a container's slot or press the bound button outside the inventory. 
To sort a container hover over the container's slots and middle mouse click.

## Configuration

### Default Configuration File

```json
{
  "templateInventory": [
    "$$$$$$$$$",
    "$$$$$$$$$",
    "$$$$a$$$$",
    "SPAsBH bFt"
  ],
  "key": {
    "P": {
      "type": "multi-item",
      "identifiers": [
        "minecraft:netherite_pickaxe",
        "minecraft:diamond_pickaxe",
        "minecraft:iron_pickaxe",
        "minecraft:golden_pickaxe",
        "minecraft:stone_pickaxe",
        "minecraft:wooden_pickaxe"
      ]
    },
    " ": {
      "type": "empty"
    },
    "A": {
      "type": "multi-item",
      "identifiers": [
        "minecraft:netherite_axe",
        "minecraft:diamond_axe",
        "minecraft:iron_axe",
        "minecraft:golden_axe",
        "minecraft:stone_axe",
        "minecraft:wooden_axe"
      ]
    },
    "a": {
      "type": "tag",
      "tag": "minecraft:arrows"
    },
    "B": {
      "type": "item",
      "identifier": "minecraft:bow"
    },
    "b": {
      "type": "multi-item",
      "identifiers": [
        "minecraft:cobblestone",
        "minecraft:netherrack",
        "minecraft:stone"
      ]
    },
    "S": {
      "type": "multi-item",
      "identifiers": [
        "minecraft:netherite_sword",
        "minecraft:diamond_sword",
        "minecraft:iron_sword",
        "minecraft:golden_sword",
        "minecraft:stone_sword",
        "minecraft:wooden_sword"
      ]
    },
    "s": {
      "type": "multi-item",
      "identifiers": [
        "minecraft:netherite_shovel",
        "minecraft:diamond_shovel",
        "minecraft:iron_shovel",
        "minecraft:golden_shovel",
        "minecraft:stone_shovel",
        "minecraft:wooden_shovel"
      ]
    },
    "t": {
      "type": "item",
      "identifier": "minecraft:torch"
    },
    "$": {
      "type": "alphabetically-sorting"
    },
    "F": {
      "type": "food"
    },
    "H": {
      "type": "multi-item",
      "identifiers": [
        "minecraft:netherite_hoe",
        "minecraft:diamond_hoe",
        "minecraft:iron_hoe",
        "minecraft:golden_hoe",
        "minecraft:stone_hoe",
        "minecraft:wooden_hoe"
      ]
    }
  },
  "priorities": " SPAsBHFabt$"
}
```

### Format

```json
{
  "templateInventory": <template>,
  "key": {
    <character key>: {
      "type": <type>,
      <arguments>
    }
  },
  "priorities": <priorities>
}
```

#### Template

A list of 4 strings of length 9, 9, 9, and then 10. Each character corresponds to a
key in the key section, and a slot in the player's inventory (the 10th character in
the 4th string corresponding to the off-hand). In the default configuration it encodes
an inventory alphabetically sorted ($) with the main hot bar having a Sword, Pickaxe,
Axe, Shovel, Bow, Hoe in that order. This is followed by an empty slot and then some
blocks, food, and torches in the off-hand.

#### Character Key

These are the characters in the template. You should define one key per character used
in the template. The value to each key should be a slot definition.

#### Slots

The key section is made up of character key -> slots pairs. Slots are objects with 1
key named type, corresponding to one of the following types, and its arguments described
below.

##### alphabetically-sorting

Alphabetically sorted slot, prefers items starting with A going to Z.

##### empty

An empty slot, will fill with an item as a last resort.

##### item

A single item, will look for a specific item and fill with that item.

*identifier* - the item identifier to look for, ex: "minecraft:poppy"

##### multi-item

A set of multiple items, will look for items in order first to last and fill with any 
found item.

*identifiers* - a list of identifiers to look for, ex: ["minecraft:poppy", 
"minecraft:bow"] will look for a poppy followed by a bow.

##### food

Searches for a food item, prefers items with more hunger and saturation values.

##### tag

Searches for any item from the given tag.

*tag* - the tag to search through, ex: "minecraft:arrows" searches for any arrows including
tipped.

#### Priorities

The order to fill slots, in the config file it makes sure empty slots are empty, then fills sword, pickaxe
axe, shovel, bow, hoe, food, arrows, blocks, torches, and finally sorted slots. This order is reversed if 
the given template can not be filled properly to fill the inventory out.

## License

This mod is licensed under the MIT license.
