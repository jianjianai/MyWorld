package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.work.error.NoAllWorldLocks;
import cn.jja8.myWorld.bukkit.work.MyWorldManger;
import cn.jja8.myWorld.bukkit.work.MyWorldTeam;
import cn.jja8.myWorld.bukkit.work.MyWorldWorldGroup;
import cn.jja8.myWorld.bukkit.work.MyWorldWorldGrouping;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.command.CommandImplement;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GoBeginningPoint implements CommandImplement {
    @Override
    public void command(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        MyWorldTeam team = MyWorldManger.getPlayer(player).getTeam();
        if (team == null) {
            player.sendMessage(ConfigBukkit.getLang().去出生点_没有团队);
            return;
        }
        MyWorldWorldGroup worldGroup = team.getWorldGroup();
        if (worldGroup == null) {
            player.sendMessage(ConfigBukkit.getLang().去出生点_团队没有世界);
            return;
        }
        worldGroup.load(new MyWorldWorldGroup.OnLoad() {
            @Override
            public void onload(MyWorldWorldGrouping worldGrouping) {
                Bukkit.getScheduler().runTask(MyWorldBukkit.getMyWorldBukkit(), () -> {
                    worldGrouping.playerBackSpawn(player);
                    player.sendMessage(ConfigBukkit.getLang().去出生点_传送成功);
                });
            }

            @Override
            public void fail(Exception exception) {
                if (exception instanceof NoAllWorldLocks){
                    player.sendMessage(ConfigBukkit.getLang().去出生点_世界被其他服务器加载);
                }else {
                    player.sendMessage(ConfigBukkit.getLang().去出生点_未知异常);
                    exception.printStackTrace();
                }
            }
        });
    }
}
