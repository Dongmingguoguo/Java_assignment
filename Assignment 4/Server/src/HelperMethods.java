import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
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

    public static void server(int port) {
        System.out.println("Chat Server started");
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(port));

            boolean running = true;
            while (running) {
                System.out.println("Waiting for request ...");
                SocketChannel socketChannel = serverSocketChannel.accept();
                System.out.println("Connected to Client");
                HelperMethods.sendMessage(socketChannel, "Please input the file path");
                String recerived_path = HelperMethods.receiveMessage(socketChannel);
                File tmpDir = new File(recerived_path);
                Boolean exist = tmpDir.exists();
                HelperMethods.sendMessage(socketChannel, "Please input the local path");
                File localDir = new File(HelperMethods.receiveMessage(socketChannel));


                if (exist){
                    HelperMethods.sendMessage(socketChannel, "File exists\n"+HelperMethods.readFile(recerived_path));
                    RandomAccessFile fromFile = new RandomAccessFile(tmpDir, "rw");
                    FileChannel fromChannel = fromFile.getChannel();
                    RandomAccessFile toFile = new RandomAccessFile(localDir, "rw");
                    FileChannel toChannel = toFile.getChannel();
                    long position = 0;
                    long count    = fromChannel.size();
                    fromChannel.transferTo(position, count, toChannel);
                } else {
                    HelperMethods.sendMessage(socketChannel, "File doesn't exists\n");
                }

                while (exist) {
                    if (HelperMethods.receiveMessage(socketChannel).equals("quit")) {
                        break;
                    }
                    HelperMethods.receiveMessage(socketChannel);
                    HelperMethods.sendMessage(socketChannel, "Received!\n" + HelperMethods.readFile(recerived_path));
                }
            }
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    public static String readFile(String q) {
        final String encode = "UTF-8";
        final CharsetDecoder cd = Charset.forName(encode).newDecoder();
        try {
            FileChannel channel = new FileInputStream(new File(q)).getChannel();
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
