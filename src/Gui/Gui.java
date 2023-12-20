package src.Gui;

import src.Chess.ChessPlate;
 
import javax.swing.*;
import java.awt.event.ActionListener;

public class Gui {
    private JPanel root;
    public JPanel plateContainer;
    private JPanel infoContainer;
    private JLabel selfNameValue;
    private JLabel opponentNameValue;
    private JButton regret;
    private JLabel selfColor;
    private JLabel whoOperate;

    public ChessPlate plate;

    public Gui(int row, int col, boolean isServer) {
        ChessPlate plate = new ChessPlate(row,col);
        JFrame frame = new JFrame("ybb");
        plateContainer.add(plate);
        this.plate = plate;
        frame.setContentPane(root);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(plate.getWidth() + 300, plate.getHeight());
        frame.setVisible(true);

        if (isServer) selfColor.setText("黑色");
        else selfColor.setText("白色");
    }

    public void setWhoOperate(boolean self) {
        whoOperate.setText(self ? "由你操作" : "对方操作");
    }

    public void render() {
        root.repaint();
    }

    public String getSelfNameValue() {
        return selfNameValue.getText();
    }

    public void setSelfNameValue(String name) {
        selfNameValue.setText(name);
    }

    public String getOpponentNameValue() {
        return opponentNameValue.getText();
    }

    public void setOpponentNameValue(String name) {
        opponentNameValue.setText(name);
    }

    public Gui onRegret(ActionListener listener) {
        regret.addActionListener(listener);
        return this;
    };

//    test only
    public static void main(String[] args) throws InterruptedException {
        ChessPlate plate = new ChessPlate(19,19);
        JFrame frame = new JFrame("ybb");
        Gui g = new Gui(19,19, true);
        g.plateContainer.add(plate);
        frame.setContentPane(g.root);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(plate.getWidth() + 500, plate.getHeight());
        frame.setVisible(true);
    }
}
