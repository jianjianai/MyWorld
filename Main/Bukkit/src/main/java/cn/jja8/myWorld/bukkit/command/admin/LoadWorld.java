package cn.jja8.myWorld.bukkit.command.admin;

import cn.jja8.myWorld.bukkit.work.MyWorldManger;
import cn.jja8.myWorld.bukkit.work.MyWorldWorldGroup;
import cn.jja8.myWorld.bukkit.work.MyWorldWorldGrouping;
import cn.jja8.myWorld.bukkit.work.error.NoAllWorldLocks;
import cn.jja8.patronSaint.bukkit.v3.command.CanSetUp;
import cn.jja8.patronSaint.bukkit.v3.command.CommandImplement;
import cn.jja8.patronSaint.bukkit.v3.command.canSetUpLoadType.Lang;
import org.bukkit.command.CommandSender;

public class LoadWorld implements CommandImplement, CanSetUp {
    @Lang public String 未指定世界名="未指定世界名";
    @Lang public String 世界不存在="<世界>世界不存在。";
    @Lang public String 加载完成 ="加载完成";
    @Lang public String 世界被其他服务器加载 = "被其他服务器加载";
    @Lang public String 未知异常 = "请在后台查看详细信息。";

    @Override
    public boolean command(CommandSender commandSender, String[] strings) {
        if (strings.length < 1) {
            commandSender.sendMessage(未指定世界名);
            return true;
        }
        MyWorldWorldGroup myWorldWorldGroup = MyWorldManger.getWorldGroup(strings[0]);
        if (myWorldWorldGroup == null) {
            commandSender.sendMessage(世界不存在.replaceAll("<世界>", strings[0]));
            return true;
        }
        myWorldWorldGroup.load(new MyWorldWorldGroup.OnLoad() {
            @Override
            public void onload(MyWorldWorldGrouping worldGrouping) {
                commandSender.sendMessage(加载完成);
            }
            @Override
            public void fail(Exception exception) {
                if (exception instanceof NoAllWorldLocks){
                    commandSender.sendMessage(世界被其他服务器加载);
                }else {
                    exception.printStackTrace();
                    commandSender.sendMessage(未知异常);
                }
            }
        });
        return true;
    }
}
