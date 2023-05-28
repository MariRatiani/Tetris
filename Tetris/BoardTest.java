
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;


public class BoardTest extends TestCase {
	Board b;
	Piece pyr1, pyr2, pyr3, pyr4, s, sRotated,stick,square;

	// This shows how to build things in setUp() to re-use
	// across tests.

	// In this case, setUp() makes shapes,
	// and also a 3X6 board, with pyr placed at the bottom,
	// ready to be used by tests.
	protected void setUp() throws Exception {
		b = new Board(3, 6);

		pyr1 = new Piece(Piece.PYRAMID_STR);
		pyr2 = pyr1.computeNextRotation();
		pyr3 = pyr2.computeNextRotation();
		pyr4 = pyr3.computeNextRotation();

		stick = new Piece(Piece.STICK_STR);
		square = new Piece(Piece.SQUARE_STR);

		s = new Piece(Piece.S1_STR);
		sRotated = s.computeNextRotation();

		b.place(pyr1, 0, 0);
	}
	public void testSample1() {
		assertEquals(1, b.getColumnHeight(0));
		assertEquals(2, b.getColumnHeight(1));
		assertEquals(2, b.getMaxHeight());
		assertEquals(3, b.getRowWidth(0));
		assertEquals(1, b.getRowWidth(1));
		assertEquals(0, b.getRowWidth(2));
	}
	public void testSample2() {
		b.commit();
		int result = b.place(sRotated, 1, 1);
		assertEquals(Board.PLACE_OK, result);
		assertEquals(1, b.getColumnHeight(0));
		assertEquals(4, b.getColumnHeight(1));
		assertEquals(3, b.getColumnHeight(2));
		assertEquals(4, b.getMaxHeight());
		b.clearRows();

	}


	public void testFirst(){
		b.commit();
		int a = b.place(stick, 0, 1);
		assertEquals(Board.PLACE_OK, a);

		b.commit();
		a = b.place(sRotated, 1, 1);

		assertEquals(Board.PLACE_ROW_FILLED, a);
		int c = b.getRowWidth(3);
		assertEquals(2, c);
		int d = b.getColumnHeight(1);
		assertEquals(4, d);
		int e = b.getMaxHeight();
		assertEquals(5, e);

		int clearedNumber = b.clearRows();
		assertEquals(1,b.getRowWidth(1));
		assertEquals(3,clearedNumber);

		b.undo();

		assertEquals(6, b.getHeight());
		assertEquals(3, b.getWidth());

		int first = b.getRowWidth(4);
		assertEquals(1, first);
		int sec = b.getMaxHeight();
		assertEquals(5, sec);
		int thrd = b.getColumnHeight(1);
		assertEquals(2, thrd);

		String str = b.toString();
		assertNotSame(str,  "");
		b.clearRows();
		b.commit();
		//Assertions.
		int res = b.place(pyr3, 100, 5);

		assertEquals(res, Board.PLACE_OUT_BOUNDS);
		Assertions.assertThrows(RuntimeException.class, () -> b.place(pyr3, 100, 5), "Heights check failed");
	}

	public void testPlace() {
		Board b = new Board(10, 20);
		int result = b.place(new Piece(Piece.L1_STR), 5, 0);
		assertEquals(Board.PLACE_OK, result);
		assertTrue(b.getGrid(5, 0));
		assertTrue(b.getGrid(6, 0));
		assertFalse(b.getGrid(7, 0));
		assertFalse(b.getGrid(6, 1));
		b.clearRows();

	}


	public void testClearing() {
		Board newB = new Board(3, 6);

		newB.place(pyr1, 0, 0);
		newB.commit();

		newB.clearRows();
		newB.commit();

		assertEquals(1, newB.getRowWidth(0));
		assertEquals(0, newB.getRowWidth(1));
		assertEquals(0, newB.getColumnHeight(0));


		newB.place(pyr4, 0, 0);
		newB.commit();
		newB.clearRows();

		assertEquals(2, newB.getColumnHeight(1));
		assertEquals(3, newB.getColumnHeight(0));

		b.clearRows();
		b.commit();


		assertEquals(1, b.getColumnHeight(1));
		assertEquals(0, b.getColumnHeight(0));

		b.place(pyr4, 0, 0);
		b.commit();

		b.clearRows();
	}

	public void testSecond(){
		assertTrue(b.getGrid(1, 1));

		b.undo();

		int plc = b.place(square, 0, 0);
		b.commit();


		plc = b.place(square, 0, 2);
		b.commit();


		plc = b.place(square, 0, 4);
		b.commit();

		assertEquals(Board.PLACE_OK, plc);

		plc = b.place(stick, 2, 0);
		b.commit();

		assertEquals(Board.PLACE_ROW_FILLED, plc);
		assertEquals(6, b.getMaxHeight());

		int ans = b.place(square, 0, 4);
		assertEquals(Board.PLACE_BAD, ans);
		b.sanityCheck();
	}


	public void testSanityCheck() {
		Board board = new Board(10, 20);
		//assertTrue(board.sanityCheck());
		board.sanityCheck();

	}

	public void testCommits(){
		b = new Board(3, 6);
		b.commit();
		b.place(s, 0, b.dropHeight(s,0));
		b.clearRows();

		b.commit();
		b.place(s,0,b.dropHeight(s,0));
		b.clearRows();

		b.commit();
		int w = b.getRowWidth(2);
		assertEquals(2,w );
		int h =  b.getRowWidth(0);
		assertEquals(2, h);
		b.clearRows();

	}

}