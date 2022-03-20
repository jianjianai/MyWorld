package cn.jja8.myWorld.bukkit.word;

import cn.jja8.myWorld.all.basic.DatasheetSupport.WorldsData;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 代表一个世界的信息
 */
public class PlayerWordInform {
    private final List<String> BeTrustList;
    private final WorldsData worldsData;
    public PlayerWordInform(WorldsData worldsData) {
        this.worldsData = worldsData;
        byte[] bytes = worldsData.getData();
        if (bytes==null){
            BeTrustList = new ArrayList<>();
            return;
        }

        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(new StringReader(new String(bytes,StandardCharsets.UTF_8)));
        BeTrustList = yamlConfiguration.getStringList("trustList");
    }

    public void save() {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.set("trustList", BeTrustList);
        worldsData.setData(yamlConfiguration.saveToString().getBytes(StandardCharsets.UTF_8));
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
