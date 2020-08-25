package Program;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ImageToString {

	//fields
	private static final String FONT = "monospaced";
	private static final int MIN_SYM_SIZE = 15;

	//filter image
	public static BufferedImage getFilteredImage(BufferedImage img) {
		
		//new image 2 (filtered)
		BufferedImage img2 = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
		
		//do some stuff
		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				
				//getting colors
				Color c0 = new Color(img.getRGB(x, y)); //color zero (at the current pixel)
				Color cx = Color.BLACK; //adjacent color (right)
				Color cy = Color.BLACK; //adjacent color (down)
				Color cxy = Color.BLACK; //adjacent color (down/right)
				
				//if available get adjacent pixels
				if(x + 1 < img.getWidth())  cx = new Color(img.getRGB(x + 1, y));
				if(y + 1 < img.getHeight()) cy = new Color(img.getRGB(x, y + 1));
				if(x + 1 < img.getWidth() && y + 1 < img.getHeight()) cxy = new Color(img.getRGB(x + 1, y + 1));
				
				//getting (average) difference (delta)
				Color dx  = new Color(Math.abs(cx.getRed()  - c0.getRed()), Math.abs(cx.getGreen()  - c0.getGreen()), Math.abs(cx.getRed()  - c0.getRed()));
				Color dy  = new Color(Math.abs(cy.getRed()  - c0.getRed()), Math.abs(cy.getGreen()  - c0.getGreen()), Math.abs(cy.getRed()  - c0.getRed()));
				Color dxy = new Color(Math.abs(cxy.getRed() - c0.getRed()), Math.abs(cxy.getGreen() - c0.getGreen()), Math.abs(cxy.getRed() - c0.getRed()));
				
				//average difference (delta)
				float weight = .5f;
				Color ad = new Color((int)((dx.getRed() + dy.getRed() + dxy.getRed()*weight) / (2+weight)), 
									 (int)((dx.getGreen() + dy.getGreen() + dxy.getGreen()*weight) / (2+weight)),
									 (int)((dx.getBlue() + dy.getBlue() + dxy.getBlue()*weight) / (2+weight)));
				
				//setting color
				img2.setRGB(x, y, ad.getRGB());
				
			}
		}
		
		return img2; //(should be img2)
	}
	
	//getting symbol images
	private static BufferedImage[] getSymbolImages(int charImgWidth, int charImgHeight, double sense) {
		
		//image array
		BufferedImage symbols[] = new BufferedImage[126-32 + 255-160];
		
		//getting all character images
		for (int i = 0; i < symbols.length; i++) {
			//new image (paper)
			symbols[i] = new BufferedImage(charImgWidth, charImgHeight, BufferedImage.TYPE_INT_ARGB_PRE);
			
			//new pencil
			Graphics2D g = (Graphics2D) symbols[i].getGraphics();
			
			//drawing character
			g.setColor(new Color((int)(Math.max(1, Math.min(255, (1-sense)*255)))));
			g.setFont(new Font(FONT, Font.PLAIN, (int)(charImgHeight * .75f))); //FONT + more
			g.drawString((char)(i < 95 ? i+32 : i+66) + "", 0, (int)(symbols[i].getHeight() * 0.5)); //drawing
			
			g.dispose(); //throwing pencil out
		}
		
		return symbols;
	}
	
	//converting
	public static String convert(BufferedImage img, int charWidth,  double sense, boolean filterImage, boolean shrinkImage, boolean silent, String name) {

		//if shrinking image - shrink image
		if(shrinkImage) {
			//checking image size
			if(img.getWidth() / charWidth > MIN_SYM_SIZE) {
				
				//getting graphics and buffered-image
				BufferedImage img2 = img;
				img = new BufferedImage(MIN_SYM_SIZE * charWidth, (int)(((float)MIN_SYM_SIZE*charWidth*img2.getHeight()) / img2.getWidth()), BufferedImage.TYPE_INT_ARGB_PRE);
				Graphics2D g = (Graphics2D) img.getGraphics();
				
				//drawing image number 1 on image number 2
				g.drawImage(img2, 0, 0, img.getWidth(), img.getHeight(), null);
				g.dispose();//dispose pen
				
				if(!silent) System.out.println("[" + name + "] Shrunk Image Successfully to " + img.getWidth() + "x" + img.getHeight());
			}
		}

		//getting size
		int symbolWidth = img.getWidth() / charWidth, 
			symbolHeight = (int)(symbolWidth * 2.15);
		
		//filtering image
		if(filterImage) {
			img = getFilteredImage(img);
			if(!silent) System.out.println("[" + name + "] Filterd Image Successfully");
		}
		
		//getting symbol images
		BufferedImage[] symbols = getSymbolImages(symbolWidth, symbolHeight, sense);
		if(!silent) System.out.println("[" + name + "] Drew Symbols Successfully");
		
		//all sub images
		String str = "";
		
		for (int y = 0; y < img.getHeight() / symbolHeight - 1; y++) {
			for (int x = 0; x < img.getWidth() / symbolWidth - 1; x++) {
				//get sub image
				BufferedImage sub = img.getSubimage(
						(int)(symbolWidth * x), 
						(int)(symbolHeight * y), 
						(int) symbolWidth, 
						(int) symbolHeight);
				
				//compare
				str += imageToChar(sub, symbols);
			}
			str += "\n";
		}
		if(!silent) System.out.println("[" + name + "] Converted Image to String Successfully");
		
		return str;
	}
	
	//image to char
	private static char imageToChar(BufferedImage img, BufferedImage[] symbols) {
		
		//all scores
		int lowestScoreAt = 0;
		float lowestScore = -1;
		
		for (int i = 0; i < symbols.length; i++) {
			
			float score = 0f; //score
			
			//checking all pixels
			for (int y = 0; y < symbols[i].getHeight(); y++) {
				for (int x = 0; x < symbols[i].getWidth(); x++) {
					
					//getting color for symbol
					Color symbolColors = new Color(symbols[i].getRGB(x, y));
					float symbolGrayscale = (symbolColors.getRed() + symbolColors.getGreen() + symbolColors.getBlue()) / 3f; //Gray scale
					
					//get color from image
					Color imgColors = new Color(img.getRGB(x, y));
					float imgGrayscale = (imgColors.getRed() + imgColors.getGreen() + imgColors.getBlue()) / 3f; //gray scale
					
					//setting score
					score += Math.abs(imgGrayscale - symbolGrayscale);
				}
			}
			
			//setting lowest score (the best)
			if(score < lowestScore || lowestScore == -1) {
				lowestScore = score;
				lowestScoreAt = i;
			}
			
		}
		
		//returning lowest score position + 32 (the character)
		return (char)(lowestScoreAt < 95 ? lowestScoreAt+32 : lowestScoreAt+66);
	}
	
}