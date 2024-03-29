package src.MessageHandler.Message;

import src.Chess.Enums.ChessType;
import src.Chess.SingleChess;
import src.MessageHandler.Message.Annotation.AcceptType;
import src.MessageHandler.Message.Enums.MessageType;
import src.MessageHandler.Message.Exception.MessageResolveException;

import java.util.Objects;

public class MessageResolver {
//    deserialize message
    @AcceptType(MessageType.OK)
    public static String resolveOKMessage(String body) {
        return body.trim();
    }

    @AcceptType(MessageType.ERROR)
    public static String resolveErrorMessage(String body) {
        return body.trim();
    }

    @AcceptType(MessageType.REGRET_RESPONSE)
    public static boolean resolveRegretResponseMessage(String body) {
        return Boolean.parseBoolean(body.trim());
    }

    @AcceptType(MessageType.CHESS_REGRET)
    public static String resolveChessRegretMessage(String body) {
        return body.trim();
    }

    @AcceptType(MessageType.CHESS_PLACE)
    public static SingleChess resolveChessPlaceMessage(String body) throws MessageResolveException {
        String[] chessInfo = body.trim().split(":");
//        check length
        if (chessInfo.length != 3) throw new MessageResolveException("棋子信息不合法");
//        check chess type
        if (
            !(
                Objects.equals(chessInfo[0], "BLACK") ||
                Objects.equals(chessInfo[0], "WHITE")
            )
        ) throw new MessageResolveException("棋子类型不合法");

        return new SingleChess(Integer.parseInt(chessInfo[1]), Integer.parseInt(chessInfo[2]), ChessType.valueOf(chessInfo[0]));
    }

    @AcceptType(MessageType.CHAT)
    public static String resolveChatMessage(String body) {
        return body.trim();
    }

    @AcceptType(MessageType.CHAT_RECEIVE)
    public static String resolveChatReceiveMessage(String body) {
        return body.trim();
    }

    @AcceptType(MessageType.HEARTBEAT)
    public static String resolveHeartbeatMessage(String body) {
        return body.trim();
    }

    @AcceptType(MessageType.LOGIN_SUCCESS)
    public static String[] resolveLoginSuccessMessage(String body) throws MessageResolveException {
        String[] PlateInfo = body.trim().split(":");
        if (PlateInfo.length != 3) throw new MessageResolveException("棋盘信息不合法");
        return PlateInfo;
    }

    @AcceptType(MessageType.LOGIN_ERROR)
    public static String resolveLoginErrorMessage(String body) {
        return body.trim();
    }

    @AcceptType(MessageType.LOGIN)
    public static String[] resolveLoginMessage(String body) throws MessageResolveException {
        String[] LoginInfo = body.trim().split(":");
        if (LoginInfo.length != 2) throw new MessageResolveException("登录信息不合法");
        return LoginInfo;
    }

    @AcceptType(MessageType.LOGOUT)
    public static String resolveLogoutMessage(String body) {
        return body.trim();
    }

//    serialize message
    public static String serializeOKMessage(String message) {
        return MessageType.OK + "$" + message;
    }

    public static String serializeErrorMessage(String message) {
        return MessageType.ERROR + "$" + message;
    }

    public static String serializeRegretResponseMessage(boolean isRegret) {
        return MessageType.REGRET_RESPONSE + "$" + isRegret;
    }

    public static String serializeChessRegretMessage() {
        return MessageType.CHESS_REGRET + "$ ";
    }

    public static String serializeChessPlaceMessage(SingleChess chess) {
        return MessageType.CHESS_PLACE + "$" + chess.type + ":" + chess.x + ":" + chess.y;
    }

    public static String serializeChatMessage(String message) {
        return MessageType.CHAT + "$" + message;
    }

    public static String serializeChatReceiveMessage(String message) {
        return MessageType.CHAT_RECEIVE + "$" + message;
    }

    public static String serializeHeartbeatMessage() {
        return MessageType.HEARTBEAT + "$ ";
    }

    public static String serializeLoginSuccessMessage(int row, int col, String name) {
        return MessageType.LOGIN_SUCCESS + "$" + row + ":" + col + ":" + name;
    }

    public static String serializeLoginErrorMessage(String message) {
        return MessageType.LOGIN_ERROR + "$" + message;
    }

    public static String serializeLoginMessage(String username, String password) {
        return MessageType.LOGIN + "$" + username + ":" + password;
    }

    public static String serializeLogoutMessage(String username) {
        return MessageType.LOGOUT + "$" + username;
    }
}
