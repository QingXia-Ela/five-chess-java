package src.MessageHandler.Message;

import src.MessageHandler.Exception.MessageParseException;
import src.MessageHandler.Exception.MessageTypeNonExistExpection;
import src.MessageHandler.Message.Enums.MessageType;

public class ClientMessage extends Message<MessageType> {
    public String message;
    public MessageType type;

    public ClientMessage parse_message(String message) throws MessageParseException, MessageTypeNonExistExpection {
        String[] message_parts = message.split(":");

        if (message_parts.length != 2) {
            throw new MessageParseException();
        }

        return new ClientMessage(message_parts[1], parse_string_2_type(message_parts[0]));
    }

    public MessageType parse_string_2_type(String type) throws MessageTypeNonExistExpection {
        String res = String.valueOf(MessageType.valueOf(type));
        if (res == null) {
            throw new MessageTypeNonExistExpection();
        }
        return MessageType.valueOf(res);
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
}
