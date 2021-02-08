package cometColour;

public enum Colours {

	Orange("orange", 217, 125, 40),
	GREEN("green", 115, 151, 202),
	BLUE("blue", 151, 187, 64),
	BLACK("black", 243, 195, 1),
	GREY("grey", 117, 161, 110),
	WHITE("white", 84, 42, 20),
	YELLOW("yellow", 47, 47, 47),
	A("yellow", 96, 160, 185),
	B("yellow", 132, 48, 63),
	C("yellow", 157, 113, 78),
	D("yellow", 101, 101, 101),
	E("yellow", 140, 60, 123),
	F("yellow", 188, 6, 5),
	G("yellow", 35, 168, 163),
	H("yellow", 139, 97, 161),
	I("yellow", 117, 155, 130),
	J("yellow", 36, 97, 177),
	K("yellow", 213, 214, 216),
	L("yellow", 72, 103, 150),
	M("yellow", 135, 109, 200),
	N("yellow", 146, 146, 146)
	;
	
	private String name;
	private int r, g, b;
	
	private Colours(String name, int r, int g, int b) {
		this.name = name;
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public String getName() {
		return name;
	}

	public int getR() {
		return r;
	}

	public int getG() {
		return g;
	}

	public int getB() {
		return b;
	}
	
}
