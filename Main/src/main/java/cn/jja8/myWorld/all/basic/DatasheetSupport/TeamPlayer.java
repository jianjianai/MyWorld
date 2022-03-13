package cn.jja8.myWorld.all.basic.DatasheetSupport;

import java.util.UUID;

public interface TeamPlayer {
    UUID getPlayerUUID();
    Status getStatus();
    /**
     * 获取玩家所在的团队
     * */
    Team getTeam();
    /**
     * 所在玩家所在的团队
     * */
    void SetTeam(Team team);
    /**
     * 设置玩家所在团队的Status
     * */
    void setStatus(Status status);
    /**
     * 获取玩家名称，名称仅用于显示，无其他用途，持久key建议使用getPlayerUUID()
     * */
    String getName();
    /**
     * 设置玩家名称
     * */
    void setName(String name);
    /**
     * 删除TeamPlayer
     * */
    void delete();

}
