package cn.jja8.myWorld.bukkit.basic.teamSupport;


import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public interface Team {
    /**
     * 获取这个团队的世界名称。
     * @return null 团队已经被删除
     */
    String getWorldName() ;

    /**
     * 设置这个团队的世界名称。
     */
    boolean setWorldName(String name);

    /**
     * 获取这个团队的团长
     * @return null 团队已经被删除
     */
    OfflinePlayer getLeader() ;

    /**
     * 获取这个团队的管理员列表
     */
    List<OfflinePlayer> admins() ;

    /**
     * 获取这个团队的成员列表
     */
    List<OfflinePlayer> members() ;

    /**
     * 判断这个人是不是团队成员（团长，管理员，成员都是）
     */
    boolean isMember(Player player) ;

    /**
     * 判断这个人是不是团队管理（团长，管理员都是）
     */
    boolean isAdmin(Player player) ;

    /**
     * 判断这个人是不是团长
     */
    boolean isLeader(Player player) ;

    /**
     * 添加成员
     */
    boolean addMember(Player player);

    /**
     * 添加管理
     */
    boolean addAdmin(Player player) ;

    /**
     * 删除成员
     */
    boolean delMember(Player player);

    /**
     * 删除管理
     */
    boolean delAdmin(Player player);

    /**
     * 获取团队名称
     * @return null 团队已经被删除
     */
    String getTeamName() ;

    int getTeamID();

    /**
     * 设置团队名称
     */
    boolean setTeamName(String name);


    /**
     * 设置团长
     * */
    boolean setLeader(Player player);
}
