package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.work.MyWorldManger;
import cn.jja8.myWorld.bukkit.work.MyWorldTeam;
import cn.jja8.myWorld.bukkit.work.MyWorldWorldGroup;
import cn.jja8.myWorld.bukkit.work.MyWorldWorldGrouping;
import cn.jja8.patronSaint.bukkit.v3.command.CommandImplement;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TrustedPeoples implements CommandImplement {
    @Override
    public boolean command(CommandSender commandSender, String[] strings) {

        if ((!(commandSender instanceof Player))) return true;
        Player player = (Player) commandSender;
        MyWorldTeam team = MyWorldManger.getPlayer(player).getTeam();
        if (team == null) {
            player.sendMessage(ConfigBukkit.getLang().信任列表_你没有团队);
            return true;
        }
        MyWorldWorldGroup worldGroup = team.getWorldGroup();
        if (worldGroup == null) {
            player.sendMessage(ConfigBukkit.getLang().信任列表_团队没有世界);
            return true;
        }
        MyWorldWorldGrouping myWorldWorldGrouping = worldGroup.getLoading();
        if (myWorldWorldGrouping == null) {
            player.sendMessage(ConfigBukkit.getLang().信任列表_世界未加载);
            return true;
        }
        String worldGroupName = worldGroup.getName();
        List<String> 信任列表 = myWorldWorldGrouping.getMyWorldWordInform().getTrust().beTrustList();
        player.sendMessage(ConfigBukkit.getLang().信任列表_信息.replaceAll("<世界>", worldGroupName).replaceAll("<数量>", String.valueOf(信任列表.size())).replaceAll("<列表>", 信任列表.toString()));
        return true;
    }
}
