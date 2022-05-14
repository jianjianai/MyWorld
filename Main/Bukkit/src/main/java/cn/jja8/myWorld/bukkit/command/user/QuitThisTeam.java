package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Status;
import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.work.MyWorldManger;
import cn.jja8.myWorld.bukkit.work.MyWorldPlayer;
import cn.jja8.myWorld.bukkit.work.MyWorldTeam;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.command.CommandImplement;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QuitThisTeam implements CommandImplement {
    @Override
    public void command(CommandSender commandSender, String[] strings) {

        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        MyWorldPlayer myWorldPlayer = MyWorldManger.getPlayer(player);
        MyWorldTeam team = myWorldPlayer.getTeam();
        if (team == null) {
            player.sendMessage(ConfigBukkit.getLang().退出团队_你没有团队);
            return;
        }
        if (myWorldPlayer.getStatus().getLevel()<= Status.leader.getLevel()) {
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
        myWorldPlayer.setTeam(null);
        player.sendMessage(ConfigBukkit.getLang().退出团队_退出成功);
    }
}
