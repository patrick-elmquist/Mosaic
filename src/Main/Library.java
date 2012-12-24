package Main;

import java.awt.Color;
import java.awt.Image;
import java.util.HashMap;

public class Library {
	private HashMap<Color, LibItem> lib;
	
	public Library() {
		this.lib = new HashMap<Color, LibItem>();
	}
	
	public void addImage(String name, Color color, Image img) {
		this.lib.put(color, new LibItem(name, color, img));
	}
	
	public LibItem findImage(Color c) {
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
		return lib.get(closest);
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
		private String name;
		private Color color;
		private Image img;
		
		public LibItem(String name, Color color, Image img) {
			this.name = name;
			this.color = color;
			this.img = img;
		}
		
		public String getName() {	return name;	}

		public Color getColor() {	return color;	}

		public Image getImg()	{	return img;		}
	}

}
