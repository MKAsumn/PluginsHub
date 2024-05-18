package fr.sakori.adafreehub.commands;

import com.sun.istack.internal.NotNull;
import fr.sakori.adafreehub.Main;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class SetLobbySpawn implements CommandExecutor {

    private final Main plugin;

    public SetLobbySpawn(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (p.hasPermission("setlobby.setlobby")) {

                Location location = p.getLocation();

                plugin.getConfig().set("lobby.world", location.getWorld().getName());
                plugin.getConfig().set("lobby.x", location.getX());
                plugin.getConfig().set("lobby.y", location.getY());
                plugin.getConfig().set("lobby.z", location.getZ());
                plugin.getConfig().set("lobby.yaw", location.getYaw());
                plugin.getConfig().set("lobby.pitch", location.getPitch());

                plugin.saveConfig();

                p.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("lobby-set"))));

            } else {

                p.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("no-permission"))));
            }

        } else {

            sender.sendMessage(ChatColor.DARK_RED + "Vous devez etre un joueur pour utiliser cette commande !");
        }

        return true;
    }
}
