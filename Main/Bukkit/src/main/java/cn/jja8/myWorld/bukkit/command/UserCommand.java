package cn.jja8.myWorld.bukkit.command;

import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.command.user.*;
import cn.jja8.myWorld.bukkit.work.MyWorldTeam;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.command.CommandManger;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class UserCommand {
    //<被邀请的玩家,团队>
    public Map<Player, MyWorldTeam> acceptInvitationMap = new HashMap<>();
    CommandManger commandManger;
    public UserCommand() {
        commandManger = new CommandManger(MyWorldBukkit.getMyWorldBukkit(), "myWorld",new String[]{"my","mw","m","w"}, ConfigBukkit.getPermission().user);
        Go go = new Go();
        commandManger.setDefaulCommand(go);
        commandManger.addCommand(new String[]{"NewTeam","创建团队"}, new NewTeam());
        commandManger.addCommand(new String[]{"DisbandOurTeam","解散团队"}, new DisbandOurTeam());
        commandManger.addCommand(new String[]{"InviteFriend","邀请成员"}, new InviteFriend(this));
        commandManger.addCommand(new String[]{"AcceptInvitation","接受邀请"}, new AcceptInvitation(this));
        commandManger.addCommand(new String[]{"QuitThisTeam","退出团队"}, new QuitThisTeam());
        commandManger.addCommand(new String[]{"TrustHim","添加信任"}, new TrustHim());
        commandManger.addCommand(new String[]{"DistrustHim","取消信任"}, new DistrustHim());
        commandManger.addCommand(new String[]{"Information","查询信息"}, new Information());
        commandManger.addCommand(new String[]{"TrustedPeoples","信任列表"}, new TrustedPeoples());
        commandManger.addCommand(new String[]{"DeleteWorld","删除世界"}, new DeleteWorld());
        commandManger.addCommand(new String[]{"ServerName"}, new ServerName());
        commandManger.addCommand(new String[]{"goBeginningPoint","去出生点"}, new goBeginningPoint());
        commandManger.addCommand(new String[]{"go","回到世界"}, go);
        commandManger.addCommand(new String[]{"NewWorld","创建世界"}, new NewWorld(go));
        commandManger.addCommand(new String[]{"GoToWorld","去世界"}, new GoToWorld(),ConfigBukkit.getPermission().comm_user_GoToWorld);
    }

    public CommandManger getCommandManger() {
        return commandManger;
    }
}
