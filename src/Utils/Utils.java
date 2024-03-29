package src.Utils;

import src.Logger.Logger;
import src.MessageHandler.Message.MessageResolver;

import javax.swing.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Utils {
    private static ExecutorService executorService = Executors.newSingleThreadExecutor();
    public static void alert(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    public static int confirm(String message) {
        return JOptionPane.showConfirmDialog(null, message);
    }

    /**
     * Show a waiting dialog. You should call {@link JOptionPane#setVisible(boolean)} manually to control visibility.
     * We suggest you only create one in the class.
     */
    public static JOptionPane showWaitingDialog(String message) {
        JOptionPane p = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
        return p;
    }

    /**
     * Check target server is online.
     * <br>
     * <b>Warning</b>: This method is blocking thread about 100ms.
     * */
    public static boolean isOnline(int port) throws Exception {
        AtomicBoolean online = new AtomicBoolean(false);
        Logger.debug("Checking if server is online at :"+port+"...");
        DatagramSocket socket = new DatagramSocket();

        Thread t = new Thread(() -> {
            try {
                byte[] hb = MessageResolver.serializeHeartbeatMessage().getBytes();

                socket.send(new DatagramPacket(hb, hb.length, InetAddress.getByName("localhost"), port));

                byte[] response = new byte[1024];

                DatagramPacket packet = new DatagramPacket(response, response.length);

                socket.receive(packet);

                online.set(true);
            } catch (Exception e) {
                Logger.error(e.getMessage());
            }
        });

        t.start();

        Thread.sleep(100);

        return online.get();
    }

    public static boolean canPortUse(int port) {
        try {
            new DatagramSocket(port).close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
