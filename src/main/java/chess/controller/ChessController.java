package chess.controller;

import chess.controller.dao.ChessGameDao;
import chess.controller.dto.ChessBoardDto;
import chess.domain.ChessGame;
import chess.domain.board.BoardFactory;
import chess.view.InputView;
import chess.view.OutputView;

import static chess.controller.ChessGameCommand.START;

public class ChessController {

    private static final InputView inputView = new InputView();
    private static final OutputView outputView = new OutputView();

    public void run() {
        outputView.printStartMessage();
        if (readChessStartCommand() == START) {
            ChessGameDao chessGameDao = new ChessGameDao();
            ChessGame savedChessGame = chessGameDao.findAll();
            outputView.printChessBoard(ChessBoardDto.from(savedChessGame.getBoard()));
            playChessGame(savedChessGame);
        }
        inputView.closeScanner();
    }

    private ChessGameCommand readChessStartCommand() {
        try {
            return ChessGameCommand.generateExecuteCommand(inputView.readChessExecuteCommand());
        } catch (IllegalArgumentException exception) {
            outputView.printErrorMessage(exception.getMessage());
            return readChessStartCommand();
        }
    }

    private void playChessGame(ChessGame savedChessGame) {
        try {
            repeatMove(savedChessGame);
        } catch (IllegalArgumentException exception) {
            outputView.printErrorMessage(exception.getMessage());
            playChessGame(savedChessGame);
        }
    }

    private void repeatMove(ChessGame savedChessGame) {
        while (isRunnable(savedChessGame)) {
            runCommand(savedChessGame, inputView.readChessGameCommand());
        }
    }

    private static boolean isRunnable(ChessGame savedChessGame) {
        if (savedChessGame.isFinished()) {
            ChessGameDao chessGameDao = new ChessGameDao();
            chessGameDao.save(new ChessGame(BoardFactory.generate(), ChessGame.FIRST_TURN));
            return false;
        }
        return !savedChessGame.isPaused();
    }

    private void runCommand(ChessGame savedChessGame, String gameCommandInput) {
        if (ChessGameCommand.isEnd(gameCommandInput)) {
            ChessGameDao chessGameDao = new ChessGameDao();
            chessGameDao.save(savedChessGame);
            savedChessGame.pauseGame();
            return;
        }
        MoveCommand chessMoveCommand = MoveCommand.from(gameCommandInput);
        savedChessGame.move(chessMoveCommand.getSource(), chessMoveCommand.getDestination());
        outputView.printChessBoard(ChessBoardDto.from(savedChessGame.getBoard()));
    }
}
