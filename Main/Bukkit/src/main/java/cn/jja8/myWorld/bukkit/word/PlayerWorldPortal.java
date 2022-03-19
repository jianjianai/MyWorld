package cn.jja8.myWorld.bukkit.word;

import cn.jja8.myWorld.bukkit.MyWorldBukkit;
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
        重新写
    }

    @EventHandler
    public void 玩家被传送门传送(PlayerPortalEvent event){
        重新写
    }
}
