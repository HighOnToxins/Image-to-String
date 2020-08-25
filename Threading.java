package Program;
import java.awt.image.BufferedImage;

public class Threading implements Runnable{
	//fields
	private BufferedImage img;
	private int charWidth, index;
	private double sense;
	private boolean filterImage, shrinkImage, silent;
	
	public Threading(BufferedImage img, int charWidth,  double sense, boolean filterImage, boolean shrinkImage, boolean silent, int index) {
		//initializing
		this.img = img;
		
		this.charWidth = charWidth;
		
		this.sense = sense;
		
		this.filterImage = filterImage;
		this.shrinkImage = shrinkImage;
		this.silent = silent;
		
		this.index = index;
		
	}
	
	public void run() {
		//convert and add it to string
		Run.setStrings(index, ImageToString.convert(img, charWidth, sense, filterImage, shrinkImage, silent, index+""));	
	}
}
 