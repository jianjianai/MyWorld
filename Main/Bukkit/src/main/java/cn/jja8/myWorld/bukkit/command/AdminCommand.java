package cn.jja8.myWorld.bukkit.command;

import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.command.admin.GoTo;
import cn.jja8.myWorld.bukkit.command.admin.LoadWorld;
import cn.jja8.myWorld.bukkit.command.admin.UnLoadAllWorld;
import cn.jja8.myWorld.bukkit.command.admin.UnLoadWorld;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.command.CommandManger;

public class AdminCommand {
    CommandManger commandManger;
    public AdminCommand() {
        commandManger = new CommandManger(MyWorldBukkit.getMyWorldBukkit(),"MyWorldAdmin",new String[]{"mwa"}, ConfigBukkit.getPermission().管理员权限);
        commandManger.addCommand(new String[]{"UnLoadWorld","卸载世界"}, new UnLoadWorld());
        commandManger.addCommand(new String[]{"UnLoadAllWorld","卸载全部世界"}, new UnLoadAllWorld());
        commandManger.addCommand(new String[]{"LoadWorld","加载世界"}, new LoadWorld());
        commandManger.addCommand(new String[]{"GoTo","进入世界"}, new GoTo());
    }

    public CommandManger getCommandManger() {
        return commandManger;
    }
}
