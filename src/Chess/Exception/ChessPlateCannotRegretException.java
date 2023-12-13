package src.Chess.Exception;

public class ChessPlateCannotRegretException extends Exception {
    @Override
    public String getMessage() {
        return "棋盘为空或当前步数无法进行悔棋操作";
    }
}
