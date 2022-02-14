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
     * 每个uuid都有对应的TeamPlayer
     * @return 不会返回null
     * */
    TeamPlayer getTamePlayer(UUID uuid);
    default void close(){};
}
