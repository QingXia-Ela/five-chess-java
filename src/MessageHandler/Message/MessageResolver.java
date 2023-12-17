package src.MessageHandler.Message;

import src.Chess.Enums.ChessType;
import src.Chess.SingleChess;
import src.MessageHandler.Message.Annotation.AcceptType;
import src.MessageHandler.Message.Enums.MessageType;
import src.MessageHandler.Message.Exception.MessageResolveException;

import java.util.Objects;

public class MessageResolver {
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
            !Objects.equals(chessInfo[0], "BLACK") ||
            !Objects.equals(chessInfo[1], "WHITE")
        ) throw new MessageResolveException("棋子类型不合法");

        return new SingleChess(Integer.parseInt(chessInfo[0]), Integer.parseInt(chessInfo[1]), ChessType.valueOf(chessInfo[2]));
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
    public static int[] resolveLoginSuccessMessage(String body) throws MessageResolveException {
        String[] PlateInfo = body.trim().split(":");
        if (PlateInfo.length != 2) throw new MessageResolveException("棋盘信息不合法");
        return new int[]{Integer.parseInt(PlateInfo[0]), Integer.parseInt(PlateInfo[1])};
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
}
