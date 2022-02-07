package cn.jja8.myWorld.bukkit.config;

import org.bukkit.World;
import org.bukkit.WorldType;

import java.util.ArrayList;

public class WorldConfig {
    public boolean 自动配置服务器名称 = true;
    public String 主世界名称 = "world";
    public long 无玩家世界最短卸载时间 = 600;
    public ArrayList<String> 禁止玩家使用的世界名称列表 =new ArrayList<>();
    public String 服务器名称 = "没有配置服务器名称，请到WorldConfig.yml中完成配置。";
    public boolean 创建世界后传送到世界 = true;
    public WordBuilder 主世界生成器 = new WordBuilder();
    public WordBuilder 地狱界生成器 = new WordBuilder();
    public WordBuilder 末地界生成器 = new WordBuilder();

    public static class WordBuilder{
        public boolean 启用 = true;
        public String 世界生成器 = "MyWorld";
        public String 世界生成器参数 = "";
        public WorldType 世界类型 = WorldType.NORMAL;
        public World.Environment 世界维度 = World.Environment.NORMAL;
        public boolean 生成建筑 = false;
    }

    public WorldConfig(){
        禁止玩家使用的世界名称列表.add("world");
        禁止玩家使用的世界名称列表.add("world_nether");
        禁止玩家使用的世界名称列表.add("world_the_end");

        地狱界生成器.世界维度 = World.Environment.NETHER;
        末地界生成器.世界维度 = World.Environment.THE_END;
    }
}
