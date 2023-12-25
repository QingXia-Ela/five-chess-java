package src.Core;

import src.Chess.ChessPlate;
import src.Chess.Enums.ChessType;
import src.Chess.Enums.PlateState;
import src.Chess.Exception.ChessAlreadyExistException;
import src.Chess.Exception.ChessPlateCannotRegretException;
import src.Chess.Exception.ExceedChessPlateException;
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
        g = new Gui(row, col, false);
        g.setSelfNameValue(selfName);
        g.setOpponentNameValue(opponentName);
        chessPlate = g.plate;
        g.setWhoOperate(false);
    }

    private void addChessPlateEvent() {
        chessPlate.onSomeoneWin(e -> {
            PlateState res = PlateState.valueOf(e.getActionCommand());
            if (res == PlateState.BLACK_WIN) {
                Utils.alert("黑方获胜");
                canOperate = true;
                g.setWhoOperate(true);
            }
            else if (res == PlateState.WHITE_WIN) {
                Utils.alert("白方获胜");
                canOperate = false;
                g.setWhoOperate(false);
            }
            chessPlate.setPlateIsBlocking(true);
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
                    g.setWhoOperate(false);
                } catch (ExceedChessPlateException ex) {
                    Utils.alert("超出棋盘范围");
                } catch (ChessAlreadyExistException ex) {
                    Utils.alert(ex.getMessage());
                } catch (PlateIsFullException ex) {
//                    if full, still send msg, and block
                try {
                    clientMessageHandler.sendMessage(MessageResolver.serializeChessPlaceMessage(
                            new SingleChess(pos[0], pos[1], ChessType.BLACK))
                    );
                } catch (IOException exc) {
                    Logger.error(exc.getMessage());
                } finally {
                    canOperate = false;
                    chessPlate.setPlateIsBlocking(true);
                    Utils.alert("棋盘已满，游戏结束");
                }
            }  catch (Exception ex) {
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

    private void regret() {
        try {
            chessPlate.setPlateIsBlocking(false);
            chessPlate.regret();
            chessPlate.canRegret();
            Logger.debug("Regret without init state");
        }
//                        init state, black can operation
        catch (ChessPlateCannotRegretException ex) {
            Logger.debug("Trigger init plate");
            canOperate = false;
            chessPlate.setPlateIsBlocking(true);
            g.setWhoOperate(false);
        } catch (Exception ex) {
            Utils.alert(ex.getMessage());
        }
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
                        PlateState p = chessPlate.calcIsWin(info.x, info.y);

                        if (p == PlateState.RUNNING) {
                            canOperate = true;
//                        self operation
                            g.setWhoOperate(true);
                        } else {
                            chessPlate.setPlateIsBlocking(true);
                        }
                    } catch (Exception ex) {
                        Logger.error(ex.getMessage());
                    }
                })
                .addEventListener(MessageType.CHESS_REGRET, e -> {
                    int choose = Utils.confirm("对方请求悔棋，是否同意？");
                    try {
                        clientMessageHandler.sendMessage(MessageResolver.serializeRegretResponseMessage(choose == 0));
                        if (choose == 0) {
                            regret();
                        }
                    } catch (Exception ex) {
                        Logger.error(ex.getMessage());
                    }
                })
                .addEventListener(MessageType.REGRET_RESPONSE, e -> {
                    boolean res = MessageResolver.resolveRegretResponseMessage(e.getActionCommand());
                    if (res) {
                        regret();
                    }
                });
    }
}
