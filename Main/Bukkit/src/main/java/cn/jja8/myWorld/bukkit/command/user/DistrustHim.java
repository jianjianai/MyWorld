package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Status;
import cn.jja8.myWorld.all.basic.DatasheetSupport.Team;
import cn.jja8.myWorld.all.basic.DatasheetSupport.TeamPlayer;
import cn.jja8.myWorld.all.basic.DatasheetSupport.WorldGroup;
import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.command.tool.TeamsPlayerTool;
import cn.jja8.myWorld.bukkit.word.PlayerWorlds;
import cn.jja8.myWorld.bukkit.work.*;
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
        MyWorldPlayer myWorldPlayer = MyWorldManger.getPlayer(player);
        MyWorldTeam team = myWorldPlayer.getTeam();
        if (team == null) {
            player.sendMessage(ConfigBukkit.getLang().取消信任_你没有团队);
            return;
        }
        if (myWorldPlayer.getStatus().getLevel()> Status.admin.getLevel()) {
            player.sendMessage(ConfigBukkit.getLang().取消信任_权限不足);
            return;
        }
        MyWorldWorldGroup worldGroup = team.getWorldGroup();
        if (worldGroup == null) {
            player.sendMessage(ConfigBukkit.getLang().取消信任_世界不存在);
        }
        MyWorldWorldGrouping myWorldWorldGrouping = worldGroup.getLoaded();
        if (myWorldWorldGrouping == null) {
            player.sendMessage(ConfigBukkit.getLang().取消信任_世界未加载);
            return;
        }
        myWorldWorldGrouping.getMyWorldWordInform().delBeTrust(strings[0]);
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
        WorldGroup worldGroup = 团队.getWorldGroup();
        if (worldGroup == null) {
            return null;
        }
        PlayerWorlds 世界 = MyWorldBukkit.getPlayerWordMangaer().getBeLoadPlayerWorlds(worldGroup);
        if (世界 == null) {
            return null;
        }
        return 世界.getPlayerWordInform().beTrustList();
    }
}
