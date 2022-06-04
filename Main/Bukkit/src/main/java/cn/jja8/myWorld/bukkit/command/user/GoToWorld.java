package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.bukkit.work.*;
import cn.jja8.patronSaint.bukkit.v3.command.CanSetUp;
import cn.jja8.patronSaint.bukkit.v3.command.CommandImplement;
import cn.jja8.patronSaint.bukkit.v3.command.NeedSet;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GoToWorld implements CommandImplement , CanSetUp {
    @NeedSet public String 没有指定世界type = "未指定type";
    @NeedSet public String 没有团队 = "你还没有加入团队";
    @NeedSet public String 团队没有世界 = "你所在的团队还没有世界";
    @NeedSet public String 世界没有加载 = "你所在的团队的世界没有加载，请先进入世界。";
    @NeedSet public String type错误 = "type'<type>'不存在";
    @NeedSet public String 传送成功 = "传送成功。";

    @Override
    public boolean command(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return true;
        Player player = (Player) commandSender;
        if (strings.length<1){
            player.sendMessage(没有指定世界type);
            return true;
        }
        MyWorldPlayer myWorldPlayer = MyWorldManger.getPlayer(player);
        MyWorldTeam team = myWorldPlayer.getTeam();
        if (team == null) {
            player.sendMessage(没有团队);
            return true;
        }
        MyWorldWorldGroup worldGroup = team.getWorldGroup();
        if (worldGroup == null) {
            player.sendMessage(团队没有世界);
            return true;
        }
        MyWorldWorldGrouping loading = worldGroup.getLoading();
        if (loading==null){
            player.sendMessage(世界没有加载);
            return true;
        }
        MyWorldWorldGroupingWorlding worlding = loading.getMyWorldWording(strings[0]);
        if (worlding==null){
            player.sendMessage(type错误.replaceAll("<type>",strings[0]));
            return true;
        }
        player.teleport(worlding.getMyWorldWorlding().getWorld().getSpawnLocation());
        player.sendMessage(传送成功);
        return true;

    }

    @Override
    public List<String> tabCompletion(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return new ArrayList<>();
        Player player = (Player) commandSender;
        MyWorldPlayer myWorldPlayer = MyWorldManger.getPlayer(player);
        MyWorldTeam team = myWorldPlayer.getTeam();
        if (team == null) {
            return new ArrayList<>();
        }
        MyWorldWorldGroup worldGroup = team.getWorldGroup();
        if (worldGroup == null) {
            return new ArrayList<>();
        }
        MyWorldWorldGrouping loading = worldGroup.getLoading();
        if (loading==null){
            return new ArrayList<>();
        }
        return new ArrayList<>(loading.getAllLoadWorld().keySet());
    }
}
