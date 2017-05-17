package board;

import java.util.ArrayList;
import java.util.List;

import static board.Capture.FALSE;
import static board.Capture.ONLY;
import static board.Capture.TRUE;
import static board.Color.BLACK;
import static board.Color.WHITE;
import static board.Square.convertToSquare;
import static java.lang.Character.isLowerCase;
import static java.lang.Character.isUpperCase;
import static java.lang.Character.toLowerCase;
import static java.lang.Character.toUpperCase;
import static org.apache.commons.lang3.StringUtils.split;

public class State {
    private Board board;

    private int moves;
    private Color sideOnMove;
    public boolean gameOver = false;

    public State() {
        getInitialState();
    }

    private void getInitialState() {
        moves = 1;
        sideOnMove = WHITE;
        board = new Board();
    }

    public void printCurrentBoard() {
        if (board == null) {
            getInitialState();
        }
        System.out.println(moves + " " + sideOnMove);
        System.out.println(board.toString());
    }

    public Move move(Move move) {

        if (toUpperCase(board.getPiece(move.getToSquare().getX(), move.getToSquare().getY())) == 'K') {
            finishedGame();
            System.out.println(sideOnMove.toString() + " wins");
            return null;
        }
        if (moves >= 40) {
            finishedGame();
            System.out.println("Game ends in draw");
            return null;
        }
        moveIsValid(move);

        final Square fromSquare = move.getFromSquare();
        final int fromSquareX = fromSquare.getX();
        final int fromSquareY = fromSquare.getY();
        char tmp = board.getPiece(fromSquareX, fromSquareY);

        final Square toSquare = move.getToSquare();
        final int toSquareX = toSquare.getX();
        final int toSquareY = toSquare.getY();
        if (toUpperCase(board.getPiece(fromSquareX, fromSquareY)) == 'P' && (toSquareY == 0 || toSquareY == 5)) {
            board.setPiece(toSquareX, toSquareY, (char) (tmp + 1));
        } else {
            board.setPiece(toSquareX, toSquareY, tmp);
        }
        board.setPiece(fromSquareX, fromSquareY, '.');

        changeSideOnMove();
        return move;
    }

    public void finishedGame() {
        printCurrentBoard();
        gameOver = true;
    }

    public Move move(String value) {
        String[] squares = split(value, '-');
        if (squares.length != 2)
            throw new IllegalArgumentException("Move is invalid");
        return move(validateMove(new Move(convertToSquare(squares[0]), convertToSquare(squares[1]))));
    }

    private Move validateMove(Move move) {
        final Square moveToSquare = move.getToSquare();
        List<Move> legalMoves = new ArrayList<Move>();
        generateMoveListForPiece(legalMoves, move.getFromSquare().getX(), move.getFromSquare().getY());
        for (Move legalMove : legalMoves) {
            final Square legalMoveToSquare = legalMove.getToSquare();
            if (legalMoveToSquare.getX() == moveToSquare.getX() && legalMoveToSquare.getY() == moveToSquare.getY())
                return move;
        }
        throw new IllegalArgumentException("Move is invalid");
    }

    public Move calculateBestMove() {
        List<Move> moves = generateMoveList();
        Move bestMove = null;
        int bestScore = 100000;
        if (moves != null) {
            for (Move move : moves) {
                State tmpState = new State();
                tmpState.board.setField(this.board.deepCopyField(this.board.getField()));
                if (toUpperCase(tmpState.board.getPiece(move.getToSquare().getX(), move.getToSquare().getY())) == 'K'){
                    return move;
                }
                tmpState.move(move);
                int tmpScore = tmpState.pointScore();
                if (bestScore > tmpScore) {
                    bestMove = move;
                    bestScore = tmpScore;
                }
            }
        }
        return bestMove;
    }

    public List<Move> generateMoveList() {
        List<Move> moves = new ArrayList<Move>();
        for (int y = 0; y < board.getField().length; y++) {
            for (int x = 0; x < board.getField()[y].length; x++) {
                if (isMoversPiece(x, y))
                    generateMoveListForPiece(moves, x, y);
            }
        }
        if (moves.isEmpty()) {
            System.out.println(this.getSideOnMove() + " is unable to move.");
            return null;
        }
        return moves;
    }

    void generateMoveListForPiece(List<Move> moves, int x, int y) {
        char p = toLowerCase(board.getPiece(x, y));
        switch (p) {
            case 'q': {
                symmscan(moves, x, y, 0, 1, false, TRUE);
                symmscan(moves, x, y, 1, 1, false, TRUE);
                break;
            }
            case 'k': {
                symmscan(moves, x, y, 0, 1, true, TRUE);
                symmscan(moves, x, y, 1, 1, true, TRUE);
                break;
            }
            case 'r': {
                symmscan(moves, x, y, 0, 1, false, TRUE);
                break;
            }
            case 'b': {
                symmscan(moves, x, y, 0, 1, true, FALSE);
                symmscan(moves, x, y, 1, 1, false, TRUE);
                break;
            }
            case 'n': {
                symmscan(moves, x, y, 1, 2, true, TRUE);
                symmscan(moves, x, y, -1, 2, true, TRUE);
                break;
            }
            case 'p': {
                int dir = getPieceColor(x, y).equals(BLACK) ? -1 : 1;
                scan(moves, x, y, -1, dir, true, ONLY);
                scan(moves, x, y, 1, dir, true, ONLY);
                scan(moves, x, y, 0, dir, true, FALSE);
                break;
            }
            default:
                break;
        }
    }

    private void symmscan(List<Move> moves, int x, int y, int dx, int dy, boolean stopShort, Capture capture) {
        for (int i = 0; i < 4; i++) {
            scan(moves, x, y, dx, dy, stopShort, capture);
            int tmp = dx;
            dx = dy;
            dy = -tmp;
        }
    }

    private void scan(List<Move> moves, int x, int y, int dx, int dy, boolean stopShort, Capture capture) {
        int x0 = x;
        int y0 = y;
        do {
            x = x + dx;
            y = y + dy;
            if (!isInBounds(x, y))
                break;
            if (isOccupied(x, y)) {
                if (isMoversPiece(x, y))
                    break;
                if (capture.equals(FALSE))
                    break;
                stopShort = true;
            } else if (capture.equals(ONLY))
                break;
            moves.add(new Move(new Square(x0, y0), new Square(x, y)));
        } while (!stopShort);
    }

    private boolean isInBounds(int x, int y) {
        return x >= 0 && x <= 4 && y >= 0 && y <= 5;
    }

    private boolean isOccupied(int x, int y) {
        return board.getPiece(x, y) != '.';
    }

    /**
     * positiv = advantage for side on move
     *
     * @return
     */
    int pointScore() {
        int score = 0;
        for (char[] row : board.getField()) {
            for (char c : row) {
                if (isUpperCase(c)) {
                    // If its white add to score
                    score += getPieceScore(toLowerCase(c));
                }
                if (isLowerCase(c)) {
                    // If its black sub from score
                    score -= getPieceScore(c);
                }
            }
        }
        if (sideOnMove.equals(BLACK))
            return -score;
        return score;
    }

    private int getPieceScore(char c) {
        switch (c) {
            case 'p': {
                return 100;
            }
            case 'n':
            case 'b': {
                return 300;
            }
            case 'r': {
                return 500;
            }
            case 'q': {
                return 900;
            }
            default:
                return 0;
        }
    }

    private void changeSideOnMove() {
        if (sideOnMove.equals(WHITE)) {
            sideOnMove = BLACK;
        } else {
            sideOnMove = WHITE;
            moves++;
        }
    }


    private void moveIsValid(Move move) {
        final Square fromSquare = move.getFromSquare();
        final int x = fromSquare.getX();
        final int y = fromSquare.getY();
        if (!isMoversPiece(x, y))

            throw new IllegalStateException("Is not movers piece");
    }

    private boolean isMoversPiece(int x, int y) {
        return isUpperCase(board.getPiece(x, y)) && sideOnMove == WHITE || isLowerCase(board.getPiece(x, y)) && sideOnMove == BLACK;
    }

    private Color getPieceColor(int x, int y) {
        if (isOccupied(x, y)) {
            if (isUpperCase(board.getPiece(x, y)))
                return WHITE;
            else return BLACK;
        } else throw new IllegalArgumentException("Field isn't occupied");
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public int getMoves() {
        return moves;
    }

    public Color getSideOnMove() {
        return sideOnMove;
    }
}
