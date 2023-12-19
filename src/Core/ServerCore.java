package src.Core;

import src.Chess.ChessPlate;
import src.Chess.Enums.ChessType;
import src.Chess.Exception.ChessAlreadyExistException;
import src.Chess.SingleChess;
import src.Gui.Gui;
import src.Logger.Logger;
import src.MessageHandler.Handler.Server;
import src.MessageHandler.Message.Enums.MessageType;
import src.MessageHandler.Message.Exception.MessageResolveException;
import src.MessageHandler.Message.MessageResolver;
import src.Utils.Utils;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class ServerCore extends Core {
    Server serverMessageHandler;
    int row;
    int col;
    Gui g;
    ChessPlate chessPlate;
    String selfName;
    boolean canOperate = true;

    /**
     * @throws Exception when port is already in use.
     * */
    public ServerCore(int port, String selfName, int row, int col) throws Exception {
        serverMessageHandler = new Server(port);
        serverMessageHandler.listen();
        this.row = row;
        this.col = col;
        g = new Gui(row, col);
        g.setSelfNameValue(selfName);
        g.setOpponentNameValue("等待加入...");
        chessPlate = g.plate;
        this.selfName = selfName;
        addMessageHandlerEvent();
        addChessPlateEvent();
    }

    private void addChessPlateEvent() {
//        chessPlate
        chessPlate.onSomeoneWin(e -> {
            Utils.alert("");
        }).addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!canOperate) {
                    return;
                }
                int[] pos = chessPlate.calcChessPos(e.getX(), e.getY());
                if (pos == null) return;

                try {
                    chessPlate.chess_place(pos[0], pos[1]);
                    serverMessageHandler.sendMessage(MessageResolver.serializeChessPlaceMessage(
                            new SingleChess(pos[0], pos[1], ChessType.BLACK))
                    );
                } catch (ChessAlreadyExistException ex) {
                    Utils.alert(ex.getMessage());
                }  catch (Exception ex) {
                    Logger.error(ex.getMessage());
                }



//                block operation
                chessPlate.setPlateIsBlocking(true);
                canOperate = false;
            }
        });
    }

    private void addMessageHandlerEvent() {
        serverMessageHandler
                .addEventListener(MessageType.LOGIN, e -> {
                    try {
                        String[] loginInfo = MessageResolver.resolveLoginMessage(e.getActionCommand());
                        String username = loginInfo[0];
                        g.setOpponentNameValue(username);
                        serverMessageHandler.sendMessage(MessageResolver.serializeLoginSuccessMessage(row, col, selfName));
                    } catch (Exception ex) {
                        try {
                            serverMessageHandler.sendMessage(MessageResolver.serializeLoginErrorMessage(ex.getMessage()));
                        } catch (IOException exc) {
                            Logger.error(exc.getMessage());
                        }
                    }
                })
                .addEventListener(MessageType.CHESS_PLACE, e -> {
                    try {
                        SingleChess info = MessageResolver.resolveChessPlaceMessage(e.getActionCommand());
//                unblock operation
                        chessPlate.setPlateIsBlocking(false);
                        chessPlate.chess_place(info.x, info.y);

                        canOperate = true;
                    } catch (Exception ex) {
                        Logger.error(ex.getMessage());
                    }
                })
                .addEventListener(MessageType.CHESS_REGRET, e -> {})
                .addEventListener(MessageType.REGRET_RESPONSE, e -> {});
    }
}
