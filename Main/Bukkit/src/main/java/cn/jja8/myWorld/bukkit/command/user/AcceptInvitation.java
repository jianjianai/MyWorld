package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.bukkit.command.UserCommand;
import cn.jja8.myWorld.bukkit.work.MyWorldManger;
import cn.jja8.myWorld.bukkit.work.MyWorldPlayer;
import cn.jja8.myWorld.bukkit.work.MyWorldTeam;
import cn.jja8.patronSaint.bukkit.v3.command.CanSetUp;
import cn.jja8.patronSaint.bukkit.v3.command.CommandImplement;
import cn.jja8.patronSaint.bukkit.v3.command.NeedSet;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AcceptInvitation implements CommandImplement, CanSetUp {
    @NeedSet public String 没被邀请 = "你没有被邀请哦。";
    @NeedSet public String 已经有团队="你已经在<团队>团队中了。";
    @NeedSet public String 接受成功="你已加入<团队>团队。";

    private final UserCommand userCommand;
    public AcceptInvitation(UserCommand userCommand) {
        this.userCommand = userCommand;
    }

    @Override
    public boolean command(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return true;
        Player player = (Player) commandSender;
        MyWorldTeam invitationTeam = userCommand.acceptInvitationMap.get(player);
        if (invitationTeam == null) {
            player.sendMessage(没被邀请);
            return true;
        }
        MyWorldPlayer myWorldPlayer = MyWorldManger.getPlayer(player);
        MyWorldTeam myWorldTeam = myWorldPlayer.getTeam();
        if (myWorldTeam != null) {
            player.sendMessage(已经有团队.replaceAll("<团队>", myWorldTeam.getTeamName()));
            return true;
        }
        myWorldPlayer.setTeam(invitationTeam);
        player.sendMessage(接受成功.replaceAll("<团队>", invitationTeam.getTeamName()));
        return true;
    }
}
