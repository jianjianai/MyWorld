package cn.jja8.myWorld.bukkit.command;

import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.command.user.*;
import cn.jja8.myWorld.bukkit.work.MyWorldTeam;
import cn.jja8.patronSaint.bukkit.v3.command.Command;
import cn.jja8.patronSaint.bukkit.v3.command.CommandList;
import cn.jja8.patronSaint.bukkit.v3.command.CommandManger;
import cn.jja8.patronSaint.bukkit.v3.command.CommandMangering;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class UserCommand {
    //<被邀请的玩家,团队>
    public Map<Player, MyWorldTeam> acceptInvitationMap = new HashMap<>();
    CommandMangering commandManger;
    public UserCommand() {
        Go go = new Go();
        commandManger = new CommandManger(
                new CommandList("MyWorld")
                        .setAliases(Arrays.asList("my","mw","m","w","小队世界"))
                        .setPower("MyWorld.user")
                        .setCommandImplement(go)
                        .addCommand(
                                new Command("NewTeam")
                                        .setAliases(Arrays.asList("创建团队"))
                                        .setCommandImplement(new NewTeam())
                        )
                        .addCommand(
                                new Command("DisbandOurTeam")
                                        .setAliases(Arrays.asList("解散团队"))
                                        .setCommandImplement(new DisbandOurTeam())
                        )
                        .addCommand(
                                new Command("InviteFriend")
                                        .setAliases(Arrays.asList("邀请成员"))
                                        .setCommandImplement(new InviteFriend(this))
                        )
                        .addCommand(
                                new Command("QuitThisTeam")
                                        .setAliases(Arrays.asList("退出团队"))
                                        .setCommandImplement(new QuitThisTeam())
                        )
                        .addCommand(
                                new Command("TrustHim")
                                        .setAliases(Arrays.asList("添加信任"))
                                        .setCommandImplement(new TrustHim())
                        )
                        .addCommand(
                                new Command("DistrustHim")
                                        .setAliases(Arrays.asList("取消信任"))
                                        .setCommandImplement( new DistrustHim())
                        )
                        .addCommand(
                                new Command("Information")
                                        .setAliases(Arrays.asList("查询信息"))
                                        .setCommandImplement( new Information())
                        )
                        .addCommand(
                                new Command("TrustedPeoples")
                                        .setAliases(Arrays.asList("信任列表"))
                                        .setCommandImplement( new TrustedPeoples())
                        )
                        .addCommand(
                                new Command("DeleteWorld")
                                        .setAliases(Arrays.asList("删除世界"))
                                        .setCommandImplement( new DeleteWorld())
                        )
                        .addCommand(
                                new Command("ServerName")
                                        .setCommandImplement( new ServerName())
                        )
                        .addCommand(
                                new Command("GoBeginningPoint")
                                        .setAliases(Arrays.asList("去出生点"))
                                        .setCommandImplement( new GoBeginningPoint())
                        )
                        .addCommand(
                                new Command("Go")
                                        .setAliases(Arrays.asList("回到世界"))
                                        .setCommandImplement(go)
                        )
                        .addCommand(
                                new Command("NewWorld")
                                        .setAliases(Arrays.asList("创建世界"))
                                        .setCommandImplement( new NewWorld(go))
                        )
                        .addCommand(
                                new Command("GoToWorld")
                                        .setAliases(Arrays.asList("去世界"))
                                        .setCommandImplement( new GoToWorld())
                                        .setPower("MyWorld.comm.user.GoToWorld")
                        )
        )
                .load(new File(new File(MyWorldBukkit.getMyWorldBukkit().getDataFolder(),"command"),"MyWorld.yaml"))
                .run(MyWorldBukkit.getMyWorldBukkit());
    }

    public CommandMangering getCommandManger() {
        return commandManger;
    }
}
