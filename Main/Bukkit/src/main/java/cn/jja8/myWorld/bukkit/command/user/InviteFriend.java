package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Status;
import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.command.UserCommand;
import cn.jja8.myWorld.bukkit.work.MyWorldManger;
import cn.jja8.myWorld.bukkit.work.MyWorldPlayer;
import cn.jja8.myWorld.bukkit.work.MyWorldTeam;
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
        MyWorldPlayer myWorldPlayer = MyWorldManger.getPlayer(player);
        MyWorldTeam playerTeam = myWorldPlayer.getTeam();
        if (playerTeam == null) {
            player.sendMessage(ConfigBukkit.getLang().邀请成员_玩家没有团队);
            return;
        }
        if (myWorldPlayer.getStatus().getLevel()>Status.admin.getLevel()) {
            player.sendMessage(ConfigBukkit.getLang().邀请成员_不是管理);
            return;
        }
        if (strings.length < 1) {
            player.sendMessage(ConfigBukkit.getLang().邀请成员_没有参数);
            return;
        }
        Player beInvitePlayer = Bukkit.getPlayer(strings[0]);
        if (beInvitePlayer == null) {
            player.sendMessage(ConfigBukkit.getLang().邀请成员_玩家不在线.replaceAll("<不在线玩家>", strings[0]));
            return;
        }
        userCommand.acceptInvitationMap.put(beInvitePlayer, playerTeam);
        beInvitePlayer.sendMessage(ConfigBukkit.getLang().邀请成员_被邀请信息.replaceAll("<玩家>", player.getName()).replaceAll("<团队>", playerTeam.getTeamName()));
        player.sendMessage(ConfigBukkit.getLang().邀请成员_邀请成功);
    }
}
