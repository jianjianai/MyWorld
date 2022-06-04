package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.command.UserCommand;
import cn.jja8.myWorld.bukkit.work.MyWorldManger;
import cn.jja8.myWorld.bukkit.work.MyWorldPlayer;
import cn.jja8.myWorld.bukkit.work.MyWorldTeam;
import cn.jja8.patronSaint.bukkit.v3.command.CommandImplement;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AcceptInvitation implements CommandImplement {
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
            player.sendMessage(ConfigBukkit.getLang().接受邀请_没被邀请);
            return true;
        }
        MyWorldPlayer myWorldPlayer = MyWorldManger.getPlayer(player);
        MyWorldTeam myWorldTeam = myWorldPlayer.getTeam();
        if (myWorldTeam != null) {
            player.sendMessage(ConfigBukkit.getLang().接受邀请_已经有团队.replaceAll("<团队>", myWorldTeam.getTeamName()));
            return true;
        }
        myWorldPlayer.setTeam(invitationTeam);
        player.sendMessage(ConfigBukkit.getLang().接受邀请_接受成功.replaceAll("<团队>", invitationTeam.getTeamName()));
        return true;
    }
}
