// Board.java

import java.util.Arrays;

/**
 CS108 Tetris Board.
 Represents a Tetris board -- essentially a 2-d grid
 of booleans. Supports tetris pieces and row clearing.
 Has an "undo" feature that allows clients to add and remove pieces efficiently.
 Does not do any drawing or have any idea of pixels. Instead,
 just represents the abstract 2-d board.
*/
public class Board {
	// Some ivars are stubbed out for you:


	private int[] widths;
	private int[] heights;
	int maxHeight;

	private boolean[][] savedGrid;
	private int oldHeights[];
	private int oldWidths[];

	private int width;
	private int height;
	private boolean[][] grid;
	private boolean DEBUG = false;
	boolean committed;
	// Here a few trivial methods are provided:

	/**
	 * Creates an empty board of the given width and height
	 * measured in blocks.
	 */
	public Board(int width, int height) {

		this.width = width;
		this.height = height;
		grid = new boolean[width][height];
		committed = true;


		maxHeight = 0;
		widths = new int[height];
		Arrays.fill(widths, 0);
		heights = new int[width];
		Arrays.fill(heights, 0);

		savedGrid = new boolean[width][height];
		oldWidths = new int[height];
		Arrays.fill(oldWidths, 0);
		oldHeights = new int[width];
		Arrays.fill(oldHeights, 0);

	}



	public int getWidth() {
		return width;
	}


	/**
	 * Returns the height of the board in blocks.
	 */
	public int getHeight() {
		return height;
	}


	private int compMaxHeight() {

		int res = 0;
		for (int i = 0; i < heights.length; i++) {
			if (heights[i] > res)
				res = heights[i];
		}
		maxHeight = res;
		return res;
	}

	/**
	 * Returns the max column height present in the board.
	 * For an empty board this is 0.
	 */
	public int getMaxHeight() {
		return maxHeight;
	}


	/**
	 * Checks the board for internal consistency -- used
	 * for debugging.
	 */
	public void sanityCheck() {

		int[] h = new int[width];
		int[] w = new int[height];

		if (!DEBUG) {
			return;
		}
		;
		int Max = 0;
		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid[x].length; y++) {
				boolean cur = grid[x][y];
				if (cur) { w[y]++; h[x] = y + 1;
					boolean comp = (y + 1) > Max;
					if (comp) Max = y + 1;
				}
			}
		}
		if (!Arrays.equals(w, widths)) throw new RuntimeException("exception");
		if (Max != getMaxHeight()) throw new RuntimeException("exception");
		if (!Arrays.equals(h, heights)) throw new RuntimeException("exception");
	}


	private int dropHeightHelper(int[] skirt, int h, int x){
		int len = skirt.length;
		for(int i = 0 ; i < len; i++){
			int hgt = heights[x + i] - skirt[i];
			if(hgt > h)
				h = hgt;
		}
		return h;
	}

	/**
	 Given a piece and an x, returns the y
	 value where the piece would come to rest
	 if it were dropped straight down at that x.

	 <p>
	 Implementation: use the skirt and the col heights
	 to compute this fast -- O(skirt length).
	*/
	public int dropHeight(Piece piece, int x) {
		int[] skirt = piece.getSkirt();
		int dropHeight = 0;
		return dropHeightHelper(skirt, dropHeight, x);
	}


	/**
	 Returns the height of the given column --
	 i.e. the y value of the highest block + 1.
	 The height is 0 if the column contains no blocks.
	*/
	public int getColumnHeight(int x) {
		int res =  this.heights[x];
		return res;
	}


	/**
	 Returns the number of filled blocks in
	 the given row.
	*/
	public int getRowWidth(int y) {
		int res =  this.widths[y];
		return res;
	}


	/**
	 Returns true if the given block is filled in the board.
	 Blocks outside of the valid width/height area
	 always return true.
	*/
	public boolean getGrid(int x, int y) {
		boolean a = (y < 0 ) || (x < 0);
		boolean b = (y >= height) || (x >= width);
		boolean res = a || b || grid[x][y];
		return res;
	}


	public static final int PLACE_OK = 0;
	public static final int PLACE_ROW_FILLED = 1;
	public static final int PLACE_OUT_BOUNDS = 2;
	public static final int PLACE_BAD = 3;

	//save old version before changing state
	private void saveCurBoard(){

		int len1 = heights.length;
		System.arraycopy(heights, 0, oldHeights, 0, len1);
		int len2 = widths.length;
		System.arraycopy(widths, 0, oldWidths, 0, len2);
		for(int i = 0;i<grid.length; i++) {
			System.arraycopy(grid[i], 0, savedGrid[i], 0, grid[i].length);
		}
	}

	boolean outOfBounds(int x, int y){
		if((x < 0) || (y < 0) || (x >= width) || (y >= height))
			return true;
		return false;
	}

	private int placeHelper(int Py, int Px, int result){
		int ny = Py + 1;
		if(heights[Px] < ny) {
			heights[Px] = ny;
		}
		grid[Px][Py] = true;

		widths[Py] = widths[Py]+ 1;

		boolean eq = width == widths[Py];
		if(eq) {
			result = PLACE_ROW_FILLED;
		}
		return result;
	}
	/**
	 Attempts to add the body of a piece to the board.
	 Copies the piece blocks into the board grid.
	 Returns PLACE_OK for a regular placement, or PLACE_ROW_FILLED
	 for a regular placement that causes at least one row to be filled.

	 <p>Error cases:
	 A placement may fail in two ways. First, if part of the piece may falls out
	 of bounds of the board, PLACE_OUT_BOUNDS is returned.
	 Or the placement may collide with existing blocks in the grid
	 in which case PLACE_BAD is returned.
	 In both error cases, the board may be left in an invalid
	 state. The client can use undo(), to recover the valid, pre-place state.
	*/
	public int place(Piece piece, int x, int y) {
		// flag !committed problem
		if (!committed) throw new RuntimeException("place commit problem");

		int result = PLACE_OK;
		saveCurBoard();
		int Px = 0;
		int Py = 0;
		committed = false;

//		if (x < 0 || y < 0) {
//			return PLACE_OUT_BOUNDS;
//		}

		TPoint body[] = piece.getBody();
		int len = body.length;

		for(int i = 0; i < len; i++){

			Py = body[i].y;
			Py+= y;
			Px = body[i].x;
			Px += x;

			if(outOfBounds(Px, Py)){
				result = PLACE_OUT_BOUNDS;
				break;
			}

			boolean b = grid[Px][Py];

			if(b){
				result = PLACE_BAD;
				break;
			}

			result = placeHelper(Py, Px, result);

		}

		compMaxHeight();
		sanityCheck();
		return result;

	}


	private void firstFor(int to){
		for(int i = 0; i < this.width; i++){
			widths[to] = 0;
			grid[i][to] = false;
		}
	}

	private void secondFor(int to, int from){
		for(int i = 0; i < this.width; i++){
			widths[to] = widths[from];
			grid[i][to] = grid[i][from];
		}
	}

	private boolean copyGivenRow(int to, int from){

		int max = compMaxHeight();

		boolean compare = (max <= from);
		if(compare){
			firstFor(to);
		}else secondFor(to, from);

		return true;
	}


	private void helperClearRows(int rowsCleared){
		int HLen = heights.length;
		int count = 0;
		int max = maxHeight;
		while(true) {
			if (count == HLen)
				break;
			int tmp = heights[count];
			tmp -= rowsCleared;
			heights[count] = tmp;
			int c = heights[count];
			helper(c, count);

			count++;
		}
	}

	/**
	 Deletes rows that are filled all the way across, moving
	 things above down. Returns the number of rows cleared.
	*/
	public int clearRows() {
		int rowsCleared = 0;
		int c = 0;
		if(committed){
			committed = false;
			saveCurBoard();
		}

		int To = 0;
		int from = 1;
		boolean filled = false;
		int thisW = width;
		for(To = 0, from = 1; from < maxHeight; To++, from++){
			boolean first = !filled && thisW == widths[To];
			if(first){
				rowsCleared += 1;
				c++;
				filled = true;
			}

			boolean sv = filled;
			while(from < maxHeight && sv &&( widths[from]==width)){
				from++;
				rowsCleared++;
				c++;
			}

			if(filled) {
				copyGivenRow(To, from);
				c++;
			}
		}

		if(filled) fillInRange(To, maxHeight);

		helperClearRows(rowsCleared);

		compMaxHeight();
		sanityCheck();
		return rowsCleared;
	}

	private void helper(int iuri, int count) {
		if ((iuri > 0)) {
			int secParameter = heights[count] - 1;
			boolean cur = grid[count][secParameter];
			if (cur) {
				heights[count] = 0;
				for (int j = 0; j < maxHeight; j++) {
					boolean bol = grid[count][j];
					if (bol)
						heights[count] = j + 1;
				}
			}
		}
	}

	private boolean fillInRange(int first, int last) {
		int i = first;
		int j = 0;
		int zr = 0;
		boolean F = false;
		for(i = first; i < last; i++){
			widths[i] = zr;
			for(j = 0; j < this.width; j++) grid[j][i] = F;
		}
		return true;
	}


	/**
	 Reverts the board to its state before up to one place
	 and one clearRows();
	 If the conditions for undo() are not met, such as
	 calling undo() twice in a row, then the second undo() does nothing.
	 See the overview docs.
	*/
	public void undo() {
		if(!committed){
			boolean temp2 = false;
			int[] saveOldW = oldWidths;
			oldWidths = widths;
			widths = saveOldW;

			int[] saveOldH = oldHeights;
			oldHeights = heights;
			heights = saveOldH;

			boolean[][] saveCurGird = savedGrid;
			savedGrid = grid;
			grid = saveCurGird;

			compMaxHeight();
		}
		commit();
		sanityCheck();
	}


	/**
	 Puts the board in the committed state.
	*/
	public void commit() {
		committed = true;
	}



	/**
	 Renders the board state as a big String, suitable for printing.
	 This is the sort of print-obj-state utility that can help see complex
	 state change over time.
	 (provided debugging utility)
	 */
	public String toString() {
		StringBuilder buff = new StringBuilder();
		for (int y = height-1; y>=0; y--) {
			buff.append('|');
			for (int x=0; x<width; x++) {
				if (getGrid(x,y)) buff.append('+');
				else buff.append(' ');
			}
			buff.append("|\n");
		}
		for (int x=0; x<width+2; x++) buff.append('-');
		return(buff.toString());
	}
}

