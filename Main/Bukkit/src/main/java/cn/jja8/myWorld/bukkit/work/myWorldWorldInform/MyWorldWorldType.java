package cn.jja8.myWorld.bukkit.work.myWorldWorldInform;

import cn.jja8.myWorld.bukkit.basic.worldDataSupport.WorldDataLock;
import cn.jja8.myWorld.bukkit.work.name.WorldsDataName;

import java.nio.charset.StandardCharsets;

/**
 * 记录世界的维度信息
 * */
public class MyWorldWorldType {
    private String type = null;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public MyWorldWorldType(WorldDataLock worldDataLock) {
        byte[] bytes = worldDataLock.getCustomDataByte(WorldsDataName.WorldType.toString());
        if (bytes!=null){
            type = new String(bytes,StandardCharsets.UTF_8);
        }
    }

    public void save(WorldDataLock worldDataLock){
        if (type!=null){
            worldDataLock.setCustomDataByte(WorldsDataName.WorldType.toString(),type.getBytes(StandardCharsets.UTF_8));
        }
    }
}
