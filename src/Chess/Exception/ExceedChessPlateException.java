package src.Chess.Exception;

/**
 * 超出棋盘范围
 */
public class ExceedChessPlateException extends Exception {
    @Override
    public String toString() {
        return "超出棋盘范围";
    }
}
