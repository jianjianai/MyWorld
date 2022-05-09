package cn.jja8.myWorld.bukkit.work;

import cn.jja8.myWorld.bukkit.word.PlayerWorlds;

public class MyWorldWorldGrouping {
    PlayerWorlds playerWorlds;
    public MyWorldWorldGrouping(PlayerWorlds playerWorlds) {
        this.playerWorlds = playerWorlds;
    }

    /**
     * 卸载全部全部世界
     * @param save 是否保存数据。
     * */
    public void unLoad(boolean save){
        playerWorlds.unLoad(save);
        playerWorlds = null;
    }
}
