package cn.jja8.myWorld.bukkit.word;

import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.basic.Portal;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * 玩家世界的三个世界的传送门处理
 * */
public class PlayerWorldPortal implements Listener {
    timer timer = new timer();
    public PlayerWorldPortal() {
        MyWorldBukkit.getMyWorldBukkit().getServer().getPluginManager().registerEvents(this, MyWorldBukkit.getMyWorldBukkit());
        MyWorldBukkit.getMyWorldBukkit().getServer().getPluginManager().registerEvents(timer, MyWorldBukkit.getMyWorldBukkit());
    }
    public static class timerTimer{
        public long 上次调用时间 = System.currentTimeMillis();
        public long 调用次数 = 0;
    }
    public static class timer implements Listener {
        Map<Entity,timerTimer> eee = new HashMap<>();

        @EventHandler
        public void 玩家离开服务器(PlayerQuitEvent event){
            Bukkit.getServer().getScheduler().runTaskAsynchronously(MyWorldBukkit.getMyWorldBukkit(), () -> new HashMap<>(eee).forEach((entity, timerTimer) -> {
                if (timerTimer.上次调用时间+1000<System.currentTimeMillis()){
                    eee.remove(entity);
                }
            }));
        }

        public boolean p(Entity entity) {
            timerTimer timer = eee.get(entity);
            if (timer==null){
                timer = new timerTimer();
                eee.put(entity,timer);
            }
            if (timer.上次调用时间+1000<System.currentTimeMillis()){//过期
                timer = new timerTimer();
                eee.put(entity,timer);
            }
            timer.上次调用时间 = System.currentTimeMillis();
            timer.调用次数++;
            return timer.调用次数==1;
        }
    }


//    @EventHandler
//    public void 实体接触传送门(EntityPortalEnterEvent event){
//        //send(event.getEntity(),event.getLocation().getBlock().getType(),event.getLocation().getWorld());
//
//    }


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
        if (timer.p(event.getEntity())){
            Portal.portalTransmission.send(
                    event.getEntity(),
                    event.getFrom().getBlock(),
                    playerWorldsFrom.getWorld(PlayerWorlds.WorldType.world),
                    playerWorldsFrom.getWorld(PlayerWorlds.WorldType.infernal),
                    playerWorldsFrom.getWorld(PlayerWorlds.WorldType.end)
            );
        }
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
        if (timer.p(event.getPlayer())){
            Portal.portalTransmission.send(
                    event.getPlayer(),
                    event.getFrom().getBlock(),
                    playerWorldsFrom.getWorld(PlayerWorlds.WorldType.world),
                    playerWorldsFrom.getWorld(PlayerWorlds.WorldType.infernal),
                    playerWorldsFrom.getWorld(PlayerWorlds.WorldType.end)
            );
        }
    }
}
