package src.MessageHandler.Handler;

import src.Logger.Logger;
import src.MessageHandler.Message.ClientMessage;
import src.MessageHandler.Message.Enums.MessageType;
import src.MessageHandler.Message.MessageResolver;
import src.MessageHandler.Message.ServerMessage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.EnumMap;

public class Server extends Handler {
    public static final String HandlerType = "Server";
    public DatagramSocket serverSocket;
    private Thread listenThread;
    private int clientPort;
    private boolean listening;

    /**
     * Create a server handler.
     * <br>
     * You need to call {@link #listen()} to start listening.
     * */
    public Server(int port) throws Exception {
        super();
        Logger.info("Create Server Message Handler at port " + port);
        this.serverSocket = new DatagramSocket(port);

//        heartbeat keep online
        addEventListener(MessageType.HEARTBEAT, e -> {
            try {
                sendMessage(MessageResolver.serializeHeartbeatMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public Server addEventListener(MessageType type, ActionListener actionListener) {
        super.addEventListener(type, actionListener);
        return this;
    }

    @Override
    public Server removeEventListener(MessageType type, ActionListener actionListener) {
        super.removeEventListener(type, actionListener);
        return this;
    }

    /**
     * Send the message to client directly.
     * <br>
     *  <b>Note</b>: You need to use {@link MessageResolver} serialize methods to get the String message.
     * @param message Message to send.
     */
    public void sendMessage(String message) throws IOException {
        if (clientPort == 0) {
            Logger.warning("Client port is not set, can not send message, operation denied.");
            return;
        }
        sendMessage(message, clientPort);
    }

    private void sendMessage(String message, int port) throws IOException {
        byte[] buf = message.getBytes();
        int len = buf.length;
        DatagramPacket packet = new DatagramPacket(buf, len, InetAddress.getByName("localhost"), port);
        serverSocket.send(packet);
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
                    serverSocket.receive(packet);
                    byte[] data = packet.getData();
                    int len = packet.getLength();
                    String in = new String(data, 0, len);
                    ClientMessage msg = clientMessageUtilsObj.parse_message(in);
//                    not from current client, we can respond heartbeat
                    if (msg.type == MessageType.HEARTBEAT) {
                        sendMessage(MessageResolver.serializeHeartbeatMessage(), packet.getPort());
                        Logger.debug("Server received heartbeat: " + in + " from port: " + packet.getPort());
                        continue;
                    }
//                    init client port
                    if (clientPort == 0) {
                        clientPort = packet.getPort();
                    }

                    actionMap.get(msg.type).forEach(action -> action.actionPerformed(new ActionEvent(msg, 0, msg.message)));
                    Logger.debug("Server received: " + in + " from port: " + packet.getPort());
                } catch (Exception e) {
                    Logger.error("Server listen error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        listenThread.start();
    }

    public void unListen() {
        listenThread.interrupt();
        try {
            serverSocket.close();
        } catch (Exception e) {
            Logger.error("Server unListen error: " + e.getMessage());
        } finally {
            listening = false;
        }
    }

    public int getClientPort() {
        return clientPort;
    }

    public void setClientPort(int clientPort) {
        this.clientPort = clientPort;
    }

    //    test only
    public static void main(String[] args) throws Exception {
        Server s = new Server(11451);
        s.listen();

        s.addEventListener(MessageType.LOGIN, e -> {
            try {
                s.sendMessage(MessageResolver.serializeLoginSuccessMessage(114,514));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }
}
