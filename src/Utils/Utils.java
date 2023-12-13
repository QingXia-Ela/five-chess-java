package src.Utils;

import javax.swing.*;

public class Utils {
    public static void alert(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    public static void confirm(String message) {
        JOptionPane.showConfirmDialog(null, message);
    }
}
