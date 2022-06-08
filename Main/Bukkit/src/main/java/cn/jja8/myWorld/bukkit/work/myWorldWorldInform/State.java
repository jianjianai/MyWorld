package cn.jja8.myWorld.bukkit.work.myWorldWorldInform;

import cn.jja8.myWorld.bukkit.basic.worldDataSupport.WorldDataLock;
import cn.jja8.myWorld.bukkit.work.name.WorldsDataName;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;

/**
 * 代表世界的状态
 * */
public class State {
    //这个世界是否被初始化
    public boolean initialization = false;

    public State(WorldDataLock worldDataLock) {
        byte[] bytes = worldDataLock.getCustomDataByte(WorldsDataName.State.toString());
        if (bytes==null){
            return;
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(new StringReader(new String(bytes, StandardCharsets.UTF_8)));
        initialization = yaml.getBoolean("initialization");
    }

    public void save(WorldDataLock worldDataLock) {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("initialization",initialization);
        worldDataLock.setCustomDataByte(WorldsDataName.State.toString(),yaml.saveToString().getBytes(StandardCharsets.UTF_8));
    }
}
