package cn.jja8.myWorld.bukkit.config;

import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.work.error.MyWorldError;
import cn.jja8.myWorld.bukkit.work.myWorldWorldInform.MyWorldWorldCreator;
import cn.jja8.patronSaint_2022_3_2_1244.allUsed.file.JarFile;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class WorldCleans {
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

    private final Map<String,Creator> clean_Creator = new HashMap<>();
    public WorldCleans() {
        File defWorlds = new File(MyWorldBukkit.getMyWorldBukkit().getDataFolder(),"WorldCleans.yml");
        JarFile.unzipFile(defWorlds, ConfigBukkit.class,"WorldCleans.yml",false);
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(defWorlds);
        for (String key : yamlConfiguration.getKeys(false)) {
            ConfigurationSection configurationSection = yamlConfiguration.getConfigurationSection(key);
            if (configurationSection!=null){
                clean_Creator.put(key,new Creator(configurationSection));
            }
        }
    }
    public Map<String, Creator> getClean_Creator() {
        return clean_Creator;
    }
    public Creator getDefault(){
        Creator def = clean_Creator.get("default");
        if (def==null){
            for (Creator value : clean_Creator.values()) {
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
