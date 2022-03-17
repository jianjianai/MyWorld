package cn.jja8.myWorld.bukkit.basic.worldDataSupport;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Lifecycle;
import net.minecraft.server.v1_16_R3.*;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;

import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

public class v1_16_R3 implements WorldDataSupport{
    File allWordFile;
    DedicatedServer console;
    CraftServer craftServer;
    Map<String, World> worlds;
    public v1_16_R3(File allWordFile){
        this.allWordFile = allWordFile;
        allWordFile.mkdirs();
        craftServer = (CraftServer) Bukkit.getServer();
        console = craftServer.getServer();
        //利用反射拿到words
        try {
            Field worldsField = CraftServer.class.getDeclaredField("worlds");
            worldsField.setAccessible(true);
            worlds = (Map<String, World>) worldsField.get(craftServer);
        } catch (NoSuchFieldException | IllegalAccessException noSuchFieldException) {
            noSuchFieldException.printStackTrace();
        }
    }

}
