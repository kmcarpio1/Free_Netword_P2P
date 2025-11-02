package src.connect;

import src.datatypes.IpPort;

class Message {
    public static final Message NULLMSG;
    static {
        NULLMSG = new Message("", null);
    }

    private final String message;
    private IpPort sender;

    public Message(String input, IpPort sender){
        if (input == null) input = "";
        message = input;
        this.sender = sender;
    }

    public IpPort getSender(){
        return (IpPort) sender.clone();
    }

    public String toString(){
        return String.copyValueOf(message.toCharArray(), 0, message.length());
    }

}
