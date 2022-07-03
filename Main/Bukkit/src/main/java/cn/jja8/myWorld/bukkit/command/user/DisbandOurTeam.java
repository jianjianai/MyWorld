package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Status;
import cn.jja8.myWorld.bukkit.work.MyWorldManger;
import cn.jja8.myWorld.bukkit.work.MyWorldPlayer;
import cn.jja8.myWorld.bukkit.work.MyWorldTeam;
import cn.jja8.patronSaint.bukkit.v3.command.CanSetUp;
import cn.jja8.patronSaint.bukkit.v3.command.CommandImplement;
import cn.jja8.patronSaint.bukkit.v3.command.canSetUpLoadType.Lang;
import cn.jja8.patronSaint.bukkit.v3.command.canSetUpLoadType.SetUp;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DisbandOurTeam implements CommandImplement, CanSetUp {

    @Lang public String 删除成功 = "删除成功";
    @Lang public String 删除确认 = "请添加一条参数”yes“来确认你确认要删除团队。";
    @Lang public String 不是团长 = "只有团长才可以删除。";
    @Lang public String 玩家没有团队 = "你没有团队";
    @Lang public String 没有删除世界 = "在删除团队之前，请先删除世界。";

    @SetUp public boolean 删除团队时必须先删除世界 = true;


    @Override
    public boolean command(CommandSender commandSender, String[] strings) {

        if ((!(commandSender instanceof Player))) return true;
        Player player = (Player) commandSender;
        MyWorldPlayer myWorldPlayer = MyWorldManger.getPlayer(player);
        MyWorldTeam team = myWorldPlayer.getTeam();
        if (team == null) {
            player.sendMessage(玩家没有团队);
            return true;
        }
        if (myWorldPlayer.getStatus().getLevel()> Status.leader.getLevel()) {
            player.sendMessage(不是团长);
            return true;
        }
        if (strings.length < 1) {
            player.sendMessage(删除确认);
            return true;
        }
        if (删除团队时必须先删除世界) {
            if (team.getWorldGroup() != null) {
                player.sendMessage(没有删除世界);
                return true;
            }
        }
        if (!"yes".equals(strings[0])) {
            player.sendMessage(删除确认);
            return true;
        }
        team.delete();
        player.sendMessage(删除成功);
        return true;
    }
}
