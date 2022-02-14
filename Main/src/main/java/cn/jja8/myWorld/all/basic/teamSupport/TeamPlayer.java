package cn.jja8.myWorld.all.basic.teamSupport;

import java.util.UUID;

public interface TeamPlayer {
    UUID getPlayerUUID();
    Status getStatus();
    Team getTeam();
    void SetTeam(Team team);
    void setStatus(Status status);
}
