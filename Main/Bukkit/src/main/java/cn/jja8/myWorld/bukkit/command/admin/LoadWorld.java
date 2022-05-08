package cn.jja8.myWorld.bukkit.command.admin;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Worlds;
import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.basic.Teams;
import cn.jja8.myWorld.bukkit.word.error.NoAllWorldLocks;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.command.CommandImplement;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class LoadWorld implements CommandImplement {
    @Override
    public void command(CommandSender commandSender, String[] strings) {
        if (strings.length < 1) {
            commandSender.sendMessage(ConfigBukkit.getLang().loadWorld_未指定世界名);
            return;
        }
        if (strings[0].contains("_nether") | strings[0].contains("_the_end")) {
            commandSender.sendMessage(ConfigBukkit.getLang().loadWorld_世界名不合法);
            return;
        }
        Worlds worlds = Teams.datasheetManager.getWorldsFromWorldsName(strings[0]);
        if (worlds == null) {
            commandSender.sendMessage(ConfigBukkit.getLang().loadWorld_世界不存在.replaceAll("<世界>", strings[0]));
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(MyWorldBukkit.getMyWorldBukkit(), () -> {
            try {
                MyWorldBukkit.getPlayerWordMangaer().loadPlayerWorlds(worlds);
            } catch (NoAllWorldLocks e) {
                commandSender.sendMessage(ConfigBukkit.getLang().loadWorld_世界被其他服务器加载);
                return;
            }
            commandSender.sendMessage(ConfigBukkit.getLang().loadWorld_加载完成);
        });
    }
}
