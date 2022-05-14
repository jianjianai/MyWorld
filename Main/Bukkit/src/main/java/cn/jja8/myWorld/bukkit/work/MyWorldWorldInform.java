package cn.jja8.myWorld.bukkit.work;

import cn.jja8.myWorld.bukkit.work.myWorldWorldInform.MyWorldWorldCreator;
import cn.jja8.myWorld.bukkit.work.name.WorldCustomDataName;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;

/**
 * 代表一个世界的信息
 * */
public class MyWorldWorldInform {
    private MyWorldWorldLock myWorldWorldLock;

    private MyWorldWorldCreator myWorldWorldCreator;
    public MyWorldWorldInform(MyWorldWorldLock myWorldWorldLock) {
        this.myWorldWorldLock =myWorldWorldLock;

        //加载myWorldWorldCreator
        myWorldWorldCreator = new MyWorldWorldCreator(myWorldWorldLock.myWorldWorld.name);
        byte[] bytes = myWorldWorldLock.worldDataLock.getCustomDataByte(WorldCustomDataName.WorldCreator.toString());
        StringReader stringReader = new StringReader(new String(bytes, StandardCharsets.UTF_8));
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(stringReader);
        stringReader.close();
        myWorldWorldCreator.loadByYaml(yamlConfiguration);
    }

    public void save(){
        //保存myWorldWorldCreator
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        myWorldWorldCreator.saveToYaml(yamlConfiguration);
        myWorldWorldLock.worldDataLock.setCustomDataByte(WorldCustomDataName.WorldCreator.toString(),yamlConfiguration.saveToString().getBytes(StandardCharsets.UTF_8));
    }

    public MyWorldWorldCreator getMyWorldWorldCreator() {
        return myWorldWorldCreator;
    }
}
