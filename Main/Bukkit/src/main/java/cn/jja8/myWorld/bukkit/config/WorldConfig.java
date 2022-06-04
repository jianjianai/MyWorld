package cn.jja8.myWorld.bukkit.config;

import java.util.ArrayList;

public class WorldConfig {
    public long 卸载空世界间隔时间 = 30;
    public boolean 自动配置服务器名称 = true;
    public String 主世界名称 = "world";
    public long 无玩家世界最短卸载时间 = 600;
    public ArrayList<String> 禁止玩家使用的世界名称列表 =new ArrayList<>();
    public String 服务器名称 = "没有配置服务器名称，请到WorldConfig.yml中完成配置。";
    public boolean 创建世界后传送到世界 = true;



    public WorldConfig(){
        禁止玩家使用的世界名称列表.add("world");
        禁止玩家使用的世界名称列表.add("world_nether");
        禁止玩家使用的世界名称列表.add("world_the_end");
    }
}
