package mopsy.productions.techtools.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

import static mopsy.productions.techtools.TechTools.modid;

public class TTNetwork {
    public static final Identifier POWER_USAGE_UPDATE = new Identifier(modid,"power_usage_update");

    public static void regC2SPackets(){

    }
    public static void regS2CPacket(){
        ClientPlayNetworking.registerGlobalReceiver(POWER_USAGE_UPDATE,S2CPackets::receivedPowerUsageUpdate);
    }
}
