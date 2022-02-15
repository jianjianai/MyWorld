package cn.jja8.myWorld.all.basic.teamSupport;

import java.util.List;
import java.util.UUID;

public interface Team {
    /**
     * 获取团队名称
     * */
    String getTeamName();
    /**
     * 设置团队名称
     * */
    void setTeamName(String teamName);
    /**
     * 获取团队的世界名称
     * */
    String getWorldName();
    /**
     * 设置团队的世界名称
     * */
    void setWorldName(String teamName);
    /**
     * 获取团队的uuid
     * */
    UUID getUUID();
    /**
     * 删除团队
     * */
    void delete();
    /**
     * 获取团队中的全部玩家
     * @param status 特定的Status的玩家。 null 不指定Status。
     * */
    List<TeamPlayer> getPlayers(Status status);
}
