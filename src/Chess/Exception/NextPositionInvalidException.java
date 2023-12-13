package src.Chess.Exception;

public class NextPositionInvalidException  extends Exception {
    @Override
    public String getMessage() {
        return "Next position invalid";
    }
}
