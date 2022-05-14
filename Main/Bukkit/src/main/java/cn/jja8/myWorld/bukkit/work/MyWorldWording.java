package cn.jja8.myWorld.bukkit.work;

import org.bukkit.World;

public class MyWorldWording {
    MyWorldWorldGrouping myWorldWorldGrouping;
    World world;
    String type;
    MyWorldWording(MyWorldWorldGrouping myWorldWorldGrouping, World world, String type) {
        this.myWorldWorldGrouping = myWorldWorldGrouping;
        this.world = world;
        this.type = type;
    }
    /**
     * 获得这个世界的信息
     * **/
    public MyWorldWordInform getMyWorldWordInform(){
        return new MyWorldWordInform(myWorldWorldGrouping.playerWorlds.getPlayerWordInform());
    }
}
