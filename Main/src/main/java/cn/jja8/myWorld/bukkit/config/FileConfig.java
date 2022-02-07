package cn.jja8.myWorld.bukkit.config;

public class FileConfig {
    public String 名称合法验证 = "^[Za-z0-9-_]+$";
    public String 团队数据库URL = "jdbc:sqlite:C://myWorld/Team.db";
    public String 玩家数据文件路径 = "C://myWorld/playerData";
    public String 玩家世界文件路径 = "C://myWorld/playerWorld";
    public int 自动保存时间 = 600;
}
