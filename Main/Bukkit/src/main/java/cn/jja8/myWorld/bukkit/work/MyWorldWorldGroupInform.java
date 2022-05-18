package cn.jja8.myWorld.bukkit.work;

import cn.jja8.myWorld.all.basic.DatasheetSupport.WorldGroupData;
import cn.jja8.myWorld.bukkit.work.myWorldWorldGroupInform.PlayerLeaveLocation;
import cn.jja8.myWorld.bukkit.work.myWorldWorldGroupInform.Trust;
import cn.jja8.myWorld.bukkit.work.name.WorldsDataName;

/**
 * 代表一个世界组信息
 * */
public class MyWorldWorldGroupInform {
    Trust trust;
    PlayerLeaveLocation playerLeaveLocation;
    MyWorldWorldGroupInform(MyWorldWorldGroup myWorldWorldGroup) {
        WorldGroupData worldGroupData = myWorldWorldGroup.worldGroup.getWorldGroupData(WorldsDataName.playerWordInform.toString());
        if (worldGroupData ==null){
            worldGroupData = myWorldWorldGroup.worldGroup.newWorldGroupData(WorldsDataName.playerWordInform.toString());
        }
        this.trust = new Trust(worldGroupData);
        this.playerLeaveLocation = new PlayerLeaveLocation(myWorldWorldGroup.worldGroup);
    }

    public Trust getTrust() {
        return trust;
    }

    public PlayerLeaveLocation getPlayerLeaveLocation() {
        return playerLeaveLocation;
    }

    public void save() {
        trust.save();
    }
}
