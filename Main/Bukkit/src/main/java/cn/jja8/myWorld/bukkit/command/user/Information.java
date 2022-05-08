package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Status;
import cn.jja8.myWorld.all.basic.DatasheetSupport.Team;
import cn.jja8.myWorld.all.basic.DatasheetSupport.Worlds;
import cn.jja8.myWorld.all.veryUtil.StringTool;
import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.command.tool.TeamsPlayerTool;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.command.CommandImplement;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class Information implements CommandImplement {
    @Override
    public void command(CommandSender commandSender, String[] strings) {

        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        Team 团队 = TeamsPlayerTool.getTeamPlayerNotNull(player).getTeam();
        if (团队 == null) {
            player.sendMessage(ConfigBukkit.getLang().查询信息_还没有团队);
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("<团队>", 团队.getTeamName());
        Worlds worlds = 团队.getWorlds();
        if (worlds == null) {
            map.put("<团队世界信息>", ConfigBukkit.getLang().查询信息_团队没有世界);
        } else {
            map.put("<团队世界信息>", ConfigBukkit.getLang().查询信息_团队世界信息.replaceAll("<世界>", worlds.getWorldsName()));
        }

        map.put("<团长>", TeamsPlayerTool.getTeamPlayersNotNull(团队, Status.leader).toString());
        map.put("<管理员列表>", TeamsPlayerTool.getTeamPlayersNotNull(团队, Status.admin).toString());
        map.put("<队员列表>", TeamsPlayerTool.getTeamPlayersNotNull(团队, Status.player).toString());
        List<String> list = StringTool.stringListReplaces(ConfigBukkit.getLang().查询信息_长信息列表, map);
        for (String s : list) {
            player.sendMessage(s);
        }
    }
}
