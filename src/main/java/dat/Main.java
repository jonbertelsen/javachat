package dat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main
{
    public static void main(String[] args)
    {
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        System.out.println("Starting ChatServer");
        Server server = new Server(9090, executorService);
        executorService.submit(server, "Server");
        System.out.println("Starting ChatClient");
        Client client = new Client("127.0.0.1", 9090, executorService);
        executorService.submit(client, "Client");
    }
}