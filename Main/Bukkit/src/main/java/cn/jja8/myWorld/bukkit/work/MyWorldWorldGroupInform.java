package cn.jja8.myWorld.bukkit.work;

import cn.jja8.myWorld.bukkit.word.PlayerWordInform;

import java.util.List;

public class MyWorldWorldGroupInform {
    PlayerWordInform playerWordInform;
    MyWorldWorldGroupInform(PlayerWordInform playerWordInform) {
        this.playerWordInform = playerWordInform;
    }
    /**
     * 取消信任玩家
     * */
    public void delBeTrust(String playerName) {
        playerWordInform.delBeTrust(playerName);
    }

    /**
     * 获取被信任的玩家列表
     * */
    public List<String> beTrustList() {
        return playerWordInform.beTrustList();
    }

    /**
     * 添加信任的玩家
     * */
    public void addBeTrust(String playerName) {
        playerWordInform.addBeTrust(playerName);
    }
}
