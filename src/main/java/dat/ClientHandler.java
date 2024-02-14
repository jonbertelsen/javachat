package dat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

public class ClientHandler implements Runnable
{
    private final Socket clientSocket;
    private final int clientId;
    private BufferedReader clientInputStream;
    private PrintWriter clientOutputStream;
    private final ConcurrentMap<Integer, ClientHandler> clientMap;
    private final BlockingQueue<Message> messageQueue;

    public ClientHandler(Socket clientSocket, ConcurrentMap clientMap, BlockingQueue messageQueue)
    {
        this.clientMap = clientMap;
        this.messageQueue = messageQueue;
        this.clientSocket = clientSocket;
        this.clientId = clientSocket.getLocalPort();
    }

    @Override
    public void run()
    {
        try
        {
            clientInputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            clientOutputStream = new PrintWriter(clientSocket.getOutputStream(), true);

            String inputLine;
            do
            {
                inputLine = clientInputStream.readLine();
                if ("exit".equals(inputLine))
                {
                    Message message = new Message(inputLine, this.toString(), "all");
                    messageQueue.add(message);
                    System.out.println("Good bye ... closing down");
                    closeResources();
                } else if (inputLine != null)
                {
                    Message message = new Message(inputLine, this.toString(), "all");
                    System.out.println("Adding to messsage queue in clienthandler: " + message.getMessage());
                    messageQueue.add(message);
                }
            } while (inputLine != null && !inputLine.equals("exit"));

        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            closeResources();
        }

    }

    private void closeResources() {
        try {
            System.out.println("Closing connection and resources.");
            if (clientInputStream != null) clientInputStream.close();
            if (clientOutputStream != null) clientOutputStream.close();
            if (clientSocket != null) clientSocket.close();
        } catch (IOException e) {
            System.err.println("Failed to close resources: " + e.getMessage());
        }
    }

    public void addMessage(Message message)
    {
        messageQueue.add(message);
    }

    public void sendMessage(String message)
    {
        clientOutputStream.println(message);
    }

    public int getClientId()
    {
        return clientId;
    }

    public Socket getClientSocket()
    {
        return clientSocket;
    }

    @Override
    public String toString()
    {
        String clientInfo = clientSocket.getInetAddress().toString() + ":" + clientSocket.getPort();
        return clientInfo;
    }
}
