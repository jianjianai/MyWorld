package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.work.*;
import cn.jja8.myWorld.bukkit.work.error.NoAllWorldLocks;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.command.CommandImplement;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GoToWorld implements CommandImplement {
    @Override
    public void command(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        if (strings.length<1){
            player.sendMessage(ConfigBukkit.getLang().GoToWorld_没有指定世界type);
            return;
        }
        MyWorldPlayer myWorldPlayer = MyWorldManger.getPlayer(player);
        MyWorldTeam team = myWorldPlayer.getTeam();
        if (team == null) {
            player.sendMessage(ConfigBukkit.getLang().GoToWorld_没有团队);
            return;
        }
        MyWorldWorldGroup worldGroup = team.getWorldGroup();
        if (worldGroup == null) {
            player.sendMessage(ConfigBukkit.getLang().GoToWorld_团队没有世界);
            return;
        }
        MyWorldWorldGrouping loading = worldGroup.getLoading();
        if (loading==null){
            player.sendMessage(ConfigBukkit.getLang().GoToWorld_世界没有加载);
            return;
        }
        MyWorldWorldGroupingWorlding worlding = loading.getMyWorldWording(strings[0]);
        if (worlding==null){
            player.sendMessage(ConfigBukkit.getLang().GoToWorld_type错误.replaceAll("<type>",strings[0]));
            return;
        }
        player.teleport(worlding.getMyWorldWorlding().getWorld().getSpawnLocation());
        player.sendMessage(ConfigBukkit.getLang().GoToWorld_传送成功);

    }

    @Override
    public List<String> TabCompletion(CommandSender commandSender, String[] strings) {
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
