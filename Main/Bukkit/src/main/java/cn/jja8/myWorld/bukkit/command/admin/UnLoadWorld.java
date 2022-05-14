package cn.jja8.myWorld.bukkit.command.admin;

import cn.jja8.myWorld.all.basic.DatasheetSupport.WorldGroup;
import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.basic.Teams;
import cn.jja8.myWorld.bukkit.word.PlayerWorlds;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.command.CommandImplement;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnLoadWorld implements CommandImplement {
    @Override
    public void command(CommandSender commandSender, String[] strings) {
        if (strings.length < 1) {
            commandSender.sendMessage(ConfigBukkit.getLang().nuLoadAllWorld_未指定世界名);
            return;
        }
        WorldGroup worldGroup = Teams.datasheetManager.getWorldGroupFromWorldsName(strings[0]);
        if (worldGroup == null) {
            commandSender.sendMessage(ConfigBukkit.getLang().nuLoadWorld_世界不存在.replaceAll("<世界>", strings[0]));
            return;
        }
        PlayerWorlds playerWorlds = MyWorldBukkit.getPlayerWordMangaer().getBeLoadPlayerWorlds(worldGroup);
        if (playerWorlds == null) {
            commandSender.sendMessage(ConfigBukkit.getLang().nuLoadWorld_世界没有加载.replaceAll("<世界>", strings[0]));
            return;
        }
        playerWorlds.unLoad(true);
        commandSender.sendMessage(ConfigBukkit.getLang().nuLoadWorld_卸载完成.replaceAll("<世界>", strings[0]));
    }

    @Override
    public List<String> TabCompletion(CommandSender commandSender, String[] strings) {
        List<WorldGroup> result;
        if (strings.length == 1) {
            result = new ArrayList<>(MyWorldBukkit.getPlayerWordMangaer().getWorldNames());
        } else {
            result = new ArrayList<>();
        }
        Map<WorldGroup, String> map = new HashMap<>();
        for (WorldGroup worldGroup : result) {
            map.put(worldGroup, worldGroup.getWorldGroupName());
        }
        return new ArrayList<>(map.values());
    }
}
