package src.Chess.Exception;

/**
 * 棋子已经存在
 */
public class ChessAlreadyExistException  extends Exception {
    @Override
    public String getMessage() {
        return "该位置已经存在棋子了";
    }
}
