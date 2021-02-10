package com.belmu.quakecraft.Core;

import com.belmu.quakecraft.Core.Map.Map;
import com.belmu.quakecraft.Core.Packets.Effects;
import com.belmu.quakecraft.Core.Packets.Title;
import com.belmu.quakecraft.Core.Railgun.Railgun;
import com.belmu.quakecraft.Core.Stats.KillStreaks;
import com.belmu.quakecraft.Core.Stats.StatsConfig;
import com.belmu.quakecraft.Quake;
import com.belmu.quakecraft.Utils.Countdown;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Belmu (https://github.com/BelmuTM/)
 */
public class GameState {

    /**
     * Game runnables, heart of this plugin.
     */
    public final Quake plugin;
    public GameState(Quake plugin) {
        this.plugin = plugin;
    }

    public java.util.Map<UUID, Integer> gameKills = new HashMap<>();
    public OfflinePlayer winner;

    private Railgun railgun;
    private double time;

    private double beforeStart;
    private double timer = GameOptions.timer;

    public boolean isStarting;
    public boolean running;

    private Countdown start;

    public void start(Railgun railgun, double time) {
        int railgunSlot = 0;
        Map map = plugin.gameMap;

        start = new Countdown(plugin,
                time,

                () -> {
                    this.railgun = railgun;
                    this.time = time;

                    isStarting = true;
                    running = false;

                    startCancelledCheck();
                },
                () -> {
                    isStarting = false;
                    running = true;

                    startTimer();
                    startGameChecks();

                    for(Player online : Bukkit.getOnlinePlayers()) {
                        gameKills.put(online.getUniqueId(), 0);

                        GameSound sound = GameSound.PLING;
                        online.playSound(online.getLocation(), sound.getSound(), 1.5f, sound.getPitch());

                        map.teleportPlayer(online);

                        online.getInventory().setItem(railgunSlot, railgun.getItemStack());
                        online.getInventory().setHeldItemSlot(railgunSlot);
                        online.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1), false);
                    }
                    Bukkit.broadcastMessage(Quake.prefix + "§eGame has started.§a Good luck!");
                },
                (t) -> {
                    beforeStart = t.getSecondsLeft();

                    if(beforeStart % 20 == 0 || beforeStart <= 10) {
                        Bukkit.broadcastMessage(Quake.prefix + "§eGame starts in §a" + (int) beforeStart + "s");

                        GameSound sound = GameSound.CLICK;
                        for(Player online : Bukkit.getOnlinePlayers())
                            online.playSound(online.getLocation(), sound.getSound(), 1.5f, sound.getPitch());
                    }
                }
        );
        start.scheduleTimer();
    }

    public void startTimer() {
        new BukkitRunnable() {

            @Override
            public void run() {
                if(running) timer--;
                /**
                 * If timer is geater than 0 and a player has reached enough kills to win.
                 */
                if(timer <= 0) {
                    /**
                     * If timer is lower than 0, then get the player that has the most kills.
                     */
                    OfflinePlayer player = Bukkit.getOfflinePlayer(maximumKey(gameKills));
                    if(player.isOnline()) winner = player;
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 20, 20);
    }

    public void stop() {
        StatsConfig config = plugin.statsConfig;
        Title title = new Title(plugin);

        for(Player online : Bukkit.getOnlinePlayers())
            online.getInventory().clear();

        if(winner != null) {
            List players = new ArrayList(sortedGameKills(false).keySet());

            StringBuilder win = new StringBuilder();
            String na = "§7N/A";
            if(!players.isEmpty()) {
                for (int i = 0; i < 3; i++) {
                    String format;

                    int n = i + 1;
                    String ordinal = ordinal(n);
                    String place = "";

                    if(n == 1) place = "§e§l" + ordinal + " Place";
                    if(n == 2) place = "§6§l" + ordinal + " Place";
                    if(n == 3) place = "§c§l" + ordinal + " Place";

                    if(i < players.size()) {
                        UUID uuid = UUID.fromString(players.get(i).toString());
                        OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
                        String pName = "§7§m" + p.getName() + "§r";

                        if(p.isOnline()) pName = "§7" + p.getName();

                        int kills = sortedGameKills(false).get(p.getUniqueId());
                        format = place + " " + pName + " §e- §a" + kills;
                    } else {
                        format = place + " " + na + " §e- " + na;
                    }
                    win.append("§r      ").append(format).append("\n");
                }
                String line = "§a§m                                                   ";
                Bukkit.broadcastMessage(line + "\n" + "§r                 §d§lQuakecraft§r" + "\n§r               §eWinner: §f" + winner.getName() + "\n§r \n" + win.toString().trim() + "\n§r \n" + line);
            }
            config.addWins(winner.getUniqueId(), 1);

            Effects effects = new Effects(plugin);
            effects.tempFireworks(winner.getPlayer(), (int) GameOptions.beforeKickAll);

            GameSound sound = GameSound.LEVEL_UP;
            for(Player online : Bukkit.getOnlinePlayers()) {

                online.playSound(online.getLocation(), sound.getSound(), 1.5f, sound.getPitch());
                title.winTitle(online, online == winner);
            }
        } else {
            GameSound sound = GameSound.CLICK;
            for(Player online : Bukkit.getOnlinePlayers()) {
                online.playSound(online.getLocation(), sound.getSound(), 1.5f, sound.getPitch());

                title.sendTitle(online, "Game", ChatColor.YELLOW, 0, 50, 0);
                title.sendSubTitle(online, "Ended", ChatColor.RED, 0, 50, 0);
            }
            Bukkit.broadcastMessage(Quake.prefix + "§e§lGame ended. §cNot enough players!");

            running = false;
            isStarting = false;
        }

        Countdown kickall = new Countdown(plugin,
                GameOptions.beforeKickAll,

                () -> {
                    gameKills.clear();
                    KillStreaks.killStreaks.clear();
                    Railgun.railguns.clear();
                },
                () -> {
                    for(Player online : Bukkit.getOnlinePlayers())
                        online.kickPlayer("§5§m                                " + "\n§6§lGame Over.\n§r §aThanks for playing!\n" + "§5§m                                ");

                    plugin.gameMap = null;

                    plugin.mapManager.chooseGameMap();

                    running = false;
                    isStarting = false;
                },
                (t) -> {}
        );
        kickall.scheduleTimer();
    }

    public void startGameChecks() {
        Map map = plugin.gameMap;

        new BukkitRunnable() {

            @Override
            public void run() {

                if(running && !isStarting) {

                    if(map != null) {
                        if (!map.isEnough()) {
                            winner = null;
                            stop();
                            this.cancel();
                        }
                    } else { stop(); this.cancel(); }

                    if(winner != null) {
                        stop();
                        this.cancel();
                    }
                } else this.cancel();
            }
        }.runTaskTimer(plugin, 20, 20);
    }

    public void startCancelledCheck() {
        Title title = new Title(plugin);

        new BukkitRunnable() {

            @Override
            public void run() {

                /**
                 * Cancels the timer if there aren't enough players waiting.
                 */
                if(isStarting) {
                    if(!plugin.gameMap.isEnough()) {
                        Bukkit.broadcastMessage(Quake.prefix + "§e§lStart cancelled. §r§cNot enough players!");

                        GameSound sound = GameSound.CLICK;
                        for (Player online : Bukkit.getOnlinePlayers()) {
                            title.sendTitle(online, "Start", ChatColor.YELLOW, 0, 75, 0);
                            title.sendSubTitle(online, "Cancelled", ChatColor.RED, 0, 75, 0);
                            online.playSound(online.getLocation(), sound.getSound(), 1.5f, sound.getPitch());
                        }
                        isStarting = false;
                        start.interrupt(true);
                        this.cancel();
                    }
                } else this.cancel();
            }
        }.runTaskTimer(plugin, 20, 20);
    }

    public int getKills(UUID uuid) {
        if(gameKills.containsKey(uuid)) {
            return gameKills.get(uuid);
        } return 0;
    }

    public void addKills(UUID uuid, int amount) {
        int kills = getKills(uuid);
        gameKills.put(uuid, kills + amount);
    }

    public double getBeforeStart() { return beforeStart; }
    public double getTimer() { return timer; }

    public String getFormattedBeforeStart() {
        return new SimpleDateFormat("mm:ss").format(beforeStart * 1000);
    }
    public String getFormattedTimer() {
        return new SimpleDateFormat("mm:ss").format(timer * 1000);
    }

    public <K, V extends Comparable<V>> K maximumKey(java.util.Map<K, V> map) {
        Optional<java.util.Map.Entry<K, V>> maxEntry = map.entrySet()
                .stream()
                .max((java.util.Map.Entry<K, V> e1, java.util.Map.Entry<K, V> e2) -> e1.getValue()
                        .compareTo(e2.getValue())
                );

        return maxEntry.get().getKey();
    }

    public LinkedHashMap<UUID, Integer> sortedGameKills(boolean natural) {
        LinkedHashMap<UUID, Integer> sorted = new LinkedHashMap<>();

        if(natural) {
            gameKills.entrySet()
                    .stream()
                    .sorted(java.util.Map.Entry.comparingByValue(Comparator.naturalOrder()))
                    .forEachOrdered(x -> sorted.put(x.getKey(), x.getValue()));
        } else {
            gameKills.entrySet()
                    .stream()
                    .sorted(java.util.Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .forEachOrdered(x -> sorted.put(x.getKey(), x.getValue()));
        }
        return sorted;
    }

    public static String ordinal(int i) {
        String[] suffixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return i + "th";
            default:
                return i + suffixes[i % 10];

        }
    }

}
