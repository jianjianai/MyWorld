package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.work.MyWorldManger;
import cn.jja8.myWorld.bukkit.work.MyWorldTeam;
import cn.jja8.myWorld.bukkit.work.MyWorldWorldGroup;
import cn.jja8.myWorld.bukkit.work.MyWorldWorldGrouping;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.command.CommandImplement;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TrustedPeoples implements CommandImplement {
    @Override
    public void command(CommandSender commandSender, String[] strings) {

        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        MyWorldTeam team = MyWorldManger.getPlayer(player).getTeam();
        if (team == null) {
            player.sendMessage(ConfigBukkit.getLang().信任列表_你没有团队);
            return;
        }
        MyWorldWorldGroup worldGroup = team.getWorldGroup();
        if (worldGroup == null) {
            player.sendMessage(ConfigBukkit.getLang().信任列表_团队没有世界);
            return;
        }
        MyWorldWorldGrouping myWorldWorldGrouping = worldGroup.getLoaded();
        if (myWorldWorldGrouping == null) {
            player.sendMessage(ConfigBukkit.getLang().信任列表_世界未加载);
            return;
        }
        String worldGroupName = worldGroup.getName();
        List<String> 信任列表 = myWorldWorldGrouping.getMyWorldWordInform().getTrust().beTrustList();
        player.sendMessage(ConfigBukkit.getLang().信任列表_信息.replaceAll("<世界>", worldGroupName).replaceAll("<数量>", String.valueOf(信任列表.size())).replaceAll("<列表>", 信任列表.toString()));
    }
}
