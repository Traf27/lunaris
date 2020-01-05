package dev.tricht.poe.assistant.item.parser;

import dev.tricht.poe.assistant.item.ItemRarity;
import dev.tricht.poe.assistant.item.types.*;
import lombok.Getter;

import java.util.ArrayList;

public class NamePart {

    private ArrayList<String> lines;

    public NamePart(ArrayList<String> lines) {
        this.lines = lines;
    }

    private String getRarityRaw() {
        return lines.get(0).split("Rarity: ")[1];
    }
    public ItemRarity getRarity() {
        switch(lines.get(0).split("Rarity: ")[1]) {
            case "Magic":
                return ItemRarity.MAGIC;
            case "Rare":
                return ItemRarity.RARE;
            case "Unique":
                return ItemRarity.UNIQUE;
            case "Normal":
            default:
                // Normal items + Currency, div cards, gems
                return ItemRarity.NORMAL;
        }
    }

    public String getItemName() {
        if (lines.size() == 2) {
            return "";
        }
        return sanitizeName(lines.get(1));
    }

    public String getBaseName() {
        if (lines.size() == 2) {
            return sanitizeName(lines.get(1));
        }

        return sanitizeName(lines.get(2));
    }

    private String sanitizeName(String name) {
        return name.replace("Superior ", "");
    }

    public ItemType getItemType() {
        int lineNum = 1;
        if (getRarity() == ItemRarity.UNIQUE || getRarity() == ItemRarity.RARE) {
            lineNum = Math.min(2, lines.size() - 1);
        }
        String name = sanitizeName(lines.get(lineNum));

        if (getRarityRaw().equals("Currency")) {
            return new CurrencyItem();
        }

        if (getRarityRaw().equals("Divination Card")) {
            return new DivinitationCardItem();
        }
        if (getRarityRaw().equals("Gem")) {
            return new GemItem();
        }

        if (name.matches("(^Sacrifice At |^Fragment of |^Mortal |^Offering to |'s Key$| Reliquary Key|Breachstone|Divine Vessel)")) {
            return new FragmentItem();
        }
        if (name.contains("Scarab")) {
            return new ScarabItem();
        }
        // TODO: Fossils?

        if (name.matches("(.*)(Belt|Stygian Vise|Rustic Sash)")) {
            return new EquipmentItem(EquipmentSlot.BELT);
        }
        if (name.matches("(.*)(Amulet|Talisman)") && !(name.contains("Leaguestone"))) {
            return new EquipmentItem(EquipmentSlot.AMULET);
        }
        if (name.contains("Ring")) {
            return new EquipmentItem(EquipmentSlot.RING);
        }
        if (name.contains("Quiver")) {
            return new EquipmentItem(EquipmentSlot.QUIVER);
        }
        if (name.contains("Flask")) {
            return new EquipmentItem(EquipmentSlot.FLASK);
        }
        if (name.contains("Map")) {
            return new MapItem();
        }
        if (name.matches("(.*)(Cobalt|Crimson|Viridian|Prismatic) Jewel")) {
            return new EquipmentItem(EquipmentSlot.JEWEL);
        }
        if (name.matches("(.*)(Murderous|Hypnotic|Searching|Ghastly) Eye Jewel")) {
            return new EquipmentItem(EquipmentSlot.ABYSS_JEWEL);
        }
        // TODO: Metamorph
        // TODO: Leaguestones

        if (name.matches("(.*)(Buckler|Bundle|Shield)")) {
            return new EquipmentItem(EquipmentSlot.SHIELD);
        }

        if (name.matches("(.*)(Gauntlets|Gloves|Mitts)")) {
            return new EquipmentItem(EquipmentSlot.GLOVES);
        }
        if (name.matches("(.*)(Boots|Greaves|Slippers)")) {
            return new EquipmentItem(EquipmentSlot.BOOTS);
        }
        if (name.matches("(.*)(Bascinet|Burgonet|Cage|Circlet|Crown|Hood|Helm|Helmet|Mask|Sallet|Tricorne|Iron Hat|Leather Cap|Rusted Coif|Wolf Pelt|Ursine Pelt|Lion Pelt)")) {
            return new EquipmentItem(EquipmentSlot.HELMET);
        }

        // Have to check this one after belt because of Leather Belt
        if (name.matches("(.*)(Armour|Brigandine|Chainmail|Coat|Doublet|Garb|Hauberk|Jacket|Lamellar|Leather|Plate|Raiment|Regalia|Ringmail|Robe|Tunic|Vest|Vestment|Chestplate|Full Dragonscale|Full Wyrmscale|Necromancer Silks|Shabby Jerkin|Silken Wrap)")) {
            return new EquipmentItem(EquipmentSlot.BODY_ARMOUR);
        }

        return new UnknownItem();
    }

    public String getNameWithoutAffixes(ArrayList<String> affixes) {
        String name = getBaseName();

        if (getRarity() == ItemRarity.MAGIC) {
            String[] parts = name.split(" of ");
            if ((parts.length > 1 && affixes.size() > 1) || (parts.length == 1 && affixes.size() == 1)) {
                return parts[0].substring(parts[0].indexOf(" ") + 1);
            }
            return parts[0];
        }

        return name;
    }
}