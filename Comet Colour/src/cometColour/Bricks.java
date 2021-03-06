package cometColour;

/**
 * Stores the Names and Colour values of the available bricks
 */
public enum Bricks {

	A("4619652/3005", 96, 160, 185),
	B("4541376/3005", 132, 48, 63),
	C("6057986/3005", 157, 113, 78),
	D("4211098/3005", 101, 101, 101),
	E("6022035/3005", 140, 60, 123),
	F("300521/3005", 188, 6, 5),
	G("4651903/3005", 139, 97, 161),
	H("4521948/3005", 117, 155, 130),
	I("300523/3005", 36, 97, 177),
	J("300501/3005", 213, 214, 216),
	K("4255413/3005", 72, 103, 150),
	L("4211389/3005", 146, 146, 146),
	M("4173805/3005", 217, 125, 40),
	N("4179830/3005", 115, 151, 202),
	O("4220634/3005", 151, 187, 64),
	P("300524/3005", 243, 195, 1),
	Q("4113915/3005", 177, 161, 110),
	R("4211242/3005", 84, 42, 20),
	S("300526/3005", 47, 47, 47),
	T("4286050/3005", 238, 149, 197);

	/**
	 * Name of the brick
	 */
	private final String name;
	/**
	 * The rgb values for the brick
	 */
	private final int r, g, b;

	/**
	 * @param name Name of the brick
	 * @param r R colour value of the brick
	 * @param g G colour value of the brick
	 * @param b B colour value of the brick
	 */
	Bricks(String name, int r, int g, int b) {
		this.name = name;
		this.r = r;
		this.g = g;
		this.b = b;
	}

	/**
	 * @return The name of the brick
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return The R colour value of the brick
	 */
	public int getR() {
		return r;
	}

	/**
	 * @return The G colour value of the brick
	 */
	public int getG() {
		return g;
	}

	/**
	 * @return The B colour value of the brick
	 */
	public int getB() {
		return b;
	}

}
