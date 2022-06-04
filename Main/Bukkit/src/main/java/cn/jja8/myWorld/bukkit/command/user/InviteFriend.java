package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Status;
import cn.jja8.myWorld.bukkit.command.UserCommand;
import cn.jja8.myWorld.bukkit.work.MyWorldManger;
import cn.jja8.myWorld.bukkit.work.MyWorldPlayer;
import cn.jja8.myWorld.bukkit.work.MyWorldTeam;
import cn.jja8.patronSaint.bukkit.v3.command.CanSetUp;
import cn.jja8.patronSaint.bukkit.v3.command.CommandImplement;
import cn.jja8.patronSaint.bukkit.v3.command.NeedSet;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InviteFriend implements CommandImplement , CanSetUp {
    private final UserCommand userCommand;
    @NeedSet public String 被邀请信息 = "你被<玩家>邀请进<团队>团队，赶紧同意吧！";
    @NeedSet public String 玩家不在线 = "玩家<不在线玩家>不在线。";
    @NeedSet public String 没有参数 = "你要邀请谁呢？请添加一个玩家名参数吧。";
    @NeedSet public String 邀请成功 = "邀请成功";
    @NeedSet public String 不是管理 = "只有管理可以邀请成员";
    @NeedSet public String 玩家没有团队 = "你没有团队";

    public InviteFriend(UserCommand userCommand) {
        this.userCommand = userCommand;
    }

    @Override
    public boolean command(CommandSender commandSender, String[] strings) {

        if ((!(commandSender instanceof Player))) return true;
        Player player = (Player) commandSender;
        MyWorldPlayer myWorldPlayer = MyWorldManger.getPlayer(player);
        MyWorldTeam playerTeam = myWorldPlayer.getTeam();
        if (playerTeam == null) {
            player.sendMessage(玩家没有团队);
            return true;
        }
        if (myWorldPlayer.getStatus().getLevel()>Status.admin.getLevel()) {
            player.sendMessage(不是管理);
            return true;
        }
        if (strings.length < 1) {
            player.sendMessage(没有参数);
            return true;
        }
        Player beInvitePlayer = Bukkit.getPlayer(strings[0]);
        if (beInvitePlayer == null) {
            player.sendMessage(玩家不在线.replaceAll("<不在线玩家>", strings[0]));
            return true;
        }
        userCommand.acceptInvitationMap.put(beInvitePlayer, playerTeam);
        beInvitePlayer.sendMessage(被邀请信息.replaceAll("<玩家>", player.getName()).replaceAll("<团队>", playerTeam.getTeamName()));
        player.sendMessage(邀请成功);
        return true;
    }
}
