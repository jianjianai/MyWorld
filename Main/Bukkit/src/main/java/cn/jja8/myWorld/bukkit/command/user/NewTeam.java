package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Status;
import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.command.tool.NameTool;
import cn.jja8.myWorld.bukkit.work.MyWorldManger;
import cn.jja8.myWorld.bukkit.work.MyWorldPlayer;
import cn.jja8.myWorld.bukkit.work.MyWorldTeam;
import cn.jja8.patronSaint.bukkit.v3.command.CanSetUp;
import cn.jja8.patronSaint.bukkit.v3.command.CommandImplement;
import cn.jja8.patronSaint.bukkit.v3.command.canSetUpLoadType.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NewTeam implements CommandImplement , CanSetUp {
    @Lang public  String 团队名称被占用 = "<团队>名称已经被其他团队占用了。";
    @Lang public  String 没有团队名参数 = "需要给团队取一个名字哦。";
    @Lang public  String 已经在团队中了 = "你已经在<团队>团队中了。";
    @Lang public  String 创建成功 = "团队创建成功";
    @Override
    public boolean command(CommandSender commandSender, String[] strings) {

        if ((!(commandSender instanceof Player))) return true;
        Player player = (Player) commandSender;
        if (strings.length < 1) {
            player.sendMessage(没有团队名参数);
            return true;
        }
        if (!NameTool.verification(strings[0])) {
            player.sendMessage(ConfigBukkit.getLang().创建世界_团队名不合法);
            return true;
        }
        MyWorldPlayer myWorldPlayer = MyWorldManger.getPlayer(player);
        MyWorldTeam team = myWorldPlayer.getTeam();
        if (team != null) {
            player.sendMessage(已经在团队中了.replaceAll("<团队>", team.getTeamName()));
            return true;
        }
        team = MyWorldManger.getTeam(strings[0]);
        if (team != null) {
            player.sendMessage(团队名称被占用.replaceAll("<团队>", team.getTeamName()));
            return true;
        }
        team = MyWorldManger.newTeam(strings[0]);
        myWorldPlayer.setTeam(team);
        myWorldPlayer.setStatus(Status.leader);
        player.sendMessage(创建成功);
        return true;
    }
}
