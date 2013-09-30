import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/** A vertex in the triangle. */
class V {

	/** Number of the vertex. */
	private int number;

	/** Distance from an end vertex which is at the bottom of the triangle. */
	private int dist;

	/**
	 * The child that has the shortest path to the end.
	 * */
	private V child;

	/**
	 * Create a new vertex.
	 * 
	 * @param number
	 *            of the vertex
	 */
	public V(int number) {
		this.number = number;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getDist() {
		return dist;
	}

	public void setDist(int dist) {
		this.dist = dist;
	}

	public V getChild() {
		return child;
	}

	public void setChild(V previous) {
		this.child = previous;
	}

}

/** Main class to compute the minimal path in the triangle. */
public class MinTrianglePath {

	/** Representation the input triangle of numbers. */
	private ArrayList<ArrayList<V>> triangle = new ArrayList<ArrayList<V>>();

	/**
	 * Number of lines in the input (which is equal to number of output
	 * numbers).
	 */
	private int rowsNumber = 0;

	/** Logging setup. */
	private static Logger LOGGER = Logger.getLogger(MinTrianglePath.class
	        .getName());
	private static Level LOGGING_LEVEL = Level.OFF;

	/** EOF - end of file sign */
	private static String END_OF_FILE = "EOF";

	/** Separator between numbers in the triangle. */
	private static String NUMBERS_SEPARATOR = " ";

	/** Start of the program. */
	public static void main(String[] args) {
		setLogging();
		MinTrianglePath minPath = new MinTrianglePath();
		minPath.readTriangle();
		LOGGER.info("Triangle read.");
		minPath.showTriangle();
		long startTime = System.currentTimeMillis();
		int[] result = minPath.computeMinPath();
		minPath.showResult(result);
		long endTime = System.currentTimeMillis();
		LOGGER.info("Elapsed time: " + (endTime - startTime) / 1000.0
		        + " seconds.");
	}

	/** Show the final result of the minimal path. */
	public void showResult(int[] result) {
		if (result == null) {
			System.out
			        .println("No elements in the triangle. There is no path.");
			return;
		}
		System.out.print("Minimal path is: ");
		int sum = 0;
		for (int i = 0; i < result.length; ++i) {
			sum += result[i];
			System.out.print(result[i]);
			if (i == result.length - 1) { // the last number
				System.out.print(" = ");
			} else {
				System.out.print(" + ");
			}
		}
		System.out.println(sum);
	}

	/** Set logging to the console and apply debug level. */
	private static void setLogging() {
		LOGGER.setLevel(LOGGING_LEVEL);
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(new SimpleFormatter());
		handler.setLevel(LOGGING_LEVEL);
		LOGGER.addHandler(handler);
	}

	/**
	 * Compute the minimal path in the triangle.
	 * 
	 * @return sequence of numbers that creates the minimal path
	 */
	private int[] computeMinPath() {
		if (rowsNumber <= 0) {
			return null;
		}
		ArrayList<V> previousRow = triangle.get(rowsNumber - 1); // last row
		for (V v : previousRow) {
			v.setDist(v.getNumber());
		}
		// bottom-up approach to build the solution
		for (int rowNumber = rowsNumber - 2; rowNumber >= 0; --rowNumber) {
			ArrayList<V> currentRow = triangle.get(rowNumber);
			for (int itemNumber = 0; itemNumber <= rowNumber; ++itemNumber) {
				V father = currentRow.get(itemNumber);
				V leftChild = previousRow.get(itemNumber);
				V rightChild = previousRow.get(itemNumber + 1);
				if (leftChild.getDist() < rightChild.getDist()) {
					father.setDist(leftChild.getDist() + father.getNumber());
					father.setChild(leftChild);
				} else {
					father.setDist(rightChild.getDist() + father.getNumber());
					father.setChild(rightChild);
				}
			}
			previousRow = currentRow;
		}
		return prepareResult();
	}

	/**
	 * Prepare a result of the algorithm.
	 * 
	 * @return sequence of numbers that create the shortest path
	 */
	private int[] prepareResult() {
		int[] result = new int[rowsNumber];
		V v = triangle.get(0).get(0); // start vertex
		for (int i = 0; i < result.length; ++i) {
			result[i] = v.getNumber();
			v = v.getChild();
		}
		return result;
	}

	/** Read triangle of numbers from a file. */
	private void readTriangle() {
		Scanner in = new Scanner(System.in);
		while (in.hasNextLine()) {
			String line = in.nextLine();
			LOGGER.fine(line);
			if (line.equals(END_OF_FILE)) {
				return;
			}
			String[] numberStrings = line.split(NUMBERS_SEPARATOR);
			if (numberStrings.length > rowsNumber + 1) {
				System.err.println("Too many values in line: "
				        + (rowsNumber + 1) + ". There are "
				        + numberStrings.length + " values but should be only: "
				        + (rowsNumber + 1) + " value(s).");
				System.exit(1);
			}
			ArrayList<V> row = new ArrayList<V>(rowsNumber + 1);
			for (int i = 0; i <= rowsNumber; ++i) {
				try {
					int number = Integer.parseInt(numberStrings[i]);
					row.add(new V(number));
				} catch (NumberFormatException ex) {
					System.err.println("The input does not contain expected "
					        + "number. The encountered input was: "
					        + numberStrings[i] + ".");
					System.exit(1);
				} catch (IndexOutOfBoundsException ex) {
					System.err.println("Line " + (rowsNumber + 1)
					        + " is too short. Up to now you entered " + i
					        + " number(s). Please enter "
					        + (rowsNumber + 1 - i)
					        + " more number(s) in the line.");
					System.exit(1);
				}
			}
			triangle.add(row);
			++rowsNumber;
		}
	}

	/** Present the triangle of numbers. */
	private void showTriangle() {
		LOGGER.fine(triangle.toString());
	}

}
