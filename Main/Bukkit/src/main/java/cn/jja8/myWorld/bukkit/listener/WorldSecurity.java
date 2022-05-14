package cn.jja8.myWorld.bukkit.listener;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Team;
import cn.jja8.myWorld.all.basic.DatasheetSupport.TeamPlayer;
import cn.jja8.myWorld.all.basic.DatasheetSupport.WorldGroup;
import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.basic.Teams;
import cn.jja8.myWorld.bukkit.config.Lang;
import cn.jja8.myWorld.bukkit.config.Permission;
import cn.jja8.myWorld.bukkit.word.PlayerWorlds;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;


/**
 * 主要用于拒绝玩家破坏其他团队的世界
 */
public class WorldSecurity implements Listener {
    UUID uuid = UUID.randomUUID();
    Lang lang = ConfigBukkit.getLang();
    Permission permission = ConfigBukkit.getPermission();
    public WorldSecurity(){
        MyWorldBukkit.getMyWorldBukkit().getServer().getPluginManager().registerEvents(this, MyWorldBukkit.getMyWorldBukkit());
    }

    @EventHandler
    public void 玩家交互(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if (!isHasAuthority(player,player.getWorld())){
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,uuid, new TextComponent(lang.世界交互_无权限.replaceAll("<世界>",player.getWorld().getName())));
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void 玩家丢东西(PlayerDropItemEvent event){
        Player player = event.getPlayer();
        if (!isHasAuthority(player,player.getWorld())){
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,uuid, new TextComponent(lang.世界交互_无权限.replaceAll("<世界>",player.getWorld().getName())));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void 点击实体(PlayerInteractEntityEvent event){
        Player player = event.getPlayer();
        if (!isHasAuthority(player,player.getWorld())){
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,uuid, new TextComponent(lang.世界交互_无权限.replaceAll("<世界>",player.getWorld().getName())));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void 实体伤害(EntityDamageByEntityEvent event){
        Entity entity = event.getDamager();
        Entity entity1 = event.getEntity();
        if (entity instanceof Player){
            Player player = (Player) entity;
            if (!isHasAuthority(player,player.getWorld())){
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,uuid, new TextComponent(lang.世界交互_无权限.replaceAll("<世界>",player.getWorld().getName())));
                event.setCancelled(true);
            }
        }else if (entity1 instanceof Player){
            Player player = (Player) entity1;
            if (!isHasAuthority(player,player.getWorld())){
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,uuid, new TextComponent(lang.世界交互_无权限.replaceAll("<世界>",player.getWorld().getName())));
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void 实体捡起物品(EntityPickupItemEvent event){
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)){
            return;
        }
        Player player = (Player) entity;
        if (!isHasAuthority(player,player.getWorld())){
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,uuid, new TextComponent(lang.世界交互_无权限.replaceAll("<世界>",player.getWorld().getName())));
            event.setCancelled(true);
        }
    }



    /**
     * 判断一个玩家在某世界是否有权限,只有信任的玩家有权限。
     */
    private boolean isHasAuthority(Player player, World world){
        if (player.hasPermission(permission.管理员权限)){
            return true;
        }
        PlayerWorlds 世界 = MyWorldBukkit.getPlayerWordMangaer().getBeLoadPlayerWorlds(world);
        //如果玩家不在玩家的世界
        if (世界==null){
            return true;
        }
        //判断世界信任
        if (世界.getPlayerWordInform().isBeTrust(player.getName())){
            return true;
        }
        //判断玩家团队
        TeamPlayer teamPlayer = Teams.datasheetManager.getTamePlayer(player.getUniqueId());
        if (teamPlayer==null){
            return false;
        }
        Team team = teamPlayer.getTeam();
        if (team==null){
            return false;
        }
        WorldGroup worldGroup = team.getWorldGroup();
        if (worldGroup ==null){
            return false;
        }
        return 世界.getName().equals(worldGroup.getWorldGroupName());
    }
}
