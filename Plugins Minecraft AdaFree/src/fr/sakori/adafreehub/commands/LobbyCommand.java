package fr.sakori.adafreehub.commands;

import com.sun.istack.internal.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Objects;

public class LobbyCommand implements CommandExecutor {
    public final class CompteARebours {
        private final Player joueur;
        private int tempsCompteARebours;
        private BukkitTask tache;

        public CompteARebours(final Player joueur, final int tempsCompteARebours) {
            this.joueur = joueur;
            this.tempsCompteARebours = tempsCompteARebours;
        }

        public void demarrer(final Runnable rappel) {
            this.tache = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                if (tempsCompteARebours <= 0) {
                    this.tache.cancel();
                    rappel.run();
                    return;
                }

                //joueur.sendMessage(ChatColor.YELLOW + "Téléportation dans " + tempsCompteARebours); //Si le joueur a un compte à rebours ou pas.
                --this.tempsCompteARebours;
            }, 0L, 20L);
        }
    }

    private final Plugin plugin;
    private final HashMap<String, Long> cooldowns = new HashMap<>();

    public LobbyCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Seuls les joueurs peuvent utiliser cette commande !");
            return true;
        }

        Player joueur = (Player) sender;

        if (!joueur.hasPermission("setlobby.lobby")) {
            joueur.sendMessage(ChatColor.RED + "Vous n'avez pas la permission d'utiliser cette commande !");
            return true;
        }

        FileConfiguration config = plugin.getConfig();
        String nomMonde = config.getString("lobby.world");
        double x = config.getDouble("lobby.x");
        double y = config.getDouble("lobby.y");
        double z = config.getDouble("lobby.z");
        float yaw = (float) config.getDouble("lobby.yaw");
        float pitch = (float) config.getDouble("lobby.pitch");

        Location lieu = new Location(Bukkit.getWorld(nomMonde), x, y, z, yaw, pitch);

        if (lieu == null || lieu.getWorld() == null) {
            joueur.sendMessage(ChatColor.RED + "Le point de spawn n'a pas été correctement défini !");
            return true;
        }

        int tempsRecharge = config.getInt("cooldown-time");

        if (cooldowns.containsKey(joueur.getName())) {
            long secondesRestantes = ((cooldowns.get(joueur.getName()) / 1000 + tempsRecharge) - System.currentTimeMillis() / 1000);
            if (secondesRestantes > 0) {
                joueur.sendMessage(ChatColor.RED + "Vous devez attendre " + secondesRestantes + " secondes avant de pouvoir utiliser cette commande à nouveau !");
                return true;
            }
        }

        cooldowns.put(joueur.getName(), System.currentTimeMillis());

        int tempsCompteARebours = config.getInt("countdown-time");

        joueur.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("lobby-countdown"))));

        new CompteARebours(joueur, tempsCompteARebours).demarrer(() -> {
            if (joueur.isOnline()) {
                joueur.teleport(lieu);
                joueur.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("lobby-arrival"))));
            }
        });

        return true;
    }
}
