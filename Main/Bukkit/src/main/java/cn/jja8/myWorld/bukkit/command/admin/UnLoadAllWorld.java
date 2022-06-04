package cn.jja8.myWorld.bukkit.command.admin;

import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.work.MyWorldManger;
import cn.jja8.myWorld.bukkit.work.MyWorldWorldGrouping;
import cn.jja8.patronSaint.bukkit.v3.command.CommandImplement;
import org.bukkit.command.CommandSender;

import java.util.function.BiConsumer;

public class UnLoadAllWorld implements CommandImplement {
    @Override
    public boolean command(CommandSender commandSender, String[] strings) {
        MyWorldManger.getLoadedWorldGrouping().forEach(new BiConsumer<String, MyWorldWorldGrouping>() {
            @Override
            public void accept(String s, MyWorldWorldGrouping myWorldWorldGrouping) {
                commandSender.sendMessage(ConfigBukkit.getLang().nuLoadAllWorld_卸载.replaceAll("<世界>", s));
                myWorldWorldGrouping.unLoad(true);
            }
        });
        commandSender.sendMessage(ConfigBukkit.getLang().nuLoadAllWorld_卸载完成);
        return true;
    }
}
