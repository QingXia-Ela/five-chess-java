package src.MessageHandler.Message.Enums;

public enum MessageType {
    OK,
    ERROR,
    REGRET_RESPONSE,
    CHESS_REGRET,
    CHESS_PLACE,
    CHAT,
    CHAT_RECEIVE,
    /** keep connection */
    HEARTBEAT,
    /** client only */
    LOGIN,
    /** client only */
    LOGOUT,
    /** server only */
    LOGIN_ERROR,
    /** server only */
    LOGIN_SUCCESS
}
