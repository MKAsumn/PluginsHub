package fr.sakori.adafreehub.listeners;

import fr.sakori.adafreehub.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;

public class LobbyListeners implements Listener {
    private final Main plugin;

    public LobbyListeners(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();

        if (!e.getPlayer().hasPlayedBefore()) {
            FileConfiguration config = plugin.getConfig();
            String worldName = config.getString("lobby.world");
            double x = config.getDouble("lobby.x");
            double y = config.getDouble("lobby.y");
            double z = config.getDouble("lobby.z");
            float yaw = (float) config.getDouble("lobby.yaw");
            float pitch = (float) config.getDouble("lobby.pitch");

            Location location = new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);

            if (location != null && location.getWorld() != null) {
                p.teleport(location);
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("lobby-arrival"))));
            } else {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("no-lobbypoint"))));
            }
        }
    }
}
