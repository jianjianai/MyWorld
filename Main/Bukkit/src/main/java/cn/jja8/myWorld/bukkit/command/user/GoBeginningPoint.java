package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.work.error.NoAllWorldLocks;
import cn.jja8.myWorld.bukkit.work.MyWorldManger;
import cn.jja8.myWorld.bukkit.work.MyWorldTeam;
import cn.jja8.myWorld.bukkit.work.MyWorldWorldGroup;
import cn.jja8.myWorld.bukkit.work.MyWorldWorldGrouping;
import cn.jja8.patronSaint.bukkit.v3.command.CanSetUp;
import cn.jja8.patronSaint.bukkit.v3.command.CommandImplement;
import cn.jja8.patronSaint.bukkit.v3.command.NeedSet;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GoBeginningPoint implements CommandImplement , CanSetUp {
    @NeedSet public String 传送成功 = "已将你传送到出生点。";
    @NeedSet public String 团队没有世界 = "你的团队没有世界。";
    @NeedSet public String 没有团队 = "你还没有加入团队。";
    @NeedSet public String 世界被其他服务器加载 = "世界已经被其他服务器加载，（这个信息如果bc服务器配置正常是不会出现的）";
    @NeedSet public String 未知异常 = "加载世界出错，请联系管理员。";
    @Override
    public boolean command(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return true;
        Player player = (Player) commandSender;
        MyWorldTeam team = MyWorldManger.getPlayer(player).getTeam();
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
                    player.teleport(worldGrouping.getSpawnLocation());
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
