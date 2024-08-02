package mopsy.productions.techtools.networking;

import mopsy.productions.techtools.blocks.entity.ElectricityMeterEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class S2CPackets {
    public static void receivedPowerUsageUpdate(MinecraftClient client, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        BlockPos pos = packetByteBuf.readBlockPos();
        if(client.world==null)return;
        if (client.world.getBlockEntity(pos) instanceof ElectricityMeterEntity entity) {
            entity.storedValues[entity.pointerToCurrentValue] = packetByteBuf.readLong();
            entity.pointerToCurrentValue = entity.pointerToCurrentValue == 15 ? 0 : entity.pointerToCurrentValue + 1;


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
}
