package fr.sakori.adafreehub.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class HubEvents implements Listener {
    private JavaPlugin plugin;
    public HashMap<String, Location> locations = new HashMap<>();
    public Set<UUID> teleportCooldown = new HashSet<>();
    public Set<UUID> teleportingPlayers = new HashSet<>();


    public HubEvents(JavaPlugin plugin) {
        this.plugin = plugin;
    }



    public JavaPlugin getPlugin() {
        return plugin;
    }

    public void teleportLobby(Player player) {
        if (locations.containsKey("Lobby")) {
            player.teleport(locations.get("Lobby"));
        } else {
            try {
                locations.put("Lobby", (Location) plugin.getConfig().get("Lobby"));
                player.teleport(locations.get("Lobby"));
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    public void setLobby(Player player) {
        plugin.getConfig().set("Lobby", player.getLocation());
        plugin.saveConfig();
        locations.put("Lobby", player.getLocation()); // Met à jour la carte des emplacements
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        teleportLobby(player);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (teleportingPlayers.contains(playerId)) {
            Location from = event.getFrom();
            Location to = event.getTo();

            int fromX = from.getBlockX();
            int fromY = from.getBlockY();
            int fromZ = from.getBlockZ();

            int toX = to.getBlockX();
            int toY = to.getBlockY();
            int toZ = to.getBlockZ();

            // Vérifie si les coordonnées du bloc ont changé
            if (fromX != toX || fromY != toY || fromZ != toZ) {
                cancelTeleportation(player);
            }
        }
    }

    private void cancelTeleportation(Player player) {
        teleportCooldown.remove(player.getUniqueId());
        teleportingPlayers.remove(player.getUniqueId());
        player.sendMessage("§cVotre téléportation a été annulée car vous vous êtes déplacé !");
    }
}
