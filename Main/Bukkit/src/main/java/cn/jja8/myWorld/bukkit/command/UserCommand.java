package cn.jja8.myWorld.bukkit.command;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Team;
import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.command.user.*;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.command.CommandImplement;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.command.CommandManger;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class UserCommand {
    //<被邀请的玩家,团队>
    public Map<Player, Team> 邀请map = new HashMap<>();

    public UserCommand() {
        CommandManger commandManger = new CommandManger(MyWorldBukkit.getMyWorldBukkit(), "myWorld",new String[]{"my","mw","m","w"}, ConfigBukkit.getPermission().使用权限);
        CommandImplement go = new Go();
        commandManger.setDefaulCommand(go);
        commandManger.addCommand(new String[]{"创建团队", "NewTeam"}, new NewTeam());
        commandManger.addCommand(new String[]{"解散团队", "DisbandOurTeam"}, new DisbandOurTeam());
        commandManger.addCommand(new String[]{"邀请成员", "InviteFriend"}, new InviteFriend(this));
        commandManger.addCommand(new String[]{"接受邀请", "AcceptInvitation"}, new AcceptInvitation(this));
        commandManger.addCommand(new String[]{"退出团队", "QuitThisTeam"}, new QuitThisTeam());
        commandManger.addCommand(new String[]{"添加信任", "TrustHim"}, new TrustHim());
        commandManger.addCommand(new String[]{"取消信任","DistrustHim"}, new DistrustHim());
        commandManger.addCommand(new String[]{"查询信息", "Information"}, new Information());
        commandManger.addCommand(new String[]{"信任列表", "TrustedPeoples"}, new TrustedPeoples());
        commandManger.addCommand(new String[]{"删除世界", "DeleteWorld"}, new DeleteWorld());
        commandManger.addCommand(new String[]{"ServerName"}, new ServerName());
        commandManger.addCommand(new String[]{"去出生点", "goBeginningPoint"}, new goBeginningPoint());
        commandManger.addCommand(new String[]{"回到世界","go"}, go);
        commandManger.addCommand(new String[]{"创建世界", "NewWorld"}, new NewWorld());
    }
}
