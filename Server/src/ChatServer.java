import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ChatServer {

    public ChatServer() {

        System.out.println("Chat Server started");
        int wrongtimes = 0;
        while (wrongtimes <= 3){
            try {

                ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
                serverSocketChannel.socket().bind(new InetSocketAddress(5000));// Build the server

                boolean running = true;
                while (running) {
                    System.out.println("Waiting for request ...");
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    System.out.println("Connected to Client");
                    HelperMethods.sendMessage(socketChannel, "Please input the file path");
                    File tmpDir = new File(HelperMethods.receiveMessage(socketChannel));
                    Boolean exist = tmpDir.exists();

                    if (exist){
                        HelperMethods.sendMessage(socketChannel, "File exists\n"+HelperMethods.readFile());
                        RandomAccessFile fromFile = new RandomAccessFile("/Users/dongmintian994410/Assignment/Server/src/Network.txt", "rw");
                        FileChannel      fromChannel = fromFile.getChannel();
                        RandomAccessFile toFile = new RandomAccessFile("/Users/dongmintian994410/Assignment/Client/src/fromfile.txt", "rw");
                        FileChannel  toChannel = toFile.getChannel();
                        long position = 0;
                        long count = fromChannel.size();
                        fromChannel.transferTo(position, count, toChannel);
                    } else {
                        HelperMethods.sendMessage(socketChannel, "File doesn't exists\n");

                    }

                    while (exist) {
                        //while the file exists it will go to the while loop for keeping receiving the message and send "received!" message back to the client part.
                        String m = HelperMethods.receiveMessage(socketChannel);
                        if (m.equalsIgnoreCase("quit")) {
                            //If the quit be sent to the server it should trigger break part for droppoing out.
                            HelperMethods.sendMessage(socketChannel, "quit");
                            socketChannel.close();
                            break;
                        }
                        HelperMethods.sendMessage(socketChannel, "Received!\n" + HelperMethods.readFile());
                    }

                }
            } catch (IOException ex) { ex.printStackTrace(); }
        }

        try {

            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(5000));// Build the server

            boolean running = true;
            while (running) {
                System.out.println("Waiting for request ...");
                SocketChannel socketChannel = serverSocketChannel.accept();
                System.out.println("Connected to Client");
                HelperMethods.sendMessage(socketChannel, "Please input the file path");
                File tmpDir = new File(HelperMethods.receiveMessage(socketChannel));
                Boolean exist = tmpDir.exists();

                if (exist){
                    HelperMethods.sendMessage(socketChannel, "File exists\n"+HelperMethods.readFile());
                    RandomAccessFile fromFile = new RandomAccessFile("/Users/dongmintian994410/Assignment/Server/src/Network.txt", "rw");
                    FileChannel      fromChannel = fromFile.getChannel();
                    RandomAccessFile toFile = new RandomAccessFile("/Users/dongmintian994410/Assignment/Client/src/fromfile.txt", "rw");
                    FileChannel  toChannel = toFile.getChannel();
                    long position = 0;
                    long count = fromChannel.size();
                    fromChannel.transferTo(position, count, toChannel);
                } else { HelperMethods.sendMessage(socketChannel, "File doesn't exists\n"); }

                while (exist) {
                    //while the file exists it will go to the while loop for keeping receiving the message and send "received!" message back to the client part.
                    String m = HelperMethods.receiveMessage(socketChannel);
                    if (m.equalsIgnoreCase("quit")) {
                    //If the quit be sent to the server it should trigger break part for droppoing out.
                        HelperMethods.sendMessage(socketChannel, "quit");
                        socketChannel.close();
                        break;
                    }
                    HelperMethods.sendMessage(socketChannel, "Received!\n" + HelperMethods.readFile());
                }

            }
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    public static void main(String[] args) {

        new ChatServer();
    }
}
