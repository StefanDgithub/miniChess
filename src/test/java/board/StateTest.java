package board;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.rules.ExpectedException.none;

public class StateTest {
    @Rule
    public ExpectedException expectedException = none();

    @Test
    public void printCurrentBoard() throws Exception {
        State state = new State();
        state.printCurrentBoard();
    }

    @Test
    public void testMoveIsValid() throws Exception {
        State state = new State();
        state.move(new Move(new Square(4, 1), new Square(1, 2)));
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Is not movers piece");
        state.move(new Move(new Square(4, 1), new Square(1, 2)));
    }

    @Test
    public void testMove() throws Exception {
        State state = new State();
        state.move("a2-a3");
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Move is invalid");
        state.move("a2-a3");
    }

    @Test
    public void testMoveTranslator() throws Exception {
        State state = new State();
        state.move("a2-a3");
        state.printCurrentBoard();
        state.move("a5-a4");
        state.printCurrentBoard();
        state.move("b2-b3");
        state.printCurrentBoard();
        state.move("b5-b4");
        state.printCurrentBoard();
    }

    @Test
    public void testMoveKing() throws Exception {
        State state = new State();
        List<Move> moves = new ArrayList<Move>();
        state.generateMoveListForPiece(moves, 4, 0);
        Assert.assertEquals(true, moves.isEmpty());
    }

    @Test
    public void testMovePawn() throws Exception {
        State state = new State();
        List<Move> moves = new ArrayList<Move>();
        state.generateMoveListForPiece(moves, 4, 1);
        Assert.assertEquals(1, moves.size());
    }

    @Test
    public void testMoveKnight() throws Exception {
        State state = new State();
        List<Move> moves = new ArrayList<Move>();
        state.generateMoveListForPiece(moves, 1, 0);
        Assert.assertEquals(2, moves.size());
    }

    @Test
    public void testMoveRook() throws Exception {
        State state = new State();
        List<Move> moves = new ArrayList<Move>();
        state.generateMoveListForPiece(moves, 0, 0);
        Assert.assertEquals(0, moves.size());
    }

    @Test
    public void testPlayChess() throws Exception {
        State state = new State();
        List<Move> moves = new ArrayList<Move>();
        state.generateMoveListForPiece(moves, 1, 0);
        state.move(moves.get(0));
        state.printCurrentBoard();
        moves = new ArrayList<Move>();
        state.generateMoveListForPiece(moves, 0, 4);
        state.move(moves.get(0));
        state.printCurrentBoard();
        moves = new ArrayList<Move>();
        state.generateMoveListForPiece(moves, 0, 0);
        Assert.assertEquals(1, moves.size());
        moves = new ArrayList<Move>();
        state.generateMoveListForPiece(moves, 2, 2);
        Assert.assertEquals(5, moves.size());
        state.move(moves.get(1));
        state.printCurrentBoard();
        state.move("a4-a3");
        state.printCurrentBoard();
    }

    @Test
    public void testMoveGenerator() {
        State state = new State();
        assertEquals(7, state.generateMoveList().size());
    }

    @Test
    public void convertPawnToQueen() {
        State state = new State();
        char[][] field = {
                {'.', '.', 'B', 'Q', 'K'},
                {'.', 'p', 'P', 'P', 'P'},
                {'.', '.', '.', '.', '.'},
                {'.', '.', '.', '.', '.'},
                {'p', '.', 'p', 'p', 'p'},
                {'k', 'q', 'b', 'n', 'r'}
        };
        state.getBoard().setField(field);
        state.printCurrentBoard();
        state.move("c2-c3");
        state.printCurrentBoard();
        System.out.println("test");
        state.move("b2-b1");
        state.printCurrentBoard();
    }

    @Test
    public void testConvertColorOfBishop() {
        State state = new State();
        char[][] field = {
                {'.', '.', 'B', 'Q', 'K'},
                {'.', 'p', 'P', 'P', 'P'},
                {'.', '.', '.', '.', '.'},
                {'.', '.', '.', '.', '.'},
                {'p', '.', 'p', 'p', 'p'},
                {'k', 'q', 'b', 'n', 'r'}
        };
        state.getBoard().setField(field);
        state.printCurrentBoard();
        state.move("c1-b1");
        state.printCurrentBoard();
    }

    @Test
    public void testUnableToMove() {
        State state = new State();
        char[][] field = {
                {'.', '.', '.', '.', '.'},
                {'.', 'p', '.', '.', '.'},
                {'.', 'P', '.', '.', '.'},
                {'.', '.', '.', '.', '.'},
                {'.', '.', '.', '.', '.'},
                {'.', '.', '.', '.', '.'}
        };
        state.getBoard().setField(field);
        List<Move> moves = new ArrayList<Move>();
        state.generateMoveListForPiece(moves, 1, 3);
        Assert.assertEquals(0, moves.size());
    }

    @Test
    public void testSimulateSkirmishError() {
        State state = new State();
        char[][] field = {
                {'R', 'N', '.', 'Q', 'K'},
                {'.', 'B', '.', '.', 'P'},
                {'.', 'P', '.', 'P', '.'},
                {'b', 'p', 'P', 'p', '.'},
                {'p', '.', 'p', '.', 'p'},
                {'k', 'q', 'r', 'n', '.'}
        };
        state.getBoard().setField(field);
        state.printCurrentBoard();
        state.move("d1-c2");
        state.printCurrentBoard();
        state.move("d6-b5");
        state.printCurrentBoard();
    }

    @Test
    public void testCalculateInitialScore() {
        State state = new State();
        Assert.assertEquals(0, state.pointScore());
    }

    @Test
    public void testCalculateScore() {
        State state = new State();
        char[][] field = {
                {'.', '.', 'B', 'Q', 'K'},
                {'.', 'p', 'P', 'P', 'P'},
                {'.', '.', '.', '.', '.'},
                {'.', '.', '.', '.', '.'},
                {'p', '.', 'p', 'p', 'p'},
                {'k', 'q', 'b', 'n', 'r'}
        };
        state.getBoard().setField(field);
        Assert.assertEquals(-100, state.pointScore());
        state.move("c2-c3");
        Assert.assertEquals(100, state.pointScore());
    }

    @Test
    public void testCalculateBestMove() {
        State state = new State();
        char[][] field = {
                {'.', '.', 'B', 'Q', 'K'},
                {'.', 'p', 'P', 'P', 'P'},
                {'.', '.', '.', '.', '.'},
                {'.', '.', '.', '.', '.'},
                {'p', '.', 'p', 'p', 'p'},
                {'k', 'R', 'q', 'n', 'r'}
        };
        state.getBoard().setField(field);
        state.printCurrentBoard();
        System.out.println(state.calculateBest(1));
        state.move(state.calculateBest(1));
        state.printCurrentBoard();
    }

    @Test
    public void testCalculateBestMove1() {
        State state = new State();
        char[][] field = {
                {'.', '.', '.', '.', '.'},
                {'.', '.', '.', '.', '.'},
                {'.', '.', '.', '.', '.'},
                {'.', '.', '.', '.', '.'},
                {'.', '.', '.', '.', '.'},
                {'k', 'R', 'q', '.', '.'}
        };
        state.getBoard().setField(field);
        state.printCurrentBoard();
        System.out.println(state.calculateBest(1));
        state.move(state.calculateBest(1));
        state.printCurrentBoard();
    }

    @Test
    public void testWinLabeling() {
        State state = new State();
        char[][] field = {
                {'.', '.', 'R', 'Q', 'K'},
                {'.', 'p', 'P', 'P', 'P'},
                {'.', '.', '.', '.', '.'},
                {'.', '.', '.', '.', '.'},
                {'p', 'B', 'p', 'p', 'p'},
                {'k', 'R', 'q', 'n', 'r'}
        };
        state.getBoard().setField(field);
        state.printCurrentBoard();
        state.move("b5-a6");
        Assert.assertEquals(Color.WHITE, state.winner);
    }

    @Test
    public void testRescueKingFromCapture() {
        State state = new State();
        char[][] field = {
                {'.', '.', '.', '.', 'K'},
                {'.', '.', 'P', 'P', '.'},
                {'.', '.', '.', '.', '.'},
                {'.', '.', '.', '.', '.'},
                {'.', 'p', 'p', '.', '.'},
                {'k', '.', '.', '.', 'q'}
        };
        state.getBoard().setField(field);
        state.printCurrentBoard();
        Assert.assertEquals("e1-d1", state.calculateBest(2).toString());
        System.out.println(state.calculateBest(2));
        state.move(state.calculateBest(2));
        state.printCurrentBoard();
        state.move(state.calculateBest(2));
        state.printCurrentBoard();
    }

    @Test
    public void testCaptureEnemy() {
        State state = new State();
        char[][] field = {
                {'R', '.', 'B', 'Q', 'K'},
                {'P', 'P', 'P', '.', 'P'},
                {'.', '.', 'p', '.', '.'},
                {'.', '.', '.', '.', '.'},
                {'p', 'p', '.', 'p', 'p'},
                {'k', 'q', 'b', 'n', 'r'}
        };
        state.getBoard().setField(field);
        state.printCurrentBoard();
        Assert.assertEquals("b2-c3", state.calculateBest(1).toString());
    }

    @Test
    public void testRescueQueenFromCapture() {
        State state = new State();
        char[][] field = {
                {'.', '.', '.', '.', 'K'},
                {'.', '.', 'P', 'P', '.'},
                {'.', '.', '.', '.', '.'},
                {'.', '.', 'Q', '.', '.'},
                {'.', 'p', 'p', '.', '.'},
                {'k', '.', 'q', '.', '.'}
        };
        state.getBoard().setField(field);
        state.printCurrentBoard();
        System.out.println(state.calculateBest(2));
        state.calculateBest(2);
        state.printCurrentBoard();
    }

    @Test
    public void testPromoteToQueenAsBestMove() {
        State state = new State();
        char[][] field = {
                {'.', '.', '.', '.', 'K'},
                {'.', '.', '.', '.', '.'},
                {'.', '.', '.', '.', '.'},
                {'.', '.', '.', '.', '.'},
                {'.', '.', 'P', '.', '.'},
                {'k', '.', '.', '.', '.'}
        };
        state.getBoard().setField(field);
        state.printCurrentBoard();
        Assert.assertEquals("c5-c6", state.calculateBest(4).toString());
        state.move(state.calculateBest(4));
        state.printCurrentBoard();


    }


}