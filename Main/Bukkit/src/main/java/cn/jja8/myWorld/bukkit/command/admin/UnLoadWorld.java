package cn.jja8.myWorld.bukkit.command.admin;

import cn.jja8.myWorld.bukkit.work.MyWorldManger;
import cn.jja8.myWorld.bukkit.work.MyWorldWorldGroup;
import cn.jja8.myWorld.bukkit.work.MyWorldWorldGrouping;
import cn.jja8.patronSaint.bukkit.v3.command.CanSetUp;
import cn.jja8.patronSaint.bukkit.v3.command.CommandImplement;
import cn.jja8.patronSaint.bukkit.v3.command.NeedSet;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class UnLoadWorld implements CommandImplement , CanSetUp {
    @NeedSet public String 世界没有加载 = "<世界>没有被加载，不用卸载哦。";
    @NeedSet public String 卸载完成="<世界>卸载完成。";
    @NeedSet public String 未指定世界名="未指定世界名";
    @NeedSet public String 世界不存在="<世界>世界不存在。";

    @Override
    public boolean command(CommandSender commandSender, String[] strings) {
        if (strings.length < 1) {
            commandSender.sendMessage(未指定世界名);
            return true;
        }
        MyWorldWorldGroup worldGroup = MyWorldManger.getWorldGroup(strings[0]);
        if (worldGroup == null) {
            commandSender.sendMessage(世界不存在.replaceAll("<世界>", strings[0]));
            return true;
        }
        MyWorldWorldGrouping myWorldWorldGrouping = worldGroup.getLoading();
        if (myWorldWorldGrouping == null) {
            commandSender.sendMessage(世界没有加载.replaceAll("<世界>", strings[0]));
            return true;
        }
        myWorldWorldGrouping.unLoad(true);
        commandSender.sendMessage(卸载完成.replaceAll("<世界>", strings[0]));
        return true;
    }

    @Override
    public List<String> tabCompletion(CommandSender commandSender, String[] strings) {
        return new ArrayList<>(MyWorldManger.getLoadedWorldGrouping().keySet());
    }
}
