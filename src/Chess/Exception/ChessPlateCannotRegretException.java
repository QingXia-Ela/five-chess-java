package src.Chess.Exception;

/**
 * 棋盘为空
 */
public class ChessPlateCannotRegretException extends Exception {
    @Override
    public String getMessage() {
        return "棋盘为空";
    }
}
