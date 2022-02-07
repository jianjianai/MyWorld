package cn.jja8.myWorld.bukkit.basic;

import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.basic.worldDataSupport.WorldDataSupport;
import org.bukkit.Bukkit;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * 一个中介类，为了兼容多版本
 * 可在使用别的插件在load阶段给worldDataSupport赋值，用于兼容更多版本。
 * */
public class WorldData {
    public static WorldDataSupport worldDataSupport = null;
    public static void load() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (worldDataSupport==null){
            String v = Bukkit.getServer().getClass().getName().split("\\.")[3];
            Class<?> cl = Class.forName("cn.jja8.myWorld.bukkit.basic.worldDataSupport."+v);
            Constructor<?> cn = cl.getConstructor(File.class);
            worldDataSupport = (WorldDataSupport) cn.newInstance(new File(MyWorldBukkit.getFileConfig().玩家世界文件路径));
        }
    }
}
