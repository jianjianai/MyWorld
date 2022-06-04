package cn.jja8.myWorld.bukkit.command;

import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.command.admin.GoTo;
import cn.jja8.myWorld.bukkit.command.admin.LoadWorld;
import cn.jja8.myWorld.bukkit.command.admin.UnLoadAllWorld;
import cn.jja8.myWorld.bukkit.command.admin.UnLoadWorld;
import cn.jja8.patronSaint.bukkit.v3.command.Command;
import cn.jja8.patronSaint.bukkit.v3.command.CommandList;
import cn.jja8.patronSaint.bukkit.v3.command.CommandManger;
import cn.jja8.patronSaint.bukkit.v3.command.CommandMangering;

import java.io.File;
import java.util.Arrays;

public class AdminCommand {
    CommandMangering commandManger;
    public AdminCommand() {
        commandManger = new CommandManger(
                new CommandList("MyWorldAdmin")
                        .setAliases(Arrays.asList("mwa","小队世界管理"))
                        .setPower("MyWorld.admin")
                        .addCommand(
                                new Command("UnLoadWorld")
                                        .setAliases(Arrays.asList("卸载世界"))
                                        .setCommandImplement(new UnLoadWorld())
                        )
                        .addCommand(
                                new Command("UnLoadAllWorld")
                                        .setAliases(Arrays.asList("卸载全部世界"))
                                        .setCommandImplement(new UnLoadAllWorld())
                        )
                        .addCommand(
                                new Command("LoadWorld")
                                        .setAliases(Arrays.asList("加载世界"))
                                        .setCommandImplement(new LoadWorld())
                        )
                        .addCommand(
                                new Command("GoTo")
                                        .setAliases(Arrays.asList("进入世界"))
                                        .setCommandImplement(new GoTo())
                        )
        )
                .load(new File(new File(MyWorldBukkit.getMyWorldBukkit().getDataFolder(),"command"),"MyWorldAdmin.yaml"))
                .run(MyWorldBukkit.getMyWorldBukkit());
    }

    public CommandMangering getCommandManger() {
        return commandManger;
    }
}
