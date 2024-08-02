package mopsy.productions.techtools.registry;

import mopsy.productions.techtools.screens.electric_meter.ElectricityMeterScreen;
import mopsy.productions.techtools.screens.electric_meter.ElectricityMeterScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static mopsy.productions.techtools.TechTools.modid;

public class TTScreenHandlers {
    public static ExtendedScreenHandlerType<ElectricityMeterScreenHandler> electricityMeterScreenHandler;

    public static void regHandlers(){
         electricityMeterScreenHandler = regHandler("electricity_meter", ElectricityMeterScreenHandler::new);
    }
    public static void regScreens(){
        regScreen(electricityMeterScreenHandler, ElectricityMeterScreen::new);
    }
    private static <T extends ScreenHandler> ExtendedScreenHandlerType<T> regHandler(String id, ExtendedScreenHandlerType.ExtendedFactory<T> factory){
        ExtendedScreenHandlerType<T> screenHandlerType = new ExtendedScreenHandlerType<>(factory);
        Registry.register(Registry.SCREEN_HANDLER, Identifier.of(modid,id),screenHandlerType);
        return screenHandlerType;
    }
    private static <M extends ScreenHandler, U extends Screen & ScreenHandlerProvider<M>> void regScreen(ScreenHandlerType<? extends M> type, HandledScreens.Provider<M, U> provider){
        HandledScreens.register(type, provider);
    }
}
