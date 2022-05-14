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
            teamPlayer.setStatus(null);
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
}
