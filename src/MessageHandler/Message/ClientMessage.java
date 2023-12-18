package src.MessageHandler.Message;

import src.Logger.Logger;
import src.MessageHandler.Exception.MessageParseException;
import src.MessageHandler.Exception.MessageTypeNonExistExpection;
import src.MessageHandler.Message.Enums.MessageType;

public class ClientMessage extends Message<MessageType> {

    public ClientMessage parse_message(String message) throws MessageParseException, MessageTypeNonExistExpection {
        String[] message_parts = message.split("\\$");

        if (message_parts.length != 2) {
            Logger.error("该信息不符合协议: " + message);
            throw new MessageParseException();
        }

        return new ClientMessage(message_parts[1], parse_string_2_type(message_parts[0]));
    }

    public MessageType parse_string_2_type(String type) throws MessageTypeNonExistExpection {
        String res = MessageType.valueOf(type).toString();
        if (res == null) {
            throw new MessageTypeNonExistExpection();
        }
        return MessageType.valueOf(type);
    }

    private static String get_message_type_from_string(MessageType type) throws MessageTypeNonExistExpection {
        return switch (type) {
            case LOGIN -> "LOGIN";
            case LOGOUT -> "LOGOUT";
            case CHAT -> "CHAT";
            case CHESS_PLACE -> "CHESS_PLACE";
            case CHESS_REGRET -> "CHESS_REGRET";
            default -> throw new MessageTypeNonExistExpection();
        };
    }

    public String parse_type_2_string(MessageType type) throws MessageTypeNonExistExpection {
        return get_message_type_from_string(type);
    }

    public ClientMessage(String message, MessageType type) {
        super(message, type);
    }

    public ClientMessage() {
        super("", MessageType.OK);
    }
}
