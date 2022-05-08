package cn.jja8.myWorld.bukkit.command.tool;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Status;
import cn.jja8.myWorld.all.basic.DatasheetSupport.Team;
import cn.jja8.myWorld.all.basic.DatasheetSupport.TeamPlayer;
import cn.jja8.myWorld.bukkit.basic.Teams;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TeamsPlayerTool {
    /**
     * 判断这个玩家是否具有管理员权限
     * */
    public static boolean isAdmin(TeamPlayer teamPlayer){
        return teamPlayer.getStatus()== Status.admin|
                isLeader(teamPlayer);
    }
    /**
     * 判断这个玩家是否具有团长权限
     * */
    public static boolean isLeader(TeamPlayer teamPlayer){
        return teamPlayer.getStatus()==Status.leader;
    }
    /**
     * 获取Player对应的TeamPlayer，如果没有就添加一个
     * */
    public static TeamPlayer getTeamPlayerNotNull(Player player){
        TeamPlayer teamPlayer = Teams.datasheetManager.getTamePlayer(player.getUniqueId());
        if (teamPlayer==null){
            teamPlayer = Teams.datasheetManager.newTamePlayer(player.getUniqueId(),player.getName());
        }
        return teamPlayer;
    }
    /**
     * 获取指定团队的指定权限的玩家列表
     * @param status null代表不指定权限 也就是获取全部玩家
     * */
    public static List<TeamPlayer> getTeamPlayersNotNull(Team team, Status status) {
        List<TeamPlayer> playerList = team.getPlayers(status);
        if (playerList==null){
            playerList = new ArrayList<>();
        }
        return playerList;
    }
}
