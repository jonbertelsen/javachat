package dat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;

public class Client implements Runnable
{
    private final int PORT;
    private final String IP;

    private Socket clientSocket;
    private PrintWriter outputStream;
    private BufferedReader inputStream;
    private ExecutorService executorService;

    public Client(String IP, int PORT, ExecutorService executorService)
    {
        this.IP = IP;
        this.PORT = PORT;
        this.executorService = executorService;
    }

    @Override
    public void run()
    {
        try
        {
            clientSocket = new Socket(IP, PORT);
            outputStream = new PrintWriter(clientSocket.getOutputStream(), true);
            inputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            MessageReceiver messageReceiver = new MessageReceiver(clientSocket, inputStream);
            executorService.submit(messageReceiver);
            sendMessages();
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

    private void sendMessages()
    {
        System.out.println("Send messages to chat server:");
        Scanner scanner = new Scanner(System.in);
        try {
            String inputLine;

            // send messages to server. The ressources will be close by receiver
            while ((inputLine = scanner.nextLine()) != null) {
                outputStream.println(inputLine);
            }
        } catch (Exception e) {
            System.err.println("Error in sendMessages: " + e.getMessage());
        } finally {
            closeResources();
        }
    }

    private void closeResources() {
        try {
            System.out.println("Closing connection and resources.");
            if (inputStream != null) inputStream.close();
            if (clientSocket != null) clientSocket.close();
        } catch (IOException e) {
            System.err.println("Failed to close resources: " + e.getMessage());
        }
    }
}
