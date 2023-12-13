package src.MessageHandler;

public class MessageParseException extends Exception {
    @Override
    public String getMessage() {
        return "传输信息转换失败或传输信息非法";
    }
}
