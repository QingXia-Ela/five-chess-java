package src.Core;

import src.Chess.ChessPlate;
import src.Gui.Gui;
import src.MessageHandler.Handler.Server;

public class ServerCore extends Core {
    Server serverMessageHandler;
    int row;
    int col;
    Gui g;
    ChessPlate chessPlate;

    /**
     * @throws Exception when port is already in use.
     * */
    public ServerCore(int port, int row, int col) throws Exception {
        serverMessageHandler = new Server(port);
        this.row = row;
        this.col = col;
        g = new Gui(row, col);
        chessPlate = g.plate;
    }
}
