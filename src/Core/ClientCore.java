package src.Core;

import src.Chess.ChessPlate;
import src.Chess.Enums.ChessType;
import src.Chess.Exception.ChessAlreadyExistException;
import src.Chess.SingleChess;
import src.Gui.Gui;
import src.Logger.Logger;
import src.MessageHandler.Handler.Client;
import src.MessageHandler.Message.Enums.MessageType;
import src.MessageHandler.Message.MessageResolver;
import src.Utils.Utils;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.SocketException;

public class ClientCore extends Core {
    Client clientMessageHandler;
    int row;
    int col;
    Gui g;
    ChessPlate chessPlate;
    String selfName;
    String opponentName;
    boolean canOperate = false;

    /**
     * Create client core, you need to ensure that the port is available to use.
     */
    public ClientCore(int port, String selfName) throws Exception {
        clientMessageHandler = new Client(port);
        clientMessageHandler.listen();
        this.selfName = selfName;
        addMessageHandlerEvent();
        clientMessageHandler.sendMessage(MessageResolver.serializeLoginMessage(selfName, "114514"));
    }

    private void showChessPlate() {
        g = new Gui(row, col);
        g.setSelfNameValue(selfName);
        g.setOpponentNameValue(opponentName);
        chessPlate = g.plate;
    }

    private void addChessPlateEvent() {
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
                    clientMessageHandler.sendMessage(MessageResolver.serializeChessPlaceMessage(
                            new SingleChess(pos[0], pos[1], ChessType.WHITE))
                    );

//                block operation
                    chessPlate.setPlateIsBlocking(true);
                    canOperate = false;
                } catch (ChessAlreadyExistException ex) {
                    Utils.alert(ex.getMessage());
                } catch (Exception ex) {
                    Logger.error(ex.getMessage());
                }
            }
        });
    }

    private void addMessageHandlerEvent() {
        clientMessageHandler
                .addEventListener(MessageType.LOGIN_SUCCESS, e -> {
                    try {
                        String[] info = MessageResolver.resolveLoginSuccessMessage(e.getActionCommand());
                        this.row = Integer.parseInt(info[0]);
                        this.col = Integer.parseInt(info[1]);
                        this.opponentName = info[2];
                        showChessPlate();
//                add chess plate event after login success
                        addChessPlateEvent();
                    } catch (Exception ex) {
                        Logger.error(ex.getMessage());
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
