package src.Chess;

import src.Chess.Exception.ChessAlreadyExistException;
import src.Chess.Exception.ChessPlateCannotRegretException;
import src.Chess.Exception.ExceedChessPlateException;
import src.Chess.Exception.NextPositionInvalidException;
import src.Logger.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.EventObject;
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

    ActionListener winListener = null;

    JPanel panel1;

    public ChessPlate(int row, int col) {
        this.row = row;
        this.col = col;
        this.space = new ChessType[row][col];
        clear_chess();
        setSize((row * 2) * SPACE_MARGIN, (col * 2) * SPACE_MARGIN);
    }

    /**
     * get next pos
     * @param pos
     * @param mode 0: row, 1: col, 2: left bottom -> right top, 3: left top -> right bottom
     * @param isLeft
     * @return next pos
     */
    private int[] getNextPos(int[] pos, int mode, boolean isLeft) throws NextPositionInvalidException {
        int r = pos[0], c = pos[1];
        int[] res = new int[]{-1 ,-1};

        switch (mode) {
            case 0 -> {
                if (isLeft) {
                    res[0] = r;
                    res[1] = c + 1;
                } else {
                    res[0] = r;
                    res[1] = c - 1;
                }
            }
            case 1 -> {
                if (isLeft) {
                    res[0] = r - 1;
                    res[1] = c;
                } else {
                    res[0] = r + 1;
                    res[1] = c;
                }
            }
            case 2 -> {
                if (isLeft) {
                    res[0] = r - 1;
                    res[1] = c - 1;
                } else {
                    res[0] = r + 1;
                    res[1] = c + 1;
                }
            }
            case 3 -> {
                if (isLeft) {
                    res[0] = r - 1;
                    res[1] = c + 1;
                } else {
                    res[0] = r + 1;
                    res[1] = c - 1;
                }
            }
        }

       if (res[0] == -1 || res[1] == -1) {
           throw new NextPositionInvalidException();
       }

        return res;
    }

    /**
     * calc current position chess is win or not
     * @param row
     * @param col
     * @param mode  0: <b>row</b>, 1: <b>col</b>, 2: <b>left bottom -> right top</b>, 3: <b>left top -> right bottom</b>
     * @return
     */
    private boolean calcChess(int row, int col, int mode) {
        ChessType t = space[row][col];
        if (t == ChessType.EMPTY) {
            return false;
        }

        int count = 1;
        int[] nextLeftPos = new int[]{row, col}, nextRightPos = new int[]{row, col};
        boolean LeftFlag = true, RightFlag = true;
        for (int i = 0; i < 9; i++) {
            if (LeftFlag) {
                try {
                    nextLeftPos = getNextPos(nextLeftPos, mode, true);

                    if (space[nextLeftPos[0]][nextLeftPos[1]] == t) {
                        count++;
                    } else {
                        LeftFlag = false;
                    }
                } catch (NextPositionInvalidException e) {
                    LeftFlag = false;
                }
            }

            if (RightFlag) {
                try {
                    nextRightPos = getNextPos(nextRightPos, mode, false);
                    if (space[nextRightPos[0]][nextRightPos[1]] == t) {
                        count++;
                    } else {
                        RightFlag = false;
                    }
                } catch (NextPositionInvalidException e) {
                    RightFlag = false;
                }
            }

            if (count == 5) return true;
        }

        return false;
    }

    private ChessType calcIsWin(int row, int col) {

        boolean flag = false;
        for (int i = 0; i < 4; i++) {
            flag |= calcChess(row, col, i);
        }

        if (flag) {
            return space[row][col];
        }

        return ChessType.EMPTY;
    }

    private void clear_chess() {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                space[i][j] = ChessType.EMPTY;
            }
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
     * <b>Warning</b>: this is a low level api, will directly affect chess plate, and will not remove from stack.
     */
    private void chess_remove(int x, int y) {
        space[x][y] = ChessType.EMPTY;
        render();
    }
    
    public void chess_place(SingleChess c) throws Exception {
        chess_place(c.x, c.y, c.type);
        progress.push(c);
        ChessType t = calcIsWin(c.x, c.y);

        Logger.debug("Chess place at x:" + c.x + " y:" + c.y + " type:" + c.type);

//        judge winner
        if (t != ChessType.EMPTY) {
            if (winListener != null) {
                winListener.actionPerformed(new ActionEvent(this, 0, t.toString()));
            }
            Logger.info("Some one win: " + t);
        }
    }

    public void regret() throws ChessPlateCannotRegretException {
        if (progress.size() == 1) {
            clear();
            return;
        }
        else if (progress.isEmpty()) {
            throw new ChessPlateCannotRegretException();
        }

        SingleChess c1 = progress.pop();
        SingleChess c2 = progress.pop();

        chess_remove(c1.x, c1.y);
        chess_remove(c2.x, c2.y);
    }

    /**
     * clear the chess plate
     *
     * like init
     */
    public void clear() {
        progress.clear();
        clear_chess();
        render();
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
        repaint();
    }

    /**
     * listen someone win event
     * @param listener action listener, you can use <code>ChessType.valueOf(command)</code> to get the enum.
     */
    public ChessPlate onSomeoneWin(ActionListener listener) {
        winListener = listener;
        return this;
    }

    /**
     * Test only
     */
    public static void main(String[] args) throws Exception {
        ChessPlate c = new ChessPlate(8, 7);

        JFrame  j = new JFrame();

        j.setSize(10 * SPACE_MARGIN, 10 * SPACE_MARGIN);

        j.add(c);

        j.setVisible(true);
//        c.chess_place(new SingleChess(0, 0, ChessType.BLACK));
//        c.chess_place(new SingleChess(0, 1, ChessType.WHITE));
//        c.render();
    }
}
