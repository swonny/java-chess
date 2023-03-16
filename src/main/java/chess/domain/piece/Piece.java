package chess.domain.piece;

import chess.domain.board.Square;
import java.util.List;

public abstract class Piece {

    protected final Color color;

    protected Piece(final Color color) {
        this.color = color;
    }

    public boolean isBlack() {
        return color.isBlack();
    }

    public abstract List<Square> findRoute(final Square source, final Square destination);

    public abstract boolean isPawn();
}
