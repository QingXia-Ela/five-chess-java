package src.Chess.Exception;

public class ChessAlreadyExistException  extends Exception {
    @Override
    public String getMessage() {
        return "该位置已经存在棋子了";
    }
}
