package dat;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

public class ClientConnector implements Runnable
{
    private final ServerSocket serverSocket;
    private final ExecutorService executorService;
    private final ConcurrentMap<String, ClientHandler> clientMap;
    private final BlockingQueue<Message> messageQueue;
    private boolean running = true;

    public ClientConnector(ServerSocket serverSocket, ConcurrentMap<String, ClientHandler> clientMap, BlockingQueue<Message> messageQueue, ExecutorService executorService)
    {
        this.serverSocket = serverSocket;
        this.clientMap = clientMap;
        this.messageQueue = messageQueue;
        this.executorService = executorService;
    }

    @Override
    public void run()
    {
        System.out.println("Client Connector started.");
        try
        {
            do
            {
                Socket clientSocket = serverSocket.accept(); // blocking call
                ClientHandler clientHandler = new ClientHandler(clientSocket, clientMap, messageQueue);
                registerClient(clientHandler);
                executorService.submit(clientHandler);
            } while (running);
        }
        catch (Exception e)
        {
            System.err.println("Error in ClientConnector: " + e.getMessage());
        }
        finally
        {
            executorService.shutdown();
        }
    }

    private void registerClient(ClientHandler clientHandler)
    {
        String clientInfo = clientHandler.toString();
        clientMap.put(clientInfo, clientHandler);
    }

    public void stop()
    {
        running = false;
    }
}
