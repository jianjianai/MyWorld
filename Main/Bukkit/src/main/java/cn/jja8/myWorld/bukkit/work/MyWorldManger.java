package cn.jja8.myWorld.bukkit.work;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Status;
import cn.jja8.myWorld.all.basic.DatasheetSupport.Team;
import cn.jja8.myWorld.all.basic.DatasheetSupport.TeamPlayer;
import cn.jja8.myWorld.all.basic.DatasheetSupport.WorldGroup;
import cn.jja8.myWorld.bukkit.basic.Teams;
import cn.jja8.myWorld.bukkit.work.error.PlayerHaveTeam;
import org.bukkit.entity.Player;

/**
 * 一个静态类
 * */
public class MyWorldManger {
    /**
     * 获取包装的玩家。
     * */
    public static MyWorldPlayer getMyWorldPlayer(Player player){
        TeamPlayer teamPlayer = Teams.datasheetManager.getTamePlayer(player.getUniqueId());
        if (teamPlayer==null){
            teamPlayer = Teams.datasheetManager.newTamePlayer(player.getUniqueId(),player.getName());
        }
        return new MyWorldPlayer(teamPlayer);
    }
    /**
     * 获取包装的团队
     * @return null 如果这个团队不存在
     * */
    public static MyWorldTeam getMyWorldTeam(String teamName){
        Team team = Teams.datasheetManager.getTeamFromTeamName(teamName);
        return team==null?null:new MyWorldTeam(team);
    }

    /**
     * 创建新的团队
     * */
    public static MyWorldTeam newMyWorldTeam(String teamName,MyWorldPlayer myWorldPlayer) throws PlayerHaveTeam {
        MyWorldTeam playerTeam = myWorldPlayer.getTeam();
        if (playerTeam!=null){
            throw new PlayerHaveTeam("团长已经拥有团队了。");
        }
        MyWorldTeam myWorldTeam = new MyWorldTeam(Teams.datasheetManager.newTeam(teamName));
        myWorldPlayer.setTeam(myWorldTeam);
        myWorldPlayer.setStatus(Status.leader);
        return myWorldTeam;
    }

    /**
     * 获取包装的世界
     * @return null 如果这个世界组不存在
     * */
    public static MyWorldWorldGroup getMyWorldWorldGroup(String worldGroupName){
        WorldGroup worldGroup = Teams.datasheetManager.getWorldGroupFromWorldsName(worldGroupName);
        return worldGroup==null?null:new MyWorldWorldGroup(worldGroup);
    }
}
