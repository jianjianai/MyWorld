package cn.jja8.myWorld.bukkit.work;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Team;
import cn.jja8.myWorld.all.basic.DatasheetSupport.TeamPlayer;
import cn.jja8.myWorld.bukkit.basic.Teams;
import org.bukkit.entity.Player;

/**
 * 代表一个玩家
 * */
public class MyWorldPlayer {
    TeamPlayer teamPlayer;
    /**
     * 通过Player创建一个MyWorldPlayer
     * */
    public MyWorldPlayer(Player player) {
         teamPlayer = Teams.datasheetManager.getTamePlayer(player.getUniqueId());
        if (teamPlayer==null){
            teamPlayer = Teams.datasheetManager.newTamePlayer(player.getUniqueId(),player.getName());
        }
    }
    /**
     * 通过teamPlayer创建一个MyWorldPlayer
     * */
    public MyWorldPlayer(TeamPlayer teamPlayer) {
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
}
