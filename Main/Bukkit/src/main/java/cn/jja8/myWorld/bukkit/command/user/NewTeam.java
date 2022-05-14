package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Status;
import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.command.tool.NameTool;
import cn.jja8.myWorld.bukkit.work.MyWorldManger;
import cn.jja8.myWorld.bukkit.work.MyWorldPlayer;
import cn.jja8.myWorld.bukkit.work.MyWorldTeam;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.command.CommandImplement;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NewTeam implements CommandImplement {
    @Override
    public void command(CommandSender commandSender, String[] strings) {

        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        if (strings.length < 1) {
            player.sendMessage(ConfigBukkit.getLang().创建团队_没有团队名参数);
            return;
        }
        if (!NameTool.verification(strings[0])) {
            player.sendMessage(ConfigBukkit.getLang().创建世界_团队名不合法);
            return;
        }
        MyWorldPlayer myWorldPlayer = MyWorldManger.getPlayer(player);
        MyWorldTeam team = myWorldPlayer.getTeam();
        if (team != null) {
            player.sendMessage(ConfigBukkit.getLang().创建团队_已经在团队中了.replaceAll("<团队>", team.getTeamName()));
            return;
        }
        team = MyWorldManger.getTeam(strings[0]);
        if (team != null) {
            player.sendMessage(ConfigBukkit.getLang().创建团队_团队名称被占用.replaceAll("<团队>", team.getTeamName()));
            return;
        }
        team = MyWorldManger.newTeam(strings[0]);
        myWorldPlayer.setTeam(team);
        myWorldPlayer.setStatus(Status.leader);
        player.sendMessage(ConfigBukkit.getLang().创建团队_创建成功);
    }
}
