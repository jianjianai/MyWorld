package cn.jja8.myWorld.bukkit.basic.worldDataSupport;

import cn.jja8.myWorld.all.veryUtil.FileLock;

import java.io.*;

public abstract class WorldDataAndLock implements WorldDataLock{
    File worldDataFile;//世界的数据文件夹
    FileLock fileLock;


    @Override
    public byte[] getCustomDataByte(String dataName) {
        File file = new File(getWorldDataFile(),dataName+".data");
        if (!file.exists()){
            return null;
        }
        try(
                FileInputStream fileInputStream = new FileInputStream(file);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ){
            int i; while ((i=fileInputStream.read())!=-1)byteArrayOutputStream.write(i);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void setCustomDataByte(String dataName, byte[] bytes) {
        File file = new File(getWorldDataFile(),dataName+".data");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try(FileOutputStream fileOutputStream = new FileOutputStream(file)){
            fileOutputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unlock() {
        fileLock.unLock();
    }

    public File getWorldDataFile() {
        return worldDataFile;
    }

}
