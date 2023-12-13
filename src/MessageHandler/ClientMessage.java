package src.MessageHandler;

public class ClientMessage extends Message<ClientMessageType> {
    public String message;
    public ClientMessageType type;

    public ClientMessage parse_message(String message) throws MessageParseException, MessageTypeNonExistExpection {
        String[] message_parts = message.split(":");

        if (message_parts.length != 2) {
            throw new MessageParseException();
        }

        return new ClientMessage(message_parts[1], parse_string_2_type(message_parts[0]));
    }

    public ClientMessageType parse_string_2_type(String type) throws MessageTypeNonExistExpection {
        String res = String.valueOf(ClientMessageType.valueOf(type));
        if (res == null) {
            throw new MessageTypeNonExistExpection();
        }
        return ClientMessageType.valueOf(res);
    }

    private static String get_message_type_from_string(ClientMessageType type) throws MessageTypeNonExistExpection {
        return switch (type) {
            case LOGIN -> "LOGIN";
            case LOGOUT -> "LOGOUT";
            case CHAT -> "CHAT";
            case CHESS_PLACE -> "CHESS_PLACE";
            case CHESS_REGRET -> "CHESS_REGRET";
            default -> throw new MessageTypeNonExistExpection();
        };
    }

    public static String parse_type_2_string_directly(ClientMessageType type) throws MessageTypeNonExistExpection {
        return get_message_type_from_string(type);
    }

    public String parse_type_2_string(ClientMessageType type) throws MessageTypeNonExistExpection {
        return get_message_type_from_string(type);
    }

    public ClientMessage(String message, ClientMessageType type) {
        super(message, type);
    }
}
