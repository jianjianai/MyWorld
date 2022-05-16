package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Status;
import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.command.tool.NameTool;
import cn.jja8.myWorld.bukkit.work.MyWorldManger;
import cn.jja8.myWorld.bukkit.work.MyWorldPlayer;
import cn.jja8.myWorld.bukkit.work.MyWorldTeam;
import cn.jja8.myWorld.bukkit.work.MyWorldWorldGroup;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.command.CommandImplement;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NewWorld implements CommandImplement {
    Go go;
    public NewWorld(Go go) {
        this.go = go;
    }

    @Override
    public void command(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        if (strings.length < 1) {
            player.sendMessage(ConfigBukkit.getLang().创建世界_需要世界名称);
            return;
        }
        if (!NameTool.verification(strings[0])) {
            player.sendMessage(ConfigBukkit.getLang().创建世界_世界名不合法);
            return;
        }
        if (ConfigBukkit.getWorldConfig().禁止玩家使用的世界名称列表.contains(strings[0].toLowerCase())) {
            player.sendMessage(ConfigBukkit.getLang().创建世界_名称禁止使用.replaceAll("<世界>", strings[0]));
            return;
        }
        MyWorldPlayer myWorldPlayer = MyWorldManger.getPlayer(player);
        MyWorldTeam team = myWorldPlayer.getTeam();
        if (team == null) {
            player.sendMessage(ConfigBukkit.getLang().创建世界_玩家没有团队);
            return;
        }
        MyWorldWorldGroup worldGroup = team.getWorldGroup();
        if (worldGroup != null) {
            player.sendMessage(ConfigBukkit.getLang().创建世界_团队已经有世界了);
            return;
        }
        if (myWorldPlayer.getStatus().getLevel() > Status.leader.getLevel()) {
            player.sendMessage(ConfigBukkit.getLang().创建世界_不是团长);
            return;
        }
        worldGroup = MyWorldManger.getWorldGroup(strings[0]);
        if (worldGroup != null) {
            player.sendMessage(ConfigBukkit.getLang().创建世界_世界名称被他人占用);
            return;
        }
        worldGroup = MyWorldManger.newWorldGroup(strings[0]);
        //创建世界了
        team.setWorldGroup(worldGroup);
        player.sendMessage(ConfigBukkit.getLang().创建世界_创建成功);
        if (ConfigBukkit.getWorldConfig().创建世界后传送到世界) {
            go.command(commandSender,new String[]{});
        }
    }
}
