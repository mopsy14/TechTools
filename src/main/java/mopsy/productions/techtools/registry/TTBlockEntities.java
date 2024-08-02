package mopsy.productions.techtools.registry;

import mopsy.productions.techtools.blocks.entity.ElectricityMeterEntity;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import team.reborn.energy.api.EnergyStorage;

import static mopsy.productions.techtools.TechTools.CREATIVE_TAB;
import static mopsy.productions.techtools.TechTools.modid;

public class TTBlockEntities {
    private static final Item.Settings def = new Item.Settings().group(CREATIVE_TAB);

    public static BlockEntityType<ElectricityMeterEntity> electricityMeter;

    public static void regBlockEntities(){
        electricityMeter = reg("electricity_meter", ElectricityMeterEntity::new, TTBlocks.electricityMeter.getBlock());
        EnergyStorage.SIDED.registerForBlockEntity(ElectricityMeterEntity::getEnergyStorageFromDirection, electricityMeter);

    }
    private static <T extends BlockEntity> BlockEntityType<T> reg(String id, BlockEntityType.BlockEntityFactory<T> entity, Block... blocks){
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, Identifier.of(modid,id), BlockEntityType.Builder.create(entity,blocks).build(null));
    }
}
