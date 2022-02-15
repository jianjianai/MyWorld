package cn.jja8.myWorld.bukkit.basic.teamSupport;

import cn.jja8.myWorld.all.basic.teamSupport.TeamPlayer;
import org.bukkit.Bukkit;

import java.util.UUID;

public class TeamPlayer_userName implements TeamPlayer {
    String name;

    public TeamPlayer_userName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public UUID getUUID() {
        return Bukkit.getOfflinePlayer(name).getUniqueId();
    }

    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                '}';
    }
}
