package cn.jja8.myWorld.bungeecord.command;

import cn.jja8.myWorld.all.basic.teamSupport.Status;
import cn.jja8.myWorld.all.basic.teamSupport.Team;
import cn.jja8.myWorld.all.basic.teamSupport.TeamPlayer;
import cn.jja8.myWorld.bungeecord.MyWorldBungeecord;
import cn.jja8.myWorld.bungeecord.basic.Teams;
import cn.jja8.myWorld.bungeecord.basic.WorldData;
import cn.jja8.myWorld.bungeecord.config.Lang;
import cn.jja8.myWorld.bungeecord.veryUtil.ServerFind;
import cn.jja8.patronSaint_2022_3_2_1244.bungeecord.command.CommandManger;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


public class myWordCommand{
    Lang lang = MyWorldBungeecord.getLang();

    public static Pattern p = Pattern.compile(MyWorldBungeecord.getFileConfig().名称合法验证);
    //<被邀请的玩家,团队>
    Map<ProxiedPlayer, Team> 邀请map = new HashMap<>();

    public myWordCommand() {
        CommandManger commandManger = new CommandManger(MyWorldBungeecord.getMyWorldBungeecord(),"myWorld",new String[]{"myWorld:myWorld","my","mw","m","w"});
        commandManger.addCommand(new String[]{"创建团队", "NewTeam"}, (commandSender, strings) -> 直接不管(commandSender,"NewTeam",strings));
        commandManger.addCommand(new String[]{"添加信任","TrustHim"},(commandSender, strings) -> 直接不管(commandSender,"TrustHim",strings));
        commandManger.addCommand(new String[]{"取消信任","DistrustHim"}, (commandSender, strings) -> 直接不管(commandSender,"DistrustHim",strings));
        commandManger.addCommand(new String[]{"查询信息","Information"}, (commandSender, strings) -> 直接不管(commandSender,"Information",strings));
        commandManger.addCommand(new String[]{"信任列表","TrustedPeoples"}, (commandSender, strings) -> 直接不管(commandSender,"TrustedPeoples",strings));
        commandManger.addCommand(new String[]{"删除世界","DeleteWorld"},(commandSender, strings) -> 直接不管(commandSender,"DeleteWorld",strings));
        commandManger.addCommand(new String[]{"解散团队","DisbandOurTeam"}, (commandSender, strings) -> 直接不管(commandSender,"DisbandOurTeam",strings));
        commandManger.addCommand(new String[]{"退出团队","QuitThisTeam"}, (commandSender, strings) ->  直接不管(commandSender,"QuitThisTeam",strings));

        commandManger.setDefaulCommand(this::返回世界);

        commandManger.addCommand(new String[]{"邀请成员","InviteFriend"}, this::邀请成员);
        commandManger.addCommand(new String[]{"接受邀请","AcceptInvitation"}, this::接受邀请);
        commandManger.addCommand(new String[]{"去出生点","goBeginningPoint"}, this::去出生点);
        commandManger.addCommand(new String[]{"回到世界","go"}, this::返回世界);
        commandManger.addCommand(new String[]{"创建世界","NewWorld"},this::创建世界);
    }

    private void 创建世界(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof ProxiedPlayer))) return;
        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) commandSender;
        if (strings.length < 1) {
            proxiedPlayer.sendMessage(new TextComponent(lang.创建世界_需要世界名称));
            return;
        }
        if (!isLegitimate(strings[0])) {
            proxiedPlayer.sendMessage(new TextComponent(lang.创建世界_世界名不合法));
            return;
        }
        if(strings[0].contains("_nether")|strings[0].contains("_the_end")){
            proxiedPlayer.sendMessage(new TextComponent(lang.创建世界_世界名不合法));
            return;
        }
        TeamPlayer teamPlayer = getTeamPlayerNotNull(proxiedPlayer);
        Team team = teamPlayer.getTeam();
        if (team==null){
            commandSender.sendMessage(new TextComponent(lang.创建世界_还没创建团队));
            return;
        }
        if (team.getWorldName()!=null){
            commandSender.sendMessage(new TextComponent(lang.创建世界_团队已经有世界了));
            return;
        }
        if (!isLeader(teamPlayer)){
            commandSender.sendMessage(new TextComponent(lang.创建世界_不是团长));
            return;
        }
        if (WorldData.worldDataSupport.isWorldExistence(strings[0])){
            commandSender.sendMessage(new TextComponent(lang.创建世界_世界名称被他人占用));
            return;
        }
        ServerInfo serverInfo = ServerFind.getWorldBeLoadServer(strings[0]);
        if (serverInfo==null){
            serverInfo = ServerFind.getLoadWorldServer();
        }
        if (serverInfo==null){
            commandSender.sendMessage(new TextComponent(lang.创建世界_所有服务器全满));
            return;
        }
        String ServerName = serverInfo.getName();
        MyWorldBungeecord.getPreciseExecution().jumpAndImplement(proxiedPlayer, serverInfo, () -> {
            proxiedPlayer.chat("/myWorld ServerName ServerName ServerName "+ ServerName);
            proxiedPlayer.chat("/myWorld NewWorld "+strings[0]);
        });
    }

    private void 接受邀请(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof ProxiedPlayer))) return;
        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) commandSender;
        Team 邀请团队 = 邀请map.get(proxiedPlayer);
        if (邀请团队 == null) {
            proxiedPlayer.sendMessage(new TextComponent(lang.接受邀请_没被邀请));
            return;
        }
        TeamPlayer teamPlayer = getTeamPlayerNotNull(proxiedPlayer);
        Team 团队 = teamPlayer.getTeam();
        if (团队 != null) {
            proxiedPlayer.sendMessage(new TextComponent(lang.接受邀请_已经有团队.replaceAll("<团队>", 团队.getTeamName())));
            return;
        }
        teamPlayer.SetTeam(邀请团队);
        teamPlayer.setStatus(Status.player);
        proxiedPlayer.sendMessage(new TextComponent(lang.接受邀请_接受成功.replaceAll("<团队>", 邀请团队.getTeamName())));
    }

    private void 邀请成员(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof ProxiedPlayer))) return;
        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) commandSender;
        TeamPlayer teamPlayer = getTeamPlayerNotNull(proxiedPlayer);
        Team 团队 = teamPlayer.getTeam();
        if (团队 == null) {
            proxiedPlayer.sendMessage(new TextComponent(lang.邀请成员_玩家没有团队));
            return;
        }
        if (!isAdmin(teamPlayer)) {
            proxiedPlayer.sendMessage(new TextComponent(lang.邀请成员_不是管理));
            return;
        }
        if (strings.length < 1) {
            proxiedPlayer.sendMessage(new TextComponent(lang.邀请成员_没有参数));
            return;
        }
        ProxiedPlayer 被邀玩家 = MyWorldBungeecord.getMyWorldBungeecord().getProxy().getPlayer(strings[0]);
        if (被邀玩家 == null) {
            proxiedPlayer.sendMessage(new TextComponent(lang.邀请成员_玩家不在线.replaceAll("<不在线玩家>", strings[0])));
            return;
        }
        邀请map.put(被邀玩家, 团队);
        被邀玩家.sendMessage(new TextComponent(lang.邀请成员_被邀请信息.replaceAll("<玩家>", proxiedPlayer.getName()).replaceAll("<团队>", 团队.getTeamName())));
        proxiedPlayer.sendMessage(new TextComponent(lang.邀请成员_邀请成功));
    }

    private void 去出生点(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof ProxiedPlayer))) return;
        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) commandSender;
        Team team = getTeamPlayerNotNull(proxiedPlayer).getTeam();
        if (team==null){
            commandSender.sendMessage(new TextComponent(lang.去出生点_还没创建团队));
            return;
        }
        String worldName = team.getWorldName();
        if (worldName==null){
            commandSender.sendMessage(new TextComponent(lang.去出生点_还没创建世界));
            return;
        }
        ServerInfo serverInfo = ServerFind.getWorldBeLoadServer(worldName);
        if (serverInfo==null){
            serverInfo = ServerFind.getLoadWorldServer();
        }
        if (serverInfo==null){
            commandSender.sendMessage(new TextComponent(lang.去出生点_所有服务器全满));
            return;
        }
        String ServerName = serverInfo.getName();
        MyWorldBungeecord.getPreciseExecution().jumpAndImplement(proxiedPlayer, serverInfo, () -> {
            proxiedPlayer.chat("/myWorld ServerName ServerName ServerName "+ ServerName);
            proxiedPlayer.chat("/myWorld goBeginningPoint");
        });
    }


    private void 返回世界(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof ProxiedPlayer))) return;
        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) commandSender;
        TeamPlayer teamPlayer = getTeamPlayerNotNull(proxiedPlayer);
        Team team = teamPlayer.getTeam();
        if (team==null){
            commandSender.sendMessage(new TextComponent(lang.返回世界_还没创建团队));
            return;
        }
        String worldName = team.getWorldName();
        if (worldName==null){
            commandSender.sendMessage(new TextComponent(lang.返回世界_还没创建世界));
            return;
        }
        ServerInfo serverInfo = ServerFind.getWorldBeLoadServer(worldName);
        if (serverInfo==null){
            serverInfo = ServerFind.getLoadWorldServer();
        }
        if (serverInfo==null){
            commandSender.sendMessage(new TextComponent(lang.返回世界_所有服务器全满));
            return;
        }
        String ServerName = serverInfo.getName();
        MyWorldBungeecord.getPreciseExecution().jumpAndImplement(proxiedPlayer, serverInfo, () -> {
            proxiedPlayer.chat("/myWorld ServerName ServerName ServerName "+ ServerName);
            proxiedPlayer.chat("/myWorld go");
        });
    }

    private void 直接不管(CommandSender commandSender, String s ,String[] strings) {
        if ((!(commandSender instanceof ProxiedPlayer))) return;
        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) commandSender;
        StringBuilder s1 = new StringBuilder("/myWorld " + s);
        for (String s2:strings){
            s1.append(" ").append(s2);
        }
        proxiedPlayer.chat(s1.toString());
    }


    private static boolean isAdmin(TeamPlayer teamPlayer){
        return teamPlayer.getStatus()== Status.admin|
                isLeader(teamPlayer);
    }
    private static boolean isLeader(TeamPlayer teamPlayer){
        return teamPlayer.getStatus()==Status.leader;
    }
    private static TeamPlayer getTeamPlayerNotNull(ProxiedPlayer player){
        TeamPlayer teamPlayer = Teams.teamManager.getTamePlayer(player.getUniqueId());
        if (teamPlayer==null){
            teamPlayer = Teams.teamManager.newTamePlayer(player.getUniqueId(),player.getName());
        }
        return teamPlayer;
    }
    /**
     * 检查字符串是否合法
     *
     * @param str 待校验字符串
     * @return 字符串必须只能由数字字母下划线组成
     */
    public static boolean isLegitimate(String str) {
        if (str.length()<3){
            return false;
        }
        return p.matcher(str).find();
    }
}
