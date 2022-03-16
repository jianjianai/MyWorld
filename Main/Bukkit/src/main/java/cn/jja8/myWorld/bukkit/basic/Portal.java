package cn.jja8.myWorld.bukkit.basic;

import cn.jja8.myWorld.bukkit.basic.portalSupport.PortalTransmission;
import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * 传送门的包装
 * */
public class Portal {
    public static PortalTransmission portalTransmission = null;
    public static void load() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (portalTransmission==null){
            String v = Bukkit.getServer().getClass().getName().split("\\.")[3];
            Class<?> cl = Class.forName("cn.jja8.myWorld.bukkit.basic.portalSupport."+v);
            Constructor<?> cn = cl.getConstructor();
            portalTransmission = (PortalTransmission) cn.newInstance();
        }
    }
}
