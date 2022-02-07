package cn.jja8.myWorld.bukkit.basic;

import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.basic.playerDataSupport.PlayerDataSupport;
import org.bukkit.Bukkit;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * 一个中介类，为了兼容多版本
 * 可在使用别的插件在load阶段给playerDataSupport赋值，用于兼容更多版本。
 * */
public class PlayerData {
    public static PlayerDataSupport playerDataSupport = null;
    public static void load() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (playerDataSupport==null){
            String v = Bukkit.getServer().getClass().getName().split("\\.")[3];
            if (MyWorldBukkit.getPlayerDataConfig().使用玩家名称保存数据){
                Class<?> cl = Class.forName("cn.jja8.myWorld.bukkit.basic.playerDataSupport."+v+"_userName");
                Constructor<?> cn = cl.getConstructor(File.class);
                playerDataSupport = (PlayerDataSupport) cn.newInstance(new File(MyWorldBukkit.getFileConfig().玩家数据文件路径));
            }else {
                Class<?> cl = Class.forName("cn.jja8.myWorld.bukkit.basic.playerDataSupport."+v+"_userUUID");
                Constructor<?> cn = cl.getConstructor(File.class);
                playerDataSupport = (PlayerDataSupport) cn.newInstance(new File(MyWorldBukkit.getFileConfig().玩家数据文件路径));
            }
        }
    }
}
