package src.Gui;

import src.Chess.ChessPlate;
 
import javax.swing.*;
import java.awt.*;

public class Gui {
    private JPanel root;
    public JPanel plateContainer;
    private JPanel infoContainer;

    private void addPlate() {
        this.plateContainer.add(new ChessPlate(19,19));
    }

    public static void main(String[] args) throws InterruptedException {
        ChessPlate plate = new ChessPlate(19,19);
        JFrame frame = new JFrame("ybb");
        Gui g = new Gui();
        g.plateContainer.add(plate);
        frame.setContentPane(g.root);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(plate.getWidth() + 300, plate.getHeight());
        frame.setVisible(true);
    }
}
