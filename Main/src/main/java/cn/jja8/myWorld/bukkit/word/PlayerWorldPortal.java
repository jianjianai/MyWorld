package cn.jja8.myWorld.bukkit.word;

import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.basic.Portal;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
            new HashMap<>(eee).forEach((entity, timerTimer) -> {
                if (timerTimer.上次调用时间+1000<System.currentTimeMillis()){
                    eee.remove(entity);
                }
            });
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


    @EventHandler
    public void 实体接触传送门(EntityPortalEnterEvent event){
        PlayerWorlds playerWorlds = MyWorldBukkit.getPlayerWordMangaer().getBeLoadPlayerWorlds(event.getLocation().getWorld());
        if (playerWorlds ==null){//不是玩家世界不管
            return;
        }
        Material bookType = event.getLocation().getBlock().getType();
        Entity entity = event.getEntity();
        Bukkit.getServer().getScheduler().runTask(MyWorldBukkit.getMyWorldBukkit(), () -> {
            if (bookType.equals(Material.END_PORTAL)){//末地门
                if (timer.p(entity)){
                    if(World.Environment.THE_END.equals(event.getLocation().getWorld().getEnvironment())){//在末地
                        World world = playerWorlds.getWorld();
                        if(world!=null){
                            Portal.portalTransmission.TpToWorld(entity, world);
                        }
                    }else {
                        World world = playerWorlds.getEndWorld();
                        if(world!=null){
                            Portal.portalTransmission.TpToWorld(entity, world);
                        }
                    }
                }
            }else if (bookType.equals(Material.NETHER_PORTAL)){//地狱门
                if (timer.p(entity)){
                    if(World.Environment.NETHER.equals(event.getLocation().getWorld().getEnvironment())){//在地狱
                        World world = playerWorlds.getWorld();
                        if(world!=null){
                            Portal.portalTransmission.TpToWorld(entity, world);
                        }
                    }else {
                        World world = playerWorlds.getInfernalWorld();
                        if(world!=null){
                            Portal.portalTransmission.TpToWorld(entity, world);
                        }
                    }
                }
            }
        });
    }


    @EventHandler
    public void 实体被传送门传送(EntityPortalEvent event){
        PlayerWorlds playerWorldsFrom = MyWorldBukkit.getPlayerWordMangaer().getBeLoadPlayerWorlds(event.getFrom().getWorld());
        if (playerWorldsFrom ==null){
            return;
        }
        PlayerWorlds playerWorldsTo = MyWorldBukkit.getPlayerWordMangaer().getBeLoadPlayerWorlds(Objects.requireNonNull(event.getTo()).getWorld());
        if (playerWorldsTo != playerWorldsFrom){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void 玩家被传送门传送(PlayerPortalEvent event){
        PlayerWorlds playerWorldsFrom = MyWorldBukkit.getPlayerWordMangaer().getBeLoadPlayerWorlds(event.getFrom().getWorld());
        if (playerWorldsFrom ==null){
           return;
        }
        PlayerWorlds playerWorldsTo = MyWorldBukkit.getPlayerWordMangaer().getBeLoadPlayerWorlds(Objects.requireNonNull(event.getTo()).getWorld());
        if (playerWorldsTo != playerWorldsFrom){
            event.setCancelled(true);
        }
    }
}
