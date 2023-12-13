package src.Chess;

import src.Chess.Exception.ChessAlreadyExistException;
import src.Chess.Exception.ChessPlateCannotRegretException;
import src.Chess.Exception.ExceedChessPlateException;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.Stack;

public class ChessPlate extends JPanel {
    public static final int SPACE_MARGIN = 42;
    public static final int CHESS_SIZE = 30;
    private final int row;
    private final int col;
    /**
     * 0: empty
     * 1: black
     * 2: white
     */
    private final ChessType[][] space;
    private final boolean TimeForWhite = false;
    private final Stack<SingleChess> progress = new Stack<>();

    JPanel panel1;

    public ChessPlate(int row, int col) {
        this.row = row;
        this.col = col;
        this.space = new ChessType[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                space[i][j] = ChessType.EMPTY;
            }
        }
        setSize((row * 2) * SPACE_MARGIN, (col * 2) * SPACE_MARGIN);
        try {
            chess_place(new SingleChess(0, 0, ChessType.BLACK));
            chess_place(new SingleChess(0, 1, ChessType.WHITE));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * place chess by x, y and type
     * <p>
     * <b>Warning</b>: this is a low level api, will not push info into stack and effect chess plate directly.
     */
    private void chess_place(int x, int y, ChessType type) throws Exception {
        if (x < 0 || x >= this.row || y < 0 || y >= this.col) {
            throw new ExceedChessPlateException();
        }

        if (Objects.requireNonNull(space[x][y]) == ChessType.EMPTY) {
            space[x][y] = type;
        } else {
            throw new ChessAlreadyExistException();
        }
        render();
    }

    /**
     * remove chess by x, y
     * <p>
     * <b>Warning</b>: this is a low level api, will directly affect chess plate.
     */
    private void chess_remove(int x, int y) {
        space[x][y] = ChessType.EMPTY;
        render();
    }
    
    public void chess_place(SingleChess c) throws Exception {
        chess_place(c.x, c.y, c.type);
        progress.push(c);
    }

    public void regret() throws ChessPlateCannotRegretException {
        if (progress.isEmpty() || progress.size() < 2) {
            throw new ChessPlateCannotRegretException();
        }

        SingleChess c1 = progress.pop();
        SingleChess c2 = progress.pop();

        chess_remove(c1.x, c1.y);
        chess_remove(c2.x, c2.y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.clearRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.orange);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.BLACK);
        for (int i = 1; i <= row; i++) {
            g.drawLine(SPACE_MARGIN, i * SPACE_MARGIN, col * SPACE_MARGIN, i * SPACE_MARGIN);

        }
        for (int j = 1; j <= col; j++) {
            g.drawLine(j * SPACE_MARGIN, SPACE_MARGIN, j * SPACE_MARGIN, row * SPACE_MARGIN);
        }
        progress.forEach(c -> {
            if (c.type == ChessType.BLACK) {
                g.setColor(Color.BLACK);
            }
            else {
                g.setColor(Color.WHITE);
            }
            if (c.type != ChessType.EMPTY) g.fillOval((c.x + 1) * SPACE_MARGIN - CHESS_SIZE / 2, (c.y + 1) * SPACE_MARGIN - CHESS_SIZE / 2, CHESS_SIZE, CHESS_SIZE);
        });
    }

    /**
     * render the chess plate
     */
    public void render() {
//        paintComponent(getGraphics());
    }

    public static void main(String[] args) {
        ChessPlate c = new ChessPlate(8, 7);

        JFrame  j = new JFrame();

        j.setSize(10 * SPACE_MARGIN, 10 * SPACE_MARGIN);

        j.add(c);

        j.setVisible(true);
    }
}
