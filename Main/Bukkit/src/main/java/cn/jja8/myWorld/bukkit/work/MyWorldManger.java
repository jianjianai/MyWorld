package cn.jja8.myWorld.bukkit.work;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Team;
import cn.jja8.myWorld.all.basic.DatasheetSupport.TeamPlayer;
import cn.jja8.myWorld.all.basic.DatasheetSupport.WorldGroup;
import cn.jja8.myWorld.bukkit.basic.Teams;
import cn.jja8.myWorld.bukkit.work.error.MyWorldError;
import cn.jja8.myWorld.bukkit.work.error.TeamAlreadyExists;
import cn.jja8.myWorld.bukkit.work.error.WorldGroupAlreadyExists;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * 一个静态类
 * */
public class MyWorldManger {
    static Map<String,MyWorldWorldGrouping> groupName_myWorldWorldGrouping = new HashMap<>();
    static Map<World, MyWorldWorldGrouping> world_MyWorldWorldGrouping = new HashMap<>();
    static Map<String,MyWorldWorldLock> worldName_MyWorldWorldLock = new HashMap<>();
    static Map<String,MyWorldWorlding> worldName_MyWorldWorlding = new HashMap<>();
    /**
     * 关闭
     * */
    public static void close(){
        for (MyWorldWorldGrouping value : groupName_myWorldWorldGrouping.values()) {
            value.unLoad(true);
        }
        groupName_myWorldWorldGrouping = new HashMap<>();
        world_MyWorldWorldGrouping = new HashMap<>();
    }

    /**
     * 获取已经加载的世界组name map
     * */
    public static Map<String,MyWorldWorldGrouping> getLoadedWorldGrouping(){
        return new HashMap<>(groupName_myWorldWorldGrouping);
    }

    /**
     * 获取包装的玩家。
     * */
    public static MyWorldPlayer getPlayer(Player player){
        TeamPlayer teamPlayer = Teams.datasheetManager.getTamePlayer(player.getUniqueId());
        if (teamPlayer==null){
            teamPlayer = Teams.datasheetManager.newTamePlayer(player.getUniqueId(),player.getName());
        }
        if (!player.getName().equals(teamPlayer.getName())) {
            teamPlayer.setName(player.getName());
        }
        return new MyWorldPlayer(teamPlayer);
    }
    /**
     * 获取包装的团队
     * @return null 如果这个团队不存在
     * */
    public static MyWorldTeam getTeam(String teamName){
        Team team = Teams.datasheetManager.getTeamFromTeamName(teamName);
        return team==null?null:new MyWorldTeam(team);
    }

    /**
     * 创建新的团队
     * */
    public static MyWorldTeam newTeam(String teamName) {
        Team team = Teams.datasheetManager.getTeamFromTeamName(teamName);
        if (team!=null){
            throw new TeamAlreadyExists("团队已经存在");
        }
        MyWorldTeam myWorldTeam = new MyWorldTeam(Teams.datasheetManager.newTeam(teamName));
        return myWorldTeam;
    }

    /**
     * 获取包装的世界
     * @return null 如果这个世界组不存在
     * */
    public static MyWorldWorldGroup getWorldGroup(String worldGroupName){
        WorldGroup worldGroup = Teams.datasheetManager.getWorldGroupFromWorldsName(worldGroupName);
        return worldGroup==null?null:new MyWorldWorldGroup(worldGroup);
    }

    /**
     * 创建新的世界组
     * */
    public static MyWorldWorldGroup newWorldGroup(String worldGroupName){
        WorldGroup worldGroup = Teams.datasheetManager.getWorldGroupFromWorldsName(worldGroupName);
        if (worldGroup!=null){
            throw new WorldGroupAlreadyExists("世界组"+worldGroupName+"已经存在，不可以创建第二个。");
        }
        worldGroup = Teams.datasheetManager.newWorldGroup(worldGroupName);
        if (worldGroup==null){
            throw new MyWorldError("未知错误！");
        }
        return new MyWorldWorldGroup(worldGroup);
    }

    /**
     * 获取世界对应的世界组
     * */
    public static MyWorldWorldGrouping getWorldGrouping(World world){
        return world_MyWorldWorldGrouping.get(world);
    }
}
