package src.MessageHandler.Handler;

import src.Logger.Logger;
import src.MessageHandler.Message.ClientMessage;
import src.MessageHandler.Message.Enums.MessageType;
import src.MessageHandler.Message.MessageResolver;
import src.MessageHandler.Message.ServerMessage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.EnumMap;

public class Server extends Handler {
//    use this to resolve message
    public static ServerMessage serverMessageUtilsObj = new ServerMessage();
    public static ClientMessage clientMessageUtilsObj = new ClientMessage();
    public static final String HandlerType = "Server";
    public DatagramSocket serverSocket;
    private final EnumMap<MessageType, ArrayList<ActionListener>> actionMap;
    private Thread listenThread;
    private int clientPort;

    /**
     * Create a server handler.
     * <br>
     * You need to call {@link #listen()} to start listening.
     * */
    public Server(int port) throws Exception {
        this.actionMap = new EnumMap<>(MessageType.class);
        for (MessageType type : MessageType.values()) {
            actionMap.put(type, new ArrayList<>());
        }
        Logger.info("Create Server Message Handler at port " + port);
        this.serverSocket = new DatagramSocket(port);
    }

    /**
     * Add message listener, event listener will receive an event that command is message body.
     * <br>
     * <b>Note</b>: You can use {@link MessageResolver} resolve methods to resolve message into standard message data struct.
     * @param type Message type
     * @param actionListener Event listener
     * @return This
     */
    public Server addEventListener(MessageType type, ActionListener actionListener) {
        actionMap.get(type).add(actionListener);
        return this;
    }

    public Server removeEventListener(MessageType type, ActionListener actionListener) {
        actionMap.get(type).remove(actionListener);
        return this;
    }

    public void listen() {
        listenThread = new Thread(() -> {
            while (true) {
                try {
                    DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
                    serverSocket.receive(packet);
                    byte[] data = packet.getData();
                    int len = packet.getLength();
//                    init client port
                    if (clientPort == 0) {
                        clientPort = packet.getPort();
                    }
                    String in = new String(data, 0, len);
                    ClientMessage msg = clientMessageUtilsObj.parse_message(in);
                    actionMap.get(msg.type).forEach(action -> action.actionPerformed(new ActionEvent(msg, 0, msg.message)));
                    Logger.debug("Server received: " + in);
                } catch (Exception e) {
                    Logger.error("Server listen error: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Send the message to client directly.
     * <br>
     *  <b>Note</b>: You need to use {@link MessageResolver} serialize methods to get the String message.
     * @param message Message to send.
     */
    public void sendMessage(String message) {
        if (clientPort == 0) {
            Logger.warning("Client port is not set, can not send message, operation denied.");
            return;
        }
        byte[] buf = message.getBytes();
        int len = buf.length;
        DatagramPacket packet = new DatagramPacket(buf, len, serverSocket.getLocalAddress(), clientPort);
    }

    public void unListen() {
        listenThread.interrupt();
        try {
            serverSocket.close();
        } catch (Exception e) {
            Logger.error("Server unListen error: " + e.getMessage());
        }
    }

//    test only
    public static void main(String[] args) {

    }
}
