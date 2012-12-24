package Main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class Library {
	private static final String LIB_FILENAME = "lib.txt";
	private static final String SPLITTER = "<>";
	private HashMap<Color, LibItem> lib;
	private HashMap<Color, LibItem> usedImgs;
	
	public Library() {
		this.lib = new HashMap<Color, LibItem>();
		this.usedImgs = new HashMap<Color, LibItem>();
		File file = new File(LIB_FILENAME);
		
		if(file.exists()) {
			System.out.print("Building library from file...");
			createLibraryFromFile(file);
			System.out.println("Finished building library from file!");
		} else {
			System.out.print("Building library from filesystem...");
			try {
				buildLibrary();
			} catch (IOException e) {	e.printStackTrace();	}
		}
		
	}
	
	private void createLibraryFromFile(File file) {
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader(file));
			String line;
			
			while((line = br.readLine()) != null) {
				String[] split = line.split(SPLITTER);
				Color c = new Color(Integer.parseInt(split[0]));
				lib.put(c, new LibItem(split[1], c));
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public LibItem findImage(Color c) {
		
		if(usedImgs.containsKey(c)) {
			return usedImgs.get(c);
		} else {
			double min = Double.MAX_VALUE;
			double distance = -1;
			Color closest = null;
			for (Color color : lib.keySet()) {
				distance = colorDistance(color, c);
				if(distance < min) {
					min = distance;
					closest = color;
				}
			}
//			System.out.println(min);
//			if(min > 150) {
//				BufferedImage b = new BufferedImage(Mosaic.IMG_WIDTH, Mosaic.IMG_HEIGHT, BufferedImage.TYPE_INT_ARGB);
//				Graphics2D g2d = (Graphics2D) b.getGraphics();
//				g2d.setColor(c);
//				g2d.drawRect(0, 0, Mosaic.IMG_WIDTH, Mosaic.IMG_HEIGHT);
//				LibItem item = new LibItem(null, c);
//				item.img = b;
//				usedImgs.put(c, item);
//				return item;
//			}
				
			usedImgs.put(c, lib.get(closest));
			return lib.get(closest);
		}
	}
	
	private void buildLibrary() throws IOException {
		long before = System.currentTimeMillis();
		System.out.println("Building library...");
		BufferedImage img;
//		File dir = new File("E:/Pictures/Wallpaper");
		File dir = new File("D:/Pictures");
		
		File[] pics = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png");
			}
		});
		
		FileWriter fstream = new FileWriter(LIB_FILENAME);
		BufferedWriter out = new BufferedWriter(fstream);
		int counter = 1;
		int max = pics.length;
		for (File file : pics) {
			System.out.println("Analyzing picture..." + file.getName() + " (" + counter + " of " + max + ")");
			img = ImageIO.read(file);
			img = resizeImage(img);
			int c = 0;
			int r = 0;
			int g = 0;
			int b = 0;
			for (int k = 0; k < img.getWidth(); k++) {
				for (int l = 0; l < img.getHeight(); l++) {
					int rgb = img.getRGB(k, l);
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
			
			Color color = new Color(rgb);
			lib.put(color, new LibItem(file.getPath(), color));
			out.write(color.getRGB() + SPLITTER + file.getPath() + "\n");
			out.flush();
			counter++;
		}
		long after = System.currentTimeMillis();
		System.out.println("Finished building library! " + lib.size() + " pictures added (" + (after-before)/1000.0/60 + " minutes)");
		
		out.close();
	}
	
	private BufferedImage resizeImage(BufferedImage bi) {
		BufferedImage resizedImage = new BufferedImage(Mosaic.IMG_WIDTH, Mosaic.IMG_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(bi, 0, 0, Mosaic.IMG_WIDTH, Mosaic.IMG_HEIGHT, null);
		g.dispose();
		return resizedImage;
	}
	
	private double colorDistance(Color c1, Color c2) {
		double rmean = ( c1.getRed() + c2.getRed() )/2;
	    int r = c1.getRed() - c2.getRed();
	    int g = c1.getGreen() - c2.getGreen();
	    int b = c1.getBlue() - c2.getBlue();
	    double weightR = 2 + rmean/256;
	    double weightG = 4.0;
	    double weightB = 2 + (255-rmean)/256;
	    return Math.sqrt(weightR*r*r + weightG*g*g + weightB*b*b);
	}
	
	class LibItem {
		private String path;
		private Color color;
		private Image img;
		
		public LibItem(String path, Color color) {
			this.path = path;
			this.color = color;
			this.img = null;
		}
		
		public String getPath() {	return path;	}

		public Color getColor() {	return color;	}

		public Image getImg()	{
			if(this.img == null) {
				Image image = null;
				try {
					image = ImageIO.read(new File(path));
				} catch (IOException e) {
					e.printStackTrace();
				}
				this.img = image;
				this.img = resizeImage((BufferedImage) this.img);
				return image;
			} else {
				return this.img;
			}
		}
	}

}
