package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Team;
import cn.jja8.myWorld.all.basic.DatasheetSupport.TeamPlayer;
import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.command.tool.TeamsPlayerTool;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.command.CommandImplement;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DisbandOurTeam implements CommandImplement {
    @Override
    public void command(CommandSender commandSender, String[] strings) {

        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        TeamPlayer teamPlayer = TeamsPlayerTool.getTeamPlayerNotNull(player);
        Team 团队 = teamPlayer.getTeam();
        if (团队 == null) {
            player.sendMessage(ConfigBukkit.getLang().删除团队_玩家没有团队);
            return;
        }
        if (!TeamsPlayerTool.isLeader(teamPlayer)) {
            player.sendMessage(ConfigBukkit.getLang().删除团队_不是团长);
            return;
        }
        if (strings.length < 1) {
            player.sendMessage(ConfigBukkit.getLang().删除团队_删除确认);
            return;
        }
        if (ConfigBukkit.getTeamConfig().删除团队时必须先删除世界) {
            if (团队.getWorlds() != null) {
                player.sendMessage(ConfigBukkit.getLang().删除团队_没有删除世界);
                return;
            }
        }
        if (!"yes".equals(strings[0])) {
            player.sendMessage(ConfigBukkit.getLang().删除团队_删除确认);
            return;
        }
        团队.delete();
        player.sendMessage(ConfigBukkit.getLang().删除团队_删除成功);
    }
}
