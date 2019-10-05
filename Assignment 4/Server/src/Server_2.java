import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;


public class Server_2 implements Runnable {

    public String server_path = "/Users/dongmintian994410/Assignment 4/Server/src/Network.txt";
    public int PORT = 5000;
    String name = "Client_2";

    @Override
    public void run() {
        System.out.println("Chat Server started");
        try {
            InetSocketAddress isa2 = new InetSocketAddress(5001);

            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(isa2);

            boolean running = true;
            while (running) {
                System.out.println("Waiting for request ...");
                SocketChannel socketChannel = serverSocketChannel.accept();
                System.out.println("Connected to Client: " + name);
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
                    String m = HelperMethods.receiveMessage(socketChannel);


                    if (m.equals("quit")) {
                        HelperMethods.sendMessage(socketChannel, "quit");
                        socketChannel.close();
                        break;
                    }

                    HelperMethods.sendMessage(socketChannel, "Received!\n" +"From-->    " +name + "\n" + HelperMethods.readFile(recerived_path));
                    System.out.println(m);
                }
            }
        } catch (IOException ex) { ex.printStackTrace(); }

    }
}
