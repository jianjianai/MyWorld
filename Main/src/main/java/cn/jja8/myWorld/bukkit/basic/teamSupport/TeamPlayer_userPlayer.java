package cn.jja8.myWorld.bukkit.basic.teamSupport;

import cn.jja8.myWorld.all.basic.teamSupport.TeamPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamPlayer_userPlayer implements TeamPlayer {
    Player player;

    public TeamPlayer_userPlayer(Player player) {
        this.player = player;
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public UUID getUUID() {
        return player.getUniqueId();
    }

    @Override
    public String toString() {
        return "{" +
                "player=" + player.getName() +
                '}';
    }
}
