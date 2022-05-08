package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Status;
import cn.jja8.myWorld.all.basic.DatasheetSupport.Team;
import cn.jja8.myWorld.all.basic.DatasheetSupport.TeamPlayer;
import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.command.UserCommand;
import cn.jja8.myWorld.bukkit.command.tool.TeamsPlayerTool;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.command.CommandImplement;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AcceptInvitation implements CommandImplement {
    private final UserCommand userCommand;

    public AcceptInvitation(UserCommand userCommand) {
        this.userCommand = userCommand;
    }

    @Override
    public void command(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        Team 邀请团队 = userCommand.邀请map.get(player);
        if (邀请团队 == null) {
            player.sendMessage(ConfigBukkit.getLang().接受邀请_没被邀请);
            return;
        }
        TeamPlayer teamPlayer = TeamsPlayerTool.getTeamPlayerNotNull(player);
        Team 团队 = teamPlayer.getTeam();
        if (团队 != null) {
            player.sendMessage(ConfigBukkit.getLang().接受邀请_已经有团队.replaceAll("<团队>", 团队.getTeamName()));
            return;
        }
        teamPlayer.setTeam(邀请团队);
        teamPlayer.setStatus(Status.player);
        player.sendMessage(ConfigBukkit.getLang().接受邀请_接受成功.replaceAll("<团队>", 邀请团队.getTeamName()));
    }
}
