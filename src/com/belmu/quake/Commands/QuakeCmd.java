package com.belmu.quake.Commands;

import com.belmu.quake.Core.Map.Map;
import com.belmu.quake.Core.Map.MapManager;
import com.belmu.quake.Core.Map.SpawnPoint;
import com.belmu.quake.Quake;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

/**
 * @author Belmu (https://github.com/BelmuTM/)
 */
public class QuakeCmd implements CommandExecutor {

    String wrongUsage = Quake.prefix + "§cWrong usage. Try §7</quake help>";
    String noPerm = Quake.prefix + "§cYou need to be operator to do that.";

    private final Quake plugin;
    public QuakeCmd(Quake plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecated")
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            MapManager mm = plugin.mapManager;
            FileConfiguration cfg = mm.getConfig();

            if(cmd.getName().equalsIgnoreCase("quake")) {

                if(args.length > 0) {

                    if (args[0].equalsIgnoreCase("map")) {
                        if(!player.isOp()) { player.sendMessage(noPerm); return false; }

                        if(args.length == 3) {
                            String name = args[2];

                            if(args[1].equalsIgnoreCase("add")) {

                                if(cfg.contains("Maps." + name)) {
                                    player.sendMessage(Quake.prefix + "§cThis map already exists.");
                                    return false;
                                }
                                new Map(name, plugin);
                                player.sendMessage(Quake.prefix + "§7Added §f'" + name + "'§7 map.");

                            } else if(args[1].equalsIgnoreCase("remove")) {

                                if(!cfg.contains("Maps." + name)) {
                                    player.sendMessage(Quake.prefix + "§cThis map doesn't exist.");
                                    return false;
                                }
                                Map map = mm.getMapByName(name);
                                map.removeMap();

                                player.sendMessage(Quake.prefix + "§7Removed §f'" + name + "'§7 map.");

                            } else if(args[1].equalsIgnoreCase("setspawn")) {

                                if(!cfg.contains("Maps." + name)) {
                                    player.sendMessage(Quake.prefix + "§cThis map doesn't exist.");
                                    return false;
                                }
                                Map map = mm.getMapByName(name);
                                Location loc = player.getLocation();

                                map.setMainSpawn(loc);

                                double x = loc.getX();
                                double y = loc.getY();
                                double z = loc.getZ();

                                player.sendMessage(Quake.prefix + "§7Successfully set spawn for §f'" + map.getName() + "'§7 at §8[X:§7" + Math.round(x) + " §8Y:§7" + Math.round(y) + " §8Z:§7" + Math.round(z) + "§8]§7.");
                            } else
                                player.sendMessage(wrongUsage);
                        } else if(args.length == 2) {

                            if(args[1].equalsIgnoreCase("list")) {

                                if(mm.getMaps().isEmpty()) {
                                    player.sendMessage(Quake.prefix + "§cThere are no saved maps in the config.");
                                    return false;
                                }

                                StringBuilder a = new StringBuilder();
                                for(Map map : mm.getMaps()) {

                                    a.append("§7").append(map.getName()).append("§f | ");
                                }
                                player.sendMessage(Quake.prefix + "§aSaved Maps:§7 " + a.toString().trim());

                            } else
                                player.sendMessage(wrongUsage);
                        } else if(args.length == 4) {
                            String name = args[2];

                            try {
                                int amount = Integer.parseInt(args[3]);

                                if(args[1].equalsIgnoreCase("setminplayers")) {

                                    if(!cfg.contains("Maps." + name)) {
                                        player.sendMessage(Quake.prefix + "§cThis map doesn't exist.");
                                        return false;
                                    }
                                    Map map = mm.getMapByName(name);
                                    map.setMinPlayers(amount);

                                    player.sendMessage(Quake.prefix + "§7Successfully set minimum players for §f'" + map.getName() + "'§7 to §a" + amount);

                                } else if(args[1].equalsIgnoreCase("setmaxplayers")) {

                                    if(!cfg.contains("Maps." + name)) {
                                        player.sendMessage(Quake.prefix + "§cThis map doesn't exist.");
                                        return false;
                                    }
                                    Map map = mm.getMapByName(name);
                                    map.setMaxPlayers(amount);

                                    player.sendMessage(Quake.prefix + "§7Successfully set maximum players for §f'" + map.getName() + "'§7 to §a" + amount);
                                } else
                                    player.sendMessage(wrongUsage);
                            } catch(NumberFormatException nfe) {
                                player.sendMessage(Quake.prefix + "§c'" + args[3] + "' isn't a valid amount!");
                            }
                        } else
                            player.sendMessage(wrongUsage);
                    } else if (args[0].equalsIgnoreCase("spawnpoint")) {
                        if(!player.isOp()) { player.sendMessage(noPerm); return false; }

                        String name = args[1];

                        if(!cfg.contains("Maps." + name)) {
                            player.sendMessage(Quake.prefix + "§cThis map doesn't exist.");
                            return false;
                        }
                        Map map = mm.getMapByName(name);

                        if(args.length == 3) {

                            if(args[2].equalsIgnoreCase("add")) {
                                String pointName = "spawn_" + (map.getSpawnPoints().size() + 1);

                                Location loc = player.getLocation();
                                new SpawnPoint(map, pointName, loc, plugin);

                                double x = loc.getX();
                                double y = loc.getY();
                                double z = loc.getZ();

                                player.sendMessage(Quake.prefix + "§7Added §a" + pointName + " §7to §f'" + map.getName() + "'§7 at §8[X:§7" + Math.round(x) + " §8Y:§7" + Math.round(y) + " §8Z:§7" + Math.round(z) + "§8]§7.");
                            }

                            else if(args[2].equalsIgnoreCase("list")) {

                                if(map.getSpawnPoints().isEmpty()) {
                                    player.sendMessage(Quake.prefix + "§7'" + map.getName() + "' §chas no spawn point.");
                                    return false;
                                }
                                StringBuilder a = new StringBuilder();

                                for(SpawnPoint spawnPoint : map.getSpawnPoints()) {
                                    a.append("§7").append(spawnPoint.getName()).append("§f | ");
                                }
                                player.sendMessage(Quake.prefix + "§aSaved Spawn Points:§7 " + a.toString().trim());
                            }

                            else if(args[2].equalsIgnoreCase("clear")) {

                                if(map.getSpawnPoints().isEmpty()) {
                                    player.sendMessage(Quake.prefix + "§7'" + map.getName() + "' §chas no spawn point.");
                                    return false;
                                }
                                for(SpawnPoint spawnPoint : map.getSpawnPoints()) spawnPoint.removeSpawnPoint();

                                player.sendMessage(Quake.prefix + "§7Cleared all spawn points on §f'" + map.getName() + "'§7.");
                            } else
                                player.sendMessage(wrongUsage);
                        }

                        else if(args.length == 4) {
                            String spawnPointName = args[3];

                            if(map.getSpawnPoints().isEmpty()) {
                                player.sendMessage(Quake.prefix + "§7'" + map.getName() + "' §chas no spawn point.");
                                return false;
                            }
                            SpawnPoint spawnPoint = mm.getSpawnPointByName(map, spawnPointName);

                            if(spawnPoint == null) {
                                player.sendMessage(Quake.prefix + "§cThis spawn point doesn't exist on §7'" + map.getName() + "'§c.");
                                return false;
                            }

                            if(args[2].equalsIgnoreCase("remove")) {

                                spawnPoint.removeSpawnPoint();
                                player.sendMessage(Quake.prefix + "§7Removed §a" + spawnPointName + " §7on §f'" + map.getName() + "'§7.");

                            } else if(args[2].equalsIgnoreCase("teleport")) {

                                player.teleport(spawnPoint.getLocation());
                                player.sendMessage(Quake.prefix + "§7Teleported §byou §7to §a" + spawnPointName + " §7on §f'" + map.getName() + "'§7.");
                            } else
                                player.sendMessage(wrongUsage);
                        } else
                            player.sendMessage(wrongUsage);
                    } else if (args[0].equalsIgnoreCase("help")) {

                        if(args.length == 1) {

                            player.sendMessage(helpMessage);
                            player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1f, Integer.MAX_VALUE);
                        } else
                            player.sendMessage(wrongUsage);
                    } else if(args[0].equalsIgnoreCase("leaderboard") || args[0].equalsIgnoreCase("lb")) {

                        LinkedHashMap<UUID, Integer> sortedKills = plugin.statsConfig.sortedKills();
                        StringBuilder leaderboard = new StringBuilder();

                        if(args.length == 1) {
                            String na = "§7N/A";
                            List players = new ArrayList(sortedKills.keySet());

                            if(!players.isEmpty()) {
                                for (int i = 0; i < 10; i++) {
                                    String format;
                                    int n = i + 1;

                                    if(i < players.size()) {
                                        UUID uuid = UUID.fromString(players.get(i).toString());

                                        OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
                                        int kills = sortedKills.get(p.getUniqueId());
                                        format = "§b#" + n + " §7" + p.getName() + " §e- §a" + kills;

                                        if (kills <= 0) format = "§b#" + n + " §7" + p.getName() + " §e- " + na;

                                    } else {
                                        format = "§b#" + n + " " + na + " §e- " + na;
                                    }
                                    leaderboard.append(format).append("\n");
                                }
                                String line = "§6§m                                  ";
                                player.sendMessage(line + "\n" + "§r      §e§l§nLeaderboard§r" + "\n \n§6Kills:\n" + leaderboard.toString().trim() + "\n" + line);

                            } else player.sendMessage(Quake.prefix + "§cThe leaderboard is empty.");
                        } else
                            player.sendMessage(wrongUsage);
                    } else
                        player.sendMessage(wrongUsage);
                } else
                    player.sendMessage(wrongUsage);
            }
        }
        return false;
    }

    String cmd = "§8/quake ";
    String startLine = "§d§m                  §r §dQuakecraft Help§r §d§m                 ";
    String endLine =   "§d§m                                                     ";
    String helpMessage =
            startLine + "\n"
            + "§a[-] §eMaps:" + "\n \n"
            + cmd + "§fmap add §7[name]" + "\n"
            + cmd + "§fmap remove §7[name]" + "\n"
            + cmd + "§fmap setspawn §7[name]" + "\n"
            + cmd + "§fmap setminplayers §7[name] [amount]" + "\n"
            + cmd + "§fmap setmaxplayers §7[name] [amount]" + "\n"
            + cmd + "§fmap list" + "\n \n"
            + "§a[-] §eSpawn Points:" + "\n \n"
            + cmd + "§fspawnpoint §7[name] §fadd" + "\n"
            + cmd + "§fspawnpoint §7[name] §fremove §7[id]" + "\n"
            + cmd + "§fspawnpoint §7[name] §fteleport §7[id]" + "\n"
            + cmd + "§fspawnpoint §7[name] §flist" + "\n \n"
            + "§a[-] §eLeaderboard:" + "\n \n"
            + cmd + "§fleaderboard|lb" + "\n \n"
            + "§7More documentation at:\n"
            + "§b§nhttps://github.com/BelmuTM/" + "\n" + endLine;
}
