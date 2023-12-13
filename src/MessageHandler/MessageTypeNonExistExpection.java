package src.MessageHandler;

public class MessageTypeNonExistExpection extends Exception {
    @Override
    public String getMessage() {
        return "查找不到相关的消息类型";
    }
}
