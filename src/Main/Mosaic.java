package Main;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import Main.Library.LibItem;


public class Mosaic {
	private final static int SIDE = 15;
	private final static int IMG_WIDTH	= SIDE;
	private final static int IMG_HEIGHT	= SIDE;
	
	private final static String FILENAME = "test2.jpg";
	
	private JFrame frame;
	private ImagePanel imgPanel;
	private ImagePanel secondPanel;
	private Library lib;
	private Image img;
	private int width;
	private int height;

	public Mosaic() {
		init();
		
//		simpleMosaic();
		advancedMosaic();
	}

	private void init() {
		try {
			this.img = ImageIO.read(this.getClass().getResourceAsStream("Images/" + FILENAME));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.width = img.getWidth(null);
		this.height = img.getHeight(null);
		
		this.imgPanel = new ImagePanel(img);
		this.secondPanel = new ImagePanel(img);
		
		this.frame = new JFrame("Mosaic");
		frame.setLayout(new GridLayout(1,2));
//		frame.add(imgPanel);
		frame.add(secondPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		this.lib = new Library();
	}
	
	private void pixelation() {
		BufferedImage bi = copy((BufferedImage) img);
		secondPanel.updateImage(bi);
		
		int s = 10;
		int ws = s;
		int hs = s;
		
		for (int i = 0; i < width; i+=s) {
			for (int j = 0; j < height; j+=s) {
				int r = 0;
				int g = 0;
				int b = 0;

				ws = (width - i < s) ? width - i : s;
				ws = (ws < 0) ? 0 : ws;
				hs = (height - j < s) ? height - j : s;
				hs = (hs < 0) ? 0 : hs;
				
				int c = 0;
				
				for (int k = 0; k < ws; k++) {
					for (int l = 0; l < hs; l++) {
						int rgb = bi.getRGB(i+k, j+l);
						r += (rgb >> 16) & 0xFF;
						g += (rgb >> 8) & 0xFF;
						b += (rgb) & 0xFF;
						c++;
					}
				}
			
				r /= c;
				g /= c;
				b /= c;

				int rgb = r;
				rgb = (rgb << 8) + g;
				rgb = (rgb << 8) + b;

				for (int k = 0; k < ws; k++) 
					for (int l = 0; l < hs; l++) 
						bi.setRGB(i+k, j+l, rgb);
				
				secondPanel.updateUI();
			}
		}
	}
	
	private void advancedMosaic() {
		BufferedImage bi = copy((BufferedImage) img);
		secondPanel.updateImage(bi);
		
		int ws = SIDE;
		int hs = SIDE;
		for (int i = 0; i < width; i+=SIDE) {
			for (int j = 0; j < height; j+=SIDE) {
				int r = 0;
				int g = 0;
				int b = 0;

				ws = (width - i < SIDE) ? width - i : SIDE;
				ws = (ws < 0) ? 0 : ws;
				hs = (height - j < SIDE) ? height - j : SIDE;
				hs = (hs < 0) ? 0 : hs;
				
				int c = 0;
				
				for (int k = 0; k < ws; k++) {
					for (int l = 0; l < hs; l++) {
						int rgb = bi.getRGB(i+k, j+l);
						r += (rgb >> 16) & 0xFF;
						g += (rgb >> 8) & 0xFF;
						b += (rgb) & 0xFF;
						c++;
					}
				}
			
				r /= c;
				g /= c;
				b /= c;

				int rgb = r;
				rgb = (rgb << 8) + g;
				rgb = (rgb << 8) + b;

				Color temp = new Color(rgb);
				LibItem item = lib.findImage(temp);
				
				if(item == null)
					System.out.println("null i=" + i + " j=" + j);

				Graphics2D grap = bi.createGraphics();
				
				grap.drawImage(item.getImg(), i,j,IMG_WIDTH,IMG_HEIGHT, null);
				grap.setColor(temp);
				grap.setStroke(new BasicStroke(1));
				grap.drawRect(i, j, IMG_WIDTH, IMG_HEIGHT);
				grap.dispose();
				secondPanel.updateUI();
			}
		}
	}
	
	/*
	private BufferedImage resizeImage(BufferedImage bi) {
		BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(bi, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
		g.dispose();
		return resizedImage;
	}
	*/
	
	private BufferedImage copy(BufferedImage bi) {
		 ColorModel cm = bi.getColorModel();
		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		 WritableRaster raster = bi.copyData(null);
		 return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
		}
	
	class ImagePanel extends JPanel {
		private static final long serialVersionUID = 4753305659422880547L;
		private Image img;
		private Dimension size;
		public ImagePanel(Image img) {
			this.img = img;
			this.size = new Dimension(img.getWidth(null),img.getHeight(null));
			setPreferredSize(size);
		}
		
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(img,0,0,null);
		}
		
		public void updateImage(BufferedImage bi) {
			img = bi;
			updateUI();
		}
	}
	
	public static void main(String[] args) {
		new Mosaic();
	}
}
