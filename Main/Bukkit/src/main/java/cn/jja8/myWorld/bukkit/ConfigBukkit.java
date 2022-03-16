package cn.jja8.myWorld.bukkit;

import cn.jja8.myWorld.bukkit.config.*;
import cn.jja8.patronSaint_2022_3_2_1244.allUsed.file.YamlConfig;

import java.io.File;
import java.util.ArrayList;

public class ConfigBukkit {
    static Permission permission;
    static FileConfig fileConfig;
    static Lang lang;
    static WorldConfig worldConfig;
    static TeamConfig teamConfig;
    static PlayerDataConfig playerDataConfig;
    static void load(){
        if (fileConfig==null){
            try {
                fileConfig = YamlConfig.loadFromFile(new File(MyWorldBukkit.getMyWorldBukkit().getDataFolder(), "FileConfig.yml"),fileConfig = new FileConfig());
            } catch (Error|Exception exception) {
                exception.printStackTrace();
            }
            if (lang==null){
                try {
                    lang = YamlConfig.loadFromFile(new File(MyWorldBukkit.getMyWorldBukkit().getDataFolder(), "Lang.yml"), lang = new Lang());
                } catch (Error|Exception exception) {
                    exception.printStackTrace();
                }
            }
            if (worldConfig==null){
                try {
                    worldConfig = YamlConfig.loadFromFile(new File(MyWorldBukkit.getMyWorldBukkit().getDataFolder(), "WorldConfig.yml"), worldConfig = new WorldConfig());
                    ArrayList<String> list = new ArrayList<>();
                    worldConfig.禁止玩家使用的世界名称列表.forEach(s -> list.add(s.toLowerCase()));
                    worldConfig.禁止玩家使用的世界名称列表 = list;
                } catch (Error|Exception exception) {
                    exception.printStackTrace();
                }
            }
            if (teamConfig==null){
                try {
                    teamConfig = YamlConfig.loadFromFile(new File(MyWorldBukkit.getMyWorldBukkit().getDataFolder(), "TeamConfig.yml"), teamConfig = new TeamConfig());
                } catch (Error|Exception exception) {
                    exception.printStackTrace();
                }
            }
            if (playerDataConfig==null){
                try {
                    playerDataConfig = YamlConfig.loadFromFile(new File(MyWorldBukkit.getMyWorldBukkit().getDataFolder(), "PlayerDataConfig.yml"), playerDataConfig = new PlayerDataConfig());
                } catch (Error|Exception exception) {
                    exception.printStackTrace();
                }
            }
            if (permission==null){
                try {
                    permission = YamlConfig.loadFromFile(new File(MyWorldBukkit.getMyWorldBukkit().getDataFolder(), "Permission.yml"),permission = new Permission());
                } catch (Error|Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }
    public static FileConfig getFileConfig() {
        return fileConfig;
    }
    public static Lang getLang() {
        return lang;
    }
    public static WorldConfig getWorldConfig() {
        return worldConfig;
    }
    public static TeamConfig getTeamConfig() {
        return teamConfig;
    }
    public static PlayerDataConfig getPlayerDataConfig(){
        return playerDataConfig;
    }
    public static Permission getPermission() {
        return permission;
    }
}
