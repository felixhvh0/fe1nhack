package me.alpha432.oyvey;

import com.google.common.eventbus.EventBus;
import me.alpha432.oyvey.manager.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OyVey implements ModInitializer, ClientModInitializer {
    public static final String NAME = "fe1nh4ck beta";
    public static final String VERSION = "1.8.5preview";

    public static float TIMER = 1f;
    public static final EventBus EVENT_BUS = new EventBus();

    public static final Logger LOGGER = LogManager.getLogger("fe1nh4ck");
    public static ServerManager serverManager;
    public static ColorManager colorManager;
    public static RotationManager rotationManager;
    public static PositionManager positionManager;
    public static HoleManager holeManager;
    public static EventManager eventManager;
    public static SpeedManager speedManager;
    public static CommandManager commandManager;
    public static FriendManager friendManager;
    public static ModuleManager moduleManager;
    public static ConfigManager configManager;


    @Override public void onInitialize() {
        eventManager = new EventManager();
        serverManager = new ServerManager();
        rotationManager = new RotationManager();
        positionManager = new PositionManager();
        friendManager = new FriendManager();
        colorManager = new ColorManager();
        commandManager = new CommandManager();
        moduleManager = new ModuleManager();
        speedManager = new SpeedManager();
        holeManager = new HoleManager();
    }


    @Override public void onInitializeClient() {
        eventManager.init();
        moduleManager.init();
        configManager = new ConfigManager();
        configManager.load();
        colorManager.init();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> configManager.save()));
    }
}