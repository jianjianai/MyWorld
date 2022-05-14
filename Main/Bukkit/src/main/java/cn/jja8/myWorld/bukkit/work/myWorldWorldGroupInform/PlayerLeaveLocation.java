package cn.jja8.myWorld.bukkit.work.myWorldWorldGroupInform;

import cn.jja8.myWorld.all.basic.DatasheetSupport.WorldGroup;
import cn.jja8.myWorld.all.basic.DatasheetSupport.WorldGroupData;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.Data.String.LocationToString;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.nio.charset.StandardCharsets;

public class PlayerLeaveLocation {
    private final WorldGroup worldGroup;
    public PlayerLeaveLocation(WorldGroup worldGroup) {
        this.worldGroup = worldGroup;
    }

    public void setPlayerLeaveLocation(Player player, Location location) {
        String dataName = "location/"+player.getUniqueId();
        WorldGroupData worldGroupData = worldGroup.getWorldGroupData(dataName);
        if (worldGroupData ==null){
            worldGroupData = worldGroup.newWorldGroupData(dataName);
        }
        worldGroupData.setData(LocationToString.totring(location).getBytes(StandardCharsets.UTF_8));
    }
    /**
     * @return null 没有这个玩家的位置
     * */
    public Location getPlayerLocation(Player player) {
        String dataName = "location/"+player.getUniqueId();
        WorldGroupData worldGroupData = worldGroup.getWorldGroupData(dataName);
        if (worldGroupData ==null){
            return null;
        }
        return LocationToString.load(new String(worldGroupData.getData(),StandardCharsets.UTF_8));
    }
}
