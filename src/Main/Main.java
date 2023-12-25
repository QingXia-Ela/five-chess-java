package src.Main;

import src.Gui.Login;
import src.Logger.Logger;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
//        Logger.setLogLevel(3);
        JFrame frame = new JFrame("Login");
        Login login = new Login(frame);
        frame.setContentPane(login.root);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 200);
        frame.setLocation(666,666);
        frame.setVisible(true);
    }
}
