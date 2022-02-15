package cn.jja8.myWorld.bungeecord.basic.teamSupport;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public interface TeamManager {
    /**
     * 通过团队名称获取团队,如果没有返回null
     */
    Team getTeam(String name);

    /**
     * 通过玩家名称获取所在团队，如果没有返回null
     */
    Team getTeam(ProxiedPlayer player);

    /**
     * 创建团队
     */
    Team createTeam(String teamName, ProxiedPlayer leader) ;
    /**
     * 删除团队
     */
    boolean deleteTeam(String teamName);


    void close();
}
