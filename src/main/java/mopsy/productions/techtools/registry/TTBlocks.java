package mopsy.productions.techtools.registry;

import mopsy.productions.techtools.blocks.block.ElectricityMeterBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static mopsy.productions.techtools.TechTools.CREATIVE_TAB;
import static mopsy.productions.techtools.TechTools.modid;

public class TTBlocks {
    private static final Item.Settings defItem = new Item.Settings().group(CREATIVE_TAB);
    public static BlockItemPair electricityMeter;

    public static void regBlocks(){
        electricityMeter = reg("electricity_meter", new ElectricityMeterBlock());
    }
    private static BlockItemPair reg(String id, Block block){
        Block regBlock = Registry.register(Registry.BLOCK,new Identifier(modid,id),block);
        BlockItem regBlockItem = Registry.register(Registry.ITEM, new Identifier(modid,id),new BlockItem(regBlock,defItem));
        return new BlockItemPair(block,regBlockItem);
    }


    public static class BlockItemPair{
        private final Block block;
        private final BlockItem item;
        BlockItemPair(Block block, BlockItem blockItem){
            this.block=block;
            this.item=blockItem;
        }
        public BlockItem getItem() {
            return item;
        }
        public Block getBlock() {
            return block;
        }
    }
}

