package src.Gui;

import javax.swing.*;
//import javax.a

public class Login {
    private JPanel root;
    private JPanel loginPanel;
    private JTextField usernameInput;
    private JTextField portInput;
    private JButton connectServerButton;
    private JButton createServerButton;

    public Login() {

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Login");
        frame.setContentPane(new Login().root);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLocation(666,666);
        frame.setVisible(true);
    }

    private void onClickConnectButton() {

    }

    private void onClickCreateServerButton() {

    }
}
