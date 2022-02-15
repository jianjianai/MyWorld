package cn.jja8.myWorld.all.basic.teamSupport;

import java.util.UUID;

public interface TeamManager {
    /**
     * 通过团队名称获取团队
     * @return null 没有叫做teamName的团队
     * */
    Team getTeam(String teamName);
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
    default void close(){};
}
