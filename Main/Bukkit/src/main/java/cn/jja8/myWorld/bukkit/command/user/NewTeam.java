package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Status;
import cn.jja8.myWorld.all.basic.DatasheetSupport.Team;
import cn.jja8.myWorld.all.basic.DatasheetSupport.TeamPlayer;
import cn.jja8.myWorld.all.basic.DatasheetSupport.WorldGroup;
import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.basic.Teams;
import cn.jja8.myWorld.bukkit.command.tool.NameTool;
import cn.jja8.myWorld.bukkit.command.tool.TeamsPlayerTool;
import cn.jja8.myWorld.bukkit.word.PlayerWorlds;
import cn.jja8.myWorld.bukkit.word.error.NoAllWorldLocks;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.command.CommandImplement;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NewTeam implements CommandImplement {
    @Override
    public void command(CommandSender commandSender, String[] strings) {

        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        if (strings.length < 1) {
            player.sendMessage(ConfigBukkit.getLang().创建团队_没有团队名参数);
            return;
        }
        if (!NameTool.verification(strings[0])) {
            player.sendMessage(ConfigBukkit.getLang().创建世界_团队名不合法);
            return;
        }
        TeamPlayer teamPlayer = TeamsPlayerTool.getTeamPlayerNotNull(player);
        Team 团队 = teamPlayer.getTeam();
        if (团队 != null) {
            player.sendMessage(ConfigBukkit.getLang().创建团队_已经在团队中了.replaceAll("<团队>", 团队.getTeamName()));
            return;
        }
        团队 = Teams.datasheetManager.getTeamFromTeamName(strings[0]);
        if (团队 != null) {
            player.sendMessage(ConfigBukkit.getLang().创建团队_团队名称被占用.replaceAll("<团队>", 团队.getTeamName()));
            return;
        }
        Team team = Teams.datasheetManager.newTeam(strings[0]);
        teamPlayer.setTeam(team);
        teamPlayer.setStatus(Status.leader);
        player.sendMessage(ConfigBukkit.getLang().创建团队_创建成功);
        //如果团队创建时顺便创建世界
        if (ConfigBukkit.getTeamConfig().创建团队时以团队名称创建世界) {
            if ((!(commandSender instanceof Player))) return;
            Player player11 = (Player) commandSender;
            if (strings.length < 1) {
                player11.sendMessage(ConfigBukkit.getLang().创建世界_需要世界名称);
                return;
            }
            if (!NameTool.verification(strings[0])) {
                player11.sendMessage(ConfigBukkit.getLang().创建世界_世界名不合法);
                return;
            }
            if (strings[0].contains("_nether") | strings[0].contains("_the_end")) {
                player11.sendMessage(ConfigBukkit.getLang().创建世界_世界名不合法);
                return;
            }
            if (ConfigBukkit.getWorldConfig().禁止玩家使用的世界名称列表.contains(strings[0].toLowerCase())) {
                player11.sendMessage(ConfigBukkit.getLang().创建世界_名称禁止使用.replaceAll("<世界>", strings[0]));
                return;
            }
            if (Bukkit.getWorld(strings[0]) != null) {
                player11.sendMessage(ConfigBukkit.getLang().创建世界_世界已经存在.replaceAll("<世界>", strings[0]));
                return;
            }
            TeamPlayer teamPlayer1 = TeamsPlayerTool.getTeamPlayerNotNull(player11);
            Team 团队11 = teamPlayer1.getTeam();
            if (团队11 == null) {
                player11.sendMessage(ConfigBukkit.getLang().创建世界_玩家没有团队);
                return;
            }
            WorldGroup worldGroup = 团队11.getWorldGroup();
            if (worldGroup != null) {
                player11.sendMessage(ConfigBukkit.getLang().创建世界_团队已经有世界了);
                return;
            }
            if (!TeamsPlayerTool.isLeader(teamPlayer1)) {
                player11.sendMessage(ConfigBukkit.getLang().创建世界_不是团长);
                return;
            }
            worldGroup = Teams.datasheetManager.newWorldGroup(strings[0]);
            if (worldGroup == null) {
                player11.sendMessage(ConfigBukkit.getLang().创建世界_世界名称被他人占用);
                return;
            }
            //创建世界了
            团队11.setWorlds(worldGroup);
            player11.sendMessage(ConfigBukkit.getLang().创建世界_创建成功);
            if (ConfigBukkit.getWorldConfig().创建世界后传送到世界) {
                WorldGroup finalWorldGroup = worldGroup;
                Bukkit.getScheduler().runTaskAsynchronously(MyWorldBukkit.getMyWorldBukkit(), () -> {
                    if ((!(commandSender instanceof Player))) return;
                    Player player1 = (Player) commandSender;
                    Team 团队1 = TeamsPlayerTool.getTeamPlayerNotNull(player1).getTeam();
                    if (团队1 == null) {
                        player1.sendMessage(ConfigBukkit.getLang().去出生点_没有团队);
                        return;
                    }
                    WorldGroup worldGroup1 = 团队1.getWorldGroup();
                    if (worldGroup1 == null) {
                        player1.sendMessage(ConfigBukkit.getLang().去出生点_团队没有世界);
                        return;
                    }
                    PlayerWorlds playerWorlds = MyWorldBukkit.getPlayerWordMangaer().getBeLoadPlayerWorlds(worldGroup1);
                    if (playerWorlds != null) {
                        playerWorlds.playerBackSpawn(player1);
                        player1.sendMessage(ConfigBukkit.getLang().去出生点_传送成功);
                        return;
                    }
                    MyWorldBukkit.getPlayerDataManager().playerLoadFinishedToRun(player1, () -> Bukkit.getScheduler().runTaskAsynchronously(MyWorldBukkit.getMyWorldBukkit(), () -> {
                        PlayerWorlds world;
                        try {
                            world = MyWorldBukkit.getPlayerWordMangaer().loadPlayerWorlds(worldGroup1);
                        } catch (NoAllWorldLocks e) {
                            player1.sendMessage(ConfigBukkit.getLang().去出生点_世界被其他服务器加载);
                            return;
                        }
                        Bukkit.getScheduler().runTask(MyWorldBukkit.getMyWorldBukkit(), () -> {
                            world.playerBackSpawn(player1);
                            player1.sendMessage(ConfigBukkit.getLang().去出生点_传送成功);
                        });
                    }));

                });
            }
        }
    }
}
