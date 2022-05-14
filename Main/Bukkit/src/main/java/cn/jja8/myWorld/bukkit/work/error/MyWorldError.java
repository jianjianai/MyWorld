package cn.jja8.myWorld.bukkit.work.error;

import cn.jja8.myWorld.bukkit.MyWorldBukkit;
public class MyWorldError extends Error{
    String in;
    public MyWorldError(String message) {
        super(message);
        in = message;
    }

    @Override
    public void printStackTrace() {
        MyWorldBukkit.getMyWorldBukkit().getLogger().severe("插件运行时发生异常，原因："+in);
        MyWorldBukkit.getMyWorldBukkit().getLogger().severe("下方是详细异常信息，如需反馈请将其完整反馈给开发者。");
        super.printStackTrace();
    }
}
