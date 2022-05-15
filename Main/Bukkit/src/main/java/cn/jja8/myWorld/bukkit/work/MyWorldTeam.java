package cn.jja8.myWorld.bukkit.work;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Status;
import cn.jja8.myWorld.all.basic.DatasheetSupport.Team;
import cn.jja8.myWorld.all.basic.DatasheetSupport.TeamPlayer;
import cn.jja8.myWorld.all.basic.DatasheetSupport.WorldGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 代表一个团队
 * */
public class MyWorldTeam {
    final Team team;
    /**
     * 使用现有的team
     * */
    MyWorldTeam(Team team) {
        this.team = team;
    }

    /**
     * 解散这个团队
     * **/
    public void delete(){
        team.delete();
    }

    /**
     * 设置团队的世界组
     * */
    public void setWorldGroup(MyWorldWorldGroup myWorldWorldGroup){
        team.setWorlds(myWorldWorldGroup==null?null:myWorldWorldGroup.worldGroup);
    }

    /**
     * 获得团队的世界组
     * */
    public MyWorldWorldGroup getWorldGroup(){
       WorldGroup worldGroup = team.getWorldGroup();
       if (worldGroup ==null){
           return null;
       }
       return new MyWorldWorldGroup(worldGroup);
    }

    /**
     * 获取团队名称
     * */
    public String getTeamName() {
        return team.getTeamName();
    }

    /**
     * 获取指定团队的指定权限的玩家列表
     * @param status null代表不指定权限 也就是获取全部玩家
     * */
    public List<MyWorldPlayer> getTeamPlayers(Status status) {
        List<TeamPlayer> playerList = team.getPlayers(status);
        List<MyWorldPlayer> myWorldPlayerList = new ArrayList<>();
        if (playerList!=null){
            for (TeamPlayer teamPlayer : playerList) {
                myWorldPlayerList.add(new MyWorldPlayer(teamPlayer));
            }
        }
        return myWorldPlayerList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyWorldTeam that = (MyWorldTeam) o;
        return team.equals(that.team);
    }

    @Override
    public int hashCode() {
        return Objects.hash(team);
    }
}
