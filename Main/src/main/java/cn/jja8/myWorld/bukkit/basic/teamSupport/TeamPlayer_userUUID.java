package cn.jja8.myWorld.bukkit.basic.teamSupport;

import cn.jja8.myWorld.all.basic.teamSupport.TeamPlayer;
import org.bukkit.Bukkit;

import java.util.UUID;

public class TeamPlayer_userUUID implements TeamPlayer {
    UUID uuid;

    public TeamPlayer_userUUID(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getName() {
        return Bukkit.getOfflinePlayer(uuid).getName();
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public String toString() {
        return "{" +
                "uuid=" + uuid +
                '}';
    }
}
