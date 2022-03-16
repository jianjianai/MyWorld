package cn.jja8.myWorld.all.basic.DatasheetSupport;

import java.util.UUID;

public interface DatasheetManager {
    /**
     * 通过团队名称获取团队
     * @return null 没有叫做teamName的团队
     * */
    Team getTeamFromTeamName(String teamName);
    /**
     * 通过世界名称获取Worlds
     * */
    Worlds getWorldsFromWorldsName(String worldsName);
    /**
     * 通过玩家UUID获得TeamPlayer
     * @return null 这个玩家还不存在
     * */
    TeamPlayer getTamePlayer(UUID uuid);
    /**
     * 创建一个新团队
     * */
    Team newTeam(String teamName);
    /**
     * 创建一个TeamPlayer
     * @return null 这个玩家已经存在
     * */
    TeamPlayer newTamePlayer(UUID uuid,String name);
    /**
     * 创建一个worlds
     * @return null 这个世界已经存在
     * */
    Worlds newWorlds(String worldName);
    /**
     * 关闭teamManger，一般在插件关闭时调用。
     * */
    default void close(){}
}
