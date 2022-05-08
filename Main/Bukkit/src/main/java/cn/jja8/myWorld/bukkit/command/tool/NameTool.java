package cn.jja8.myWorld.bukkit.command.tool;

import cn.jja8.myWorld.bukkit.ConfigBukkit;

import java.util.regex.Pattern;

/**
 * 请不要在配置文件加载完成之前初始化
 * */
public class NameTool {
    public static Pattern p;
    public static void load(){
        p = Pattern.compile(ConfigBukkit.getFileConfig().名称合法验证);
    }
    /**
     * 检查字符串是否合法
     *
     * @param str 待校验字符串
     * @return 字符串必须只能由数字字母下划线组成
     */
    public static boolean verification(String str){
        if (str.length()<3){
            return false;
        }
        if (p==null){
            return false;
        }
        return p.matcher(str).find();
    }
}
