package cn.jja8.myWorld.bukkit.word;

import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.basic.Portal;
import cn.jja8.myWorld.bukkit.word.name.PlayerWorldTypeAtName;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;

/**
 * 玩家世界的三个世界的传送门处理
 * */
public class PlayerWorldPortal implements Listener {
    public PlayerWorldPortal() {
        MyWorldBukkit.getMyWorldBukkit().getServer().getPluginManager().registerEvents(this, MyWorldBukkit.getMyWorldBukkit());
    }

    @EventHandler
    public void 实体被传送门传送(EntityPortalEvent event){
        PlayerWorlds playerWorldsFrom = MyWorldBukkit.getPlayerWordMangaer().getBeLoadPlayerWorlds(event.getFrom().getWorld());
        if (playerWorldsFrom ==null){
            return;
        }
        Location to = event.getTo();
        if (to!=null){
            PlayerWorlds playerWorldsTo = MyWorldBukkit.getPlayerWordMangaer().getBeLoadPlayerWorlds(to.getWorld());
            if (playerWorldsTo == playerWorldsFrom){
                return;
            }
        }
        event.setCancelled(true);
        Portal.portalTransmission.send(
                event.getEntity(),
                event.getFrom().getBlock(),
                playerWorldsFrom.getWorld(PlayerWorldTypeAtName.world),
                playerWorldsFrom.getWorld(PlayerWorldTypeAtName.infernal),
                playerWorldsFrom.getWorld(PlayerWorldTypeAtName.end)
        );
    }

    @EventHandler
    public void 玩家被传送门传送(PlayerPortalEvent event){
        PlayerWorlds playerWorldsFrom = MyWorldBukkit.getPlayerWordMangaer().getBeLoadPlayerWorlds(event.getFrom().getWorld());
        if (playerWorldsFrom ==null){
            return;
        }
        Location to = event.getTo();
        if (to!=null){
            PlayerWorlds playerWorldsTo = MyWorldBukkit.getPlayerWordMangaer().getBeLoadPlayerWorlds(to.getWorld());
            if (playerWorldsTo == playerWorldsFrom){
                return;
            }
        }
        event.setCancelled(true);
        Portal.portalTransmission.send(
                event.getPlayer(),
                event.getFrom().getBlock(),
                playerWorldsFrom.getWorld(PlayerWorldTypeAtName.world),
                playerWorldsFrom.getWorld(PlayerWorldTypeAtName.infernal),
                playerWorldsFrom.getWorld(PlayerWorldTypeAtName.end)
        );
    }
}
