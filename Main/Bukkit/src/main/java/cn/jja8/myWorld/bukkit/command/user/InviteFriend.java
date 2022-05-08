package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Team;
import cn.jja8.myWorld.all.basic.DatasheetSupport.TeamPlayer;
import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.command.UserCommand;
import cn.jja8.myWorld.bukkit.command.tool.TeamsPlayerTool;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.command.CommandImplement;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InviteFriend implements CommandImplement {
    private final UserCommand userCommand;

    public InviteFriend(UserCommand userCommand) {
        this.userCommand = userCommand;
    }

    @Override
    public void command(CommandSender commandSender, String[] strings) {

        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        TeamPlayer teamPlayer = TeamsPlayerTool.getTeamPlayerNotNull(player);
        Team 团队 = teamPlayer.getTeam();
        if (团队 == null) {
            player.sendMessage(ConfigBukkit.getLang().邀请成员_玩家没有团队);
            return;
        }
        if (!TeamsPlayerTool.isAdmin(teamPlayer)) {
            player.sendMessage(ConfigBukkit.getLang().邀请成员_不是管理);
            return;
        }
        if (strings.length < 1) {
            player.sendMessage(ConfigBukkit.getLang().邀请成员_没有参数);
            return;
        }
        Player 被邀玩家 = Bukkit.getPlayer(strings[0]);
        if (被邀玩家 == null) {
            player.sendMessage(ConfigBukkit.getLang().邀请成员_玩家不在线.replaceAll("<不在线玩家>", strings[0]));
            return;
        }
        userCommand.邀请map.put(被邀玩家, 团队);
        被邀玩家.sendMessage(ConfigBukkit.getLang().邀请成员_被邀请信息.replaceAll("<玩家>", player.getName()).replaceAll("<团队>", 团队.getTeamName()));
        player.sendMessage(ConfigBukkit.getLang().邀请成员_邀请成功);
    }
}
