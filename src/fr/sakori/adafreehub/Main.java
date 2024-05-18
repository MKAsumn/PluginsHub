package fr.sakori.adafreehub;

import fr.sakori.adafreehub.commands.LobbyCommand;
import fr.sakori.adafreehub.events.HubEvents;
import fr.sakori.adafreehub.commands.SetLobbySpawn;
import fr.sakori.adafreehub.listeners.LobbyListeners;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public class Main extends JavaPlugin {
    private static Main singletonInstance;

    public static FileConfiguration config;
    ConsoleCommandSender clogger = this.getServer().getConsoleSender();

    public Main() {
        singletonInstance = this;
    }

    public static Main getInstance() {
        return singletonInstance;
    }

    @Override
    public void onEnable() {
        System.out.println("[Hub-AdaFree] c'est bien lance !");

        //config.yml
        config = this.getConfig();

        File configFile = new File(getDataFolder(), "config.yml");

        if(!configFile.exists()) {
            getConfig().options().copyDefaults();
            saveDefaultConfig();
            reloadConfig();
        }

        saveConfig();

        HubEvents hubEvents = new HubEvents(this);

        Bukkit.getPluginManager().registerEvents(hubEvents, this);

        //getCommand("lobby").setExecutor(new SetLobbySpawn(hubEvents));
        //getCommand("setLobby").setExecutor(new SetLobbySpawn(hubEvents));

        Objects.requireNonNull(getCommand("setlobby")).setExecutor(new SetLobbySpawn(this));
        Objects.requireNonNull(getCommand("lobby")).setExecutor(new LobbyCommand(this));

        getServer().getPluginManager().registerEvents(new LobbyListeners(this), this);


    }

    @Override
    public void onDisable() {
        System.out.println("[Hub-AdaFree] c'est bien arrete !");
    }
}
