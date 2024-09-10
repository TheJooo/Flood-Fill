import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class FloodFillFromImage {

    private static int imageCounter = 0;
    private static String imagePrefix = "flood_fill_step_";
    private static int pixelsFilled = 0; 

    public static void floodFill(BufferedImage image, int startX, int startY, Color newColor) throws IOException {
        int rows = image.getHeight();
        int cols = image.getWidth();
        int originalColor = image.getRGB(startX, startY);

        if (originalColor == newColor.getRGB()) return;

        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{startX, startY});
        image.setRGB(startX, startY, newColor.getRGB());
        pixelsFilled++; 

        if (pixelsFilled % 20 == 0) {
            saveImage(image, imagePrefix + imageCounter++ + ".png");
        }

        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0];
            int y = current[1];

            for (int i = 0; i < 4; i++) {
                int newX = x + dx[i];
                int newY = y + dy[i];

                if (newX >= 0 && newX < cols && newY >= 0 && newY < rows && image.getRGB(newX, newY) == originalColor) {
                    image.setRGB(newX, newY, newColor.getRGB());
                    queue.add(new int[]{newX, newY});
                    pixelsFilled++; 

                    if (pixelsFilled % 50 == 0) {
                        saveImage(image, imagePrefix + imageCounter++ + ".png");
                    }
                }
            }
        }

        saveImage(image, imagePrefix + imageCounter++ + ".png");
    }

    public static BufferedImage loadImage(String filePath) throws IOException {
        File file = new File(filePath);
        return ImageIO.read(file);
    }

    public static void saveImage(BufferedImage image, String filename) throws IOException {
        File file = new File(filename);
        ImageIO.write(image, "png", file);
    }

    public static void deleteGeneratedImages() {
        for (int i = 0; i < imageCounter; i++) {
            File file = new File(imagePrefix + i + ".png");
            if (file.exists()) {
                file.delete();
            }
        }
        System.out.println("All generated images have been deleted.");
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.out.println("Usage: java FloodFillFromImage <image-path> <start-x> <start-y>");
            return;
        }

        String imagePath = args[0];
        int startX = Integer.parseInt(args[1]);
        int startY = Integer.parseInt(args[2]);

        BufferedImage image = loadImage(imagePath);

        System.out.println("Starting Flood Fill...");
        floodFill(image, startX, startY, Color.ORANGE);  

        System.out.println("Flood Fill complete.");

        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to delete all generated images? (yes/no)");
        String response = scanner.nextLine().trim().toLowerCase();

        if (response.equals("yes")) {
            deleteGeneratedImages();
        } else {
            System.out.println("Images have been kept.");
        }
        scanner.close();
    }
}
