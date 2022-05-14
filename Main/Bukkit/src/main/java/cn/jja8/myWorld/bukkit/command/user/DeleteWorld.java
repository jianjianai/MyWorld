package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Team;
import cn.jja8.myWorld.all.basic.DatasheetSupport.TeamPlayer;
import cn.jja8.myWorld.all.basic.DatasheetSupport.WorldGroup;
import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.command.tool.TeamsPlayerTool;
import cn.jja8.myWorld.bukkit.work.error.NoWorldLocks;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.command.CommandImplement;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeleteWorld implements CommandImplement {
    @Override
    public void command(CommandSender commandSender, String[] strings) {

        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        if (strings.length < 1) {
            player.sendMessage(ConfigBukkit.getLang().删除世界_删除确认);
            return;
        }
        if (!"yes".equals(strings[0])) {
            player.sendMessage(ConfigBukkit.getLang().删除世界_删除确认);
            return;
        }
        TeamPlayer teamPlayer = TeamsPlayerTool.getTeamPlayerNotNull(player);
        Team 团队 = teamPlayer.getTeam();
        if (团队 == null) {
            player.sendMessage(ConfigBukkit.getLang().删除世界_玩家没有团队);
            return;
        }
        if (!TeamsPlayerTool.isLeader(teamPlayer)) {
            player.sendMessage(ConfigBukkit.getLang().删除世界_不是团长);
            return;
        }
        WorldGroup worldGroup = 团队.getWorldGroup();
        if (worldGroup == null) {
            player.sendMessage(ConfigBukkit.getLang().删除世界_世界不存在);
            return;
        }
        try {
            MyWorldBukkit.getPlayerWordMangaer().delPlayerWorlds(worldGroup);
        } catch (NoWorldLocks e) {
            player.sendMessage(ConfigBukkit.getLang().删除世界_世界被其他服务器加载);
        }
        player.sendMessage(ConfigBukkit.getLang().删除世界_删除成功);
    }
}
