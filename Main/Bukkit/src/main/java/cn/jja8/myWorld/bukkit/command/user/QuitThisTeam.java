package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Status;
import cn.jja8.myWorld.bukkit.work.MyWorldManger;
import cn.jja8.myWorld.bukkit.work.MyWorldPlayer;
import cn.jja8.myWorld.bukkit.work.MyWorldTeam;
import cn.jja8.patronSaint.bukkit.v3.command.CanSetUp;
import cn.jja8.patronSaint.bukkit.v3.command.CommandImplement;
import cn.jja8.patronSaint.bukkit.v3.command.canSetUpLoadType.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QuitThisTeam implements CommandImplement , CanSetUp {
    @Lang public String 退出成功 = "退出成功";
    @Lang public String 团长不能退出 = "团长不能退出团队";
    @Lang public String 退出确认 = "请添加一条参数”yes“来确认你确认要退出团队。";
    @Lang public String 你没有团队 = "你不在任何团队中";

    @Override
    public boolean command(CommandSender commandSender, String[] strings) {

        if ((!(commandSender instanceof Player))) return true;
        Player player = (Player) commandSender;
        MyWorldPlayer myWorldPlayer = MyWorldManger.getPlayer(player);
        MyWorldTeam team = myWorldPlayer.getTeam();
        if (team == null) {
            player.sendMessage(你没有团队);
            return true;
        }
        if (myWorldPlayer.getStatus().getLevel()<= Status.leader.getLevel()) {
            player.sendMessage(团长不能退出);
            return true;
        }
        if (strings.length < 1) {
            player.sendMessage(退出确认);
            return true;
        }
        if (!"yes".equals(strings[0])) {
            player.sendMessage(退出确认);
            return true;
        }
        myWorldPlayer.setTeam(null);
        player.sendMessage(退出成功);
        return true;
    }
}
