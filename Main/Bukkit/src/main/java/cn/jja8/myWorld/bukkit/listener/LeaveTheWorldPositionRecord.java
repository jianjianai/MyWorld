package cn.jja8.myWorld.bukkit.listener;

import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.work.*;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * 用于记录玩家离开世界时的位置
 */
public class LeaveTheWorldPositionRecord implements Listener {


    public LeaveTheWorldPositionRecord() {
        MyWorldBukkit.getMyWorldBukkit().getServer().getPluginManager().registerEvents(this,MyWorldBukkit.getMyWorldBukkit());
    }

    @EventHandler
    public void 玩家传送(PlayerTeleportEvent event){
        MyWorldWorldGrouping form = MyWorldManger.getWorldGrouping(event.getFrom().getWorld());
        MyWorldWorldGrouping to = MyWorldManger.getWorldGrouping(event.getTo().getWorld());
        if (form==null){
            return;
        }
        if (form==to){
            return;
        }
        setPlayerLeaveLocation(event.getPlayer(), event.getFrom());
    }

    @EventHandler
    public void 玩家离开服务器(PlayerQuitEvent event){
        setPlayerLeaveLocation(event.getPlayer(),event.getPlayer().getLocation());
    }

    private static void setPlayerLeaveLocation(Player player, Location location){
        if (location.getWorld()==null){
            return;
        }
        MyWorldWorldGrouping myWorldWorldGrouping = MyWorldManger.getWorldGrouping(location.getWorld());
        if (myWorldWorldGrouping==null){
            return;
        }
        MyWorldPlayer myWorldPlayer = MyWorldManger.getPlayer(player);
        MyWorldTeam myWorldTeam = myWorldPlayer.getTeam();
        if (myWorldTeam==null){
            return;
        }
        MyWorldWorldGroup myWorldWorldGroup = myWorldTeam.getWorldGroup();
        if (myWorldWorldGroup==null){
            return;
        }
        MyWorldWorldGrouping playerMyWorldWorldGrouping = myWorldWorldGroup.getLoading();
        if (playerMyWorldWorldGrouping==null){
            return;
        }
        if (myWorldWorldGrouping!=playerMyWorldWorldGrouping){
            return;
        }
        myWorldWorldGrouping.getMyWorldWordInform().getPlayerLeaveLocation().setPlayerLeaveLocation(player,location);
    }
}
