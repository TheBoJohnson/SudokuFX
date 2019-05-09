public class Tuple {
	private int first;
	private int second;

	public Tuple() {
		first = -1;
		second = -1;
	}

	public Tuple(int first, int second) {
		this.first = first;
		this.second = second;
	}

	public void setFirst(int index) {
		first = index;
	}

	public void setSecond(int index) {
		second = index;
	}

	public void setTuple(int first, int second) {
		this.first = first;
		this.second = second;
	}

	public int getFirst() {
		return first;
	}

	public int getSecond() {
		return second;
	}

	@Override
	public String toString() {
		String toReturn = "";

		toReturn += "First: " + first + "\n";
		toReturn += "Second: " + second;

		return toReturn;
	}

}
