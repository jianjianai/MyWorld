package cn.jja8.myWorld.bukkit.command;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Status;
import cn.jja8.myWorld.all.basic.DatasheetSupport.Team;
import cn.jja8.myWorld.all.basic.DatasheetSupport.TeamPlayer;
import cn.jja8.myWorld.all.basic.DatasheetSupport.Worlds;
import cn.jja8.myWorld.all.veryUtil.StringTool;
import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.basic.Teams;
import cn.jja8.myWorld.bukkit.config.Lang;
import cn.jja8.myWorld.bukkit.config.Permission;
import cn.jja8.myWorld.bukkit.config.TeamConfig;
import cn.jja8.myWorld.bukkit.config.WorldConfig;
import cn.jja8.myWorld.bukkit.word.PlayerWorlds;
import cn.jja8.myWorld.bukkit.word.error.NoWorldLocks;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.command.CommandImplement;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.command.CommandManger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class UserCommand {
    WorldConfig worldConfig = ConfigBukkit.getWorldConfig();
    Lang lang = ConfigBukkit.getLang();
    TeamConfig teamConfig = ConfigBukkit.getTeamConfig();
    Permission permission = ConfigBukkit.getPermission();

    public static Pattern p = Pattern.compile(ConfigBukkit.getFileConfig().名称合法验证);
    //<被邀请的玩家,团队>
    Map<Player, Team> 邀请map = new HashMap<>();

    public UserCommand() {
        CommandManger commandManger = new CommandManger(MyWorldBukkit.getMyWorldBukkit(), "myWorld",new String[]{"my","mw","m","w"},permission.使用权限);
        commandManger.setDefaulCommand(this::返回世界);
        commandManger.addCommand(new String[]{"创建团队","NewTeam"},this::创建团队);
        commandManger.addCommand(new String[]{"解散团队","DisbandOurTeam"}, this::解散团队);
        commandManger.addCommand(new String[]{"邀请成员","InviteFriend"}, this::邀请成员);
        commandManger.addCommand(new String[]{"接受邀请","AcceptInvitation"}, this::接受邀请);
        commandManger.addCommand(new String[]{"退出团队","QuitThisTeam"}, this::退出团队);
        commandManger.addCommand(new String[]{"添加信任","TrustHim"}, this::添加信任);
        commandManger.addCommand(new String[]{"取消信任","DistrustHim"}, new CommandImplement() {
            @Override
            public void command(CommandSender commandSender, String[] strings) {
                取消信任(commandSender, strings);
            }

            @Override
            public List<String> TabCompletion(CommandSender commandSender, String[] strings) {
                return 取消信任_NBT补全(commandSender, strings);
            }
        });
        commandManger.addCommand(new String[]{"查询信息","Information"}, this::查询信息);
        commandManger.addCommand(new String[]{"信任列表","TrustedPeoples"}, this::信任列表);
        commandManger.addCommand(new String[]{"删除世界","DeleteWorld"}, this::删除世界);
        commandManger.addCommand(new String[]{"ServerName"}, this::ServerName);


        commandManger.addCommand(new String[]{"去出生点","goBeginningPoint"}, this::去出生点);
        commandManger.addCommand(new String[]{"回到世界","go"}, this::返回世界);
        commandManger.addCommand(new String[]{"创建世界","NewWorld"}, this::创建世界);
    }


    private void ServerName(CommandSender commandSender, String[] strings) {
        if (!worldConfig.自动配置服务器名称){
            return;
        }
        if (strings.length==3){
           if (strings[0].equals("ServerName")&&strings[1].equals("ServerName")){
               worldConfig.服务器名称 = strings[2];
           }
        }
    }

    private void 返回世界(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        Team team = getTeamPlayerNotNull(player).getTeam();
        if (team == null) {
            player.sendMessage(lang.返回世界_没有团队);
            return;
        }
        Worlds worlds = team.getWorlds();
        if (worlds == null) {
            player.sendMessage(lang.返回世界_团队没有世界);
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(MyWorldBukkit.getMyWorldBukkit(), () -> {
            PlayerWorlds playerWorlds = MyWorldBukkit.getPlayerWordMangaer().loadPlayerWorlds(worlds);
            if (playerWorlds==null){
                player.sendMessage(lang.返回世界_世界被其他服务器加载);
                return;
            }
            Bukkit.getScheduler().runTask(MyWorldBukkit.getMyWorldBukkit(), () -> {
                playerWorlds.playerBack(player);
                player.sendMessage(lang.返回世界_传送成功);
            });
        });
    }

    private void 删除世界(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        if (strings.length < 1) {
            player.sendMessage(lang.删除世界_删除确认);
            return;
        }
        if (!"yes".equals(strings[0])) {
            player.sendMessage(lang.删除世界_删除确认);
            return;
        }
        TeamPlayer teamPlayer = getTeamPlayerNotNull(player);
        if (!isLeader(teamPlayer)) {
            player.sendMessage(lang.删除世界_不是团长);
            return;
        }
        Team 团队 = teamPlayer.getTeam();
        if (团队 == null) {
            player.sendMessage(lang.删除世界_玩家没有团队);
            return;
        }
        Worlds worlds = 团队.getWorlds();
        if (worlds==null){
            player.sendMessage(lang.删除世界_世界不存在);
            return;
        }
        try {
            MyWorldBukkit.getPlayerWordMangaer().delPlayerWorlds(worlds);
        } catch (NoWorldLocks e) {
            player.sendMessage(lang.删除世界_世界被其他服务器加载);
        }
        player.sendMessage(lang.删除世界_删除成功);
    }

    private void 信任列表(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        Team 团队 = getTeamPlayerNotNull(player).getTeam();
        if (团队 == null) {
            player.sendMessage(lang.信任列表_你没有团队);
            return;
        }
        Worlds worlds = 团队.getWorlds();
        if (worlds == null) {
            player.sendMessage(lang.信任列表_团队没有世界);
            return;
        }
        PlayerWorlds 世界 = MyWorldBukkit.getPlayerWordMangaer().getBeLoadPlayerWorlds(worlds);
        if (世界==null){
            player.sendMessage(lang.信任列表_世界未加载);
            return;
        }
        String 世界名称 = 世界.getName();
        List<String> 信任列表 = 世界.getPlayerWordInform().BeTrustList();
        player.sendMessage(lang.信任列表_信息.replaceAll("<世界>",世界名称).replaceAll("<数量>", String.valueOf(信任列表.size())).replaceAll("<列表>",信任列表.toString()));
    }

    private void 查询信息(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        Team 团队 = getTeamPlayerNotNull(player).getTeam();
        if (团队==null){
            player.sendMessage(lang.查询信息_还没有团队);
            return;
        }

        HashMap<String,String> map = new HashMap<>();
        map.put("<团队>",团队.getTeamName());
        Worlds worlds = 团队.getWorlds();
        if (worlds==null){
            map.put("<团队世界信息>",lang.查询信息_团队没有世界);
        }else {
            map.put("<团队世界信息>",lang.查询信息_团队世界信息.replaceAll("<世界>",worlds.getWorldsName()));
        }

        map.put("<团长>",getPlayersNotNull(团队,Status.leader).toString());
        map.put("<管理员列表>",getPlayersNotNull(团队,Status.admin).toString());
        map.put("<队员列表>",getPlayersNotNull(团队,Status.player).toString());
        List<String> list = StringTool.stringListReplaces(lang.查询信息_长信息列表,map);
        for (String s : list) {
            player.sendMessage(s);
        }
    }

    private List<TeamPlayer> getPlayersNotNull(Team team,Status status) {
        List<TeamPlayer> playerList = team.getPlayers(status);
        if (playerList==null){
            playerList = new ArrayList<>();
        }
        return playerList;
    }

    private List<String> 取消信任_NBT补全(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return null;
        Player player = (Player) commandSender;
        TeamPlayer teamPlayer = getTeamPlayerNotNull(player);
        Team 团队 = teamPlayer.getTeam();
        if (团队 == null) {
            return null;
        }
        if (!isLeader(teamPlayer)){
            return null;
        }
        Worlds worlds = 团队.getWorlds();
        if (worlds==null){
            return null;
        }
        PlayerWorlds 世界 = MyWorldBukkit.getPlayerWordMangaer().getBeLoadPlayerWorlds(worlds);
        if (世界==null){
            return null;
        }
        return 世界.getPlayerWordInform().BeTrustList();
    }


    private void 取消信任(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        if (strings.length < 1) {
            player.sendMessage(lang.取消信任_没有参数);
            return;
        }
        TeamPlayer teamPlayer = getTeamPlayerNotNull(player);
        Team 团队 = teamPlayer.getTeam();
        if (团队 == null) {
            player.sendMessage(lang.取消信任_你没有团队);
            return;
        }
        if (!isAdmin(teamPlayer)){
            player.sendMessage(lang.取消信任_权限不足);
            return;
        }
        Worlds worlds = 团队.getWorlds();
        if (worlds==null){
            player.sendMessage(lang.取消信任_世界不存在);
        }
        PlayerWorlds 世界 = MyWorldBukkit.getPlayerWordMangaer().getBeLoadPlayerWorlds(worlds);
        if (世界==null){
            player.sendMessage(lang.取消信任_世界未加载);
            return;
        }
        世界.getPlayerWordInform().delBeTrust(strings[0]);
        player.sendMessage(lang.取消信任_取消成功);
    }

    private void 添加信任(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        if (strings.length < 1) {
            player.sendMessage(lang.添加信任_没有参数);
            return;
        }
        TeamPlayer teamPlayer = getTeamPlayerNotNull(player);
        Team 团队 = teamPlayer.getTeam();
        if (团队 == null) {
            player.sendMessage(lang.添加信任_你没有团队);
            return;
        }
        if (!isAdmin(teamPlayer)){
            player.sendMessage(lang.添加信任_权限不足);
            return;
        }
        Worlds worlds = 团队.getWorlds();
        if (worlds==null){
            player.sendMessage(lang.添加信任_世界不存在);
        }
        PlayerWorlds 世界 = MyWorldBukkit.getPlayerWordMangaer().getBeLoadPlayerWorlds(worlds);
        if (世界==null){
            player.sendMessage(lang.添加信任_世界未加载);
            return;
        }
        世界.getPlayerWordInform().addBeTrust(strings[0]);
        player.sendMessage(lang.添加信任_添加成功);
    }

    private void 退出团队(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        TeamPlayer teamPlayer = getTeamPlayerNotNull(player);
        Team 团队 = teamPlayer.getTeam();
        if (团队 == null) {
            player.sendMessage(lang.退出团队_你没有团队);
            return;
        }
        if (isLeader(teamPlayer)) {
            player.sendMessage(lang.退出团队_团长不能退出);
            return;
        }
        if (strings.length < 1) {
            player.sendMessage(lang.退出团队_退出确认);
            return;
        }
        if (!"yes".equals(strings[0])) {
            player.sendMessage(lang.退出团队_退出确认);
            return;
        }
        teamPlayer.setTeam(null);
        teamPlayer.setStatus(null);
        player.sendMessage(lang.退出团队_退出成功);
    }

    private void 接受邀请(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        Team 邀请团队 = 邀请map.get(player);
        if (邀请团队 == null) {
            player.sendMessage(lang.接受邀请_没被邀请);
            return;
        }
        TeamPlayer teamPlayer = getTeamPlayerNotNull(player);
        Team 团队 = teamPlayer.getTeam();
        if (团队 != null) {
            player.sendMessage(lang.接受邀请_已经有团队.replaceAll("<团队>", 团队.getTeamName()));
            return;
        }
        teamPlayer.setTeam(邀请团队);
        teamPlayer.setStatus(Status.player);
        player.sendMessage(lang.接受邀请_接受成功.replaceAll("<团队>", 邀请团队.getTeamName()));
    }

    private void 邀请成员(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        TeamPlayer teamPlayer = getTeamPlayerNotNull(player);
        Team 团队 = teamPlayer.getTeam();
        if (团队 == null) {
            player.sendMessage(lang.邀请成员_玩家没有团队);
            return;
        }
        if (!isAdmin(teamPlayer)){
            player.sendMessage(lang.邀请成员_不是管理);
            return;
        }
        if (strings.length < 1) {
            player.sendMessage(lang.邀请成员_没有参数);
            return;
        }
        Player 被邀玩家 = Bukkit.getPlayer(strings[0]);
        if (被邀玩家 == null) {
            player.sendMessage(lang.邀请成员_玩家不在线.replaceAll("<不在线玩家>", strings[0]));
            return;
        }
        邀请map.put(被邀玩家, 团队);
        被邀玩家.sendMessage(lang.邀请成员_被邀请信息.replaceAll("<玩家>", player.getName()).replaceAll("<团队>", 团队.getTeamName()));
        player.sendMessage(lang.邀请成员_邀请成功);
    }

    private void 解散团队(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        TeamPlayer teamPlayer = getTeamPlayerNotNull(player);
        Team 团队 = teamPlayer.getTeam();
        if (团队 == null) {
            player.sendMessage(lang.删除团队_玩家没有团队);
            return;
        }
        if (!isLeader(teamPlayer)) {
            player.sendMessage(lang.删除团队_不是团长);
            return;
        }
        if (strings.length < 1) {
            player.sendMessage(lang.删除团队_删除确认);
            return;
        }
        if (teamConfig.删除团队时必须先删除世界){
            if (团队.getWorlds()!=null){
                player.sendMessage(lang.删除团队_没有删除世界);
                return;
            }
        }
        if (!"yes".equals(strings[0])) {
            player.sendMessage(lang.删除团队_删除确认);
            return;
        }
        团队.delete();
        player.sendMessage(lang.删除团队_删除成功);
    }

    private void 创建团队(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        if (strings.length < 1) {
            player.sendMessage(lang.创建团队_没有团队名参数);
            return;
        }
        if (!isLegitimate(strings[0])) {
            player.sendMessage(lang.创建世界_团队名不合法);
            return;
        }
        TeamPlayer teamPlayer = getTeamPlayerNotNull(player);
        Team 团队 = teamPlayer.getTeam();
        if (团队 != null) {
            player.sendMessage(lang.创建团队_已经在团队中了.replaceAll("<团队>", 团队.getTeamName()));
            return;
        }
        团队 = Teams.datasheetManager.getTeamFromTeamName(strings[0]);
        if (团队 != null) {
            player.sendMessage(lang.创建团队_团队名称被占用.replaceAll("<团队>", 团队.getTeamName()));
            return;
        }
        Team team = Teams.datasheetManager.newTeam(strings[0]);
        teamPlayer.setTeam(team);
        teamPlayer.setStatus(Status.leader);
        player.sendMessage(lang.创建团队_创建成功);
        //如果团队创建时顺便创建世界
        if (teamConfig.创建团队时以团队名称创建世界) {
            创建世界(commandSender, strings);
        }
    }

    private void 创建世界(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        if (strings.length < 1) {
            player.sendMessage(lang.创建世界_需要世界名称);
            return;
        }
        if (!isLegitimate(strings[0])) {
            player.sendMessage(lang.创建世界_世界名不合法);
            return;
        }
        if(strings[0].contains("_nether")|strings[0].contains("_the_end")){
            player.sendMessage(lang.创建世界_世界名不合法);
            return;
        }
        if (worldConfig.禁止玩家使用的世界名称列表.contains(strings[0].toLowerCase())){
            player.sendMessage(lang.创建世界_名称禁止使用.replaceAll("<世界>",strings[0]));
            return;
        }
        if (Bukkit.getWorld(strings[0])!=null){
            player.sendMessage(lang.创建世界_世界已经存在.replaceAll("<世界>",strings[0]));
            return;
        }
        TeamPlayer teamPlayer = getTeamPlayerNotNull(player);
        Team 团队 = teamPlayer.getTeam();
        if (团队 == null) {
            player.sendMessage(lang.创建世界_玩家没有团队);
            return;
        }
        Worlds worlds = 团队.getWorlds();
        if (worlds != null) {
            player.sendMessage(lang.创建世界_团队已经有世界了);
            return;
        }
        if (!isLeader(teamPlayer)) {
            player.sendMessage(lang.创建世界_不是团长);
            return;
        }
        worlds = Teams.datasheetManager.newWorlds(strings[0]);
        if (worlds==null){
            player.sendMessage(lang.创建世界_世界名称被他人占用);
            return;
        }
        //创建世界了
        团队.setWorlds(worlds);
        player.sendMessage(lang.创建世界_创建成功);
        if (worldConfig.创建世界后传送到世界) {
            Worlds finalWorlds = worlds;
            Bukkit.getScheduler().runTaskAsynchronously(MyWorldBukkit.getMyWorldBukkit(), () -> {
                去出生点(commandSender, new String[]{});
            });
        }
    }

    private void 去出生点(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        Team 团队 = getTeamPlayerNotNull(player).getTeam();
        if (团队 == null) {
            player.sendMessage(lang.去出生点_没有团队);
            return;
        }
        Worlds worlds = 团队.getWorlds();
        if (worlds == null) {
            player.sendMessage(lang.去出生点_团队没有世界);
            return;
        }
        MyWorldBukkit.getPlayerDataManager().playerLoadFinishedToRun(player, () -> Bukkit.getScheduler().runTaskAsynchronously(MyWorldBukkit.getMyWorldBukkit(), () -> {
            PlayerWorlds playerWorlds = MyWorldBukkit.getPlayerWordMangaer().loadPlayerWorlds(worlds);
            if (playerWorlds==null){
                player.sendMessage(lang.去出生点_世界被其他服务器加载);
                return;
            }
            Bukkit.getScheduler().runTask(MyWorldBukkit.getMyWorldBukkit(), () -> {
                playerWorlds.playerBackSpawn(player);
                player.sendMessage(lang.去出生点_传送成功);
            });
        }));
    }

    private static boolean isAdmin(TeamPlayer teamPlayer){
        return teamPlayer.getStatus()==Status.admin|
                isLeader(teamPlayer);
    }
    private static boolean isLeader(TeamPlayer teamPlayer){
        return teamPlayer.getStatus()==Status.leader;
    }
    private static TeamPlayer getTeamPlayerNotNull(Player player){
        TeamPlayer teamPlayer = Teams.datasheetManager.getTamePlayer(player.getUniqueId());
        if (teamPlayer==null){
            teamPlayer = Teams.datasheetManager.newTamePlayer(player.getUniqueId(),player.getName());
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
