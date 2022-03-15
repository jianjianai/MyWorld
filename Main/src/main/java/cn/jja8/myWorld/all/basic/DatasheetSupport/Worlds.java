package cn.jja8.myWorld.all.basic.DatasheetSupport;

import java.util.List;
import java.util.UUID;

public interface Worlds {
    /**
     * 尝试得到这个世界的锁
     * @return null 这个世界已经被上锁
     * */
    WorldLock getWorldLock(String lockServerName);
    /**
     * 获取上锁服务器名称
     * @return null 没被上锁
     * */
    String getLockServerName();
    /**
     * 获取世界组里的全部世界
     * */
    List<String> getWorldsList();
    /**
     * 给世界组添加一个世界
     * */
    void putWorld(String worldName);
    /**
     * 获取世界组名称
     * */
    String getWorldsName();
    /**
     * 获取世界组uuid
     * */
    UUID getUUID();
    /**
     * 删除世界组
     * */
    void delete();
    /**
     * 获取世界组所在的Team
     * @return null 世界组不属于任何团队
     * */
    Team getTeam();
    /**
     * 获取世界组的某自定义数据
     * @return null 没有这个自定义数据
     * */
    WorldsData getWorldsData(String dataName);
    /**
     * 创建一个新自定义数据
     * @return null 这个数据已经存在
     * */
    WorldsData newWorldsData(String dataName);

}
