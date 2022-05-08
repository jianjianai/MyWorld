package cn.jja8.myWorld.bukkit.command.admin;

import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.word.PlayerWorlds;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.command.CommandImplement;
import org.bukkit.command.CommandSender;

public class NuLoadAllWorld implements CommandImplement {
    @Override
    public void command(CommandSender commandSender, String[] strings) {

        for (PlayerWorlds world : MyWorldBukkit.getPlayerWordMangaer().getWorlds()) {
            commandSender.sendMessage(ConfigBukkit.getLang().nuLoadAllWorld_卸载.replaceAll("<世界>", world.getName()));
            world.unLoad(true);
        }
        commandSender.sendMessage(ConfigBukkit.getLang().nuLoadAllWorld_卸载完成);
    }
}
