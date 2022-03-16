package cn.jja8.myWorld.bukkit.word;

import cn.jja8.myWorld.bukkit.basic.WorldData;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 代表一个世界的信息
 */
public class PlayerWordInform {
    private final List<String> BeTrustList;
    private final String playerWorldName;
    public PlayerWordInform(String playerWorldName) {
        this.playerWorldName = playerWorldName;
        InputStream inputStream = WorldData.worldDataSupport.getCustomDataInputStream(playerWorldName,"PlayerWordInform");
        if (inputStream==null){
            BeTrustList = new ArrayList<>();
            return;
        }
        InputStreamReader inputStreamReader =new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(inputStreamReader);
        try { inputStreamReader.close(); } catch (IOException ignored) { }
        try { inputStream.close(); } catch (IOException ignored) { }
        BeTrustList = yamlConfiguration.getStringList("trustList");
    }

    public void save() {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.set("trustList", BeTrustList);

        OutputStream outputStream = WorldData.worldDataSupport.getCustomDataOutputStream(playerWorldName,"PlayerWordInform");
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream,StandardCharsets.UTF_8);
        try {
            outputStreamWriter.write(yamlConfiguration.saveToString());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        try {outputStreamWriter.close(); } catch (IOException ignored) {  }
        try { outputStream.close(); } catch (IOException ignored) {  }
    }

    /**
     * 判断玩家是否有权限
     */
    public boolean isBeTrust(String PlayerName) {
       return BeTrustList.contains(PlayerName);
    }
    /**
     * 添加信任的玩家
     * */
    public boolean addBeTrust(String PlayerName){
        return BeTrustList.add(PlayerName);
    }
    /**
     * 删除信任的玩家
     * */
    public boolean delBeTrust(String PlayerName){
        return BeTrustList.remove(PlayerName);
    }
    /**
     * 信任玩家列表
     * */
    public List<String> BeTrustList(){
        return new ArrayList<>(BeTrustList);
    }

}
