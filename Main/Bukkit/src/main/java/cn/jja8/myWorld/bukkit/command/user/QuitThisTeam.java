package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Team;
import cn.jja8.myWorld.all.basic.DatasheetSupport.TeamPlayer;
import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.command.tool.TeamsPlayerTool;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.command.CommandImplement;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QuitThisTeam implements CommandImplement {
    @Override
    public void command(CommandSender commandSender, String[] strings) {

        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        TeamPlayer teamPlayer = TeamsPlayerTool.getTeamPlayerNotNull(player);
        Team 团队 = teamPlayer.getTeam();
        if (团队 == null) {
            player.sendMessage(ConfigBukkit.getLang().退出团队_你没有团队);
            return;
        }
        if (TeamsPlayerTool.isLeader(teamPlayer)) {
            player.sendMessage(ConfigBukkit.getLang().退出团队_团长不能退出);
            return;
        }
        if (strings.length < 1) {
            player.sendMessage(ConfigBukkit.getLang().退出团队_退出确认);
            return;
        }
        if (!"yes".equals(strings[0])) {
            player.sendMessage(ConfigBukkit.getLang().退出团队_退出确认);
            return;
        }
        teamPlayer.setTeam(null);
        teamPlayer.setStatus(null);
        player.sendMessage(ConfigBukkit.getLang().退出团队_退出成功);
    }
}
