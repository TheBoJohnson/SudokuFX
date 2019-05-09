import java.io.*;
import java.util.regex.Pattern;
import java.util.InputMismatchException;

public class Model {
	private int[][] puzzleGrid;
	private boolean[][] editableCells;
	private boolean shouldWarn;
	private Tuple selectedIndexTuple;

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_RED = "\u001B[31m";

	public Model() {
		puzzleGrid = new int[9][9];
		editableCells = new boolean[9][9];
		shouldWarn = true;
		selectedIndexTuple = new Tuple();

		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				puzzleGrid[i][j] = 0;
				editableCells[i][j] = false;
			}
		}
	}

	public void toggleWarnings() {
		shouldWarn = !shouldWarn;
	}

	// Accessor Methods
	public int[][] getPuzzleGrid() {
		return puzzleGrid;
	}

	public boolean[][] getEditableCells() {
		return editableCells;
	}

	public boolean getShouldWarn() {
		return shouldWarn;
	}

	public Tuple getSelectedIndexTuple() {
		return selectedIndexTuple; 
	}

	public void setSelected(int row, int col) {
		selectedIndexTuple.setTuple(row, col);
	}

	public boolean makeMove(int row, int col, int newNum) {
		if (row < 0 || row > 8) {
			System.out.println("The row index is invalid");
			return false;
		}

		if (col < 0 || col > 8) {
			System.out.println("The col index is invalid");
			return false;
		}

		if (newNum < 1 || newNum > 9) {
			System.out.println("The number entered number is not valid");
			return false;
		}

		if (!editableCells[row][col]) {
			System.out.println("That cell was apart of the original puzzle");
			return false;
		}

		if (shouldWarn) {
			int oldValue = puzzleGrid[row][col];
			puzzleGrid[row][col] = newNum;

			if (!checkMove(row, col)) {
				System.out.println("That move is not valid");
				puzzleGrid[row][col] = oldValue;
				return false;
			}

			return true;
		} else {
			puzzleGrid[row][col] = newNum;
			return true;
		}
	}

	public void printPuzzle() {
		int row = 0;

		for(int i = 0; i < 13; i++) {
			if (i % 4 == 0) {
				for(int j = 0; j < 13; j++) {
					System.out.print("-");
				}
				System.out.println();
			} else {
				int col = 0;

				for(int j = 0; j < 13; j++) {
					if (j % 4 == 0) {
						System.out.print("|");
					} else {
						if (!editableCells[row][col]) {
							System.out.print(ANSI_RED + puzzleGrid[row][col] + "");
							System.out.print(ANSI_RESET);
						}
						else {
							System.out.print(puzzleGrid[row][col]);
						}
						col++;
					}
				}

				System.out.println();
				row++;
			}
		}
	}

	public void loadPuzzleFromFile(String fileName) {
		int[][] bufferGrid = new int[9][9];
		boolean[][] bufferEditables = new boolean[9][9];

		// TODO: intialize every boolean in the buffer Editables as false
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				bufferEditables[i][j] = false;
			}
		}

		try {
			FileReader fr = new FileReader(fileName);
			BufferedReader br = new BufferedReader(fr);

			for(int i = 0; i < 9; i++) {
				for(int j = 0; j < 9; j++) {
					int character = br.read();

					if (character == 46) {
						bufferGrid[i][j] = 0;
						bufferEditables[i][j] = true;
					} else {
						bufferGrid[i][j] = Character.getNumericValue(character);
					}
				}
				// consume the newline character at the end
				br.read();
			}

			br.close();
			
		} catch(Exception e) {
			e.printStackTrace();
		}

		puzzleGrid = bufferGrid;
		editableCells = bufferEditables;
	}
	
	// Methods to check to see if a move is legal
	public boolean checkMove(int row, int col) {
		return validateRow(row, col) && validateCol(row, col) && validateSquare(row, col);
	}

	private boolean validateRow(int row, int col) {
		int targetNum = puzzleGrid[row][col];

		for(int i = 0; i < 9; i++) {
			if (puzzleGrid[row][i] == targetNum && i != col) {
				return false;
			}
		}

		return true;
	}

	private boolean validateCol(int row, int col) {
		int targetNum = puzzleGrid[row][col];

		for(int i = 0; i < 9; i++) {
			if (puzzleGrid[i][col] == targetNum && i != row) {
				return false;
			}
		}

		return true;
	}

	private boolean validateSquare(int row, int col) {
		int targetNum = puzzleGrid[row][col];

		int startRow = 3 * (row / 3);
		int startCol = 3 * (col / 3);

		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				if(puzzleGrid[startRow + i][startCol + j] == targetNum && (startRow + i != row) && (startCol + j != col)) {
					return false;
				}
			}
		}

		return true;
	}

	// Methods to check to see if a final solution is correct
	public boolean validatePuzzle() {
		return validateRows() && validateCols() && validateSquares();
	}

	private boolean validateRows() {
		boolean[] canUse = new boolean[9];
		for(int row = 0; row < 9; row++) {
			// Reset the canUse array for each row
			for(int j = 0; j < 9; j++) {
				canUse[j] = true;
			}

			// Check each cell of the row to see what number has been used
			for(int col = 0; col < 9; col++) {
				if (puzzleGrid[row][col] <= 0 || puzzleGrid[row][col] >= 10) {
					// TODO: Throw an Error
				}

				if (puzzleGrid[row][col] == 0 || !canUse[puzzleGrid[row][col] - 1]) {
					return false;
				}

				canUse[puzzleGrid[row][col] - 1] = false;
			}
		}

		return true;
	}

	private boolean validateCols() {
		boolean[] canUse = new boolean[9];
		for(int col = 0; col < 9; col++) {
			// Reset the canUse array for each col
			for(int j = 0; j < 9; j++) {
				canUse[j] = true;
			}

			// Check each cell of the row to see what number has been used
			for(int row = 0; row < 9; row++) {
				if (puzzleGrid[row][col] <= 0 || puzzleGrid[row][col] >= 10) {
					// TODO: Throw an Error
				}

				if (puzzleGrid[row][col] == 0 || !canUse[puzzleGrid[row][col] - 1]) {
					return false;
				}

				canUse[puzzleGrid[row][col] - 1] = false;
			}
		}

		return true;
	}

	private boolean validateSquares() {
		boolean[] canUse = new boolean[9];

		for(int i = 0; i < 9; i+=3) {
			for(int j = 0; j < 9; j+=3) {
				// Reset the canUse array for each square
				for(int a = 0; a < 9; a++) {
					canUse[a] = true;
				}
				for(int k = 0; k < 3; k++) {
					for(int l = 0; l < 3; l++) {
						if (puzzleGrid[i + k][j + l] - 1 == 0 || !canUse[puzzleGrid[i + k][j + l] - 1]) {
							return false;
						}

						canUse[puzzleGrid[i + k][j + l] - 1] = false;
					}
				}
			}
		}
		return true;
	}
}
