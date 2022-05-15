package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Status;
import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.work.*;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.command.CommandImplement;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TrustHim implements CommandImplement {
    @Override
    public void command(CommandSender commandSender, String[] strings) {

        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        if (strings.length < 1) {
            player.sendMessage(ConfigBukkit.getLang().添加信任_没有参数);
            return;
        }
        MyWorldPlayer myWorldPlayer = MyWorldManger.getPlayer(player);
        MyWorldTeam team = myWorldPlayer.getTeam();
        if (team == null) {
            player.sendMessage(ConfigBukkit.getLang().添加信任_你没有团队);
            return;
        }
        if (myWorldPlayer.getStatus().getLevel()>Status.admin.getLevel()) {
            player.sendMessage(ConfigBukkit.getLang().添加信任_权限不足);
            return;
        }
        MyWorldWorldGroup worldGroup = team.getWorldGroup();
        if (worldGroup == null) {
            player.sendMessage(ConfigBukkit.getLang().添加信任_世界不存在);
        }
        MyWorldWorldGrouping beLoadWorlds = worldGroup.getLoading();
        if (beLoadWorlds == null) {
            player.sendMessage(ConfigBukkit.getLang().添加信任_世界未加载);
            return;
        }
        beLoadWorlds.getMyWorldWordInform().getTrust().addBeTrust(strings[0]);
        player.sendMessage(ConfigBukkit.getLang().添加信任_添加成功);
    }
}
