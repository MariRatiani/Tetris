import junit.framework.TestCase;

import java.util.*;

/*
  Unit test for Piece class -- starter shell.
 */
public class PieceTest extends TestCase {
	// You can create data to be used in the your
	// test cases like this. For each run of a test method,
	// a new PieceTest object is created and setUp() is called
	// automatically by JUnit.
	// For example, the code below sets up some
	// pyramid and s pieces in instance variables
	// that can be used in tests.
	private Piece pyr1, pyr2, pyr3, pyr4;
	private Piece piece1, piece2;
	private Piece s, sRotated;
	private Piece[] pieces;

	protected void setUp() throws Exception {
		super.setUp();
		pyr1 = new Piece(Piece.PYRAMID_STR);
		pyr2 = pyr1.computeNextRotation();
		pyr3 = pyr2.computeNextRotation();
		pyr4 = pyr3.computeNextRotation();

		s = new Piece(Piece.S1_STR);
		sRotated = s.computeNextRotation();

		pyr1 = new Piece(Piece.L2_STR);
		pyr1 = new Piece(Piece.S1_STR);
		pyr1 = new Piece(Piece.S2_STR);
		pyr1 = new Piece(Piece.SQUARE_STR);
		pyr1 = new Piece(Piece.PYRAMID_STR);

		pieces = Piece.getPieces();
	}

	// Here are some sample tests to get you started

	public void testSampleSize() {
		// Check size of pyr piece
		assertEquals(3, pyr1.getWidth());
		assertEquals(2, pyr1.getHeight());

		// Now try after rotation
		// Effectively we're testing size and rotation code here
		assertEquals(2, pyr2.getWidth());
		assertEquals(3, pyr2.getHeight());

//		// Now try with some other piece, made a different way
		Piece l = new Piece(Piece.STICK_STR);
		assertEquals(1, l.getWidth());
		assertEquals(4, l.getHeight());
	}


	// Test the skirt returned by a few pieces
	public void testSampleSkirt() {
		// Note must use assertTrue(Arrays.equals(... as plain .equals does not work
		// right for arrays.
		assertTrue(Arrays.equals(new int[] {0, 0, 0}, pyr1.getSkirt()));
		assertTrue(Arrays.equals(new int[] {1, 0, 1}, pyr3.getSkirt()));

		assertTrue(Arrays.equals(new int[] {0, 0, 1}, s.getSkirt()));
		assertTrue(Arrays.equals(new int[] {1, 0}, sRotated.getSkirt()));
	}


	public void testFirstL(){
		piece1 = new Piece(Piece.L1_STR);
		assertEquals(2, piece1.getWidth());
		assertEquals(3, piece1.getHeight());
	}



	public void testS2(){
		piece1 = new Piece(Piece.S2_STR);
		assertEquals(3, piece1.getWidth());
		assertEquals(2, piece1.getHeight());

		assertTrue(Arrays.equals(new int[] {1, 0, 0}, piece1.getSkirt()));

		String newP = "1 1	1 0  0 1  2 0";
		piece2 = new Piece(newP);
		assertTrue(piece1.equals(piece2));
	}

	public void testSquare(){
		piece1 = new Piece(Piece.SQUARE_STR);
		assertEquals(2, piece1.getWidth());
		assertEquals(2, piece1.getHeight());

		assertTrue(Arrays.equals(new int[] {0, 0}, piece1.getSkirt()));
		String newP = "1 1  1 0  0 1  0 0";
		piece2 = new Piece(newP);
		assertTrue(piece1.equals(piece2));
	}

	public void testStick(){
		piece1 = new Piece(Piece.STICK_STR);
		assertEquals(1, piece1.getWidth());
		assertEquals(4, piece1.getHeight());

		assertTrue(Arrays.equals(new int[] {0}, piece1.getSkirt()));
		//String comp = piece1.toString();
		String newP = "0 1	0 0	 0 3  0 2";
		piece2 = new Piece(newP);
		assertTrue(piece1.equals(piece2));
	}


	public void testFirstLRotations(){
		piece1 = new Piece(Piece.L1_STR);
		//piece1.
		Piece OurRotated = piece1.computeNextRotation();

		String rotated = "0 0  1 0  2 0  2 1";
		Piece realRotated = new Piece(rotated);
		assertTrue(OurRotated.equals(realRotated));
	}

	public void testRotations(){
		Piece st1 = new Piece(Piece.STICK_STR);

//		pieces = Piece.getPieces();
	//	System.out.println(pieces[Piece.STICK]);
		Piece st2 = st1.computeNextRotation();;
		assertTrue(st1.equals(pieces[Piece.STICK]));
		assertTrue(st2.equals(pieces[Piece.STICK].fastRotation()));
		assertTrue(st1.equals(pieces[Piece.STICK].fastRotation().fastRotation()));

	}

	public void testRotations2(){
		Piece L = new Piece(Piece.L1_STR);

		assertTrue(L.equals(pieces[Piece.L1].fastRotation().fastRotation().fastRotation().fastRotation()));
		assertTrue(sRotated.equals(pieces[Piece.S1].fastRotation().fastRotation().fastRotation().fastRotation().fastRotation()));

		Piece Pyr = new Piece(Piece.PYRAMID_STR);
		Pyr = Pyr.computeNextRotation();

		assertTrue(Pyr.equals(pieces[Piece.PYRAMID].fastRotation().fastRotation().fastRotation().fastRotation().fastRotation()));

		Piece sqr = new Piece(Piece.SQUARE_STR);
		Piece sqr2 = sqr.computeNextRotation();
		assertTrue(sqr2.equals(pieces[Piece.SQUARE].fastRotation().fastRotation().fastRotation().fastRotation().fastRotation().fastRotation().fastRotation()));
	}
}

