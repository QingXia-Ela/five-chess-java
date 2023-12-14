package src.Chess;

import src.Chess.Enums.ChessType;
import src.Chess.Enums.PlateState;
import src.Chess.Exception.*;
import src.Logger.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;
import java.util.Stack;

public class ChessPlate extends JPanel {
    public static final int SPACE_MARGIN = 42;
    public static final int HALF_SPACE_MARGIN = SPACE_MARGIN / 2;
    public static final int CHESS_SIZE = 30;
    public static final int HALF_CHESS_SIZE = CHESS_SIZE / 2;
    private final int row;
    private final int col;
    /**
     * 0: empty
     * 1: black
     * 2: white
     */
    private final ChessType[][] space;

    private boolean PlateIsBlocking = false;
    private boolean TimeForWhite = false;
    private final Stack<SingleChess> progress = new Stack<>();

    ActionListener winListener = null;

    JPanel panel1;

    public void setPlateIsBlocking(boolean plateIsBlocking) {
        PlateIsBlocking = plateIsBlocking;
    }

    public boolean getPlateIsBlocking() {
        return PlateIsBlocking;
    }

    /**
     * create a chess plate
     * <p></p>
     * <b>Note</b>: You may need to add <code>MouseListener</code> manually and use "{@link #calcChessPos(int, int)} " to get real chess pos when clicking, and manually judge logic by yourself. Because plate are only a component to show chess.
     * @param row plate row
     * @param col plate col
     */
    public ChessPlate(int row, int col) {
        this.row = row;
        this.col = col;
        this.space = new ChessType[row][col];
        clear_chess();
        setSize((row + 2) * SPACE_MARGIN, (col + 2) * SPACE_MARGIN);
    }

    /**
     * calc real chess pos
     * @param clickX mouse click x
     * @param clickY mouse click y
     * @return real chess pos, it will return null if click pos is not inside a chess
     */
    public int[] calcChessPos(int clickX, int clickY) {
        //               valid pos
        if (clickX < HALF_SPACE_MARGIN || clickY < HALF_SPACE_MARGIN || clickX > (row + 1) * SPACE_MARGIN + HALF_SPACE_MARGIN || clickY > (col + 1) * SPACE_MARGIN + HALF_SPACE_MARGIN) return null;

        int x = (clickX - HALF_SPACE_MARGIN) / SPACE_MARGIN, y = (clickY - HALF_SPACE_MARGIN) / SPACE_MARGIN;

//              click is inside a chess size chess
        int minX = (x + 1) * SPACE_MARGIN - HALF_CHESS_SIZE, minY = (y + 1) * SPACE_MARGIN - HALF_CHESS_SIZE, maxX = (x + 1) * SPACE_MARGIN + HALF_CHESS_SIZE, maxY = (y + 1) * SPACE_MARGIN + HALF_CHESS_SIZE;
        if (clickX < minX || clickY < minY || clickX > maxX || clickY > maxY) return null;

        Logger.debug("Chess Plate: Mouse clicked at " + clickX + ", " + clickY + ", parsed pos: " + x + ", " + y);

        return new int[]{x, y};
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

       if (res[0] == -1 || res[1] == -1 || res[0] >= row || res[1] >= col) {
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

    private PlateState calcIsWin(int row, int col) {
        if (progress.size() == this.row * this.col) return PlateState.PLATE_FULL;

        boolean flag = false;
        for (int i = 0; i < 4; i++) {
            flag |= calcChess(row, col, i);
        }

        if (flag) {
            switch (space[row][col]) {
                case BLACK -> {
                    return PlateState.BLACK_WIN;
                }
                case WHITE -> {
                    return PlateState.WHITE_WIN;
                }
            }
        }

        return PlateState.RUNNING;
    }

    private void clear_chess() {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                space[i][j] = ChessType.EMPTY;
            }
        }
    }

    /**
     * place chess by x, y and type, will render chess plate.
     * <p>
     * <b>Warning</b>: this is a low level api, will not push info into stack and effect chess plate directly.
     */
    private void chess_place(int x, int y, ChessType type) throws ExceedChessPlateException, ChessAlreadyExistException  {
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

    /**
     * Place a chess by "SingleChess" object.
     * <br>
     * <b>Note</b>: This is a low level api. Unless you want to control the next Chess type manually, or we really recommend use {@link #chess_place(int, int)} instead. Because it will auto judge next chess color.
     * @param c single chess
     * @throws Exception "ChessAlreadyExistException" or "ExceedChessPlateException"
     */
    public void chess_place(SingleChess c) throws PlateIsFullException, ExceedChessPlateException, ChessAlreadyExistException {
        if (PlateIsBlocking) return;
        chess_place(c.x, c.y, c.type);
        progress.push(c);
        PlateState t = calcIsWin(c.x, c.y);

        Logger.debug("Chess place at x:" + c.x + " y:" + c.y + " type:" + c.type);

//        judge winner
        if (t == PlateState.PLATE_FULL) {
            setPlateIsBlocking(true);
            throw new PlateIsFullException();
        }
        else if (t != PlateState.RUNNING) {
            Logger.info("Some one win: " + t);
            setPlateIsBlocking(true);
            if (winListener != null) {
//                this may cause render fail when it has thread block mission
                winListener.actionPerformed(new ActionEvent(this, 0, t.toString()));
            }
        }
    }

    /**
     * Place a chess by x and y. Type will auto judge.
     * @param x chess real x pos
     * @param y chess real y pos
     */
    public void chess_place(int x, int y) throws PlateIsFullException, ExceedChessPlateException, ChessAlreadyExistException {
        if (PlateIsBlocking) return;
        chess_place(new SingleChess(x, y, TimeForWhite ? ChessType.WHITE : ChessType.BLACK));
        TimeForWhite = !TimeForWhite;
    }

    public void regret() throws ChessPlateCannotRegretException {
        if (PlateIsBlocking) return;
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
     * <p>
     * like init, it will unblock plate
     */
    public void clear() {
        progress.clear();
        clear_chess();
        render();
        TimeForWhite = false;
        setPlateIsBlocking(false);
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
        Logger.debug("Render chess plate");
        repaint();
    }

    /**
     * listen someone win event
     * <p>
     * <b>Note</b>: It will only add once, and it always uses the listener which is join at latest time.
     * <p>
     * <b>Warning</b>: If you have thread block mission, you should put the task into a new thread in order to prevent chess plate blocking.
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
        ChessPlate c = new ChessPlate(2, 2);

        JFrame  j = new JFrame();

        j.setSize((19 + 2) * SPACE_MARGIN, (19 + 2) * SPACE_MARGIN);

        j.add(c);

        j.setVisible(true);

        c.onSomeoneWin(e -> {

            System.out.println(e.getActionCommand());
            new Thread(() -> {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                c.clear();
                System.out.println("clean!");
            }).start();

        }).addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Logger.debug("Get Click! " + e.getX() + " " + e.getY());

                int[] pos = c.calcChessPos(e.getX(), e.getY());
                if (pos == null) return;

                try {
                    c.chess_place(pos[0], pos[1]);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }
}
