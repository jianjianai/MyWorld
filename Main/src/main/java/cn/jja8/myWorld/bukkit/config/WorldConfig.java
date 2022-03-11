package cn.jja8.myWorld.bukkit.config;

import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.generator.ChunkGenerator;

import java.util.ArrayList;

public class WorldConfig {
    public long 卸载空世界间隔时间 = 30;
    public boolean 自动配置服务器名称 = true;
    public String 主世界名称 = "world";
    public long 无玩家世界最短卸载时间 = 600;
    public ArrayList<String> 禁止玩家使用的世界名称列表 =new ArrayList<>();
    public String 服务器名称 = "没有配置服务器名称，请到WorldConfig.yml中完成配置。";
    public boolean 创建世界后传送到世界 = true;
    public WorldBuilder 主世界生成器 = new WorldBuilder();
    public WorldBuilder 地狱界生成器 = new WorldBuilder();
    public WorldBuilder 末地界生成器 = new WorldBuilder();

    public static class WorldBuilder {
        public boolean 启用 = true;
        public String 世界生成器 = "MyWorld";
        public String 世界生成器参数 = "";
        public WorldType 世界类型 = WorldType.NORMAL;
        public World.Environment 世界维度 = World.Environment.NORMAL;
        public boolean 生成建筑 = false;

        public WorldCreator getWordBuilder(String wordName){
            WorldCreator 世界生成器 = new WorldCreator(wordName);
            世界生成器.generateStructures(生成建筑);
            世界生成器.environment(世界维度);
            世界生成器.type(世界类型);
            ChunkGenerator chunkGenerator = WorldCreator.getGeneratorForName(wordName, this.世界生成器,null);
            if (chunkGenerator!=null){
                世界生成器.generator(chunkGenerator);
            }else {
                MyWorldBukkit.getMyWorldBukkit().getLogger().warning("世界生成器"+this.世界生成器+"不存在！");
            }
            世界生成器.generatorSettings(世界生成器参数);
            return 世界生成器;
        }
    }

    public WorldConfig(){
        禁止玩家使用的世界名称列表.add("world");
        禁止玩家使用的世界名称列表.add("world_nether");
        禁止玩家使用的世界名称列表.add("world_the_end");

        地狱界生成器.世界维度 = World.Environment.NETHER;
        末地界生成器.世界维度 = World.Environment.THE_END;
    }
}
