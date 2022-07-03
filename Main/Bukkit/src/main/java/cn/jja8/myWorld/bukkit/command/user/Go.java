package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.work.*;
import cn.jja8.myWorld.bukkit.work.error.NoAllWorldLocks;
import cn.jja8.patronSaint.bukkit.v3.command.CanSetUp;
import cn.jja8.patronSaint.bukkit.v3.command.CommandImplement;
import cn.jja8.patronSaint.bukkit.v3.command.canSetUpLoadType.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Go implements CommandImplement , CanSetUp {
    @Lang public String 世界被其他服务器加载 = "世界已经被其他服务器加载，（这个信息如果bc服务器配置正常是不会出现的）";
    @Lang public String 团队没有世界 = "你的团队没有世界。";
    @Lang public String 没有团队 = "你还没有加入团队。";
    @Lang public String 传送成功 = "已回到上次的位置。";
    @Lang public String 未知异常 = "加载世界出错，请联系管理员。";



    @Override
    public boolean command(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return true;
        Player player = (Player) commandSender;
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
        worldGroup.load(new MyWorldWorldGroup.OnLoad() {
            @Override
            public void onload(MyWorldWorldGrouping worldGrouping) {
                Bukkit.getScheduler().runTask(MyWorldBukkit.getMyWorldBukkit(), () -> {
                    Location l = worldGrouping.getMyWorldWordInform().getPlayerLeaveLocation().getPlayerLocation(player);
                    if (l==null){
                        player.teleport(worldGrouping.getSpawnLocation());
                    }else {
                        player.teleport(l);
                    }
                    player.sendMessage(传送成功);
                });
            }

            @Override
            public void fail(Exception exception) {
                if (exception instanceof NoAllWorldLocks){
                    player.sendMessage(世界被其他服务器加载);
                }else {
                    player.sendMessage(未知异常);
                    exception.printStackTrace();
                }

            }
        });
        return true;
    }
}
