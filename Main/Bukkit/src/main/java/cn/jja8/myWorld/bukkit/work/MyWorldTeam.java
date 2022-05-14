package cn.jja8.myWorld.bukkit.work;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Team;
import cn.jja8.myWorld.all.basic.DatasheetSupport.WorldGroup;

/**
 * 代表一个团队
 * */
public class MyWorldTeam {
    Team team;
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

}
