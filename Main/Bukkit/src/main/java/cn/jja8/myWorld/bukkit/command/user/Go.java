package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.work.error.NoAllWorldLocks;
import cn.jja8.myWorld.bukkit.work.*;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.command.CommandImplement;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Go implements CommandImplement {
    @Override
    public void command(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        MyWorldPlayer myWorldPlayer = MyWorldManger.getPlayer(player);
        MyWorldTeam team = myWorldPlayer.getTeam();
        if (team == null) {
            player.sendMessage(ConfigBukkit.getLang().返回世界_没有团队);
            return;
        }
        MyWorldWorldGroup worldGroup = team.getWorldGroup();
        if (worldGroup == null) {
            player.sendMessage(ConfigBukkit.getLang().返回世界_团队没有世界);
            return;
        }
        worldGroup.load(new MyWorldWorldGroup.OnLoad() {
            @Override
            public void onload(MyWorldWorldGrouping worldGrouping) {
                Bukkit.getScheduler().runTask(MyWorldBukkit.getMyWorldBukkit(), () -> {
                    Location l = worldGrouping.getMyWorldWordInform().getPlayerLeaveLocation().getPlayerLocation(player);
                    if (l==null){
                        player.teleport(worldGrouping.getSpawnLocation());
                    }
                    player.sendMessage(ConfigBukkit.getLang().返回世界_传送成功);
                });
            }

            @Override
            public void fail(Exception exception) {
                if (exception instanceof NoAllWorldLocks){
                    player.sendMessage(ConfigBukkit.getLang().返回世界_世界被其他服务器加载);
                }else {
                    player.sendMessage(ConfigBukkit.getLang().返回世界_未知异常);
                    exception.printStackTrace();
                }

            }
        });
    }
}
