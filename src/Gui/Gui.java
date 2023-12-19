package src.Gui;

import src.Chess.ChessPlate;
 
import javax.swing.*;

public class Gui {
    private JPanel root;
    public JPanel plateContainer;
    private JPanel infoContainer;
    private JLabel selfNameValue;
    private JLabel opponentNameValue;
    private JButton regret;

    public ChessPlate plate;

    public Gui(int row, int col){
        ChessPlate plate = new ChessPlate(row,col);
        JFrame frame = new JFrame("ybb");
        plateContainer.add(plate);
        frame.setContentPane(root);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(plate.getWidth() + 500, plate.getHeight());
        frame.setVisible(true);
    }

    public void render() {
        root.repaint();
    }

//    test only
    public static void main(String[] args) throws InterruptedException {
        ChessPlate plate = new ChessPlate(19,19);
        JFrame frame = new JFrame("ybb");
        Gui g = new Gui(19,19);
        g.plateContainer.add(plate);
        frame.setContentPane(g.root);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(plate.getWidth() + 500, plate.getHeight());
        frame.setVisible(true);
    }
}
