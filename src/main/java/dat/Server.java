package dat;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.*;

public class Server implements Runnable
{
    private final int port;
    private final ExecutorService executorService;
    private final ConcurrentMap<String, ClientHandler> clientMap = new ConcurrentHashMap<>();
    private final BlockingQueue<Message> messageQueue = new ArrayBlockingQueue<>(50);
    private ClientConnector clientConnector;

    public Server(int port, ExecutorService executorService)
    {
        this.port = port;
        this.executorService = executorService;
    }

    @Override
    public void run()
    {
        try (ServerSocket serverSocket = new ServerSocket(port))
        {
            System.out.println("Server started on port: " + port);
            clientConnector = new ClientConnector(serverSocket, clientMap, messageQueue, executorService);
            executorService.submit(clientConnector);

            Message message = new Message("Server started", "Server", "all");

            while (!Thread.currentThread().isInterrupted() && !message.getMessage().equals("exit"))
            {
                message = messageQueue.take();
                System.out.println("Message received: " + message.getMessage());
                for (ClientHandler clientHandler : clientMap.values())
                {
                    clientHandler.sendMessage(message.getMessage());
                }
            }
        }
        catch (IOException e)
        {
            System.err.println("Server encountered an IOException: " + e.getMessage());
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            System.err.println("Server was interrupted: " + e.getMessage());
        }
        finally
        {
            System.out.println("Server shutting down.");
            clientConnector.stop();
            executorService.shutdownNow();
            // Close resources, if any, here
        }
    }
}
