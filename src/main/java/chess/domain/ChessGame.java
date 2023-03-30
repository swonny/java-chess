package chess.domain;

import chess.domain.board.Board;
import chess.domain.board.Square;
import chess.domain.piece.Color;
import chess.domain.piece.Pawn;
import chess.domain.piece.Piece;

import java.util.*;

public class ChessGame {

    public static final Color FIRST_TURN = Color.WHITE;

    private final Board board;
    private Color movableTurn;
    private boolean pause;

    public ChessGame(Board board, Color movableTurn) {
        this.board = board;
        this.movableTurn = movableTurn;
        pause = false;
    }

    public void move(final Square source, final Square destination) {
        final Optional<Piece> piece = board.findPieceOf(source);
        validateEmpty(piece);
        validateTurn(piece);
        moveToDestination(piece.get(), source, destination);
        movableTurn = movableTurn.oppositeTurn();
    }

    private void validateTurn(Optional<Piece> piece) {
        if (piece.get().isBlack() != movableTurn.isBlack()) {
            throw new IllegalArgumentException(String.format("현재 이동 가능한 기물은 %s색 입니다.", movableTurn));
        }
    }

    private static void validateEmpty(Optional<Piece> piece) {
        if (piece.isEmpty()) {
            throw new IllegalArgumentException("움직일 기물이 존재하지 않습니다.");
        }
    }

    private void moveToDestination(final Piece piece, final Square source, final Square destination) {
        final List<Square> route = piece.findRoute(source, destination);
        if (!isMovable(piece, source, route)) {
            throw new IllegalArgumentException("움직일 수 없는 위치입니다.");
        }
        board.move(source, destination);
    }

    private boolean isMovable(final Piece piece, final Square source, final List<Square> route) {
        if (piece.isSameClass(Pawn.class)) {
            return board.canMovePawn(source, route);
        }
        return board.canMove(source, route);
    }

    public Map<Color, Double> getResult() {
        Map<Color, Double> results = new HashMap<>();
        results.put(Color.BLACK, board.resultOf(Color.BLACK));
        results.put(Color.WHITE, board.resultOf(Color.WHITE));
        return results;
    }

    public Board getBoard() {
        return board;
    }

    public boolean isFinished() {
        return !board.hasBothKing();
    }

    public Color getTurn() {
        return movableTurn.oppositeTurn();
    }

    public void pauseGame() {
        pause = true;
    }

    public boolean isPaused() {
        return pause;
    }

    public String getWinner() {
        Map<Color, Double> result = getResult();
        List<Color> orderedResult = new ArrayList<>(result.keySet());
        orderedResult.sort(Comparator.comparing(result::get));
        Collections.reverse(orderedResult);
        if (result.get(orderedResult.get(0)) == result.get(orderedResult.get(1))) {
            return String.join(", ", Color.WHITE.name(), Color.BLACK.name());
        }
        return orderedResult.get(0).name();
    }
}
