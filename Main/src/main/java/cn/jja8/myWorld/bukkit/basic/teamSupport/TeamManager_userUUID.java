package cn.jja8.myWorld.all.basic.teamSupport;

import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.basic.teamSupport.TeamPlayer_userUUID;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeamManager_userUUID implements TeamManager {
    cn.jja8.myWorld.all.data.team.TeamManager teamManager ;
    public TeamManager_userUUID(){
        teamManager = new cn.jja8.myWorld.all.data.team.TeamManager(MyWorldBukkit.getFileConfig().团队数据库URL);
    }

    static class Team implements cn.jja8.myWorld.all.basic.teamSupport.Team {
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
         * 获取这个团队的团长
         *
         * @return null 团队已经被删除
         */
        @Override
        public TeamPlayer getLeader() {
            return new TeamPlayer_userUUID(UUID.fromString(team.getLeader()));
        }

        /**
         * 获取这个团队的管理员列表
         */
        @Override
        public List<TeamPlayer> admins() {
            ArrayList<TeamPlayer> arrayList = new ArrayList<>();
            for (String s : team.getAdminList()) {
                arrayList.add(new TeamPlayer_userUUID(UUID.fromString(s)));
            }
            return arrayList;
        }

        /**
         * 获取这个团队的成员列表
         */
        @Override
        public List<TeamPlayer> members() {
            ArrayList<TeamPlayer> arrayList = new ArrayList<>();
            for (String s : team.getMemberList()) {
                arrayList.add(new TeamPlayer_userUUID(UUID.fromString(s)));
            }
            return arrayList;
        }

        /**
         * 判断这个人是不是团队成员（团长，管理员，成员都是）
         */
        @Override
        public boolean isMember(TeamPlayer player) {
            return team.isMember(player.getUUID().toString());
        }

        /**
         * 判断这个人是不是团队管理（团长，管理员都是）
         */
        @Override
        public boolean isAdmin(TeamPlayer player) {
            return team.isAdmin(player.getUUID().toString());
        }

        /**
         * 判断这个人是不是团长
         */
        @Override
        public boolean isLeader(TeamPlayer player) {
            return team.isLeader(player.getUUID().toString());
        }

        /**
         * 添加成员
         */
        @Override
        public boolean addMember(TeamPlayer player) {
            return team.addMember(player.getUUID().toString());
        }

        /**
         * 添加管理
         */
        @Override
        public boolean addAdmin(TeamPlayer player) {
            return team.addAdmin(player.getUUID().toString());
        }

        /**
         * 删除成员
         *
         */
        @Override
        public boolean delMember(TeamPlayer player) {
            return team.delMember(player.getUUID().toString());
        }

        /**
         * 删除管理
         *
         */
        @Override
        public boolean delAdmin(TeamPlayer player) {
            return team.delAdmin(player.getUUID().toString());
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
        public int getTeamID() {
            return team.getTeamID();
        }

        @Override
        public boolean setLeader(TeamPlayer player) {
            return team.setLeader(player.getUUID().toString());
        }
    }
    /**
     * 通过团队名称获取团队,如果没有返回null
     */
    @Override
    public Team getTeam(String name) {
        cn.jja8.myWorld.all.data.team.Team s = teamManager.getTeamFromName(name);
        if (s!=null){
            return new Team(s);
        }
        return null;
    }

    /**
     * 通过玩家名称获取所在团队，如果没有返回null
     */
    @Override
    public Team getTeam(TeamPlayer player) {
        cn.jja8.myWorld.all.data.team.Team s = teamManager.getTeamFromPlayer(player.getUUID().toString());
        if (s!=null){
            return new Team(s);
        }
        return null;
    }

    /**
     * 创建团队
     */
    @Override
    public Team createTeam(String teamName, TeamPlayer leader) {
        return new Team(teamManager.newTeam(teamName, leader.getName()));
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
