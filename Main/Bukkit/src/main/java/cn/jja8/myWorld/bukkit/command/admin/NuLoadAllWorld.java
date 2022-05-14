package cn.jja8.myWorld.bukkit.command.admin;

import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.work.MyWorldManger;
import cn.jja8.myWorld.bukkit.work.MyWorldWorldGrouping;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.command.CommandImplement;
import org.bukkit.command.CommandSender;

import java.util.function.BiConsumer;

public class NuLoadAllWorld implements CommandImplement {
    @Override
    public void command(CommandSender commandSender, String[] strings) {
        MyWorldManger.getLoadedWorldGrouping().forEach(new BiConsumer<String, MyWorldWorldGrouping>() {
            @Override
            public void accept(String s, MyWorldWorldGrouping myWorldWorldGrouping) {
                commandSender.sendMessage(ConfigBukkit.getLang().nuLoadAllWorld_卸载.replaceAll("<世界>", s));
                myWorldWorldGrouping.unLoad(true);
            }
        });
        commandSender.sendMessage(ConfigBukkit.getLang().nuLoadAllWorld_卸载完成);
    }
}
