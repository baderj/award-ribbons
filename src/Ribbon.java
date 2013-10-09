/** 
 * Ribbons.java
 * Purpose: Generates Images of Ribbons
 *
 * @author Johannes Bader
 * @version 1.0 7.2.2004
 */

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import java.util.Collections;

public class Ribbon {
	private RenderedImage rendImage;
	private Random random;
	
	// width of shadows in pixel
	private final int shadowWidth = 3;
	// width of ribbon
	private final int width = 118 * shadowWidth;
	// height of ribbon
	private final int height = 32 * shadowWidth;

	public Ribbon() {
		this.random = new Random();
	}

	/**
	 * Generates the ribbon and save it to file
	 * 
	 * @param name filename of ribbon without extension
	 */
	public void genAndSave(String name) {
		rendImage = generateRibbon();		
		try {
			File file = new File(name + ".png");
			ImageIO.write(rendImage, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Generated ribbon " + name);
	}

	/**
	 * Generates the ribbon
	 * 
	 * @return a rendered ribbon
	 */
	private RenderedImage generateRibbon() {
		BufferedImage bufferedImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bufferedImage.createGraphics();

		addPattern(width, height, g2d);
		addShadow(width, height, g2d);

		g2d.dispose();
		return bufferedImage;
	}

	/**
	 * Gets a random color from 
	 * 
	 * @param colors a list of colors to choose from
	 * @param filter a taboo color not to be picked 
	 * @return a rendered ribbon	 
	 */
	private Color getRandomColor(List<Color> colors, Color filter) {
		Collections.shuffle(colors);
		if (colors.size() <= 1 || !colors.get(0).equals(filter))
			return colors.get(0);
		else
			return colors.get(1);
	}

	/**
	 * add pattern to ribbon (colored stripes)
	 */
	private void addPattern(int width, int height, Graphics2D g2d) {
		int nr_colors = random.nextInt(3) + 2;
		float density = random.nextFloat()*2 + 1;
		List<Color> colors = getRandomColors(nr_colors);
		List<Integer> widths = new ArrayList<Integer>();

		Color currentColor = colors.get(0);
		g2d.setColor(currentColor);

		g2d.fillRect(0, 0, width, height);

		boolean full = false;
		int xposfrom = 0;
		while (!full) {
			int xposto = xposfrom + getStripeWidth(width, widths, density);
			if (xposto > width / 2) {
				xposto = width / 2;
				full = true;
			}
			currentColor = getRandomColor(colors, currentColor);
			g2d.setColor(currentColor);
			g2d.fillRect(xposfrom, 0, xposto - xposfrom, height);
			g2d.fillRect(width - xposto, 0, xposto - xposfrom, height);
			xposfrom = xposto;
		}
	}

	/**
	 * Get a list of random colors from candidates
	 * 
	 * @param nr number of colors to pick
	 * @return  list of nr colors from list of candidates 
	 */
	private List<Color> getRandomColors(int nr) {
		ArrayList<Color> colors = new ArrayList<Color>();
		// blue
		colors.add(new Color(36, 36, 101));
		// dark blue
		colors.add(new Color(0, 0, 78));
		// white
		colors.add(new Color(252, 252, 252));
		// old red
		colors.add(new Color(177, 0, 14));
		// light blue
		colors.add(new Color(131, 200, 255));
		// yellow
		colors.add(new Color(219, 219, 34));
		// red
		colors.add(new Color(212, 0, 0));
		// green
		colors.add(new Color(0, 177, 0));
		// black
		colors.add(new Color(40, 40, 40));
		// gold
		colors.add(new Color(252, 204, 52));
		// orange
		colors.add(new Color(239, 89, 35));
		// purple
		colors.add(new Color(120, 37, 136));

		Collections.shuffle(colors);
		return colors.subList(0, nr);
	}

	/**
	 * Get width for colored stripe
	 * 
	 * @param width width of ribbon
	 * @param widths list of previous widths
	 * @param density how dense the stripes should be
	 * @return stripe width 
	 */
	private int getStripeWidth(int width, List<Integer> widths, float density) {
		if (random.nextFloat() < 0.2 || widths.size() == 0) {
			int newwidth = 0;
			if (random.nextFloat() < 0.8)
				newwidth = new Integer(
						(int) ((Math.pow(2, random.nextFloat()) - 1) * width
								/ density / 2.0));
			if( newwidth < 3)
				newwidth = new Integer(10) + 3;
			widths.add(newwidth);
			return widths.get(widths.size() - 1);
		} else {
			int idx = random.nextInt(widths.size());
			return widths.get(idx);
		}
	}

	/**
	 * Add shadow 
	 * 
	 * @param width width of ribbon
	 * @param height height of ribbon	 *  
	 */
	private void addShadow(int width, int height, Graphics2D g2d) {
		int[][] color = new int[width][height];
		g2d.setColor(new Color(0, 0, 0));
		for (int i = 0; i < height; i++)
			for (int j = 0; j < width - 1; j++) {
				int up = -1;
				int left = -1;
				if (i > 0)
					up = color[j][i - 1];
				if (j > 0)
					left = color[j - 1][i];
				int newp = shadowTransparency(up, left);
				int trans = newp;
				if (i % (shadowWidth * 2) < shadowWidth)
					trans = newp - 10;
				g2d.setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, (float) (trans / 255.0)));
				color[j][i] = newp;
				g2d.drawLine(j, i, j + 1, i);
			}
	}

	/**
	 * Determine intensity of Shawod 
	 * 
	 * @param up shadow intensity of pixel above
	 * @param left shadow intensity of pixel to the left
	 */
	private int shadowTransparency(int up, int left) {
		int strength = 2;
		int max = 50;
		int min = 10;

		int f = random.nextInt(strength * 2 + 1) - strength;
		double n = -1;
		if (up > 0 && left > 0)
			n = (((up + left) / 2.0 + (double) f));
		else if (up > 0)
			n = (double) (up + (double) f);
		else if (left > 0)
			n = (double) (left + (double) f);
		else
			n = random.nextInt(max - min) + min;
		if (Math.floor(n) != Math.ceil(n)) {
			if (random.nextFloat() <= 0.5)
				n = Math.ceil(n);
			else
				n = Math.floor(n);
		}
		if (n < min)
			n = min;
		if (n > max)
			n = max;
		return (int) n;
	}

	public static void main(String[] args) {
		if (args.length != 1)
			throw new IllegalArgumentException(
					"usage: java -cp . Ribbon <nr_of_ribbons>");
		int nr = 0;
		try {
			nr = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			System.err.println("Firs commandline argument must be an integer");
			System.exit(1);
		}
		Ribbon ribbon = new Ribbon();
		for (int i = 1; i <= nr; i++)
			ribbon.genAndSave("ribbon_" + i);
	}
}
