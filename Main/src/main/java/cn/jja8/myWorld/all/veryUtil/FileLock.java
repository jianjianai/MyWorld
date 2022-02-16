package cn.jja8.myWorld.all.veryUtil;


import cn.jja8.patronSaint_2022_2_7_1713.allUsed.file.ReadFile;
import cn.jja8.patronSaint_2022_2_7_1713.allUsed.file.YamlConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;
/**
 * @author jianjianai
 * */
public class FileLock {
    static Logger logger = Logger.getLogger("Lock");
    public static class lockWork{
        File 锁file,信息file;
        FileOutputStream 文件锁;

        private lockWork(File 文件夹路径,String 文件名称){
            锁file = new File(文件夹路径,文件名称+".lock");
            信息file = new File(文件夹路径,文件名称+".from");
            文件夹路径.mkdirs();
        }
        public boolean isLocked() {
            //可以打开写入流说明没有被占用，代表没锁。
            FileOutputStream liu = null;
            try {
                liu = new FileOutputStream(锁file);
            } catch (IOException exception) {
                logger.info("[debug]文件已经被打开"+exception.getMessage());
            }
            java.nio.channels.FileLock 锁 = null;
            if (liu!=null){
                try {
                    锁 = liu.getChannel().tryLock();
                } catch (IOException ignored) {
                }
                try {
                    if (锁!=null){
                        锁.close();
                    }
                } catch (IOException ignored) {
                }
                try {
                    liu.close();
                } catch (IOException ignored) {
                }
            }
            return 锁==null;
        }
        public void Lock(lockNews lookNews) {
            try {
                文件锁 = new FileOutputStream(锁file);
                java.nio.channels.FileLock 锁 = 文件锁.getChannel().tryLock();
                if (锁==null){
                    throw new Error("已经被其他服务器上锁！");
                }
                ReadFile.WriteToFile(YamlConfig.saveToString(lookNews),信息file);
            } catch (IOException exception) {
                throw new Error("已经被其他服务器上锁！");
            }
        }
        public void unlock() {
            if (文件锁==null){
                throw new Error("被其他服务器上锁或没被锁时，无法解锁！");
            }
            try {
                文件锁.close();
            } catch (IOException exception) {
                logger.info("[debug]文件无法关闭"+exception.getMessage());
            }
            锁file.delete();
            信息file.delete();
        }
        public lockNews news(){
            try {
                return YamlConfig.loadFromString(ReadFile.readFormFile(信息file), lockNews.class);
            } catch (IOException exception) {
                exception.printStackTrace();
                return new lockNews(null);
            }
        }
    }
    public static class lockNews {
        public String 服务器名称;
        public int 最大玩家数量  = 20;

        public lockNews(){服务器名称="null";}
        public lockNews(String 服务器名称) {
            this.服务器名称 = 服务器名称;
        }
    }
    //获得锁的操作类
    public static lockWork git(File folder,String FileName){
        return new lockWork(folder,FileName);
    }
}
