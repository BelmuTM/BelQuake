package com.belmu.quake.Core.Packets.Scoreboard;

import com.belmu.quake.Quake;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

/**
 * @author Belmu (https://github.com/BelmuTM/)
 */
public class PlayerTeam {

    public final Quake plugin;
    public PlayerTeam(Quake plugin) {
        this.plugin = plugin;
    }

    public Team team;

    /*
    Puts all players in the quake team.
    If a player becomes invisible, their team
    will still be able to slightly see them.
    It's a visual effect used for the spawn
    protection in quakecraft.
     */
    public void addToTeam(Player player) {
        String teamName = "quake";

        org.bukkit.scoreboard.Scoreboard scoreboard = player.getScoreboard();
        team = scoreboard.getTeam(teamName) == null ? scoreboard.registerNewTeam(teamName) : scoreboard.getTeam(teamName);

        team.setCanSeeFriendlyInvisibles(true);
        team.setNameTagVisibility(NameTagVisibility.ALWAYS);

        team.addEntry(player.getName());
    }

}
