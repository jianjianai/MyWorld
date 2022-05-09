package cn.jja8.myWorld.bukkit.work;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Team;
import cn.jja8.myWorld.all.basic.DatasheetSupport.Worlds;

/**
 * 代表一个团队
 * */
public class MyWorldTeam {
    Team team;
    /**
     * 使用现有的team
     * */
    public MyWorldTeam(Team team) {
        this.team = team;
    }

    /**
     * 创建一个新团队
     * */
    public MyWorldTeam(String name){
        ...
    }

    /**
     * 获得团队的世界组
     * */
    public MyWorldWorldGroup getWorldGroup(){
       Worlds worlds = team.getWorlds();
       if (worlds==null){
           return null;
       }
       return new MyWorldWorldGroup(worlds);
    }
}
