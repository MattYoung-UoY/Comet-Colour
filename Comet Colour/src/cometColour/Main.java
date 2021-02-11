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

public class Main {

	public static void main(String[] args) {
		
		/*	pixels[x][y][0] -> (x,y) R val
		 *  pixels[x][y][1] -> (x,y) G val
		 *  pixels[x][y][2] -> (x,y) B val
		 */
		int[][][] pixels = loadImageFromFile("res/comet.png");
		int[][][] bricks = basicBrickPicking(pixels);
		writeImage(pixelsToImage(bricks), "res/output.png");

		bom("res/output.png", "res/bom.txt");
	}

	private static void bom(String image, String bomPath) {
		int[][][] pixels = loadImageFromFile(image);

		Map<String, Integer> bom = new HashMap<>();

		for (int[][] column : pixels) {
			for (int[] pixel : column) {

				String brickName = "";

				for (Bricks brick : Bricks.values()) {
					if (brick.getR() == pixel[0] && brick.getG() == pixel[1] && brick.getB() == pixel[2]) {
						brickName = brick.getName();
						break;
					}
				}

				if (brickName.equals(""))
					throw new IllegalArgumentException("Pixel (" + Arrays.toString(pixel) + ") is not in the colour set!");

				if (bom.containsKey(brickName))
					bom.put(brickName, bom.get(brickName) + 1);
				else
					bom.put(brickName, 1);
			}
		}

		StringBuilder outStr = new StringBuilder();

		int total = 0;

		for (String brick : bom.keySet()) {
			total += bom.get(brick);
			outStr.append(brick).append(": ").append(bom.get(brick)).append("pcs\n");
		}

		outStr.append("Total: ").append(total).append("pcs");

		try {
			FileWriter outFile = new FileWriter(bomPath);
			outFile.write(outStr.toString());
			outFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static int colDiff(Color c1, Color c2) {

		int diffR = c1.getRed() - c2.getRed();
		int diffG = c1.getGreen() - c2.getGreen();
		int diffB = c1.getBlue() - c2.getBlue();

		return Math.abs(diffR - diffG) + Math.abs(diffG - diffB) + Math.abs(diffB - diffR);
	}

	private static int[][][] basicBrickPicking(int[][][] pixels) {
		int dim = 50;

		int[][][] bricks = new int[dim][dim][3];

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

						int pr = pixels[x_coord][y_coord][0];
						int pg = pixels[x_coord][y_coord][1];
						int pb = pixels[x_coord][y_coord][2];

						r += pr;
						g += pg;
						b += pb;

					}
				}
				float ppbSquared = PIXELS_PER_BRICK * PIXELS_PER_BRICK;
				r /= ppbSquared;
				g /= ppbSquared;
				b /= ppbSquared;

				r = Math.round(r);
				g = Math.round(g);
				b = Math.round(b);

				Bricks closest = null;

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
				bricks[i][j][0] = closest.getR();
				bricks[i][j][1] = closest.getG();
				bricks[i][j][2] = closest.getB();
			}
		}

		return bricks;
	}

	private static int[][][] loadImageFromFile(String filepath) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(filepath));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		int[][][] pixels;

		if (image.getWidth() <= image.getHeight())
			pixels = new int[image.getWidth()][image.getWidth()][3];
		else
			pixels = new int[image.getHeight()][image.getHeight()][3];

		for (int col = 0; col < pixels.length; col++) {
			for (int row = 0; row < pixels[0].length; row++) {
				pixels[col][row][0] = (0xFF0000 & image.getRGB(col, row)) >> 16;
				pixels[col][row][1] = (0x00FF00 & image.getRGB(col, row)) >> 8;
				pixels[col][row][2] = 0x0000FF & image.getRGB(col, row);
			}
		}
		return pixels;
	}

	private static void writeImage(BufferedImage image, String filepath) {
		File file = new File(filepath);
		try {
			ImageIO.write(image, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static BufferedImage pixelsToImage(int[][][] pixels) {
		BufferedImage toRet = new BufferedImage(pixels.length, pixels[0].length, BufferedImage.TYPE_INT_RGB);
		for (int col = 0; col < pixels.length; col++) {
			for (int row = 0; row < pixels[0].length; row++) {
				toRet.setRGB(col, row, (pixels[col][row][0] << 16 | pixels[col][row][1] << 8) | pixels[col][row][2]);
			}
		}
		return toRet;
	}

}
