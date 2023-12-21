package src.MessageHandler.Handler;

import src.Logger.Logger;
import src.MessageHandler.Message.ClientMessage;
import src.MessageHandler.Message.Enums.MessageType;
import src.MessageHandler.Message.Exception.MessageResolveException;
import src.MessageHandler.Message.MessageResolver;
import src.MessageHandler.Message.ServerMessage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;

public class Client extends Handler{
    public static final String HandlerType = "Client";

    public DatagramSocket clientSocket;
    private Thread listenThread;
    private int serverPort;
    private boolean listening;

    public Client(int port) throws SocketException {
        super();
        this.serverPort = port;
        Logger.info("Create Client Message Handler to port " + port);
        this.clientSocket = new DatagramSocket();
//        heartbeat keep online
        addEventListener(MessageType.HEARTBEAT, e -> {
            Logger.info("Heartbeat received");
        });
    }

    @Override
    public Client addEventListener(MessageType type, ActionListener actionListener) {
        super.addEventListener(type, actionListener);
        return this;
    }

    @Override
    public Client removeEventListener(MessageType type, ActionListener actionListener) {
        super.removeEventListener(type, actionListener);
        return this;
    }

    /**
     * Send the message to server directly.
     * <br>
     *  <b>Note</b>: You need to use {@link MessageResolver} serialize methods to get the String message.
     * @param message Message to send.
     */
    public void sendMessage(String message) throws IOException {
        byte[] buf = message.getBytes();
        int len = buf.length;
        DatagramPacket packet = new DatagramPacket(buf, len, InetAddress.getByName("localhost"), serverPort);
        clientSocket.send(packet);
    }

    public void listen() {
        if (listening) {
            return;
        }
        listening = true;
        listenThread = new Thread(() -> {
            while (true) {
                try {
                    DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
                    clientSocket.receive(packet);
                    byte[] data = packet.getData();
                    int len = packet.getLength();

                    String in = new String(data, 0, len);
                    ServerMessage msg = serverMessageUtilsObj.parse_message(in);
                    actionMap.get(msg.type).forEach(action -> action.actionPerformed(new ActionEvent(msg, 0, msg.message)));
                    Logger.debug("Client received: " + in);
                } catch (Exception e) {
                    Logger.error("Client listen error: " + e.getMessage());
                }
            }
        });
        listenThread.start();
    }

    public void unListen() {
        listenThread.interrupt();
        try {
            clientSocket.close();
        } catch (Exception e) {
            Logger.error("Server unListen error: " + e.getMessage());
        } finally {
            listening = false;
        }
    }

    //    test only
    public static void main(String[] args) throws Exception {
        Client c = new Client(1145);
        c.listen();
        Thread.sleep(1000);
        c.sendMessage(MessageResolver.serializeHeartbeatMessage());
    }
}
