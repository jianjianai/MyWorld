package cn.jja8.myWorld.bukkit.command.admin;

import cn.jja8.myWorld.bukkit.work.MyWorldManger;
import cn.jja8.myWorld.bukkit.work.MyWorldWorldGrouping;
import cn.jja8.patronSaint.bukkit.v3.command.CanSetUp;
import cn.jja8.patronSaint.bukkit.v3.command.CommandImplement;
import cn.jja8.patronSaint.bukkit.v3.command.NeedSet;
import org.bukkit.command.CommandSender;

import java.util.function.BiConsumer;

public class UnLoadAllWorld implements CommandImplement, CanSetUp {
   @NeedSet public String 卸载 = "正在卸载<世界>";
   @NeedSet public String 卸载完成="所有世界已经卸载完成。";
    
    @Override
    public boolean command(CommandSender commandSender, String[] strings) {
        MyWorldManger.getLoadedWorldGrouping().forEach(new BiConsumer<String, MyWorldWorldGrouping>() {
            @Override
            public void accept(String s, MyWorldWorldGrouping myWorldWorldGrouping) {
                commandSender.sendMessage(卸载.replaceAll("<世界>", s));
                myWorldWorldGrouping.unLoad(true);
            }
        });
        commandSender.sendMessage(卸载完成);
        return true;
    }
}
