//package cn.jja8.MyWorldBukkit_LoliServer_1_16_5;
//
//
//import cn.jja8.myWorld.bukkit.VersionCompatibleAPI;
//import cn.jja8.myWorld.bukkit.config.FileConfig;
//import cn.jja8.patronSaint_2022_2_7_1713.allUsed.file.YamlConfig;
//import org.bukkit.plugin.java.JavaPlugin;
//
//import java.io.File;
//import java.io.IOException;
//
//
//public class LoliServer extends JavaPlugin {
//
//    @Override
//    public void onLoad() {
//        try {
//            FileConfig fileConfig = YamlConfig.loadFromFile(new File(getDataFolder(), "FileConfig.yml"), new FileConfig());
//            VersionCompatibleAPI.regWorldDataSupport(new v1_16_R3(new File(fileConfig.玩家世界文件路径)));
//        } catch (IOException exception) {
//            exception.printStackTrace();
//        }
//    }
//}
