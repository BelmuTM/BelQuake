package com.belmu.quakecraft.Core.Packets.Scoreboard;

import com.belmu.quakecraft.Core.GameState;
import com.belmu.quakecraft.Quake;
import fr.minuskube.netherboard.Netherboard;
import fr.minuskube.netherboard.bukkit.BPlayerBoard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.*;

/**
 * @author Belmu (https://github.com/BelmuTM/)
 */
public class GameScoreboard {

    public final Quake plugin;
    public final GameState state;
    public GameScoreboard(Quake plugin) {
        this.plugin = plugin;
        this.state = plugin.gameState;
    }

    public Map<Player, BPlayerBoard> scoreBoards = new HashMap<>();

    int titleFrame = 0;
    String frame_0 = "§5§lQuakecraft";
    String frame_1 = "§d§lQuakecraft";

    public void createScoreboard(Player player, String name, Scoreboard scoreboard) {

        Netherboard nb = Netherboard.instance();
        BPlayerBoard sb;

        if(!nb.getBoards().isEmpty() || nb.getBoards() != null) {
            int boards = nb.getBoards().size();
            sb = nb.createBoard(player, scoreboard, "scoreboard_" + (boards + 1));
        } else sb = nb.createBoard(player, scoreboard, "scoreboard_0");

        sb.setName(name);
        createTeam(scoreboard, player.getName());

        scoreBoards.put(player, sb);
    }

    public void update(Player player) {
        com.belmu.quakecraft.Core.Map.Map map = plugin.gameMap;
        String mapName = "N/A";

        String line = "§7§m                        ";
        int maxPlayers = 0;

        if(plugin.gameMap != null) {
            mapName = map.getName();
            maxPlayers = map.getMaxPlayers(map.getName());
        }

        if(maxPlayers == 0) maxPlayers = Bukkit.getMaxPlayers();

        for (Map.Entry<Player, BPlayerBoard> scoreBoard : scoreBoards.entrySet()) {
            BPlayerBoard sb = scoreBoard.getValue();

            animateTitle(sb);

            if(!state.running) {
                sb.set(line, 6);
                sb.set("§fMap» §a" + mapName, 5);
                sb.set("§fPlayers» §7" + Bukkit.getOnlinePlayers().size() + "§8/§d" + maxPlayers, 4);

                if(map != null) {
                    if (!map.isEnough()) {
                        int needed = map.getMinPlayers(mapName) - Bukkit.getOnlinePlayers().size();

                        if(needed >= 0) {
                            sb.set("§e" + needed + " §fPlayer(s) needed", 2);
                            sb.set("§fto start.", 1);
                            sb.set("§0", 3);
                        }
                    } else {
                        if (state.isStarting) {
                            sb.set("§eStarting in", 2);
                            sb.set("§a" + state.getFormattedBeforeStart(), 1);
                            sb.set("§0", 3);
                        }
                    }
                }
            } else {
                sb.set("§8", 4);
                int size = 0;

                if(!state.sortedGameKills(true).isEmpty()) {
                    for(int i = 0; i < 6; i++) {

                        if(i < state.sortedGameKills(true).size()) {
                            List<UUID> killsList = new ArrayList<>(state.sortedGameKills(true).keySet());

                            UUID uuid = UUID.fromString(killsList.get(i).toString());
                            OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
                            String pName = "§7§m" + Bukkit.getOfflinePlayer(uuid).getName() + "§r";

                            if (p.isOnline()) pName = "§b" + p.getName();
                            if (p == player) pName = "§b§l" + p.getName() + "§r";

                            String a = pName + "§f: §a" + state.getKills(uuid);
                            String b = ChatColor.stripColor(a);

                            if (b.length() > 16) {
                                a = pName.substring(0, Math.min(pName.length(), 13)) + "§f: §a" + state.getKills(uuid);
                            }
                            sb.set(a, 2 + i);
                            size += 1;
                        }
                    }
                }
                sb.set(line, 6 + size);

                sb.set("§fMap» §a" + mapName, 5 + size);
                if(state.getTimer() >= 0) sb.set("§fTimer» §e" + state.getFormattedTimer(), 4 + size);

                sb.set("§1", 3 + size);
                sb.set("§0", 1);
            }
            sb.set(line + "§r ", 0);
        }
    }

    public void animateTitle(BPlayerBoard sb) {
        if(titleFrame == 0) sb.setName(frame_0);

        titleFrame += 1;
        if(titleFrame % 5 == 0) {
            sb.setName(frame_1);
            titleFrame = 0;
        }
    }

    public void createTeam(Scoreboard scoreboard, String teamName) {
        Team team;
        if(scoreboard.getTeam(teamName) == null) scoreboard.registerNewTeam(teamName);

        team = scoreboard.getTeam(teamName);

        team.setCanSeeFriendlyInvisibles(true);
        team.setNameTagVisibility(NameTagVisibility.ALWAYS);

        for(Player online : Bukkit.getOnlinePlayers()) if(!team.getEntries().contains(online.getName())) team.addEntry(online.getName());
    }

    public void addToTeams(Player player, String teamName) {
        GameScoreboard scoreboard = new GameScoreboard(plugin);
        BPlayerBoard playerBoard = scoreboard.scoreBoards.get(player);

        for(Player scoreboardPlayers : scoreboard.scoreBoards.keySet()) {

            if(playerBoard.getScoreboard() != null && playerBoard.getScoreboard().getTeam(teamName) != null &&
                    !playerBoard.getScoreboard().getTeam(teamName).getEntries().contains(scoreboardPlayers.getName()))

                playerBoard.getScoreboard().getTeam(teamName).addEntry(scoreboardPlayers.getName());
        }
    }

}
