package src.MessageHandler.Message;

import src.MessageHandler.Exception.MessageParseException;
import src.MessageHandler.Exception.MessageTypeNonExistExpection;
import src.MessageHandler.Message.Enums.ServerMessageType;

public class ServerMessage extends Message<ServerMessageType> {
    public String message;
    public ServerMessageType type;

    public ServerMessage parse_message(String message) throws MessageParseException, MessageTypeNonExistExpection {
        String[] message_parts = message.split(":");

        if (message_parts.length != 2) {
            throw new MessageParseException();
        }

        return new ServerMessage(message_parts[1], parse_string_2_type(message_parts[0]));
    }

    public ServerMessageType parse_string_2_type(String type) throws MessageTypeNonExistExpection {
        String res = String.valueOf(ServerMessageType.valueOf(type));
        if (res == null) {
            throw new MessageTypeNonExistExpection();
        }
        return ServerMessageType.valueOf(res);
    }

    private static String get_message_type_from_string(ServerMessageType type) throws MessageTypeNonExistExpection {
        return switch (type) {
            case LOGIN_ERROR -> "LOGIN_ERROR";
            case LOGIN_SUCCESS -> "LOGIN_SUCCESS";
            case LOGOUT_ERROR -> "LOGOUT_ERROR";
            case LOGOUT_SUCCESS -> "LOGOUT_SUCCESS";
            case OK -> "OK";
            case ERROR -> "ERROR";
            default ->  throw new MessageTypeNonExistExpection();
        };
    }

    public static String parse_type_2_string_directly(ServerMessageType type) throws MessageTypeNonExistExpection {
        return get_message_type_from_string(type);
    }

    public String parse_type_2_string(ServerMessageType type) throws MessageTypeNonExistExpection {
        return get_message_type_from_string(type);
    }

    public ServerMessage(String message, ServerMessageType type) {
        super(message, type);
    }
}
