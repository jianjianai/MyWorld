package cn.jja8.myWorld.bukkit.command.admin;

import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.work.MyWorldManger;
import cn.jja8.myWorld.bukkit.work.MyWorldWorldGroup;
import cn.jja8.myWorld.bukkit.work.MyWorldWorldGrouping;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.command.CommandImplement;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class UnLoadWorld implements CommandImplement {
    @Override
    public void command(CommandSender commandSender, String[] strings) {
        if (strings.length < 1) {
            commandSender.sendMessage(ConfigBukkit.getLang().nuLoadAllWorld_未指定世界名);
            return;
        }
        MyWorldWorldGroup worldGroup = MyWorldManger.getWorldGroup(strings[0]);
        if (worldGroup == null) {
            commandSender.sendMessage(ConfigBukkit.getLang().nuLoadWorld_世界不存在.replaceAll("<世界>", strings[0]));
            return;
        }
        MyWorldWorldGrouping myWorldWorldGrouping = worldGroup.getLoaded();
        if (myWorldWorldGrouping == null) {
            commandSender.sendMessage(ConfigBukkit.getLang().nuLoadWorld_世界没有加载.replaceAll("<世界>", strings[0]));
            return;
        }
        myWorldWorldGrouping.unLoad(true);
        commandSender.sendMessage(ConfigBukkit.getLang().nuLoadWorld_卸载完成.replaceAll("<世界>", strings[0]));
    }

    @Override
    public List<String> TabCompletion(CommandSender commandSender, String[] strings) {
        return new ArrayList<>(MyWorldManger.getLoadedWorldGrouping().keySet());
    }
}
