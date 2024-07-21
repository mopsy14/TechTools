package mopsy.productions.techtools.registry;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static mopsy.productions.techtools.TechTools.CREATIVE_TAB;
import static mopsy.productions.techtools.TechTools.modid;

public class TTItems {
    private static final Item.Settings def = new Item.Settings().group(CREATIVE_TAB);

    public static Item testItem;

    public static void regItems(){
        testItem = reg("test_item", new Item(def));
    }
    private static Item reg(String id, Item item){
        return Registry.register(Registry.ITEM, Identifier.of(modid,id),item);
    }
}
