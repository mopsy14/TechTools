package mopsy.productions.techtools;

import mopsy.productions.techtools.networking.TTNetwork;
import mopsy.productions.techtools.registry.TTScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TechToolsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TTNetwork.regS2CPacket();
        TTScreenHandlers.regScreens();
    }
}
