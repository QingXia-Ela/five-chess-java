package src.Gui;

import src.Core.ClientCore;
import src.Core.ServerCore;
import src.Gui.Exception.ValidateErrorException;
import src.MessageHandler.Handler.Client;
import src.MessageHandler.Message.MessageResolver;
import src.Utils.Utils;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.DatagramSocket;
import java.net.SocketException;
//import javax.a

public class Login {
    private JPanel root;
    private JPanel loginPanel;
    private JTextField usernameInput;
    private JTextField portInput;
    private JButton connectServerButton;
    private JButton createServerButton;
    private JTextField plateWidth;
    private JTextField plateHeight;
    private JFrame outerFrame;

    public Login(JFrame outerFrame) {
        this.outerFrame = outerFrame;
        connectServerButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onClickConnectButton();
            }
        });

        createServerButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onClickCreateServerButton();
            }
        });
    }

    /**
     * Will validate plate size
     */
    private int[] validatePlateSize() throws ValidateErrorException {
//        plate size should bigger than 5
        if (Integer.parseInt(plateWidth.getText()) < 5 || Integer.parseInt(plateHeight.getText()) < 5) {
            throw new ValidateErrorException("棋盘大小不合法");
        }
        return new int[]{Integer.parseInt(plateWidth.getText()), Integer.parseInt(plateHeight.getText())};
    }

    /**
     * Will validate username and port
     * */
    private int validateInputs() throws ValidateErrorException {
        if (usernameInput.getText().isEmpty()) {
            throw new ValidateErrorException("用户名不能为空");
        }
        int port;
        try {
            port = Integer.parseInt(portInput.getText());
        } catch (NumberFormatException e) {
            throw new ValidateErrorException("端口输入数字不合法！");
        } catch (Exception e) {
            throw new ValidateErrorException(e.getMessage());
        }

        if (port < 0 || port > 65535) {
            throw new ValidateErrorException("端口不合法");
        }

        return port;
    }

    private void onClickConnectButton() {
        try {
            int port = validateInputs();
            boolean isOnline = Utils.isOnline(port);
            if (!isOnline) {
                throw new ValidateErrorException("服务器离线");
            }
            new ClientCore(port, usernameInput.getText());
            outerFrame.setVisible(false);
        } catch (Exception e) {
            Utils.alert("错误:" + e.getMessage());
        }
    }

    private void onClickCreateServerButton() {
        try {
            int port = validateInputs();
            boolean canUse = Utils.canPortUse(port);
            if (!canUse) {
                throw new ValidateErrorException("端口被占用");
            }
            int[] info = validatePlateSize();
//        create server
            new ServerCore(port, usernameInput.getText(), info[0], info[1]);
            outerFrame.setVisible(false);
        } catch (Exception e) {
            Utils.alert("错误:" + e.getMessage());
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Login");
        Login login = new Login(frame);
        frame.setContentPane(login.root);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 200);
        frame.setLocation(666,666);
        frame.setVisible(true);
    }
}
