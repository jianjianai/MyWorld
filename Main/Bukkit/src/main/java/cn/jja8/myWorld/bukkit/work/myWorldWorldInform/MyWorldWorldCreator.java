package cn.jja8.myWorld.bukkit.work.myWorldWorldInform;

import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MyWorldWorldCreator {
    private long seed; //种子
    private World.Environment environment; //环境
    private String generator; //区块加载器名称
    private String generatorSettings; //区块加载器设置
    private WorldType type;//世界类型
    private boolean generateStructures;//是否生成结构
    private boolean hardcore; //不知道是什么东西。

    public Map<String, Object> gameRule = new HashMap<>(); //世界规则
    public Difficulty difficulty = Difficulty.EASY; //难度

    public MyWorldWorldCreator() {
        this.seed = (new Random()).nextLong();
    }


    public void copy(MyWorldWorldCreator myWorldWorldCreator) {
        seed = myWorldWorldCreator.seed;
        environment = myWorldWorldCreator.environment; //环境
        generator = myWorldWorldCreator.generator; //区块加载器名称
        generatorSettings = myWorldWorldCreator.generatorSettings; //区块加载器设置
        type = myWorldWorldCreator.type;//世界类型
        generateStructures = myWorldWorldCreator.generateStructures;//是否生成结构
        hardcore = myWorldWorldCreator.hardcore; //不知道是什么东西。

        gameRule = new HashMap<>(myWorldWorldCreator.gameRule); //世界规则
        difficulty = myWorldWorldCreator.difficulty; //难度
    }

    public void loadByYaml(ConfigurationSection configurationSection) {
        String environment, type, difficulty;
        ConfigurationSection gameRule;
        seed = configurationSection.getLong("seed", seed);
        environment = configurationSection.getString("environment", "NORMAL");
        generator = configurationSection.getString("generator", "Minecraft");
        generatorSettings = configurationSection.getString("generatorSettings", "");
        type = configurationSection.getString("type", "NORMAL");
        generateStructures = configurationSection.getBoolean("generateStructures", true);
        hardcore = configurationSection.getBoolean("hardcore", false);
        gameRule = configurationSection.getConfigurationSection("gameRule");
        difficulty = configurationSection.getString("difficulty", "HARD");

        if (gameRule != null) {
            this.gameRule = gameRule.getValues(false);
        }
        try {
            this.environment = World.Environment.valueOf(environment);
        } catch (IllegalArgumentException illegalArgumentException) {
            MyWorldBukkit.getMyWorldBukkit().getLogger().warning("environment '" + environment + "' 不存在！");
        }
        try {
            this.type = WorldType.valueOf(type);
        } catch (IllegalArgumentException illegalArgumentException) {
            MyWorldBukkit.getMyWorldBukkit().getLogger().warning("type '" + environment + "' 不存在！");
        }
        try {
            this.difficulty = Difficulty.valueOf(difficulty);
        } catch (IllegalArgumentException illegalArgumentException) {
            MyWorldBukkit.getMyWorldBukkit().getLogger().warning("difficulty '" + environment + "' 不存在！");
        }
    }

    public void saveToYaml(ConfigurationSection configurationSection) {
        configurationSection.set("seed", seed);
        configurationSection.set("environment", environment.toString());
        configurationSection.set("generator", generator);
        configurationSection.set("generatorSettings", generatorSettings);
        configurationSection.set("type", type.toString());
        configurationSection.set("generateStructures", generateStructures);
        configurationSection.set("hardcore", hardcore);
        configurationSection.createSection("gameRule", gameRule);
        configurationSection.set("difficulty", difficulty.toString());
    }

    /**
     * 设置种子
     */
    public void setSeed(long seed) {
        this.seed = seed;
    }

    /**
     * 设置环境
     */
    public void setEnvironment(World.Environment environment) {
        this.environment = environment;
    }

    /**
     * 设置区块加载器
     */
    public void setGenerator(String generator) {
        this.generator = generator;
    }

    /**
     * 设置区块加载器设置
     */
    public void setGeneratorSettings(String generatorSettings) {
        this.generatorSettings = generatorSettings;
    }

    /**
     * 设置世界类型
     */
    public void setType(WorldType type) {
        this.type = type;
    }

    /**
     * 设置是否生成结构
     */
    public void setGenerateStructures(boolean generateStructures) {
        this.generateStructures = generateStructures;
    }

    /**
     * 设置不知道是什么东西的东西
     */
    public void setHardcore(boolean hardcore) {
        this.hardcore = hardcore;
    }

    /**
     * 设置游戏模式
     */
    public void setGameRule(Map<String, Object> gameRule) {
        this.gameRule = gameRule;
    }

    /**
     * 设置难度
     */
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * 获得世界的构造器
     */
    public WorldCreator getWorldCreator(String name) {
        WorldCreator worldCreator = new WorldCreator(name);
        worldCreator.seed(seed);
        worldCreator.environment(environment);
        worldCreator.generator(generator);
        worldCreator.generatorSettings(generatorSettings);
        worldCreator.type(type);
        worldCreator.generateStructures(generateStructures);
        worldCreator.hardcore(hardcore);
        return worldCreator;
    }

    /**
     * 完成世界加载后的设置
     */
    public void setting(World world) {
        Bukkit.getScheduler().runTask(MyWorldBukkit.getMyWorldBukkit(), () -> {
            //设置难度
            world.setDifficulty(difficulty);
            //设置规则
            gameRule.forEach((s, o) -> {
                GameRule<Object> gameRule = (GameRule<Object>) GameRule.getByName(s);
                if (gameRule == null) {
                    MyWorldBukkit.getMyWorldBukkit().getLogger().warning("规则" + s + "无效！");
                    return;
                }
                if (o instanceof String) {
                    if (gameRule.getType().equals(Integer.class)) {
                        try {
                            o = Integer.valueOf((String) o);
                        } catch (NumberFormatException ignored) {

                        }
                    } else if (gameRule.getType().equals(Boolean.class)) {
                        if (o.equals("true")) {
                            o = true;
                        } else if (o.equals("false")) {
                            o = false;
                        }
                    }
                }
                if (!(gameRule.getType().equals(o.getClass()))) {
                    MyWorldBukkit.getMyWorldBukkit().getLogger().warning("规则" + s + "需要" + gameRule.getType() + "而提供的是" + o.getClass());
                    return;
                }
                world.setGameRule(gameRule, o);
            });
        });
    }

}
