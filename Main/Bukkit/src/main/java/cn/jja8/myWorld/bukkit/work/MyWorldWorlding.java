package cn.jja8.myWorld.bukkit.work;

import org.bukkit.World;

public class MyWorldWorlding {
    MyWorldWorldGrouping myWorldWorldGrouping;
    World world;
    String type;
    MyWorldWorlding(MyWorldWorldGrouping myWorldWorldGrouping, World world, String type) {
        this.myWorldWorldGrouping = myWorldWorldGrouping;
        this.world = world;
        this.type = type;
    }
}
