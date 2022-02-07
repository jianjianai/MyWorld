package cn.jja8.myWorld.bungeecord.basic.teamSupport;


import net.md_5.bungee.api.connection.ProxiedPlayer;

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
     * 判断这个人是不是团队成员（团长，管理员，成员都是）
     */
    boolean isMember(ProxiedPlayer player) ;

    /**
     * 判断这个人是不是团队管理（团长，管理员都是）
     */
    boolean isAdmin(ProxiedPlayer player) ;

    /**
     * 判断这个人是不是团长
     */
    boolean isLeader(ProxiedPlayer player) ;

    /**
     * 添加成员
     */
    boolean addMember(ProxiedPlayer player);

    /**
     * 添加管理
     */
    boolean addAdmin(ProxiedPlayer player) ;

    /**
     * 删除成员
     */
    boolean delMember(ProxiedPlayer player);

    /**
     * 删除管理
     */
    boolean delAdmin(ProxiedPlayer player);

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
    boolean setLeader(ProxiedPlayer player);
}
