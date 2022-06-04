package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Status;
import cn.jja8.myWorld.all.veryUtil.StringTool;
import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.work.MyWorldManger;
import cn.jja8.myWorld.bukkit.work.MyWorldTeam;
import cn.jja8.myWorld.bukkit.work.MyWorldWorldGroup;
import cn.jja8.patronSaint.bukkit.v3.command.CommandImplement;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class Information implements CommandImplement {
    @Override
    public boolean command(CommandSender commandSender, String[] strings) {

        if ((!(commandSender instanceof Player))) return true;
        Player player = (Player) commandSender;
        MyWorldTeam team = MyWorldManger.getPlayer(player).getTeam();
        if (team == null) {
            player.sendMessage(ConfigBukkit.getLang().查询信息_还没有团队);
            return true;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("<团队>", team.getTeamName());
        MyWorldWorldGroup worldGroup = team.getWorldGroup();
        if (worldGroup == null) {
            map.put("<团队世界信息>", ConfigBukkit.getLang().查询信息_团队没有世界);
        } else {
            map.put("<团队世界信息>", ConfigBukkit.getLang().查询信息_团队世界信息.replaceAll("<世界>", worldGroup.getName()));
        }

        map.put("<团长>", team.getTeamPlayers(Status.leader).toString());
        map.put("<管理员列表>", team.getTeamPlayers(Status.admin).toString());
        map.put("<队员列表>", team.getTeamPlayers(Status.player).toString());
        List<String> list = StringTool.stringListReplaces(ConfigBukkit.getLang().查询信息_长信息列表, map);
        for (String s : list) {
            player.sendMessage(s);
        }
        return true;
    }
}
