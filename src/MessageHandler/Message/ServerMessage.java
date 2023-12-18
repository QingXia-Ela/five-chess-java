package src.MessageHandler.Message;

import src.Logger.Logger;
import src.MessageHandler.Exception.MessageParseException;
import src.MessageHandler.Exception.MessageTypeNonExistExpection;
import src.MessageHandler.Message.Enums.MessageType;

public class ServerMessage extends Message<MessageType> {
    public ServerMessage parse_message(String message) throws MessageParseException, MessageTypeNonExistExpection {
        String[] message_parts = message.split("\\$");

        if (message_parts.length != 2) {
            Logger.error("该信息不符合协议: " + message);
            throw new MessageParseException();
        }

        return new ServerMessage(message_parts[1], parse_string_2_type(message_parts[0]));
    }

    public MessageType parse_string_2_type(String type) throws MessageTypeNonExistExpection {
        String res = String.valueOf(MessageType.valueOf(type));
        if (res == null) {
            throw new MessageTypeNonExistExpection();
        }
        return MessageType.valueOf(res);
    }

    private static String get_message_type_to_string(MessageType type) throws MessageTypeNonExistExpection {
        return switch (type) {
            case LOGIN_ERROR -> "LOGIN_ERROR";
            case LOGIN_SUCCESS -> "LOGIN_SUCCESS";
            case OK -> "OK";
            case ERROR -> "ERROR";
            default ->  throw new MessageTypeNonExistExpection();
        };
    }

    public String parse_type_2_string(MessageType type) throws MessageTypeNonExistExpection {
        return get_message_type_to_string(type);
    }

    public ServerMessage(String message, MessageType type) {
        super(message, type);
    }

    /**
     * tool register, you will use this when you only need to parse message
     * */
    public ServerMessage() {
        super("", MessageType.OK);
    }
}
