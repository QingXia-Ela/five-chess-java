package src.Core;

import src.Chess.ChessPlate;
import src.Chess.Enums.ChessType;
import src.Chess.Exception.ChessAlreadyExistException;
import src.Chess.Exception.ChessPlateCannotRegretException;
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
                serverMessageHandler.sendMessage(MessageResolver.serializeChessRegretMessage());
            } catch (IOException ex) {
                Logger.error(ex.getMessage());
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
                .addEventListener(MessageType.CHESS_REGRET, e -> {
                    int choose = Utils.confirm("对方请求悔棋，是否同意？");
                    try {
                        serverMessageHandler.sendMessage(MessageResolver.serializeRegretResponseMessage(choose == 0));
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
                            canOperate = true;
                            chessPlate.setPlateIsBlocking(false);
                        } catch (Exception ex) {
                            Utils.alert(ex.getMessage());
                        }
                    }
                });
    }
}
