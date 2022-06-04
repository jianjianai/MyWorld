package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Status;
import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.command.tool.NameTool;
import cn.jja8.myWorld.bukkit.config.WorldCreators;
import cn.jja8.myWorld.bukkit.work.MyWorldManger;
import cn.jja8.myWorld.bukkit.work.MyWorldPlayer;
import cn.jja8.myWorld.bukkit.work.MyWorldTeam;
import cn.jja8.myWorld.bukkit.work.MyWorldWorldGroup;
import cn.jja8.patronSaint.bukkit.v3.command.CommandImplement;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class NewWorld implements CommandImplement {
    Go go;
    public NewWorld(Go go) {
        this.go = go;
    }

    @Override
    public boolean command(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return true;
        Player player = (Player) commandSender;
        if (strings.length < 1) {
            player.sendMessage(ConfigBukkit.getLang().创建世界_需要世界名称);
            return true;
        }
        if (!NameTool.verification(strings[0])) {
            player.sendMessage(ConfigBukkit.getLang().创建世界_世界名不合法);
            return true;
        }
        if (ConfigBukkit.getWorldConfig().禁止玩家使用的世界名称列表.contains(strings[0].toLowerCase())) {
            player.sendMessage(ConfigBukkit.getLang().创建世界_名称禁止使用.replaceAll("<世界>", strings[0]));
            return true;
        }
        MyWorldPlayer myWorldPlayer = MyWorldManger.getPlayer(player);
        MyWorldTeam team = myWorldPlayer.getTeam();
        if (team == null) {
            player.sendMessage(ConfigBukkit.getLang().创建世界_玩家没有团队);
            return true;
        }
        MyWorldWorldGroup worldGroup = team.getWorldGroup();
        if (worldGroup != null) {
            player.sendMessage(ConfigBukkit.getLang().创建世界_团队已经有世界了);
            return true;
        }
        if (myWorldPlayer.getStatus().getLevel() > Status.leader.getLevel()) {
            player.sendMessage(ConfigBukkit.getLang().创建世界_不是团长);
            return true;
        }
        worldGroup = MyWorldManger.getWorldGroup(strings[0]);
        if (worldGroup != null) {
            player.sendMessage(ConfigBukkit.getLang().创建世界_世界名称被他人占用);
            return true;
        }
        WorldCreators.Creator creator;
        if (strings.length>=2){
            creator = ConfigBukkit.getDefWorlds().getCreatorName_Creator().get(strings[1]);
            if (creator==null){
                player.sendMessage(ConfigBukkit.getLang().创建世界_世界生成器不存在.replaceAll("<WorldClean>",strings[1]));
                return true;
            }
        }else {
            creator = ConfigBukkit.getDefWorlds().getDefault();
        }
        worldGroup = MyWorldManger.newWorldGroup(strings[0],creator);
        //创建世界了
        team.setWorldGroup(worldGroup);
        player.sendMessage(ConfigBukkit.getLang().创建世界_创建成功);
        if (ConfigBukkit.getWorldConfig().创建世界后传送到世界) {
            go.command(commandSender,new String[]{});
        }
        return  true;
    }

    @Override
    public List<String> tabCompletion(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return new ArrayList<>();
        if (strings.length!=2){
            return new ArrayList<>();
        }
        return new ArrayList<>(ConfigBukkit.getDefWorlds().getCreatorName_Creator().keySet());
    }
}
