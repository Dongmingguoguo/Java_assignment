import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class HelperMethods {

    public static void sendFixedLengthMessage(SocketChannel socketChannel, String message) {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(64);
            buffer.put(message.getBytes());
            buffer.flip();
            while (buffer.hasRemaining()) {
                socketChannel.write(buffer);
            }
            System.out.println("Sent: " + message);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String readFile() {
        final String encode = "UTF-8";
        final CharsetDecoder cd = Charset.forName(encode).newDecoder();
        try {
            FileChannel channel = new FileInputStream(new File("/Users/dongmintian994410/Assignment/Server/src/Network.txt")).getChannel();
            ByteBuffer buf = ByteBuffer.allocate(1024);
            CharBuffer cBuf = CharBuffer.allocate(1024);
            int bytesRead = channel.read(buf);
            StringBuilder sb = new StringBuilder();
            int length = 0;
            while (bytesRead != -1) {
                buf.flip();
                //指定未完待续
                cd.decode(buf, cBuf, false);
                cBuf.flip();
                length += cBuf.limit();
                //打印出来
                //System.out.println(cBuf.toString());
                //cBuf.clear();
                //buf.compact();
                //bytesRead = channel.read(buf);
                return new String(cBuf.array(), 0, cBuf.limit());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return encode;
    }



    public static String receiveFixedLengthMessage(SocketChannel socketChannel) {
        String message = "";
        try {
            ByteBuffer byteBuffer = ByteBuffer.allocate(64);
            socketChannel.read(byteBuffer);
            byteBuffer.flip();
            while (byteBuffer.hasRemaining()) {
                message += (char) byteBuffer.get();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return message;
    }

    public static void sendMessage(SocketChannel socketChannel, String message) {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(message.length() + 1);
            buffer.put(message.getBytes());
            buffer.put((byte) 0x00);
            buffer.flip();
            while (buffer.hasRemaining()) {
                socketChannel.write(buffer);
            }
            System.out.println("Sent: " + message);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String receiveMessage(SocketChannel socketChannel) {
        try {
            ByteBuffer byteBuffer = ByteBuffer.allocate(16);
            String message = "";
            while (socketChannel.read(byteBuffer) > 0) {
                char byteRead = 0x00;
                byteBuffer.flip();
                while (byteBuffer.hasRemaining()) {
                    byteRead = (char) byteBuffer.get();
                    if (byteRead == 0x00) {
                        break;
                    }
                    message += byteRead;
                }
                if (byteRead == 0x00) {
                    break;
                }
                byteBuffer.clear();
            }
            return message;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return "";
    }

}
