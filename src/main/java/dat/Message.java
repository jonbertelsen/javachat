package dat;

public class Message
{
    private String message;
    private String sender;
    private String receivers;

    public Message(String message, String sender, String receivers)
    {
        this.message = message;
        this.sender = sender;
        this.receivers = receivers;
    }

    public String getMessage()
    {
        return message;
    }

    public String getSender()
    {
        return sender;
    }

    public String getReceivers()
    {
        return receivers;
    }
}
