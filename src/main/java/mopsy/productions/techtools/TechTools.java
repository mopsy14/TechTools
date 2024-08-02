package mopsy.productions.techtools;

import mopsy.productions.techtools.networking.TTNetwork;
import mopsy.productions.techtools.registry.TTBlockEntities;
import mopsy.productions.techtools.registry.TTBlocks;
import mopsy.productions.techtools.registry.TTItems;
import mopsy.productions.techtools.registry.TTScreenHandlers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TechTools implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("tech_tools");
	public static final String modid = "tech_tools";
	public static final ItemGroup CREATIVE_TAB = FabricItemGroupBuilder.build(
			new Identifier(modid, "tech_tools_items"), () -> new ItemStack(TTItems.testItem));

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing TechTools");

		TTItems.regItems();
		TTBlocks.regBlocks();
		TTBlockEntities.regBlockEntities();
		TTScreenHandlers.regHandlers();
		TTNetwork.regC2SPackets();



		LOGGER.info("TechTools started");
	}
}