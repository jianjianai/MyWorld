package cn.jja8.myWorld.all.basic.DatasheetSupport;

/**
 * 需要重写hashCode和equals方法
 * */
public interface WorldGroupData {
    byte[] getData();
    void setData(byte[] newData);
}
