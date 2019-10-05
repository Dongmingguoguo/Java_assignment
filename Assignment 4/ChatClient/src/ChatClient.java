import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ChatClient{
    String client_path = "/Users/dongmintian994410/Assignment 4/ChatClient/src/file_2.txt";
    String server_path = "/Users/dongmintian994410/Assignment 4/Server/src/Network.txt";
    String name = "ChatClient_1";

    public ChatClient() {
        SocketAddress address = new InetSocketAddress("127.0.0.1", 5000);
        File writeName = new File(client_path);
        try (SocketChannel socketChannel = SocketChannel.open(address)) {
            System.out.println("Connected to Chat Server");
            String message;
            Scanner scanner = new Scanner(System.in);
            String ban = ".*"+server_path+".*";
            String ban2 = ".*"+client_path+".*";

            int count1 = 0;

            while (true) {
                // Receive message
                System.out.println("The message from the server");
                System.out.println("Message: " + HelperMethods.receiveMessage(socketChannel));
                System.out.print("> ");
                message = scanner.nextLine();

                if (message.equalsIgnoreCase("quit")) {
                    HelperMethods.sendMessage(socketChannel, "quit");
                    socketChannel.close();
                    break;
                }

                try {
                    try ( FileWriter writer = new FileWriter(writeName, true);
                          BufferedWriter out = new BufferedWriter(writer))
                    {
                        boolean isMatch = Pattern.matches(ban2 ,message) || Pattern.matches(ban ,message);
                        if (!isMatch)
                        {
                            out.write(message);
                            out.write("\n");
                            out.flush();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


                RandomAccessFile fromFile = new RandomAccessFile(client_path, "rw");
                FileChannel fromChannel = fromFile.getChannel();
                RandomAccessFile toFile = new RandomAccessFile(server_path, "rw");
                FileChannel  toChannel = toFile.getChannel();
                long position = 0;
                long count    = fromChannel.size();
                fromChannel.transferTo(position, count, toChannel);
                HelperMethods.sendMessage(socketChannel, message);
            }
        } catch (IOException ex) {
        }
    }

    public static void main(String[] args) {
        new ChatClient();
    }
}
