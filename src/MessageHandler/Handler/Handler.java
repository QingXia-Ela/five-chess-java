package src.MessageHandler.Handler;

import src.MessageHandler.Message.ClientMessage;
import src.MessageHandler.Message.Enums.MessageType;
import src.MessageHandler.Message.MessageResolver;
import src.MessageHandler.Message.ServerMessage;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EnumMap;

public class Handler {
    //    use this to resolve message
    public static ServerMessage serverMessageUtilsObj = new ServerMessage();
    public static ClientMessage clientMessageUtilsObj = new ClientMessage();

    protected EnumMap<MessageType, ArrayList<ActionListener>> actionMap;

    public Handler() {
        actionMap = new EnumMap<>(MessageType.class);
        for (MessageType type : MessageType.values()) {
            actionMap.put(type, new ArrayList<>());
        }
    }

    /**
     * Add message listener, event listener will receive an event that command is message body.
     * <br>
     * <b>Note</b>: You can use {@link MessageResolver} resolve methods to resolve message into standard message data struct.
     * @param type Message type
     * @param actionListener Event listener
     * @return This
     */
    public Handler addEventListener(MessageType type, ActionListener actionListener) {
        actionMap.get(type).add(actionListener);
        return this;
    }

    public Handler removeEventListener(MessageType type, ActionListener actionListener) {
        actionMap.get(type).remove(actionListener);
        return this;
    }
}
