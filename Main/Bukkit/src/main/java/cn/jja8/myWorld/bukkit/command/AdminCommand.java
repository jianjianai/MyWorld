package cn.jja8.myWorld.bukkit.command;

import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.command.admin.GoTo;
import cn.jja8.myWorld.bukkit.command.admin.LoadWorld;
import cn.jja8.myWorld.bukkit.command.admin.NuLoadAllWorld;
import cn.jja8.myWorld.bukkit.command.admin.UnLoadWorld;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.command.CommandManger;

public class AdminCommand {
    CommandManger commandManger;
    public AdminCommand() {
        commandManger = new CommandManger(MyWorldBukkit.getMyWorldBukkit(),"MyWorldAdmin",new String[]{"mwa"}, ConfigBukkit.getPermission().管理员权限);
        commandManger.addCommand("UnLoadWorld", new UnLoadWorld());
        commandManger.addCommand("NuLoadAllWorld", new NuLoadAllWorld());
        commandManger.addCommand("LoadWorld", new LoadWorld());
        commandManger.addCommand("GoTo", new GoTo());
    }
}
