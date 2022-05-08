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

public class goBeginningPoint implements CommandImplement {
    @Override
    public void command(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        Team 团队 = TeamsPlayerTool.getTeamPlayerNotNull(player).getTeam();
        if (团队 == null) {
            player.sendMessage(ConfigBukkit.getLang().去出生点_没有团队);
            return;
        }
        Worlds worlds = 团队.getWorlds();
        if (worlds == null) {
            player.sendMessage(ConfigBukkit.getLang().去出生点_团队没有世界);
            return;
        }
        PlayerWorlds playerWorlds = MyWorldBukkit.getPlayerWordMangaer().getBeLoadPlayerWorlds(worlds);
        if (playerWorlds != null) {
            playerWorlds.playerBackSpawn(player);
            player.sendMessage(ConfigBukkit.getLang().去出生点_传送成功);
            return;
        }
        MyWorldBukkit.getPlayerDataManager().playerLoadFinishedToRun(player, () -> Bukkit.getScheduler().runTaskAsynchronously(MyWorldBukkit.getMyWorldBukkit(), () -> {
            PlayerWorlds world;
            try {
                world = MyWorldBukkit.getPlayerWordMangaer().loadPlayerWorlds(worlds);
            } catch (NoAllWorldLocks e) {
                player.sendMessage(ConfigBukkit.getLang().去出生点_世界被其他服务器加载);
                return;
            }
            Bukkit.getScheduler().runTask(MyWorldBukkit.getMyWorldBukkit(), () -> {
                world.playerBackSpawn(player);
                player.sendMessage(ConfigBukkit.getLang().去出生点_传送成功);
            });
        }));
    }
}
