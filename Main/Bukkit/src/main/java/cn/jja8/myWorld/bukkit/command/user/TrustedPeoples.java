package cn.jja8.myWorld.bukkit.command.user;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Team;
import cn.jja8.myWorld.all.basic.DatasheetSupport.Worlds;
import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.command.tool.TeamsPlayerTool;
import cn.jja8.myWorld.bukkit.word.PlayerWorlds;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.command.CommandImplement;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TrustedPeoples implements CommandImplement {
    @Override
    public void command(CommandSender commandSender, String[] strings) {

        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        Team 团队 = TeamsPlayerTool.getTeamPlayerNotNull(player).getTeam();
        if (团队 == null) {
            player.sendMessage(ConfigBukkit.getLang().信任列表_你没有团队);
            return;
        }
        Worlds worlds = 团队.getWorlds();
        if (worlds == null) {
            player.sendMessage(ConfigBukkit.getLang().信任列表_团队没有世界);
            return;
        }
        PlayerWorlds 世界 = MyWorldBukkit.getPlayerWordMangaer().getBeLoadPlayerWorlds(worlds);
        if (世界 == null) {
            player.sendMessage(ConfigBukkit.getLang().信任列表_世界未加载);
            return;
        }
        String 世界名称 = 世界.getName();
        List<String> 信任列表 = 世界.getPlayerWordInform().BeTrustList();
        player.sendMessage(ConfigBukkit.getLang().信任列表_信息.replaceAll("<世界>", 世界名称).replaceAll("<数量>", String.valueOf(信任列表.size())).replaceAll("<列表>", 信任列表.toString()));
    }
}
