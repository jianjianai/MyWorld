package cn.jja8.myWorld.bungeecord.basic.teamSupport;


import cn.jja8.myWorld.bungeecord.MyWorldBungeecord;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class TeamManager_userUUID implements TeamManager {
    cn.jja8.myWorld.all.data.team.TeamManager teamManager ;
    public TeamManager_userUUID(){
        teamManager = new cn.jja8.myWorld.all.data.team.TeamManager(MyWorldBungeecord.getFileConfig().团队数据库URL);
    }

    static class Team implements cn.jja8.myWorld.bungeecord.basic.teamSupport.Team {
        cn.jja8.myWorld.all.data.team.Team team;
        Team(cn.jja8.myWorld.all.data.team.Team team){
            this.team = team;
        }

        /**
         * 获取这个团队的世界名称。
         *
         * @return null 团队已经被删除
         */
        @Override
        public String getWorldName() {
            return team.getWorldName();
        }

        /**
         * 设置这个团队的世界名称。
         */
        @Override
        public boolean setWorldName(String name) {
            return team.setWorldName(name);
        }


        /**
         * 判断这个人是不是团队成员（团长，管理员，成员都是）
         */
        @Override
        public boolean isMember(ProxiedPlayer player) {
            return team.isMember(player.getUniqueId().toString());
        }

        /**
         * 判断这个人是不是团队管理（团长，管理员都是）
         */
        @Override
        public boolean isAdmin(ProxiedPlayer player) {
            return team.isAdmin(player.getUniqueId().toString());
        }

        /**
         * 判断这个人是不是团长
         */
        @Override
        public boolean isLeader(ProxiedPlayer player) {
            return team.isLeader(player.getUniqueId().toString());
        }

        /**
         * 添加成员
         */
        @Override
        public boolean addMember(ProxiedPlayer player) {
            return team.addMember(player.getUniqueId().toString());
        }


        /**
         * 添加管理
         */
        @Override
        public boolean addAdmin(ProxiedPlayer player) {
            return team.addAdmin(player.getUniqueId().toString());
        }

        /**
         * 删除成员
         */
        @Override
        public boolean delMember(ProxiedPlayer player) {
            return team.delMember(player.getUniqueId().toString());
        }

        /**
         * 删除管理
         */
        @Override
        public boolean delAdmin(ProxiedPlayer player) {
            return team.delAdmin(player.getUniqueId().toString());
        }

        @Override
        public int getTeamID() {
            return team.getTeamID();
        }

        /**
         * 获取团队名称
         *
         * @return null 团队已经被删除
         */
        @Override
        public String getTeamName() {
            return team.getTeamName();
        }

        /**
         * 设置团队名称
         */
        @Override
        public boolean setTeamName(String name) {
            return team.setTeamName(name);
        }

        @Override
        public boolean setLeader(ProxiedPlayer player) {
            return team.setLeader(player.getUniqueId().toString());
        }

    }
    /**
     * 通过团队名称获取团队,如果没有返回null
     */
    @Override
    public TeamManager_userName.Team getTeam(String name) {
        cn.jja8.myWorld.all.data.team.Team s = teamManager.getTeamFromName(name);
        if (s!=null){
            return new TeamManager_userName.Team(s);
        }
        return null;
    }

    /**
     * 通过玩家名称获取所在团队，如果没有返回null
     */
    @Override
    public TeamManager_userName.Team getTeam(ProxiedPlayer player) {
        cn.jja8.myWorld.all.data.team.Team s = teamManager.getTeamFromPlayer(player.getUniqueId().toString());
        if (s!=null){
            return new TeamManager_userName.Team(s);
        }
        return null;
    }

    /**
     * 创建团队
     */
    @Override
    public Team createTeam(String teamName, ProxiedPlayer leader) {
        cn.jja8.myWorld.all.data.team.Team team = teamManager.newTeam(teamName, leader.getUniqueId().toString());
        if (team!=null){
            return new Team(team);
        }
        return null;
    }

    /**
     * 删除团队
     */
    @Override
    public boolean deleteTeam(String teamName) {
        return teamManager.delTeam(teamName);
    }

    @Override
    public void close() {
        teamManager.close();
    }
}
