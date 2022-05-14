package cn.jja8.myWorld.bukkit.command.admin;

import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.work.error.NoAllWorldLocks;
import cn.jja8.myWorld.bukkit.work.MyWorldManger;
import cn.jja8.myWorld.bukkit.work.MyWorldWorldGroup;
import cn.jja8.myWorld.bukkit.work.MyWorldWorldGrouping;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.command.CommandImplement;
import org.bukkit.command.CommandSender;

public class LoadWorld implements CommandImplement {
    @Override
    public void command(CommandSender commandSender, String[] strings) {
        if (strings.length < 1) {
            commandSender.sendMessage(ConfigBukkit.getLang().loadWorld_未指定世界名);
            return;
        }
        MyWorldWorldGroup myWorldWorldGroup = MyWorldManger.getWorldGroup(strings[0]);
        if (myWorldWorldGroup == null) {
            commandSender.sendMessage(ConfigBukkit.getLang().loadWorld_世界不存在.replaceAll("<世界>", strings[0]));
            return;
        }
        myWorldWorldGroup.load(new MyWorldWorldGroup.OnLoad() {
            @Override
            public void onload(MyWorldWorldGrouping worldGrouping) {
                commandSender.sendMessage(ConfigBukkit.getLang().loadWorld_加载完成);
            }
            @Override
            public void fail(Exception exception) {
                if (exception instanceof NoAllWorldLocks){
                    commandSender.sendMessage(ConfigBukkit.getLang().loadWorld_世界被其他服务器加载);
                }else {
                    exception.printStackTrace();
                    commandSender.sendMessage(ConfigBukkit.getLang().loadWorld_未知异常);
                }
            }
        });
    }
}
