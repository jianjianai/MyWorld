package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Status;
import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.work.MyWorldManger;
import cn.jja8.myWorld.bukkit.work.MyWorldPlayer;
import cn.jja8.myWorld.bukkit.work.MyWorldTeam;
import cn.jja8.patronSaint.bukkit.v3.command.CommandImplement;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DisbandOurTeam implements CommandImplement {
    @Override
    public boolean command(CommandSender commandSender, String[] strings) {

        if ((!(commandSender instanceof Player))) return true;
        Player player = (Player) commandSender;
        MyWorldPlayer myWorldPlayer = MyWorldManger.getPlayer(player);
        MyWorldTeam team = myWorldPlayer.getTeam();
        if (team == null) {
            player.sendMessage(ConfigBukkit.getLang().删除团队_玩家没有团队);
            return true;
        }
        if (myWorldPlayer.getStatus().getLevel()> Status.leader.getLevel()) {
            player.sendMessage(ConfigBukkit.getLang().删除团队_不是团长);
            return true;
        }
        if (strings.length < 1) {
            player.sendMessage(ConfigBukkit.getLang().删除团队_删除确认);
            return true;
        }
        if (ConfigBukkit.getTeamConfig().删除团队时必须先删除世界) {
            if (team.getWorldGroup() != null) {
                player.sendMessage(ConfigBukkit.getLang().删除团队_没有删除世界);
                return true;
            }
        }
        if (!"yes".equals(strings[0])) {
            player.sendMessage(ConfigBukkit.getLang().删除团队_删除确认);
            return true;
        }
        team.delete();
        player.sendMessage(ConfigBukkit.getLang().删除团队_删除成功);
        return true;
    }
}
