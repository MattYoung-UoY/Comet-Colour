package cometColour;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

public class Main {

	public static void main(String[] args) {
		
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File("res/comet.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*	pixels[x][y][0] -> (x,y) R val
		 *  pixels[x][y][1] -> (x,y) G val
		 *  pixels[x][y][2] -> (x,y) B val
		 */
		
		int[][][] pixels;
		
		if(image.getWidth() <= image.getHeight())
			pixels = new int[image.getWidth()][image.getWidth()][3];
		else
			pixels = new int[image.getHeight()][image.getHeight()][3];
		
		for(int col = 0; col < pixels.length; col++) {
			for(int row = 0; row < pixels[0].length; row++) {
				pixels[col][row][0] = (0xFF0000 & image.getRGB(col, row)) >> 16;
				pixels[col][row][1] = (0x00FF00 & image.getRGB(col, row)) >> 8;
				pixels[col][row][2] = 0x0000FF & image.getRGB(col, row);
			}
		}
		
		HashMap<Color, Integer> clustering = new HashMap<>();
		final float DIFF = 0.15f;
		
		for(int col = 0; col < pixels.length; col++) {
			for(int row = 0; row < pixels[0].length; row++) {

				float r = pixels[col][row][0] / (float) 255;
				float g = pixels[col][row][1] / (float) 255;
				float b = pixels[col][row][2] / (float) 255;
				
				if(clustering.isEmpty()) {
					clustering.put(new Color(r, g, b), 1);
					continue;
				}
				
				Color closest = null;
				for(Color c: clustering.keySet()) {
					
					float diffCol = Math.abs(r - (c.getRed() / (float) 255)) + Math.abs(g - (c.getGreen() / (float) 255)) + Math.abs(b - (c.getBlue() / (float) 255));
					
					if(diffCol <= DIFF) {
						clustering.put(c, clustering.get(c) + 1);
						closest = c;
						break;
					}
					
				}
				
				if(closest == null) clustering.put(new Color(r, g, b), 1);
				
			}
		}
		
		List<Color> backgrounds = new ArrayList<Color>();
		Color temp = clustering.keySet().stream().max(Comparator.comparing(c -> clustering.get(c))).orElseThrow();
		clustering.remove(temp);
		//backgrounds.add(temp);
		temp = clustering.keySet().stream().max(Comparator.comparing(c -> clustering.get(c))).orElseThrow();
		clustering.remove(temp);
		//backgrounds.add(temp);
		backgrounds.add(new Color(0, 0, 0));
		System.out.println(backgrounds);
		
//		for(int row = 0; row < pixels.length; row++) {
//			System.out.print("[");
//			for(int col = 0; col < pixels[0].length; col++) {
//				System.out.print("(" + pixels[row][col][0] + ", " + pixels[row][col][1] + ", " + pixels[row][col][2] + "), ");
//			}
//			System.out.println("]");
//		}
		
		//final float BRICK_WIDTH = 0.8f;
		//final float RESULT_WIDTH = 35f;
		//int dim = (int) Math.ceil(RESULT_WIDTH / BRICK_WIDTH);
		int dim = 70;

		int[][][] bricks = new int[dim][dim][3];
		int[][][] ds = new int[dim][dim][3];
		
		final int PIXELS_PER_BRICK = pixels.length / dim;
		
		//final float BG_DIFF_THRES = 100f, BG_MULT = 0.00001f;
		
		for(int i = 0; i < dim; i++) {
			for(int j = 0; j < dim; j++) {
				float r = 0;
				float g = 0;
				float b = 0;
				
				//float pCount = 0;
				
				for(int pixI = 0; pixI < PIXELS_PER_BRICK; pixI++) {
					for(int pixJ = 0; pixJ < PIXELS_PER_BRICK; pixJ++) {
						
						int pr = pixels[((i * pixels.length) / dim) + pixI][((j * pixels.length) / dim) + pixJ][0];
						int pg = pixels[((i * pixels.length) / dim) + pixI][((j * pixels.length) / dim) + pixJ][1];
						int pb = pixels[((i * pixels.length) / dim) + pixI][((j * pixels.length) / dim) + pixJ][2];
						
//						boolean set = false;
//						for(Color c: backgrounds) {
//							float diffCol = Math.abs(pr - c.getRed()) + Math.abs(pg - c.getGreen()) + Math.abs(pb - c.getBlue());
//							if(diffCol <= BG_DIFF_THRES) {
//								set = true;
//								break;
//							}
//						}
//						
//						if(!set) {
							r += pr;
							g += pg;
							b += pb;
//							pCount++;
//						}
						
					}
				}
				float ppbSquared = PIXELS_PER_BRICK * PIXELS_PER_BRICK;
				r /= ppbSquared;
				g /= ppbSquared;
				b /= ppbSquared;

				r = Math.round(r);
				g = Math.round(g);
				b = Math.round(b);
				
				ds[i][j][0] = Math.round(r);
				ds[i][j][1] = Math.round(g);
				ds[i][j][2] = Math.round(b);
				
				Colours closest = null;
				
				for(Colours col: Colours.values()) {
					if(closest == null) {
						closest = col;
						continue;
					}

					float diffCol = Math.abs(r - col.getR()) + Math.abs(g - col.getG()) + Math.abs(b - col.getB());
					float diffClosest = Math.abs(r - closest.getR()) + Math.abs(g - closest.getG()) + Math.abs(b - closest.getB());
					
					if(diffCol < diffClosest) {
						closest = col;
					}
					
				}

				bricks[i][j][0] = closest.getR();
				bricks[i][j][1] = closest.getG();
				bricks[i][j][2] = closest.getB();
			}
		}
		
		writeImage(pixelsToImage(ds), "res/ds.png");
		
		writeImage(pixelsToImage(bricks), "res/output.png");
		
	}
	
	private static boolean writeImage(BufferedImage image, String filepath) {
		File file = new File(filepath);
		try {
			ImageIO.write(image, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	private static BufferedImage pixelsToImage(int[][][] pixels) {
		BufferedImage toRet = new BufferedImage(pixels.length, pixels[0].length, BufferedImage.TYPE_INT_RGB);
		for(int col = 0; col < pixels.length; col++) {
			for(int row = 0; row < pixels[0].length; row++) {
				toRet.setRGB(col, row, (pixels[col][row][0] << 16 | pixels[col][row][1] << 8) | pixels[col][row][2]);
			}
		}
		return toRet;
	}
	
}
