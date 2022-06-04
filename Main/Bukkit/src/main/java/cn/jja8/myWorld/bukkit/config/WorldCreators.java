package cn.jja8.myWorld.bukkit.config;

import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.work.error.MyWorldError;
import cn.jja8.myWorld.bukkit.work.myWorldWorldInform.MyWorldWorldCreator;
import cn.jja8.patronSaint.all.V2.file.JarFile;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
/**
 * 世界生成器组
 * */
public class WorldCreators {
    public static class Creator{
        private final Map<String,MyWorldWorldCreator> Type_defMyWorldWorldCreator = new HashMap<>();
        public Creator(ConfigurationSection yamlConfiguration) {
            for (String key : yamlConfiguration.getKeys(false)) {
                MyWorldWorldCreator cre = new MyWorldWorldCreator();
                ConfigurationSection configurationSection = yamlConfiguration.getConfigurationSection(key);
                if (configurationSection!=null){
                    cre.loadByYaml(configurationSection);
                }
                Type_defMyWorldWorldCreator.put(key,cre);
            }
        }
        public Map<String, MyWorldWorldCreator> getType_defMyWorldWorldCreator() {
            return Type_defMyWorldWorldCreator;
        }
    }

    private final Map<String,Creator> creatorName_Creator = new HashMap<>();
    public WorldCreators() {
        File defWorlds = new File(MyWorldBukkit.getMyWorldBukkit().getDataFolder(),"WorldCreators.yml");
        JarFile.unzipFile(defWorlds, ConfigBukkit.class,"WorldCreators.yml",false);
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(defWorlds);
        for (String key : yamlConfiguration.getKeys(false)) {
            ConfigurationSection configurationSection = yamlConfiguration.getConfigurationSection(key);
            if (configurationSection!=null){
                creatorName_Creator.put(key,new Creator(configurationSection));
            }
        }
    }
    /**
     * 获得世界生成器名字map
     * */
    public Map<String, Creator> getCreatorName_Creator() {
        return creatorName_Creator;
    }
    /**
     * 获得默认的世界生成器
     * */
    public Creator getDefault(){
        Creator def = creatorName_Creator.get("default");
        if (def==null){
            for (Creator value : creatorName_Creator.values()) {
                def = value;
                break;
            }
        }
        if (def==null){
            throw new MyWorldError("WorldCleans.yml中没有任何可用的世界生成器！");
        }
        return def;
    }
}
