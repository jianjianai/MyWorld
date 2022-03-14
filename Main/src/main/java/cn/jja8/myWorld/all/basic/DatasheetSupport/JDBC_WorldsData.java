package cn.jja8.myWorld.all.basic.DatasheetSupport;

public class JDBC_WorldsData implements WorldsData{
    JDBC_DatasheetManger datasheetManger;
    String worldsUUID,DataName;

    public JDBC_WorldsData(JDBC_DatasheetManger datasheetManger, String worldsUUID, String dataName) {
        this.datasheetManger = datasheetManger;
        this.worldsUUID = worldsUUID;
        DataName = dataName;
    }
}
