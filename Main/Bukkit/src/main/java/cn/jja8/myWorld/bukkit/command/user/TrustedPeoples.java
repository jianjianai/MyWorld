package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.bukkit.work.MyWorldManger;
import cn.jja8.myWorld.bukkit.work.MyWorldTeam;
import cn.jja8.myWorld.bukkit.work.MyWorldWorldGroup;
import cn.jja8.myWorld.bukkit.work.MyWorldWorldGrouping;
import cn.jja8.patronSaint.bukkit.v3.command.CanSetUp;
import cn.jja8.patronSaint.bukkit.v3.command.CommandImplement;
import cn.jja8.patronSaint.bukkit.v3.command.canSetUpLoadType.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TrustedPeoples implements CommandImplement , CanSetUp {
    @Lang public String 团队没有世界 = "你所在的团队没有世界";
    @Lang public String 信息 = "你所在的世界<世界>中，共有<数量>位信任玩家。分别是：<列表>";
    @Lang public String 你没有团队 = "你不在任何一个团队中。";
    @Lang public String 世界未加载 = "世界没有加载时无法查看信任玩家，请先进入你所在团队的世界。";

    @Override
    public boolean command(CommandSender commandSender, String[] strings) {

        if ((!(commandSender instanceof Player))) return true;
        Player player = (Player) commandSender;
        MyWorldTeam team = MyWorldManger.getPlayer(player).getTeam();
        if (team == null) {
            player.sendMessage(你没有团队);
            return true;
        }
        MyWorldWorldGroup worldGroup = team.getWorldGroup();
        if (worldGroup == null) {
            player.sendMessage(团队没有世界);
            return true;
        }
        MyWorldWorldGrouping myWorldWorldGrouping = worldGroup.getLoading();
        if (myWorldWorldGrouping == null) {
            player.sendMessage(世界未加载);
            return true;
        }
        String worldGroupName = worldGroup.getName();
        List<String> 信任列表 = myWorldWorldGrouping.getMyWorldWordInform().getTrust().beTrustList();
        player.sendMessage(信息.replaceAll("<世界>", worldGroupName).replaceAll("<数量>", String.valueOf(信任列表.size())).replaceAll("<列表>", 信任列表.toString()));
        return true;
    }
}
