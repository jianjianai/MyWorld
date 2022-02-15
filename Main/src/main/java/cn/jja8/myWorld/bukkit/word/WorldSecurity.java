package cn.jja8.myWorld.bukkit.word;

import cn.jja8.myWorld.all.basic.teamSupport.TeamPlayer;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.all.basic.teamSupport.Team;
import cn.jja8.myWorld.bukkit.basic.Teams;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;


/**
 * 主要用于拒绝玩家破坏其他团队的世界
 */
public class WorldSecurity implements Listener {

    public WorldSecurity(){
        MyWorldBukkit.getMyWorldBukkit().getServer().getPluginManager().registerEvents(this, MyWorldBukkit.getMyWorldBukkit());
    }

    @EventHandler
    public void 玩家交互(PlayerInteractEvent event){
        if (!isHasAuthority(event.getPlayer(),event.getPlayer().getWorld())){
            event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(MyWorldBukkit.getLang().世界交互_无权限.replaceAll("<世界>",event.getPlayer().getWorld().getName())));
            event.setCancelled(true);
        }
    }


    /**
     * 判断一个玩家在某世界是否有权限,只有信任的玩家有权限。
     */
    private boolean isHasAuthority(Player player, World world){
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
        TeamPlayer teamPlayer = Teams.teamManager.getTamePlayer(player.getUniqueId());
        if (teamPlayer==null){
            return false;
        }
        Team team = teamPlayer.getTeam();
        if (team==null){
            return false;
        }
        return 世界.getName().equals(team.getWorldName());
    }
}
