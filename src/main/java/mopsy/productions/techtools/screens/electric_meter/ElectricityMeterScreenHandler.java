package mopsy.productions.techtools.screens.electric_meter;

import mopsy.productions.techtools.blocks.entity.ElectricityMeterEntity;
import mopsy.productions.techtools.registry.TTScreenHandlers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;

public class ElectricityMeterScreenHandler extends ScreenHandler {
    protected final BlockPos blockPos;
    public ElectricityMeterScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf){
        //Create handler
        this(syncId, playerInventory, buf.readBlockPos());

        //Read previous meter data:
        if(playerInventory.player.world==null)return;
        if (playerInventory.player.world.getBlockEntity(blockPos) instanceof ElectricityMeterEntity entity){
            buf.readLongArray(entity.storedValues);
            entity.pointerToCurrentValue = buf.readInt();

            int i2 = entity.pointerToCurrentValue;
            for (int i = 0; i < entity.storedValues.length; i++) {
                entity.clientSortedValues[i] = entity.storedValues[i2];
                if (i2 < entity.storedValues.length - 1)
                    i2++;
                else
                    i2=0;
            }
        }
    }
    public ElectricityMeterScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos blockPos) {
        super(TTScreenHandlers.electricityMeterScreenHandler, syncId);
        this.blockPos = blockPos;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
