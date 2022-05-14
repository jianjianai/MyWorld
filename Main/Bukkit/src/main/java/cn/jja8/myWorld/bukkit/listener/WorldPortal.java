package cn.jja8.myWorld.bukkit.listener;

import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.basic.Portal;
import cn.jja8.myWorld.bukkit.work.name.PlayerWorldTypeAtName;
import cn.jja8.myWorld.bukkit.work.MyWorldManger;
import cn.jja8.myWorld.bukkit.work.MyWorldWorldGrouping;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;

/**
 * 玩家世界的三个世界的传送门处理
 * */
public class WorldPortal implements Listener {
    public WorldPortal() {
        MyWorldBukkit.getMyWorldBukkit().getServer().getPluginManager().registerEvents(this, MyWorldBukkit.getMyWorldBukkit());
    }

    @EventHandler
    public void 实体被传送门传送(EntityPortalEvent event){
        MyWorldWorldGrouping playerWorldsFrom = MyWorldManger.getWorldGrouping(event.getFrom().getWorld());
        if (playerWorldsFrom ==null){
            return;
        }
        Location to = event.getTo();
        if (to!=null){
            MyWorldWorldGrouping playerWorldsTo = MyWorldManger.getWorldGrouping(to.getWorld());
            if (playerWorldsTo == playerWorldsFrom){
                return;
            }
        }
        event.setCancelled(true);
        Portal.portalTransmission.send(
                event.getEntity(),
                event.getFrom().getBlock(),
                playerWorldsFrom.getMyWorldWording(PlayerWorldTypeAtName.world.toString()).getWorld(),
                playerWorldsFrom.getMyWorldWording(PlayerWorldTypeAtName.infernal.toString()).getWorld(),
                playerWorldsFrom.getMyWorldWording(PlayerWorldTypeAtName.end.toString()).getWorld()
        );
    }

    @EventHandler
    public void 玩家被传送门传送(PlayerPortalEvent event){
        MyWorldWorldGrouping playerWorldsFrom = MyWorldManger.getWorldGrouping(event.getFrom().getWorld());
        if (playerWorldsFrom ==null){
            return;
        }
        Location to = event.getTo();
        if (to!=null){
            MyWorldWorldGrouping playerWorldsTo = MyWorldManger.getWorldGrouping(to.getWorld());
            if (playerWorldsTo == playerWorldsFrom){
                return;
            }
        }
        event.setCancelled(true);
        Portal.portalTransmission.send(
                event.getPlayer(),
                event.getFrom().getBlock(),
                playerWorldsFrom.getMyWorldWording(PlayerWorldTypeAtName.world.toString()).getWorld(),
                playerWorldsFrom.getMyWorldWording(PlayerWorldTypeAtName.infernal.toString()).getWorld(),
                playerWorldsFrom.getMyWorldWording(PlayerWorldTypeAtName.end.toString()).getWorld()
        );
    }
}
