package cn.jja8.myWorld.bukkit.command;

import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.config.Lang;
import cn.jja8.myWorld.bukkit.config.Permission;
import cn.jja8.myWorld.bukkit.word.PlayerWorlds;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.command.CommandImplement;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.command.CommandManger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

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
                return loadWorlds(commandSender,strings);
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
                return loadWorlds(commandSender,strings);
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
        PlayerWorlds playerWorlds = MyWorldBukkit.getPlayerWordMangaer().getBeLoadPlayerWorlds(strings[0]);
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
        if (!MyWorldBukkit.getPlayerWordMangaer().isWorldExistence(strings[0])){
            commandSender.sendMessage(lang.loadWorld_世界不存在.replaceAll("<世界>",strings[0]));
            return;
        }
        MyWorldBukkit.getPlayerWordMangaer().loadPlayerWorlds(strings[0], playerWorlds -> {
            commandSender.sendMessage(lang.loadWorld_加载完成);
        });
    }

    private void nuLoadAllWorld(CommandSender commandSender, String[] strings) {
        for (PlayerWorlds world : MyWorldBukkit.getPlayerWordMangaer().getWorlds()) {
            commandSender.sendMessage(lang.nuLoadAllWorld_卸载.replaceAll("<世界>",world.getName()));
            MyWorldBukkit.getPlayerWordMangaer().unloadPlayerWorlds(world,true);
        }
        commandSender.sendMessage(lang.nuLoadAllWorld_卸载完成);
    }

    private void nuLoadWorld(CommandSender commandSender, String[] strings) {
        if (strings.length<1){
            commandSender.sendMessage(lang.nuLoadAllWorld_未指定世界名);
            return;
        }
        PlayerWorlds playerWorlds = MyWorldBukkit.getPlayerWordMangaer().getBeLoadPlayerWorlds(strings[0]);
        if (playerWorlds==null){
            commandSender.sendMessage(lang.nuLoadWorld_世界没有加载.replaceAll("<世界>",strings[0]));
            return;
        }
        MyWorldBukkit.getPlayerWordMangaer().unloadPlayerWorlds(playerWorlds,true);
        commandSender.sendMessage(lang.nuLoadWorld_卸载完成.replaceAll("<世界>",strings[0]));
    }
    private List<String> loadWorlds(CommandSender commandSender, String[] strings) {
        if (strings.length==1){
            return new ArrayList<>(MyWorldBukkit.getPlayerWordMangaer().getWorldNames());
        }
        return new ArrayList<>();
    }
}
