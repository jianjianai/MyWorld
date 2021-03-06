package cn.jja8.myWorld.all.basic.DatasheetSupport;

import java.util.List;
import java.util.UUID;
/**
 * 需要重写hashCode和equals方法
 * */
public interface WorldGroup {
    /**
     * 获取世界组里的全部世界
     * */
    List<String> getWorldList();
    /**
     * 给世界组添加一个世界
     * */
    void addWorld(String worldName);
    /**
     * 获取世界组名称
     * */
    String getWorldGroupName();
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
    WorldGroupData getWorldGroupData(String dataName);
    /**
     * 创建一个新自定义数据
     * @return null 这个数据已经存在
     * */
    WorldGroupData newWorldGroupData(String dataName);

    /**
     * 删除一个世界
     * */
    void removeWorld(String worldName);

    /**
     * 返回是否包含这个世界
     * */
    boolean containsWorld(String worldName);
}
