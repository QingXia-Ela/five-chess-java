package src.Chess;

import src.Chess.Enums.ChessType;

public class SingleChess {
    public ChessType type;
    public int x;
    public int y;

    public SingleChess(int x, int y, ChessType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }
}
