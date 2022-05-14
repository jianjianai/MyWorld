package cn.jja8.myWorld.bukkit.work;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Status;
import cn.jja8.myWorld.all.basic.DatasheetSupport.Team;
import cn.jja8.myWorld.all.basic.DatasheetSupport.TeamPlayer;

/**
 * 代表一个玩家
 * */
public class MyWorldPlayer {
    TeamPlayer teamPlayer;
    /**
     * 通过teamPlayer创建一个MyWorldPlayer
     * */
    MyWorldPlayer(TeamPlayer teamPlayer) {
        this.teamPlayer = teamPlayer;
    }

    /**
     * 获取玩家所在的Team
     * */
    public MyWorldTeam getTeam(){
        Team team = teamPlayer.getTeam();
        if (team==null){
            return null;
        }
        return new MyWorldTeam(team);
    }

    /**
     * 所在玩家所在的Team
     * */
    public void setTeam(MyWorldTeam myWorldTeam) {
        if (myWorldTeam==null){
            teamPlayer.setTeam(null);
            teamPlayer.setStatus(Status.unKnow);
        }else {
            teamPlayer.setTeam(myWorldTeam.team);
            teamPlayer.setStatus(Status.player);
        }
    }

    /**
     * 设置玩家的级别
     * */
    public void setStatus(Status status) {
        if (teamPlayer.getTeam()!=null) {
            teamPlayer.setStatus(status);
        }
    }
    /**
     * 获取玩家的级别,级别越低权限越高。
     * */
    public Status getStatus(){
        return teamPlayer.getStatus();
    }

    /**
     * 获取玩家名称
     * */
    public String getName(){
        return teamPlayer.getName();
    }

    @Override
    public String toString() {
        return getName();
    }
}
