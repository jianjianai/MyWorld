package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Team;
import cn.jja8.myWorld.all.basic.DatasheetSupport.TeamPlayer;
import cn.jja8.myWorld.all.basic.DatasheetSupport.Worlds;
import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.command.tool.TeamsPlayerTool;
import cn.jja8.myWorld.bukkit.word.PlayerWorlds;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.command.CommandImplement;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class DistrustHim implements CommandImplement {
    @Override
    public void command(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        if (strings.length < 1) {
            player.sendMessage(ConfigBukkit.getLang().取消信任_没有参数);
            return;
        }
        TeamPlayer teamPlayer = TeamsPlayerTool.getTeamPlayerNotNull(player);
        Team 团队 = teamPlayer.getTeam();
        if (团队 == null) {
            player.sendMessage(ConfigBukkit.getLang().取消信任_你没有团队);
            return;
        }
        if (!TeamsPlayerTool.isAdmin(teamPlayer)) {
            player.sendMessage(ConfigBukkit.getLang().取消信任_权限不足);
            return;
        }
        Worlds worlds = 团队.getWorlds();
        if (worlds == null) {
            player.sendMessage(ConfigBukkit.getLang().取消信任_世界不存在);
        }
        PlayerWorlds 世界 = MyWorldBukkit.getPlayerWordMangaer().getBeLoadPlayerWorlds(worlds);
        if (世界 == null) {
            player.sendMessage(ConfigBukkit.getLang().取消信任_世界未加载);
            return;
        }
        世界.getPlayerWordInform().delBeTrust(strings[0]);
        player.sendMessage(ConfigBukkit.getLang().取消信任_取消成功);
    }

    @Override
    public List<String> TabCompletion(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return null;
        Player player = (Player) commandSender;
        TeamPlayer teamPlayer = TeamsPlayerTool.getTeamPlayerNotNull(player);
        Team 团队 = teamPlayer.getTeam();
        if (团队 == null) {
            return null;
        }
        if (!TeamsPlayerTool.isLeader(teamPlayer)) {
            return null;
        }
        Worlds worlds = 团队.getWorlds();
        if (worlds == null) {
            return null;
        }
        PlayerWorlds 世界 = MyWorldBukkit.getPlayerWordMangaer().getBeLoadPlayerWorlds(worlds);
        if (世界 == null) {
            return null;
        }
        return 世界.getPlayerWordInform().BeTrustList();
    }
}
