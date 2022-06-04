package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Status;
import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.command.tool.NameTool;
import cn.jja8.myWorld.bukkit.config.WorldCreators;
import cn.jja8.myWorld.bukkit.work.MyWorldManger;
import cn.jja8.myWorld.bukkit.work.MyWorldPlayer;
import cn.jja8.myWorld.bukkit.work.MyWorldTeam;
import cn.jja8.myWorld.bukkit.work.MyWorldWorldGroup;
import cn.jja8.patronSaint.bukkit.v3.command.CanSetUp;
import cn.jja8.patronSaint.bukkit.v3.command.CommandImplement;
import cn.jja8.patronSaint.bukkit.v3.command.NeedSet;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class NewWorld implements CommandImplement , CanSetUp {
    @NeedSet public  String 世界名称被他人占用 = "这个世界名称已经被占用，请换一个吧。";
    @NeedSet public  String 需要世界名称 = "世界需要一个名称哦";
    @NeedSet public  String 不是团长 = "只有团长才可以创建世界。";
    @NeedSet public  String 团队已经有世界了 = "你所在的团队已经有世界了";
    @NeedSet public  String 玩家没有团队 = "创建世界之前需要先加入一个团队";
    @NeedSet public  String 创建成功 = "世界创建成功";
    @NeedSet public  String 世界名不合法 = "世界创建失败，世界名不合法。{长度必须大于3，只能使用小写字母}";
    @NeedSet public  String 名称禁止使用 = "<世界>名称被禁止使用";
    @NeedSet public  String 世界生成器不存在 = "世界生成器<WorldClean>不存在。";

    Go go;
    public NewWorld(Go go) {
        this.go = go;
    }

    @Override
    public boolean command(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return true;
        Player player = (Player) commandSender;
        if (strings.length < 1) {
            player.sendMessage(需要世界名称);
            return true;
        }
        if (!NameTool.verification(strings[0])) {
            player.sendMessage(世界名不合法);
            return true;
        }
        if (ConfigBukkit.getWorldConfig().禁止玩家使用的世界名称列表.contains(strings[0].toLowerCase())) {
            player.sendMessage(名称禁止使用.replaceAll("<世界>", strings[0]));
            return true;
        }
        MyWorldPlayer myWorldPlayer = MyWorldManger.getPlayer(player);
        MyWorldTeam team = myWorldPlayer.getTeam();
        if (team == null) {
            player.sendMessage(玩家没有团队);
            return true;
        }
        MyWorldWorldGroup worldGroup = team.getWorldGroup();
        if (worldGroup != null) {
            player.sendMessage(团队已经有世界了);
            return true;
        }
        if (myWorldPlayer.getStatus().getLevel() > Status.leader.getLevel()) {
            player.sendMessage(不是团长);
            return true;
        }
        worldGroup = MyWorldManger.getWorldGroup(strings[0]);
        if (worldGroup != null) {
            player.sendMessage(世界名称被他人占用);
            return true;
        }
        WorldCreators.Creator creator;
        if (strings.length>=2){
            creator = ConfigBukkit.getDefWorlds().getCreatorName_Creator().get(strings[1]);
            if (creator==null){
                player.sendMessage(世界生成器不存在.replaceAll("<WorldClean>",strings[1]));
                return true;
            }
        }else {
            creator = ConfigBukkit.getDefWorlds().getDefault();
        }
        worldGroup = MyWorldManger.newWorldGroup(strings[0],creator);
        //创建世界了
        team.setWorldGroup(worldGroup);
        player.sendMessage(创建成功);
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
