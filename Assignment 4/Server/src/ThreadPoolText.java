import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolText {
    public static void main(String[] args) {
        ExecutorService exec = Executors.newFixedThreadPool(3);
        exec.submit(new ChatServer());
        exec.submit(new Server_2());
    }
}
