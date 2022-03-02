package cn.jja8.myWorld.bungeecord;

import cn.jja8.myWorld.bungeecord.basic.Teams;
import cn.jja8.myWorld.bungeecord.basic.WorldData;
import cn.jja8.myWorld.bungeecord.command.myWordCommand;
import cn.jja8.myWorld.bungeecord.config.FileConfig;
import cn.jja8.myWorld.bungeecord.config.Lang;
import cn.jja8.myWorld.bungeecord.config.PlayerDataConfig;
import cn.jja8.myWorld.bungeecord.config.ServerConfig;
import cn.jja8.myWorld.bungeecord.worker.PreciseExecution;
import cn.jja8.patronSaint_2022_3_2_1244.allUsed.file.YamlConfig;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class MyWorldBungeecord extends Plugin implements Listener {
    static FileConfig fileConfig = new FileConfig();
    static ServerConfig serverConfig = new ServerConfig();
    static Lang lang = new Lang();
    static PlayerDataConfig playerDataConfig=new PlayerDataConfig();

    static myWordCommand myWordCommand;
    static MyWorldBungeecord myWorldBungeecord;
    static PreciseExecution preciseExecution;

    public static PlayerDataConfig getPlayerDataConfig() {
        return playerDataConfig;
    }
    public static FileConfig getFileConfig() {
        return fileConfig;
    }
    public static ServerConfig getServerConfig() {
        return serverConfig;
    }
    public static Lang getLang() {
        return lang;
    }


    public static MyWorldBungeecord getMyWorldBungeecord() {
        return myWorldBungeecord;
    }
    public static myWordCommand getMyWordCommand() {
        return myWordCommand;
    }

    public static PreciseExecution getPreciseExecution() {
        return preciseExecution;
    }

    @Override
    public void onEnable() {
        myWorldBungeecord = this;
        //加载配置
        try {
            fileConfig = YamlConfig.loadFromFile(new File(getDataFolder(), "fileConfig.yml"), fileConfig);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        try {
            serverConfig = YamlConfig.loadFromFile(new File(getDataFolder(), "serverConfig.yml"), serverConfig);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        try {
            playerDataConfig = YamlConfig.loadFromFile(new File(getDataFolder(), "playerDataConfig.yml"), playerDataConfig);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        try {
            lang = YamlConfig.loadFromFile(new File(getDataFolder(), "lang.yml"), lang);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        WorldData.load();
        Teams.load();

        getLogger().warning("当前非正式版本，若有bug您可以前往 “PlugClub/插件实验室 - 820131534” 交流和反馈。");
        preciseExecution = new PreciseExecution();
        myWordCommand=new myWordCommand();
    }

    @Override
    public void onDisable() {
        Teams.unLoad();
    }

}
