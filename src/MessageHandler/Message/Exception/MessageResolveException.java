package src.MessageHandler.Message.Exception;

public class MessageResolveException extends Exception {
    public MessageResolveException() {

    }
    public MessageResolveException(String message) {
        super(message);
    }
    @Override
    public String getMessage() {
        return "消息解析异常";
    }
}
