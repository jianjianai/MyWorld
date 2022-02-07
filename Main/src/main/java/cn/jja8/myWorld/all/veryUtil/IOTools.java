package cn.jja8.myWorld.all.veryUtil;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author 钟小白Core
 */
public class IOTools {
    /**
     * 把字节数组写入文件
     *
     * @param c    要写入的字节数组
     * @param path 要写入的路径
     */
    public static void writeFile(byte[] c, String path) throws IOException {
        FileOutputStream i = new FileOutputStream(path);
        i.write(c, 0, c.length);
        i.flush();
    }

    /**
     * 把字符串写入文件
     *
     * @param text   要写入的文本
     * @param encode 使用的编码
     * @param path   写入的路径
     */
    public static void writeTextFile(String text, String encode, String path){
        try {
            writeFile(text.getBytes(encode), path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 写入规范的json文件到指定目录
     *
     * @param jsonObject 要写入的JSONObject
     * @param path       要写入的路径
     */
    public static void writeJsonFile(JSONObject jsonObject, String path) {
        writeTextFile(toPrettyFormat(jsonObject.toString()), "utf8", path);
    }

    /**
     * 把文件都成字节数组
     *
     * @param path 要写入的路径
     * @return 字节数组
     */
    public static byte[] readFile(String path) throws IOException {
        FileInputStream i = new FileInputStream(path);
        byte[] buf = new byte[i.available()];
        i.read(buf, 0, buf.length);
        return buf;
    }

    /**
     * 从目录使用指定编码读文件
     *
     * @param path   要读取的路径
     * @param encode 使用的编码
     * @return 读出的文件
     */
    public static String readTextFile(String path, String encode) throws IOException {
        return new String(readFile(path), encode);
    }


    /**
     * 格式化输出JSON字符串
     *
     * @return 格式化后的JSON字符串
     */
    public static String toPrettyFormat(String json) {
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonObject);
    }

    /**
     * 从文件得到JSONObject
     *
     * @param path 要读取的路径
     * @return JSONObject
     */
    public static JSONObject getJSONObject(String path) {
        try {
            return (JSONObject) (new JSONParser().parse(IOTools.readTextFile(path, "utf8")));
        } catch (ParseException | IOException e) {
//            throw new RuntimeException(path + ":文件异常！请检查！");
            return new JSONObject();
        }
    }

    public static void zhengliJsonFile(String path) throws IOException, ParseException {
        IOTools.writeTextFile(toPrettyFormat(new JSONParser().parse(IOTools.readTextFile(path, "utf8")).toString()), "utf8", path);
    }

    /**
     * @param arlList 列表去重
     */
    public static void removeDuplicate(ArrayList arlList) {
        HashSet h = new HashSet(arlList);
        arlList.clear();
        arlList.addAll(h);
    }

}