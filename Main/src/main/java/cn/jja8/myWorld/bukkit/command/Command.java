package cn.jja8.myWorld.bukkit.command;

import cn.jja8.myWorld.all.veryUtil.StringTool;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.basic.teamSupport.Team;
import cn.jja8.myWorld.bukkit.basic.Teams;
import cn.jja8.myWorld.bukkit.word.PlayerWorlds;
import cn.jja8.patronSaint_2022_2_7_1713.bukkit.command.CommandImplement;
import cn.jja8.patronSaint_2022_2_7_1713.bukkit.command.CommandManger;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Command {
    public static Pattern p = Pattern.compile(MyWorldBukkit.getFileConfig().名称合法验证);
    //<被邀请的玩家,团队>
    Map<Player, Team> 邀请map = new HashMap<>();

    public Command() {
        CommandManger commandManger = new CommandManger(MyWorldBukkit.getMyWorldBukkit(), "myWorld");
        commandManger.setDefaulCommand(this::返回世界);
        commandManger.addCommand(new String[]{"创建团队","NewTeam"}, this::创建团队);
        commandManger.addCommand(new String[]{"解散团队","DisbandOurTeam"}, this::解散团队);
        commandManger.addCommand(new String[]{"邀请成员","InviteFriend"}, this::邀请成员);
        commandManger.addCommand(new String[]{"接受邀请","AcceptInvitation"}, this::接受邀请);
        commandManger.addCommand(new String[]{"创建世界","NewWord"}, this::创建世界);
        commandManger.addCommand(new String[]{"去出生点","goBeginningPoint"}, this::去出生点);
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
        commandManger.addCommand(new String[]{"回到世界","go"}, this::返回世界);
        commandManger.addCommand(new String[]{"ServerName"}, this::ServerName);
    }


    private void ServerName(CommandSender commandSender, String[] strings) {
        if (!MyWorldBukkit.getWorldConfig().自动配置服务器名称){
            return;
        }
        if (strings.length==3){
           if (strings[0].equals("ServerName")&&strings[1].equals("ServerName")){
               MyWorldBukkit.getWorldConfig().服务器名称 = strings[2];
           }
        }
    }

    private void 返回世界(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        MyWorldBukkit.getPlayerDataManager().playerLoadFinishedToRun(player, () -> {
            Team 团队 = Teams.teamManager.getTeam(player);
            if (团队 == null) {
                player.sendMessage(MyWorldBukkit.getLang().返回世界_没有团队);
                return;
            }
            String 世界名称 = 团队.getWorldName();
            if (世界名称 == null) {
                player.sendMessage(MyWorldBukkit.getLang().返回世界_团队没有世界);
                return;
            }
            PlayerWorlds 世界 = MyWorldBukkit.getPlayerWordMangaer().loadPlayerWorlds(世界名称);
            if (世界==null){
                player.sendMessage(MyWorldBukkit.getLang().返回世界_世界被其他服务器加载);
                return;
            }
            Location 位置 = 世界.loadPlayerLocation(player.getName());
            if (位置!=null){
                player.teleport(位置);
            }else {
                player.teleport(世界.getWorld().getSpawnLocation());
            }
            player.sendMessage(MyWorldBukkit.getLang().返回世界_传送成功);
        });
    }

    private void 删除世界(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        if (strings.length < 1) {
            player.sendMessage(MyWorldBukkit.getLang().删除世界_删除确认);
            return;
        }
        if (!"确认".equals(strings[0])) {
            player.sendMessage(MyWorldBukkit.getLang().删除世界_删除确认);
            return;
        }
        Team 团队 = Teams.teamManager.getTeam(player);
        if (团队 == null) {
            player.sendMessage(MyWorldBukkit.getLang().删除世界_玩家没有团队);
            return;
        }
        if (!团队.isLeader(player)) {
            player.sendMessage(MyWorldBukkit.getLang().删除世界_不是团长);
            return;
        }
        PlayerWorlds 世界 = MyWorldBukkit.getPlayerWordMangaer().getBeLoadPlayerWorlds(团队.getWorldName());
        if (世界==null){
            player.sendMessage(MyWorldBukkit.getLang().删除世界_世界未加载);
            return;
        }
        MyWorldBukkit.getPlayerWordMangaer().delPlayerWorlds(团队.getWorldName());
        团队.setWorldName(null);
        player.sendMessage(MyWorldBukkit.getLang().删除世界_删除成功);
    }

    private void 信任列表(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        Team 团队 = Teams.teamManager.getTeam(player);
        if (团队 == null) {
            player.sendMessage(MyWorldBukkit.getLang().信任列表_你没有团队);
            return;
        }
        PlayerWorlds 世界 = MyWorldBukkit.getPlayerWordMangaer().getBeLoadPlayerWorlds(团队.getWorldName());
        if (世界==null){
            player.sendMessage(MyWorldBukkit.getLang().信任列表_世界未加载);
            return;
        }
        String 世界名称 = 世界.getWorld().getName();
        List<String> 信任列表 = 世界.getPlayerWordInform().BeTrustList();
        player.sendMessage(MyWorldBukkit.getLang().信任列表_信息.replaceAll("<世界>",世界名称).replaceAll("<数量>", String.valueOf(信任列表.size())).replaceAll("<列表>",信任列表.toString()));
    }

    private void 查询信息(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        Team 团队 = Teams.teamManager.getTeam(player);
        if (团队==null){
            player.sendMessage(MyWorldBukkit.getLang().查询信息_还没有团队);
            return;
        }

        HashMap<String,String> map = new HashMap<>();
        map.put("<团队>",团队.getTeamName());
        String 世界名称 = 团队.getWorldName();
        if (世界名称==null){
            map.put("<团队世界信息>",MyWorldBukkit.getLang().查询信息_团队没有世界);
        }else {
            map.put("<团队世界信息>",MyWorldBukkit.getLang().查询信息_团队世界信息.replaceAll("<世界>",团队.getWorldName()));
        }
        map.put("<团长>",团队.getLeader().toString());
        map.put("<管理员列表>",团队.admins().toString());
        map.put("<队员列表>",团队.members().toString());
        List<String> list = StringTool.stringListReplaces(MyWorldBukkit.getLang().查询信息_长信息列表,map);
        for (String s : list) {
            player.sendMessage(s);
        }
    }

    private List<String> 取消信任_NBT补全(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return null;
        Player player = (Player) commandSender;
        Team 团队 = Teams.teamManager.getTeam(player);
        if (团队 == null) {
            return null;
        }
        if (!团队.isAdmin(player)){
            return null;
        }
        PlayerWorlds 世界 = MyWorldBukkit.getPlayerWordMangaer().getBeLoadPlayerWorlds(团队.getWorldName());
        if (世界==null){
            return null;
        }
        return 世界.getPlayerWordInform().BeTrustList();
    }


    private void 取消信任(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        if (strings.length < 1) {
            player.sendMessage(MyWorldBukkit.getLang().取消信任_没有参数);
            return;
        }
        Team 团队 = Teams.teamManager.getTeam(player);
        if (团队 == null) {
            player.sendMessage(MyWorldBukkit.getLang().取消信任_你没有团队);
            return;
        }
        if (!团队.isAdmin(player)){
            player.sendMessage(MyWorldBukkit.getLang().取消信任_权限不足);
            return;
        }
        PlayerWorlds 世界 = MyWorldBukkit.getPlayerWordMangaer().getBeLoadPlayerWorlds(团队.getWorldName());
        if (世界==null){
            player.sendMessage(MyWorldBukkit.getLang().取消信任_世界未加载);
            return;
        }
        世界.getPlayerWordInform().delBeTrust(strings[0]);
        player.sendMessage(MyWorldBukkit.getLang().取消信任_取消成功);
    }

    private void 添加信任(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        if (strings.length < 1) {
            player.sendMessage(MyWorldBukkit.getLang().添加信任_没有参数);
            return;
        }
        Team 团队 = Teams.teamManager.getTeam(player);
        if (团队 == null) {
            player.sendMessage(MyWorldBukkit.getLang().添加信任_你没有团队);
            return;
        }
        if (!团队.isAdmin(player)){
            player.sendMessage(MyWorldBukkit.getLang().添加信任_权限不足);
            return;
        }
        PlayerWorlds 世界 = MyWorldBukkit.getPlayerWordMangaer().getBeLoadPlayerWorlds(团队.getWorldName());
        if (世界==null){
            player.sendMessage(MyWorldBukkit.getLang().添加信任_世界未加载);
            return;
        }
        世界.getPlayerWordInform().addBeTrust(strings[0]);
        player.sendMessage(MyWorldBukkit.getLang().添加信任_添加成功);
    }

    private void 退出团队(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        Team 团队 = Teams.teamManager.getTeam(player);
        if (团队 == null) {
            player.sendMessage(MyWorldBukkit.getLang().退出团队_你没有团队);
            return;
        }
        if (团队.isLeader(player)) {
            player.sendMessage(MyWorldBukkit.getLang().退出团队_团长不能退出);
            return;
        }
        if (strings.length < 1) {
            player.sendMessage(MyWorldBukkit.getLang().退出团队_退出确认);
            return;
        }
        if (!"确认".equals(strings[0])) {
            player.sendMessage(MyWorldBukkit.getLang().退出团队_退出确认);
            return;
        }
        团队.delMember(player);
        团队.delAdmin(player);
        player.sendMessage(MyWorldBukkit.getLang().退出团队_退出成功);
    }

    private void 接受邀请(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        Team 邀请团队 = 邀请map.get(player);
        if (邀请团队 == null) {
            player.sendMessage(MyWorldBukkit.getLang().接受邀请_没被邀请);
            return;
        }
        Team 团队 = Teams.teamManager.getTeam(player);
        if (团队 != null) {
            player.sendMessage(MyWorldBukkit.getLang().接受邀请_已经有团队.replaceAll("<团队>", 团队.getTeamName()));
            return;
        }
        邀请团队.addMember(player);
        player.sendMessage(MyWorldBukkit.getLang().接受邀请_接受成功.replaceAll("<团队>", 邀请团队.getTeamName()));
    }

    private void 邀请成员(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        Team 团队 = Teams.teamManager.getTeam(player);
        if (团队 == null) {
            player.sendMessage(MyWorldBukkit.getLang().邀请成员_玩家没有团队);
            return;
        }
        if (!团队.isAdmin(player)) {
            player.sendMessage(MyWorldBukkit.getLang().邀请成员_不是管理);
            return;
        }
        if (strings.length < 1) {
            player.sendMessage(MyWorldBukkit.getLang().邀请成员_没有参数);
            return;
        }
        Player 被邀玩家 = Bukkit.getPlayer(strings[0]);
        if (被邀玩家 == null) {
            player.sendMessage(MyWorldBukkit.getLang().邀请成员_玩家不在线.replaceAll("<不在线玩家>", strings[0]));
            return;
        }
        邀请map.put(被邀玩家, 团队);
        被邀玩家.sendMessage(MyWorldBukkit.getLang().邀请成员_被邀请信息.replaceAll("<玩家>", player.getName()).replaceAll("<团队>", 团队.getTeamName()));
        player.sendMessage(MyWorldBukkit.getLang().邀请成员_邀请成功);
    }

    private void 解散团队(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        Team 团队 = Teams.teamManager.getTeam(player);
        if (团队 == null) {
            player.sendMessage(MyWorldBukkit.getLang().删除团队_玩家没有团队);
            return;
        }
        if (!团队.isLeader(player)) {
            player.sendMessage(MyWorldBukkit.getLang().删除团队_不是团长);
            return;
        }
        if (strings.length < 1) {
            player.sendMessage(MyWorldBukkit.getLang().删除团队_删除确认);
            return;
        }
        if (MyWorldBukkit.getTeamConfig().删除团队时必须先删除世界){
            if (团队.getWorldName()!=null){
                player.sendMessage(MyWorldBukkit.getLang().删除团队_没有删除世界);
                return;
            }
        }
        if (!"确认".equals(strings[0])) {
            player.sendMessage(MyWorldBukkit.getLang().删除团队_删除确认);
            return;
        }
        if (Teams.teamManager.deleteTeam(团队.getTeamName()))
            player.sendMessage(MyWorldBukkit.getLang().删除团队_删除成功);
        else
            player.sendMessage(团队.getTeamName() + "删除失败");
    }

    private void 创建团队(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        if (strings.length < 1) {
            player.sendMessage(MyWorldBukkit.getLang().创建团队_没有团队名参数);
            return;
        }
        if (!isLegitimate(strings[0])) {
            player.sendMessage(MyWorldBukkit.getLang().创建世界_团队名不合法);
            return;
        }
        Team 团队 = Teams.teamManager.getTeam(player);
        if (团队 != null) {
            player.sendMessage(MyWorldBukkit.getLang().创建团队_已经在团队中了.replaceAll("<团队>", 团队.getTeamName()));
            return;
        }
        团队 = Teams.teamManager.getTeam(strings[0]);
        if (团队 != null) {
            player.sendMessage(MyWorldBukkit.getLang().创建团队_团队名称被占用.replaceAll("<团队>", 团队.getTeamName()));
            return;
        }
        Teams.teamManager.createTeam(strings[0], player);
        player.sendMessage(MyWorldBukkit.getLang().创建团队_创建成功);
        //如果团队创建时顺便创建世界
        if (MyWorldBukkit.getTeamConfig().创建团队时以团队名称创建世界) {
            创建世界(commandSender, strings);
        }
    }

    private void 创建世界(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        MyWorldBukkit.getPlayerDataManager().playerLoadFinishedToRun(player, () -> {
            if (strings.length < 1) {
                player.sendMessage(MyWorldBukkit.getLang().创建世界_需要世界名称);
                return;
            }
            if (!isLegitimate(strings[0])) {
                player.sendMessage(MyWorldBukkit.getLang().创建世界_世界名不合法);
                return;
            }
            if(strings[0].contains("_nether")|strings[0].contains("_the_end")){
                player.sendMessage(MyWorldBukkit.getLang().创建世界_世界名不合法);
                return;
            }
            if (MyWorldBukkit.getWorldConfig().禁止玩家使用的世界名称列表.contains(strings[0].toLowerCase())){
                player.sendMessage(MyWorldBukkit.getLang().创建世界_名称禁止使用.replaceAll("<世界>",strings[0]));
                return;
            }
            if (Bukkit.getWorld(strings[0])!=null){
                player.sendMessage(MyWorldBukkit.getLang().创建世界_世界已经存在.replaceAll("<世界>",strings[0]));
                return;
            }
            Team 团队 = Teams.teamManager.getTeam(player);
            if (团队 == null) {
                player.sendMessage(MyWorldBukkit.getLang().创建世界_玩家没有团队);
                return;
            }
            String s = 团队.getWorldName();
            if (s != null) {
                player.sendMessage(MyWorldBukkit.getLang().创建世界_团队已经有世界了);
                return;
            }
            if (!团队.isLeader(player)) {
                player.sendMessage(MyWorldBukkit.getLang().创建世界_不是团长);
                return;
            }
            if (MyWorldBukkit.getPlayerWordMangaer().isWorldExistence(strings[0])) {
                player.sendMessage(MyWorldBukkit.getLang().创建世界_世界名称被他人占用);
                return;
            }
            //创建世界了
            团队.setWorldName(strings[0]);
            MyWorldBukkit.getPlayerWordMangaer().loadPlayerWorlds(strings[0]);
            player.sendMessage(MyWorldBukkit.getLang().创建世界_创建成功);
            //如果创建后传送到世界
            if (MyWorldBukkit.getWorldConfig().创建世界后传送到世界) {
                去出生点(commandSender, new String[]{});
            }
        });
    }

    private void 去出生点(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        MyWorldBukkit.getPlayerDataManager().playerLoadFinishedToRun(player, () -> {
            Team 团队 = Teams.teamManager.getTeam(player);
            if (团队 == null) {
                player.sendMessage(MyWorldBukkit.getLang().去出生点_没有团队);
                return;
            }
            String 世界名称 = 团队.getWorldName();
            if (世界名称 == null) {
                player.sendMessage(MyWorldBukkit.getLang().去出生点_团队没有世界);
                return;
            }
            PlayerWorlds 世界 = MyWorldBukkit.getPlayerWordMangaer().loadPlayerWorlds(世界名称);
            if (世界==null){
                player.sendMessage(MyWorldBukkit.getLang().去出生点_世界被其他服务器加载);
                return;
            }
            player.teleport(世界.getWorld().getSpawnLocation());
            player.sendMessage(MyWorldBukkit.getLang().去出生点_传送成功);
        });
    }


    /**
     * 检查字符串是否合法
     *
     * @param str 待校验字符串
     * @return 字符串必须只能由数字字母下划线组成
     */
    public static boolean isLegitimate(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        return p.matcher(str).find();
    }
}
