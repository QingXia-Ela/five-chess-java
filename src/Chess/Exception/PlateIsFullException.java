package src.Chess.Exception;

/**
 * 棋盘已经满了
 */
public class PlateIsFullException extends Exception {
    @Override
    public String getMessage() {
        return "棋盘已经满了";
    }
}
