package cn.jja8.myWorld.all.basic.teamSupport;

import java.util.UUID;

public interface Team {
    String getTeamName();
    void setTeamName(String teamName);
    String getWorldName();
    void setWorldName(String teamName);

    UUID getUUID();
    void delete();
}
