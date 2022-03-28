package cn.jja8.myWorld.bukkit.config;

import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.generator.ChunkGenerator;

public class WorldBuilder {
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

    public byte[] saveToByte() {
        不急慢慢写
    }

    public static WorldBuilder loadAsByte( byte[] bytes){
        不急慢慢写
    }
}
