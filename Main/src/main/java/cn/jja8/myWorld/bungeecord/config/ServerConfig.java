package cn.jja8.myWorld.bungeecord.config;

import java.util.ArrayList;
import java.util.List;

public class ServerConfig {
    public List<String> 非MyWorld服务器列表 = new ArrayList<>();
    //超过服务器还能加入多少玩家时，停止分配服务器加载世界。
    public int 子服务器预留可加入玩家数 = 10;
    public ServerConfig(){
        非MyWorld服务器列表.add("登录");
        非MyWorld服务器列表.add("大厅");
    }
}
