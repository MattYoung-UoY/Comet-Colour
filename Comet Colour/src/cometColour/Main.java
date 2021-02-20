package cometColour;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * Entry-point for the program
 */
public class Main {

	public static void main(String[] args) {
		
		/*	pixels[x][y][0] -> (x,y) R val
		 *  pixels[x][y][1] -> (x,y) G val
		 *  pixels[x][y][2] -> (x,y) B val
		 */
		//Gets the pixel colour values from the image file
		int[][][] pixels = loadImageFromFile("res/comet.png");
		//Gets the bricks required to construct the image
		int[][][] bricks = basicBrickPicking(pixels);
		//Creates the bricks output image
		writeImage(pixelsToImage(bricks), "res/output.png");

		//Creates the Bill of Materials
		bom("res/output.png", "res/bom.txt");
	}

	/**
	 * Generates a Bill of Materials
	 * @param image The bricks image to generate the BoM for
	 * @param bomPath The output path for the BoM
	 */
	private static void bom(String image, String bomPath) {
		//Loads the pixel colour information for the image
		int[][][] pixels = loadImageFromFile(image);

		//Map to count how many of each brick have appeared
		Map<String, Integer> bom = new HashMap<>();

		for (int[][] column : pixels) {
			for (int[] pixel : column) {

				String brickName = "";

				//Find the brick for this pixel in the image
				for (Bricks brick : Bricks.values()) {
					if (brick.getR() == pixel[0] && brick.getG() == pixel[1] && brick.getB() == pixel[2]) {
						brickName = brick.getName();
						break;
					}
				}

				//If there is no brick for this pixel throw an IllegalArgumentException
				if (brickName.equals(""))
					throw new IllegalArgumentException("Pixel (" + Arrays.toString(pixel) + ") is not in the colour set!");

				//If the brick has already been found then add 1 to it's counter, otherwise make a new entry for the brick
				if (bom.containsKey(brickName))
					bom.put(brickName, bom.get(brickName) + 1);
				else
					bom.put(brickName, 1);
			}
		}

		//Creates an output String with all of the information in it
		StringBuilder outStr = new StringBuilder();

		int total = 0;

		for (String brick : bom.keySet()) {
			total += bom.get(brick);
			outStr.append(brick).append(": ").append(bom.get(brick)).append("pcs\n");
		}

		outStr.append("Total: ").append(total).append("pcs");

		//Writes the output String to file
		try {
			FileWriter outFile = new FileWriter(bomPath);
			outFile.write(outStr.toString());
			outFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Calculates the difference between the two colours. Larger values represent a larger difference between the two colours
	 * @param c1 Colour 1
	 * @param c2 Colour 2
	 * @return A measurement of how different the two colours are
	 */
	private static int colDiff(Color c1, Color c2) {

		//Finds the difference between the R, G, and B values for the two colours
		int diffR = c1.getRed() - c2.getRed();
		int diffG = c1.getGreen() - c2.getGreen();
		int diffB = c1.getBlue() - c2.getBlue();

		//Returns the difference between the two colours
		return Math.abs(diffR - diffG) + Math.abs(diffG - diffB) + Math.abs(diffB - diffR);
	}

	/**
	 * Determines which bricks should be used to create the brick representation
	 * @param pixels The pixels to determine the brick representation for
	 * @return The brick representation of the image
	 */
	private static int[][][] basicBrickPicking(int[][][] pixels) {
		//The dimensions of the brick representation
		int dim = 50;

		//The bricks colour information
		int[][][] bricks = new int[dim][dim][3];

		//Calculates how many pixels should be used to calculate each brick colour
		final int PIXELS_PER_BRICK = pixels.length / dim;

		for (int i = 0; i < bricks.length; i++) {
			for (int j = 0; j < bricks[0].length; j++) {
				float r = 0;
				float g = 0;
				float b = 0;

				for (int pixI = 0; pixI < PIXELS_PER_BRICK; pixI++) {
					for (int pixJ = 0; pixJ < PIXELS_PER_BRICK; pixJ++) {

						int x_coord = ((i * pixels.length) / dim) + pixI;
						int y_coord = ((j * pixels.length) / dim) + pixJ;

						//Sums all of the R, G, and B values for all of the pixels for the current brick
						r += pixels[x_coord][y_coord][0];
						g += pixels[x_coord][y_coord][1];
						b += pixels[x_coord][y_coord][2];

					}
				}
				float ppbSquared = PIXELS_PER_BRICK * PIXELS_PER_BRICK;
				//Calculates the average R, G, and B values for the brick
				r /= ppbSquared;
				g /= ppbSquared;
				b /= ppbSquared;

				r = Math.round(r);
				g = Math.round(g);
				b = Math.round(b);

				Bricks closest = null;

				//Finds the closest coloured brick
				for (Bricks brick : Bricks.values()) {
					if (closest == null) {
						closest = brick;
						continue;
					}

					Color c = new Color((int) r, (int) g, (int) b);
					int diffCol = colDiff(c, new Color(brick.getR(), brick.getG(), brick.getB()));
					int diffClosest = colDiff(c, new Color(closest.getR(), closest.getG(), closest.getB()));

					if (diffCol < diffClosest) {
						closest = brick;
					}

				}

				assert closest != null; // if Colours.values() is empty, closest is null, then NullPointerException.

				//Sets the brick colour
				bricks[i][j][0] = closest.getR();
				bricks[i][j][1] = closest.getG();
				bricks[i][j][2] = closest.getB();
			}
		}

		return bricks;
	}

	/**
	 * @param filepath Image to load
	 * @return Returns the pixel colour data
	 */
	private static int[][][] loadImageFromFile(String filepath) {
		//Reads the image
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(filepath));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		//Creates the pixels colour data array
		int[][][] pixels;

		if (image.getWidth() <= image.getHeight())
			pixels = new int[image.getWidth()][image.getWidth()][3];
		else
			pixels = new int[image.getHeight()][image.getHeight()][3];

		//Populates the pixels array
		for (int col = 0; col < pixels.length; col++) {
			for (int row = 0; row < pixels[0].length; row++) {
				//image.getRGB() returns an int of the form ARGB where each character is a byte in the int, hence the bitwise operators
				//This could be optimised by extracting the image.getRGB() calls into a variable
				pixels[col][row][0] = (0xFF0000 & image.getRGB(col, row)) >> 16;
				pixels[col][row][1] = (0x00FF00 & image.getRGB(col, row)) >> 8;
				pixels[col][row][2] = 0x0000FF & image.getRGB(col, row);
			}
		}
		return pixels;
	}

	/**
	 * @param image Image to write
	 * @param filepath Path to write the file to
	 */
	private static void writeImage(BufferedImage image, String filepath) {
		File file = new File(filepath);
		try {
			ImageIO.write(image, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param pixels Pixels colour data to convert to an Image
	 * @return Image object for the given pixel colour data
	 */
	private static BufferedImage pixelsToImage(int[][][] pixels) {
		BufferedImage toRet = new BufferedImage(pixels.length, pixels[0].length, BufferedImage.TYPE_INT_RGB);
		for (int col = 0; col < pixels.length; col++) {
			for (int row = 0; row < pixels[0].length; row++) {
				//The image.setRGB() method uses the same binary representation as the image.getRGB() method as in the loadImageFromFile method, hence the bitwise operators
				toRet.setRGB(col, row, (pixels[col][row][0] << 16 | pixels[col][row][1] << 8) | pixels[col][row][2]);
			}
		}
		return toRet;
	}

}
