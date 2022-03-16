package cn.jja8.myWorld.all.veryUtil;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.OverlappingFileLockException;
import java.nio.charset.StandardCharsets;

public class FileLock {
    FileOutputStream fileOutputStream;
    FileChannel channel;
    java.nio.channels.FileLock fileLock;
    File file;

    public FileLock(FileOutputStream fileOutputStream, FileChannel channel, java.nio.channels.FileLock fileLock, File file) {
        this.fileOutputStream = fileOutputStream;
        this.channel = channel;
        this.fileLock = fileLock;
        this.file = file;
    }

    public void unLock(){
        try {
            if (fileLock!=null){
                fileLock.close();
            }
        }catch (IOException  ioException1){
            ioException1.printStackTrace();
        }
        try {
            if (channel!=null){
                channel.close();
            }
        }catch (IOException  ioException1){
            ioException1.printStackTrace();
        }
        try {
            if (fileOutputStream!=null){
                fileOutputStream.close();
            }
        }catch (IOException  ioException1){
            ioException1.printStackTrace();
        }
        file.delete();
    }

    /**
     * 给指定文件上锁
     * @return null 文件已经被锁
     * **/
    public static FileLock getFileLock(File file,String LockServerName){
        FileOutputStream fileOutputStream = null;
        FileChannel channel = null;
        java.nio.channels.FileLock fileLock;
        try {
            File parent = file.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            file.createNewFile();
            fileOutputStream = new FileOutputStream(file);
            channel = fileOutputStream.getChannel();
            try {
                fileLock = channel.tryLock(0,10,false);
            }catch (Exception exception){
                return null;
            }
            if (fileLock!=null){
                fileOutputStream.write(new byte[10]);
                fileOutputStream.write(LockServerName.getBytes(StandardCharsets.UTF_8));
                fileOutputStream.flush();
                return new FileLock(fileOutputStream,channel,fileLock,file);
            }else {
                try {
                    channel.close();
                }catch (IOException  ioException1){
                    ioException1.printStackTrace();
                }
                try {
                    fileOutputStream.close();
                }catch (IOException  ioException1){
                    ioException1.printStackTrace();
                }
            }
        }catch (IOException exception){
            exception.printStackTrace();
            try {
                if (channel!=null){
                    channel.close();
                }
            }catch (IOException  ioException1){
                ioException1.printStackTrace();
            }
            try {
                if (fileOutputStream!=null){
                    fileOutputStream.close();
                }
            }catch (IOException  ioException1){
                ioException1.printStackTrace();
            }
        }
        return null;

    }
    /**
     * 获得上锁服务器名称
     * @return null 没被上锁
     * */
    public static String getLockServerName(File file){
        File parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        if (!file.isFile()) {
            return null;
        }

        try (FileInputStream fileInputStream = new FileInputStream(file);
             FileChannel channel = fileInputStream.getChannel()){
            java.nio.channels.FileLock fileLock = null;
            try {
                fileLock = channel.tryLock(0,10,true);
            }catch (OverlappingFileLockException overlappingFileLockException){
                try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();){
                    int i;
                    fileInputStream.skip(10);
                    while ((i=fileInputStream.read())!=-1){
                        byteArrayOutputStream.write(i);
                    }
                    return new String(byteArrayOutputStream.toByteArray(),StandardCharsets.UTF_8);
                }catch (IOException ioException){
                    ioException.printStackTrace();
                }
            } catch (Exception exception){
                exception.printStackTrace();
            }
            if (fileLock!=null){
                fileLock.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
