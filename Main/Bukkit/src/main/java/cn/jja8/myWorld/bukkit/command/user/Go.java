package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Team;
import cn.jja8.myWorld.all.basic.DatasheetSupport.Worlds;
import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.command.tool.TeamsPlayerTool;
import cn.jja8.myWorld.bukkit.word.PlayerWorlds;
import cn.jja8.myWorld.bukkit.word.error.NoAllWorldLocks;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.command.CommandImplement;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Go implements CommandImplement {
    @Override
    public void command(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        Team team = TeamsPlayerTool.getTeamPlayerNotNull(player).getTeam();
        if (team == null) {
            player.sendMessage(ConfigBukkit.getLang().返回世界_没有团队);
            return;
        }
        Worlds worlds = team.getWorlds();
        if (worlds == null) {
            player.sendMessage(ConfigBukkit.getLang().返回世界_团队没有世界);
            return;
        }
        PlayerWorlds playerWorlds = MyWorldBukkit.getPlayerWordMangaer().getBeLoadPlayerWorlds(worlds);
        if (playerWorlds != null) {
            playerWorlds.playerBack(player);
            player.sendMessage(ConfigBukkit.getLang().返回世界_传送成功);
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(MyWorldBukkit.getMyWorldBukkit(), () -> {
            PlayerWorlds world;
            try {
                world = MyWorldBukkit.getPlayerWordMangaer().loadPlayerWorlds(worlds);
            } catch (NoAllWorldLocks e) {
                player.sendMessage(ConfigBukkit.getLang().返回世界_世界被其他服务器加载);
                return;
            }
            Bukkit.getScheduler().runTask(MyWorldBukkit.getMyWorldBukkit(), () -> {
                world.playerBack(player);
                player.sendMessage(ConfigBukkit.getLang().返回世界_传送成功);
            });
        });
    }
}
