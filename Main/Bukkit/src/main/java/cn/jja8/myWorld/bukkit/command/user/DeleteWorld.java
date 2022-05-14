package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Status;
import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.work.MyWorldManger;
import cn.jja8.myWorld.bukkit.work.MyWorldPlayer;
import cn.jja8.myWorld.bukkit.work.MyWorldTeam;
import cn.jja8.myWorld.bukkit.work.MyWorldWorldGroup;
import cn.jja8.myWorld.bukkit.work.error.NoWorldLocks;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.command.CommandImplement;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeleteWorld implements CommandImplement {
    @Override
    public void command(CommandSender commandSender, String[] strings) {

        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        if (strings.length < 1) {
            player.sendMessage(ConfigBukkit.getLang().删除世界_删除确认);
            return;
        }
        if (!"yes".equals(strings[0])) {
            player.sendMessage(ConfigBukkit.getLang().删除世界_删除确认);
            return;
        }
        MyWorldPlayer myWorldPlayer = MyWorldManger.getPlayer(player);
        MyWorldTeam team = myWorldPlayer.getTeam();
        if (team == null) {
            player.sendMessage(ConfigBukkit.getLang().删除世界_玩家没有团队);
            return;
        }
        if (myWorldPlayer.getStatus().getLevel()> Status.leader.getLevel()) {
            player.sendMessage(ConfigBukkit.getLang().删除世界_不是团长);
            return;
        }
        MyWorldWorldGroup worldGroup = team.getWorldGroup();
        if (worldGroup == null) {
            player.sendMessage(ConfigBukkit.getLang().删除世界_世界不存在);
            return;
        }
        try {
            worldGroup.delete();
        } catch (NoWorldLocks e) {
            player.sendMessage(ConfigBukkit.getLang().删除世界_世界被其他服务器加载);
        }
        player.sendMessage(ConfigBukkit.getLang().删除世界_删除成功);
    }
}
