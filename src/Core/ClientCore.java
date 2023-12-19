package src.Core;

import src.Chess.ChessPlate;
import src.Chess.Enums.ChessType;
import src.Chess.Enums.PlateState;
import src.Chess.Exception.ChessAlreadyExistException;
import src.Chess.Exception.ChessPlateCannotRegretException;
import src.Chess.Exception.PlateIsFullException;
import src.Chess.SingleChess;
import src.Gui.Gui;
import src.Logger.Logger;
import src.MessageHandler.Handler.Client;
import src.MessageHandler.Message.Enums.MessageType;
import src.MessageHandler.Message.MessageResolver;
import src.Utils.Utils;

import javax.swing.*;
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
            PlateState res = PlateState.valueOf(e.getActionCommand());
            if (res == PlateState.BLACK_WIN) {
                Utils.alert("黑方获胜");
            }
            else if (res == PlateState.WHITE_WIN) {
                Utils.alert("白方获胜");
            }
            chessPlate.setPlateIsBlocking(true);
            canOperate = false;
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
                } catch (PlateIsFullException ex) {

                } catch (ChessAlreadyExistException ex) {
                    Utils.alert(ex.getMessage());
                } catch (Exception ex) {
                    Logger.error(ex.getMessage());
                }
            }
        });
        g.onRegret(e -> {
            if (!canOperate) {
                return;
            }
            try {
                chessPlate.canRegret();
            } catch (Exception ex) {
                Utils.alert(ex.getMessage());
                return;
            }

            int choose = Utils.confirm("是否要进行悔棋？");
            if (choose > 0) return;

            p.setVisible(true);

            try {
                clientMessageHandler.sendMessage(MessageResolver.serializeChessRegretMessage());
            } catch (IOException ex) {
                Logger.error(ex.getMessage());
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
                .addEventListener(MessageType.CHESS_REGRET, e -> {
                    int choose = Utils.confirm("对方请求悔棋，是否同意？");
                    try {
                        clientMessageHandler.sendMessage(MessageResolver.serializeRegretResponseMessage(choose == 0));
                        if (choose == 0) {
                            chessPlate.regret();
                        }
                    } catch (Exception ex) {
                        Logger.error(ex.getMessage());
                    }
                })
                .addEventListener(MessageType.REGRET_RESPONSE, e -> {
                    boolean res = MessageResolver.resolveRegretResponseMessage(e.getActionCommand());
                    if (res) {
                        try {
                            chessPlate.regret();
                            chessPlate.canRegret();
                        }
//                        init state, white cannot operation
                        catch (ChessPlateCannotRegretException ex) {
                            canOperate = false;
                            chessPlate.setPlateIsBlocking(true);
                        } catch (Exception ex) {
                            Utils.alert(ex.getMessage());
                        }
                    }
                });
    }
}
