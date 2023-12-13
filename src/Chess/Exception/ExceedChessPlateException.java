package src.Chess.Exception;

public class ExceedChessPlateException extends Exception {
    @Override
    public String toString() {
        return "超出棋盘范围";
    }
}
