package cn.jja8.myWorld.all.veryUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * 代码参考https://www.mcbbs.net/thread-1280861-1-1.html
 * （其实是复制的aWa）
 * */
public class PingServer {
    /**
     * varInt 读取函数
     * @apiNote https://wiki.vg/index.php?title=Protocol&oldid=16681
     */
    protected static int readVarInt(DataInputStream in) throws IOException {
        int i = 0;
        int j = 0;
        while (true) {
            int k = in.readByte();
            i |= (k & 0x7F) << (j++ * 7);
            if (j > 5)
                throw new RuntimeException("VarInt too big");
            if ((k & 0x80) != 0x80)
                break;
        }
        return i;
    }
    /**
     * varInt 写入函数
     * */
    protected static void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
        while (true) {
            if ((paramInt & ~0x7F) == 0) {
                out.writeByte(paramInt);
                return;
            }
            out.writeByte(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
    }
    /**
     * 发送数据包格式为：数据包长度 + 内容
     * */
    public static String connect(SocketAddress socketAddress) throws IOException {
        try (Socket socket = new Socket()) {
            socket.setSoTimeout(9000);
            socket.connect(socketAddress, 9000);
            try (
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    DataInputStream in = new DataInputStream(socket.getInputStream());
                    //> Handshake
                    ByteArrayOutputStream handshake_bytes = new ByteArrayOutputStream();
                    DataOutputStream handshake = new DataOutputStream(handshake_bytes)
            ) {
                handshake.writeByte(0x00);
                writeVarInt(handshake, 4);
                writeVarInt(handshake, "127.0.0.1".length());
                handshake.writeBytes("127.0.0.1");
                handshake.writeShort(25565);
                writeVarInt(handshake, 1);

                //< Status Handshake
                writeVarInt(out, handshake_bytes.size()); // Size of packet
                out.write(handshake_bytes.toByteArray());

                //< Status Request
                out.writeByte(0x01); // Size of packet
                out.writeByte(0x00);

                //< Status Response
                // https://wiki.vg/Protocol#Response
                readVarInt(in); // Size
                int pingVersion = readVarInt(in);
                int length = readVarInt(in);
                byte[] data = new byte[length];
                in.readFully(data);
                return new String(data, StandardCharsets.UTF_8);
            }
        }
    }
}
