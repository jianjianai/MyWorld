package cn.jja8.myWorld.all.basic.DatasheetSupport;

import java.util.List;
import java.util.UUID;

public interface Worlds {
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
}
