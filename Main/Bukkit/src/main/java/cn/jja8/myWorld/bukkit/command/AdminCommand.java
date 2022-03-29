package cn.jja8.myWorld.bukkit.command;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Worlds;
import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.basic.Teams;
import cn.jja8.myWorld.bukkit.config.Lang;
import cn.jja8.myWorld.bukkit.config.Permission;
import cn.jja8.myWorld.bukkit.word.PlayerWorlds;
import cn.jja8.myWorld.bukkit.word.error.NoAllWorldLocks;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.command.CommandImplement;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.command.CommandManger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminCommand {
    Permission permission = ConfigBukkit.getPermission();
    Lang lang = ConfigBukkit.getLang();
    public AdminCommand() {
        CommandManger commandManger = new CommandManger(MyWorldBukkit.getMyWorldBukkit(),"MyWorldAdmin",new String[]{"mwa"},permission.管理员权限);
        commandManger.addCommand("unLoadWorld", new CommandImplement() {
            @Override
            public void command(CommandSender commandSender, String[] strings) {
                nuLoadWorld(commandSender,strings);
            }

            @Override
            public List<String> TabCompletion(CommandSender commandSender, String[] strings) {
                return new ArrayList<>(worldsNames(loadWorlds(commandSender,strings)).values());
            }
        });
        commandManger.addCommand("nuLoadAllWorld",this::nuLoadAllWorld);
        commandManger.addCommand("LoadWorld",this::loadWorld);
        commandManger.addCommand("goTo", new CommandImplement() {
            @Override
            public void command(CommandSender commandSender, String[] strings) {
                goTo(commandSender,strings);
            }

            @Override
            public List<String> TabCompletion(CommandSender commandSender, String[] strings) {
                return new ArrayList<>(worldsNames(loadWorlds(commandSender,strings)).values());
            }
        });
    }

    private void goTo(CommandSender commandSender, String[] strings) {
        if ((!(commandSender instanceof Player))) return;
        Player player = (Player) commandSender;
        if (strings.length<1){
            commandSender.sendMessage(lang.goTo_未指定世界名);
            return;
        }
        Worlds worlds = Teams.datasheetManager.getWorldsFromWorldsName(strings[0]);
        if (worlds==null) {
            commandSender.sendMessage(lang.goTo_世界不存在.replaceAll("<世界>",strings[0]));
            return;
        }
        PlayerWorlds playerWorlds = MyWorldBukkit.getPlayerWordMangaer().getBeLoadPlayerWorlds(worlds);
        if (playerWorlds==null){
            commandSender.sendMessage(lang.goTo_世界没有加载.replaceAll("<世界>",strings[0]));
            return;
        }
        playerWorlds.playerBack(player);
        player.sendMessage(lang.goTo_传送成功);
    }

    private void loadWorld(CommandSender commandSender, String[] strings) {
        if (strings.length<1){
            commandSender.sendMessage(lang.loadWorld_未指定世界名);
            return;
        }
        if(strings[0].contains("_nether")|strings[0].contains("_the_end")){
            commandSender.sendMessage(lang.loadWorld_世界名不合法);
            return;
        }
        Worlds worlds = Teams.datasheetManager.getWorldsFromWorldsName(strings[0]);
        if (worlds==null) {
            commandSender.sendMessage(lang.loadWorld_世界不存在.replaceAll("<世界>",strings[0]));
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(MyWorldBukkit.getMyWorldBukkit(), () -> {
            try {
                MyWorldBukkit.getPlayerWordMangaer().loadPlayerWorlds(worlds);
            } catch (NoAllWorldLocks e) {
                commandSender.sendMessage(lang.loadWorld_世界被其他服务器加载);
                return;
            }
            commandSender.sendMessage(lang.loadWorld_加载完成);
        });
    }

    private void nuLoadAllWorld(CommandSender commandSender, String[] strings) {
        for (PlayerWorlds world : MyWorldBukkit.getPlayerWordMangaer().getWorlds()) {
            commandSender.sendMessage(lang.nuLoadAllWorld_卸载.replaceAll("<世界>",world.getName()));
            world.unLoad(true);
        }
        commandSender.sendMessage(lang.nuLoadAllWorld_卸载完成);
    }

    private void nuLoadWorld(CommandSender commandSender, String[] strings) {
        if (strings.length<1){
            commandSender.sendMessage(lang.nuLoadAllWorld_未指定世界名);
            return;
        }
        Worlds worlds = Teams.datasheetManager.getWorldsFromWorldsName(strings[0]);
        if (worlds==null) {
            commandSender.sendMessage(lang.nuLoadWorld_世界不存在.replaceAll("<世界>",strings[0]));
            return;
        }
        PlayerWorlds playerWorlds = MyWorldBukkit.getPlayerWordMangaer().getBeLoadPlayerWorlds(worlds);
        if (playerWorlds==null){
            commandSender.sendMessage(lang.nuLoadWorld_世界没有加载.replaceAll("<世界>",strings[0]));
            return;
        }
        playerWorlds.unLoad(true);
        commandSender.sendMessage(lang.nuLoadWorld_卸载完成.replaceAll("<世界>",strings[0]));
    }
    private List<Worlds> loadWorlds(CommandSender commandSender, String[] strings) {
        if (strings.length==1){
            return new ArrayList<>(MyWorldBukkit.getPlayerWordMangaer().getWorldNames());
        }
        return new ArrayList<>();
    }
    private Map<Worlds,String> worldsNames(List<Worlds> list){
        Map<Worlds,String> map = new HashMap<>();
        for (Worlds worlds : list) {
            map.put(worlds,worlds.getWorldsName());
        }
        return map;
    }
}
