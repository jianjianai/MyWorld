package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Status;
import cn.jja8.myWorld.all.veryUtil.StringTool;
import cn.jja8.myWorld.bukkit.work.MyWorldManger;
import cn.jja8.myWorld.bukkit.work.MyWorldTeam;
import cn.jja8.myWorld.bukkit.work.MyWorldWorldGroup;
import cn.jja8.patronSaint.bukkit.v3.command.CanSetUp;
import cn.jja8.patronSaint.bukkit.v3.command.CommandImplement;
import cn.jja8.patronSaint.bukkit.v3.command.canSetUpLoadType.Lang;
import cn.jja8.patronSaint.bukkit.v3.tool.Config.ConfigurationSectionSetDefGet;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Information implements CommandImplement , CanSetUp {
    @Lang public  String 团队世界信息 = "该团队的世界是：<世界>";
    @Lang public  String 团队没有世界 = "你加入的团队还没有世界";
    @Lang public  String 还没有团队 = "你现在还没有加入团队";
    public List<String> 团队信息列表 = Arrays.asList(
            "你所在的团队是：<团队>",
            "<团队世界信息>",
            "团长是：<团长>",
            "管理员有<管理员列表>",
            "队员有<队员列表>");

    @Override
    public void load(ConfigurationSectionSetDefGet set) {
        CanSetUp.super.load(set);
        团队信息列表 = set.getStringListSetDef("团队信息列表", 团队信息列表);
    }

    @Override
    public boolean command(CommandSender commandSender, String[] strings) {


        if ((!(commandSender instanceof Player))) return true;
        Player player = (Player) commandSender;
        MyWorldTeam team = MyWorldManger.getPlayer(player).getTeam();
        if (team == null) {
            player.sendMessage(还没有团队);
            return true;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("<团队>", team.getTeamName());
        MyWorldWorldGroup worldGroup = team.getWorldGroup();
        if (worldGroup == null) {
            map.put("<团队世界信息>", 团队没有世界);
        } else {
            map.put("<团队世界信息>", 团队世界信息.replaceAll("<世界>", worldGroup.getName()));
        }

        map.put("<团长>", team.getTeamPlayers(Status.leader).toString());
        map.put("<管理员列表>", team.getTeamPlayers(Status.admin).toString());
        map.put("<队员列表>", team.getTeamPlayers(Status.player).toString());
        List<String> list = StringTool.stringListReplaces(团队信息列表, map);
        for (String s : list) {
            player.sendMessage(s);
        }
        return true;
    }
}
