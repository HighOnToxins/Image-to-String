package Program;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class Run {

	private static String[] str;
	
	public static void setStrings(int i, String s) {
		str[i] = s;
	}
	
	//main method
	public static void main(String[] args) {
		//this class
		Scanner in = new Scanner(System.in);
		
		//	recipe
		//get image path
		System.out.println("Image Path:");
		String path = in.nextLine(); 
//		String path = "C:\\Users\\Markus Brun Olsen\\Google Drev\\Art\\wallpapers\\rdr2 screen shot.jpg";
//		System.out.println("C:\\Users\\Markus Brun Olsen\\Google Drev\\Art\\wallpapers\\rdr2 screen shot.jpg");
		
		// get image
		BufferedImage img = getImage(path);
		
		//filter image?
		System.out.println("\nFilter image? (Y/n): ");
		String filterImage = in.nextLine();
		
		String again = "";
		do {
			//get image size
			System.out.println("\nNew Image Width (character count):");
			int w = in.nextInt();
			in.nextLine(); //doing scanner stuff
			
			//get color sensitivity
			System.out.println("\nColor Sensitivity (0 - 100): ");
			double sense = in.nextInt() / 100.0;
			in.nextLine(); //doing scanner stuff
			
			//file
			System.out.println("\nGet as file? (Y/n): ");
			String file = in.nextLine();

			//threads
			System.out.println("\nHow many threads would you like to use? (!0):");
			int threadSize = in.nextInt();
			in.nextLine(); //doing scanner stuff
			
			//get symbols, convert image and print
			System.out.println("\nLOADING...");
//			String string = ImageToString.convert(img, w, sense, !filterImage.equalsIgnoreCase("n"), true, false);
			long startTime = System.currentTimeMillis()/1000;
			
			//Run threads
			str = new String[threadSize];
			
			Thread[] threads = new Thread[threadSize];
			for (int i = 0; i < threadSize; i++) {
				
				//get sub-image
				BufferedImage subImage = img.getSubimage(0, (img.getHeight()*i)/threadSize, img.getWidth(), img.getHeight()/threadSize);
				
				//add and start thread
				threads[i] = new Thread(new Threading(subImage, w, sense, !filterImage.equalsIgnoreCase("n"), true, false, i));
				threads[i].start();
				
			}
			
			//waiting for cores to be done
			while(true) {
				int done = 0;
				for (int i = 0; i < threadSize; i++) {
					if(!threads[i].isAlive()) {
						done ++;
					}
				}
				if(done >= str.length) {
					break;
				}
			}
			
			//merge images
			String string = "";
			for (int i = 0; i < str.length; i++) {
				string += str[i];
			}
			
			if(!file.equalsIgnoreCase("n")) { //did not write no
				export(string, path.substring(0, path.length() - 4) + ".txt");	
			}else {
				System.out.println("\n" + string);		
			}
			
			//time taken
			System.out.println("Completed Task in " + (int)((System.currentTimeMillis()/1000) - startTime) + " Seconds");
			
			//try again?
			System.out.println("\nTry again with same Image and size? (Y/n): ");
			again = in.nextLine();
		}while(!again.equalsIgnoreCase("n"));
			
		//discard stuff
		in.close();
	}

	//getting image and sizes 
	private static BufferedImage getImage(String path) {

		//image
		BufferedImage img = null;
		
		//try
		try {
			
			//get image from path
		    img = ImageIO.read(new File(path));
		  
		    //user stuff
		    System.out.println("Read image Successfully");
		} catch (Exception e) {
			//something went wrong
			System.err.println("SOMETHING WENT WRONG WHILE READING IMAGE!");
		}
		
		//return
		return img;
	}

	//export text
	private static void export(String str, String path) {
		try {
			//image
		    BufferedWriter writer = new BufferedWriter(new FileWriter(path));
		   
		    //write
		    writer.write(str);
		   
		    //close
		    writer.close();
		    System.out.println("Saved text file successfully! at " + path);
		}catch(Exception e){
			System.err.println("Could not save text file!");
		}
	}
	
}
