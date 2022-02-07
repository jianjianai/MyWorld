package cn.jja8.myWorld.bukkit.basic.teamSupport;
import org.bukkit.entity.Player;

public interface TeamManager {
    /**
     * 通过团队名称获取团队,如果没有返回null
     */
    Team getTeam(String name);

    /**
     * 通过玩家名称获取所在团队，如果没有返回null
     */
    Team getTeam(Player player);

    /**
     * 创建团队
     */
    Team createTeam(String teamName, Player leader) ;
    /**
     * 删除团队
     */
    boolean deleteTeam(String teamName);

    void close();
}
