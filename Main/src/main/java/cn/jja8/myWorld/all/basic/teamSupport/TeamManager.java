package cn.jja8.myWorld.all.basic.teamSupport;

import java.util.UUID;

public interface TeamManager {
    /**
     * 通过团队名称获取团队
     * @return null 没有叫做teamName的团队
     * */
    Team getTeamFromTeamName(String teamName);
    /**
     * 通过世界名称获取团队
     * @return null 没有叫做worldName的世界
     * */
    Team getTeamFromWorldName(String worldName);
    /**
     * 创建一个新团队
     * */
    Team newTeam(String teamName);
    /**
     * 通过玩家UUID获得TeamPlayer
     * @return null 这个玩家还不存在
     * */
    TeamPlayer getTamePlayer(UUID uuid);
    /**
     * 创建一个TeamPlayer
     * @return null 这个玩家已经存在
     * */
    TeamPlayer newTamePlayer(UUID uuid,String name);
    /**
     * 关闭teamManger，一般在插件关闭时调用。
     * */
    default void close(){}
}
