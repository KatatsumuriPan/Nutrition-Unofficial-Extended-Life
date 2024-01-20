package ca.wescook.nutrition;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import ca.wescook.nutrition.capabilities.CapabilityManager;
import ca.wescook.nutrition.events.EventEatFood;
import ca.wescook.nutrition.events.EventPlayerDeath;
import ca.wescook.nutrition.events.EventPlayerJoinWorld;
import ca.wescook.nutrition.events.EventRegistry;
import ca.wescook.nutrition.events.EventWorldTick;
import ca.wescook.nutrition.gui.ModGuiHandler;
import ca.wescook.nutrition.network.ModPacketHandler;
import ca.wescook.nutrition.potions.ModPotions;
import ca.wescook.nutrition.proxy.IProxy;
import ca.wescook.nutrition.utility.ChatCommand;
import ca.wescook.nutrition.utility.Config;
import ca.wescook.nutrition.utility.DataImporter;

@Mod(
     modid = Tags.MODID,
     version = Tags.VERSION,
     name = Tags.MODNAME,
     acceptedMinecraftVersions = "[1.12.2]")

public class Nutrition {

    // Create instance of mod
    @Mod.Instance
    public static Nutrition instance;

    // Create instance of proxy
    // This will vary depending on if the client or server is running
    @SidedProxy(
                clientSide = "ca.wescook.nutrition.proxy.ClientProxy",
                serverSide = "ca.wescook.nutrition.proxy.ServerProxy")
    public static IProxy proxy;

    // Events
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Config.registerConfigs(event.getModConfigurationDirectory()); // Load Config file
        ModPacketHandler.registerMessages(); // Register network messages
        CapabilityManager.register(); // Register capability

        ModPotions.createPotions(); // Register custom potions
        MinecraftForge.EVENT_BUS.register(new EventRegistry()); // Register custom potions
        MinecraftForge.EVENT_BUS.register(new EventPlayerJoinWorld()); // Attach capability to player
        MinecraftForge.EVENT_BUS.register(new EventPlayerDeath()); // Player death and warping
        MinecraftForge.EVENT_BUS.register(new EventEatFood()); // Register use item event
        MinecraftForge.EVENT_BUS.register(new EventWorldTick()); // Register update event for nutrition decay and potion
        // effects

        Nutrition.proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(Nutrition.instance, new ModGuiHandler()); // Register GUI handler

        Nutrition.proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        DataImporter.reload(); // Load nutrients and effects

        Nutrition.proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new ChatCommand());
    }
}
