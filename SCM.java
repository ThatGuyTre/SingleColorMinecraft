import java.io.File;
import java.io.IOException;
import java.awt.*;
import java.awt.image.*;
import java.awt.Color;

import javax.imageio.ImageIO;

//BEFORE RUNNING THIS MAKE SURE YOU HAVE AN OUTPUT FOLDER WITH ALL NECESSARY FOLDERS AND PATHS

public class SCM {
    public static void main(String[] args) throws Exception {
        File assetFolder = new File("assets");
        showFiles(assetFolder.listFiles());
    }

    public static void showFiles(File[] files) throws IOException {
        BufferedImage curImage;
        for (File f : files) {// For each file in the folder
            if (f.isDirectory()) {
                System.out.println("Directory: " + f.getAbsolutePath());
                showFiles(f.listFiles()); // Calls same method again.
            } else {
                // This is a file. Do file-type things here.
                if(f.getName().endsWith(".png")){// Makes sure the file is a PNG
                    curImage = ImageIO.read(f);
                    System.out.println("Found a png");
                    
                    File newPath = new File(movePathToOutputDir(f));
                    System.out.println("Made file");
                    if (newPath.createNewFile()) {
                      System.out.println("File created: " + newPath.getName());
                    } else {
                      System.out.println("File already exists.");
                    }
                    
                    ImageIO.write(
                    convertToBufferedImage(averageColorVoodoo(curImage)),
                    "png",
                    new File(movePathToOutputDir(f)));
                    
                    System.out.println("Recolored " + f.getName());

                }
                // System.out.println("File: " + f.getAbsolutePath());
            }
        }
    }

    public static String movePathToOutputDir(File f){
        //get current file path
        String dir = f.getPath();
        System.out.println("output\\" + dir);
        //Change the prefix to the new assets folder (maybe call it output or something)
        return "output\\" + dir;
    }

    // convert Image to BufferedImage, idk why i need to do this but i copy pasted it and the coding gods seem to be appeased
    public static BufferedImage convertToBufferedImage(Image img) {

        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bi = new BufferedImage(
                img.getWidth(null), img.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics2D = bi.createGraphics();
        graphics2D.drawImage(img, 0, 0, null);
        graphics2D.dispose();

        return bi;
    }

    public static Image averageColorVoodoo(Image image){
        BufferedImage img = convertToBufferedImage(image);

        double sumRedSquared = 0;
        double sumGreenSquared = 0;
        double sumBlueSquared = 0;
        int opaquePixels = 0;

        for (int y = 0; y < img.getHeight(null); y++) {
            for (int x = 0; x < img.getWidth(null); x++) {
                //Retrieving contents of a pixel
                int pixel = img.getRGB(x,y);
                //Creating a Color object from pixel value
                Color color = new Color(pixel, true);
                //Retrieving the R G B values
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();
                int alpha = color.getAlpha();

                if(alpha > 0){//Only count the pixel if the color is not completely transparent
                    sumRedSquared += red*red;
                    sumGreenSquared += green*green;
                    sumBlueSquared += blue*blue;

                    opaquePixels++;
                }
            }
        }
        ////////////NOW GOING TO SET THE NEW PICTURE TO THE AVERAGE COLOR
        if(opaquePixels>0){
            int averageRed = (int) Math.sqrt((sumRedSquared/opaquePixels));
            int averageBlue = (int) Math.sqrt((sumBlueSquared/opaquePixels));
            int averageGreen = (int) Math.sqrt((sumGreenSquared/opaquePixels));

            Color averageColor = new Color(averageRed,averageGreen,averageBlue);
            Color colWithAlpha;

            for (int y = 0; y < img.getHeight(null); y++) {
                for (int x = 0; x < img.getWidth(null); x++) {
                    Color pixelColor = new Color (img.getRGB(x,y), true);
                    colWithAlpha = new Color(averageColor.getRed(), averageColor.getGreen(), averageColor.getBlue(), pixelColor.getAlpha());
                    img.setRGB(x,y,colWithAlpha.getRGB());
                }
            }

            //If the image is a perfect square, set the image to a 1x1 picture for o p t i m i s a t i o n
            if(img.getHeight(null) == img.getWidth(null)){
                img.getScaledInstance(1, 1, Image.SCALE_DEFAULT);
            }
        }
        return img;
    }
}
