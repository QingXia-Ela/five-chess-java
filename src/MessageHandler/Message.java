package src.MessageHandler;

public abstract class Message<T extends Enum> {
    public T type;
    public String message;

    public abstract Message<T> parse_message(String message) throws MessageParseException, MessageTypeNonExistExpection;

    public abstract T parse_string_2_type(String type) throws MessageTypeNonExistExpection;

    public abstract String parse_type_2_string(T type) throws MessageTypeNonExistExpection;

    @Override
    public String toString() {
        try {
            return parse_type_2_string(type) + ":" + message + "\n";
        } catch (MessageTypeNonExistExpection e) {
            return "ERROR:" + e.getMessage() + "\n";
        }
    }

    public Message(String message, T type) {
        this.message = message;
        this.type = type;
    }
}
