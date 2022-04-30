package cn.jja8.myWorld.bukkit.config;

import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import org.bukkit.GameRule;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    public Map<String,Object> 主世界规则 = new HashMap<>();
    public Map<String,Object> 地狱世界规则 = new HashMap<>();
    public Map<String,Object> 末地界规则 = new HashMap<>();


    public WorldConfig(){
        禁止玩家使用的世界名称列表.add("world");
        禁止玩家使用的世界名称列表.add("world_nether");
        禁止玩家使用的世界名称列表.add("world_the_end");

        地狱界生成器.世界维度 = World.Environment.NETHER;
        末地界生成器.世界维度 = World.Environment.THE_END;

        主世界规则.put("keepInventory",true);
        主世界规则.put("maxEntityCramming",5);
        地狱世界规则.put("keepInventory",true);
        地狱世界规则.put("maxEntityCramming",5);
        末地界规则.put("keepInventory",true);
        末地界规则.put("disableElytraMovementCheck",true);
        末地界规则.put("maxEntityCramming",5);
    }

    public static void setGameRule(Map<String,Object> gameRuleMap,World world){
        gameRuleMap.forEach((s, o) -> {
            GameRule<Object> gameRule = (GameRule<Object>) GameRule.getByName(s);
            if (gameRule==null){
                MyWorldBukkit.getMyWorldBukkit().getLogger().warning("规则"+s+"无效！");
                return;
            }
            if (o instanceof String){
                if (gameRule.getType().equals(Integer.class)) {
                    try {
                        o = Integer.valueOf((String) o);
                    }catch (NumberFormatException ignored){

                    }
                }else if (gameRule.getType().equals(Boolean.class)){
                    if (o.equals("true")) {
                        o = true;
                    }else if (o.equals("false")){
                        o = false;
                    }
                }
            }
            if (!(gameRule.getType().equals(o.getClass()))) {
                MyWorldBukkit.getMyWorldBukkit().getLogger().warning("规则"+s+"需要"+ gameRule.getType() +"而提供的是"+o.getClass());
                return;
            }
            world.setGameRule(gameRule,o);
        });
    }
}
