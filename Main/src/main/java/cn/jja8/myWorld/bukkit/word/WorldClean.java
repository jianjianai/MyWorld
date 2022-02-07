package cn.jja8.myWorld.bukkit.word;

import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于卸载不需要被继续加载的世界
 * */
public class WorldClean  implements Listener {
    List<PlayerWorlds> 空世界 = new ArrayList<>();
    List<PlayerWorlds> 过期空世界 = new ArrayList<>();
    public WorldClean(){
        MyWorldBukkit.getMyWorldBukkit().getServer().getPluginManager().registerEvents(this, MyWorldBukkit.getMyWorldBukkit());
        new BukkitRunnable(){
            @Override
            public void run() {
                //卸载世界
                List<PlayerWorlds> clean = 过期空世界;
                过期空世界 = 空世界;
                空世界 = new ArrayList<>();
                clean.forEach(playerWord -> MyWorldBukkit.getPlayerWordMangaer().unloadPlayerWorlds(playerWord,true));
                //扫描空世界
                List<PlayerWorlds> playerWorldsList = new ArrayList<>(MyWorldBukkit.getPlayerWordMangaer().nameMap.values());
                playerWorldsList.removeAll(空世界);
                playerWorldsList.removeAll(过期空世界);
                playerWorldsList.forEach(playerWord -> {
                    if (playerWord.getWorld()!=null){
                        if (playerWord.getWorld().getPlayers().size()>0){
                            return;
                        }
                    }
                    if (playerWord.getInfernalWorld()!=null){
                        if (playerWord.getInfernalWorld().getPlayers().size()>0){
                            return;
                        }
                    }
                    if (playerWord.getEndWorld()!=null){
                        if (playerWord.getEndWorld().getPlayers().size()>0){
                            return;
                        }
                    }
                    空世界.add(playerWord);
                });
            }
        }.runTaskTimer(MyWorldBukkit.getMyWorldBukkit(), MyWorldBukkit.getWorldConfig().无玩家世界最短卸载时间*20, MyWorldBukkit.getWorldConfig().无玩家世界最短卸载时间*20);
    }

    @EventHandler
    public void 玩家传送(PlayerTeleportEvent event){
        Location lo = event.getTo();
        if (lo==null){
            return;
        }
        World word = lo.getWorld();
        if (word==null){
            return;
        }
        PlayerWorlds wo = MyWorldBukkit.getPlayerWordMangaer().getBeLoadPlayerWorlds(word);
        if (wo==null){
            return;
        }
        空世界.remove(wo);
        过期空世界.remove(wo);
    }

}
