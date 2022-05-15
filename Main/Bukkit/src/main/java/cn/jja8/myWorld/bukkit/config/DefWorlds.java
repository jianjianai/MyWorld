package cn.jja8.myWorld.bukkit.config;

import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.work.myWorldWorldInform.MyWorldWorldCreator;
import cn.jja8.patronSaint_2022_3_2_1244.allUsed.file.JarFile;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DefWorlds {
    private final Map<String,MyWorldWorldCreator> Type_defMyWorldWorldCreator = new HashMap<>();
    public DefWorlds() {
        File defWorlds = new File(MyWorldBukkit.getMyWorldBukkit().getDataFolder(),"defWorlds.yml");
        JarFile.unzipFile(defWorlds, ConfigBukkit.class,"defWorlds.yml",false);
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(defWorlds);
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
