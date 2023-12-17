package src.MessageHandler.Handler;

import src.Logger.Logger;
import src.MessageHandler.Exception.MessageParseException;
import src.MessageHandler.Exception.MessageTypeNonExistExpection;
import src.MessageHandler.Message.ClientMessage;
import src.MessageHandler.Message.Enums.MessageType;
import src.MessageHandler.Message.MessageResolver;
import src.MessageHandler.Message.ServerMessage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.EnumMap;

public class Server extends Handler {
//    use this to resolve message
    public static ServerMessage serverMessageUtilsObj = new ServerMessage();
    public static ClientMessage clientMessageUtilsObj = new ClientMessage();
    public static final String HandlerType = "Server";
    public ServerSocket serverSocket;
    private EnumMap<MessageType, ArrayList<ActionListener>> actionMap;
    private Thread listenThread;

    public Server(int port) throws Exception {
        Logger.debug("Create Server Message Handler at port " + port);
        this.serverSocket = new ServerSocket(port);
    }

    /**
     * Add message listener, event listener will receive an event that command is message body.
     * <br>
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
                    Socket s = serverSocket.accept();
                    String in = new DataInputStream(s.getInputStream()).readUTF();
                    ClientMessage msg = clientMessageUtilsObj.parse_message(in);
                    actionMap.get(msg.type).forEach(action -> action.actionPerformed(new ActionEvent(msg, 0, msg.message)));
                    Logger.debug("Server received: " + in);
                } catch (Exception e) {
                    Logger.error("Server listen error: " + e.getMessage());
                }
            }
        });
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
