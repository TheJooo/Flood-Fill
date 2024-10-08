import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.util.ArrayList;

public class FloodFillFromImage {
    private static int imageCounter = 0;
    private static String imagePrefix = "flood_fill_step_";
    private static int pixelsFilled = 0;
    private static List<BufferedImage> imageFrames = new ArrayList<>();

    public static void floodFillWithQueue(BufferedImage image, int startX, int startY, Color newColor) throws IOException {
        floodFill(image, startX, startY, newColor, true);
    }

    public static void floodFillWithStack(BufferedImage image, int startX, int startY, Color newColor) throws IOException {
        floodFill(image, startX, startY, newColor, false);
    }

    public static void floodFill(BufferedImage image, int startX, int startY, Color newColor, boolean useQueue) throws IOException {
        int rows = image.getHeight();
        int cols = image.getWidth();
        int originalColor = image.getRGB(startX, startY);

        if (originalColor == newColor.getRGB()) return;

        if (useQueue) {
            LinkedList<int[]> queue = new LinkedList<>();
            queue.add(new int[]{startX, startY});
            performFloodFill(image, queue, newColor, originalColor, rows, cols);
        } else {
            Stack<int[]> stack = new Stack<>();
            stack.push(new int[]{startX, startY});
            performFloodFill(image, stack, newColor, originalColor, rows, cols);
        }
    }

    private static void performFloodFill(BufferedImage image, LinkedList<int[]> queue, Color newColor, int originalColor, int rows, int cols) throws IOException {
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0];
            int y = current[1];

            for (int i = 0; i < 4; i++) {
                int newX = x + dx[i];
                int newY = y + dy[i];

                if (isValidPosition(newX, newY, rows, cols, image, originalColor)) {
                    image.setRGB(newX, newY, newColor.getRGB());
                    queue.add(new int[]{newX, newY});
                    pixelsFilled++;

                    if (pixelsFilled % 50 == 0) {
                        saveFrame(image);
                    }
                }
            }
        }
        saveFrame(image);
    }

    private static void performFloodFill(BufferedImage image, Stack<int[]> stack, Color newColor, int originalColor, int rows, int cols) throws IOException {
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        while (!stack.isEmpty()) {
            int[] current = stack.pop();
            int x = current[0];
            int y = current[1];

            for (int i = 0; i < 4; i++) {
                int newX = x + dx[i];
                int newY = y + dy[i];

                if (isValidPosition(newX, newY, rows, cols, image, originalColor)) {
                    image.setRGB(newX, newY, newColor.getRGB());
                    stack.push(new int[]{newX, newY});
                    pixelsFilled++;

                    if (pixelsFilled % 50 == 0) {
                        saveFrame(image);
                    }
                }
            }
        }
        saveFrame(image);
    }

    private static boolean isValidPosition(int x, int y, int rows, int cols, BufferedImage image, int originalColor) {
        return x >= 0 && x < cols && y >= 0 && y < rows && image.getRGB(x, y) == originalColor;
    }

    public static BufferedImage loadImage(String filePath) throws IOException {
        File file = new File(filePath);
        return ImageIO.read(file);
    }

    public static void saveFrame(BufferedImage image) throws IOException {
        BufferedImage copy = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = copy.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        imageFrames.add(copy);

        String fileName = imagePrefix + imageCounter++ + ".png";
        File file = new File(fileName);
        ImageIO.write(copy, "png", file);
        System.out.println("Saved frame: " + fileName);
    }

    public static void createAnimation() throws IOException {
        if (imageFrames.isEmpty()) {
            System.out.println("No frames to create animation.");
            return;
        }

        String outputGif = "flood_fill_animation.gif";
        File outputFile = new File(outputGif);

        try (ImageOutputStream output = ImageIO.createImageOutputStream(outputFile)) {
            GifSequenceWriter writer = new GifSequenceWriter(output, imageFrames.get(0).getType(), 100, true);

            for (BufferedImage frame : imageFrames) {
                writer.writeToSequence(frame);
            }

            writer.close();
        } catch (IOException e) {
            System.err.println("Error while creating the GIF animation: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Animation created: " + outputGif);
    }

    public static void deleteGeneratedImages() {
        File currentDirectory = new File(".");

        File[] files = currentDirectory.listFiles();
        
        if (files != null) {
            for (File file : files) {
                if (file.getName().startsWith(imagePrefix) && file.getName().endsWith(".png")) {
                    if (file.delete()) {
                        System.out.println("Deleted: " + file.getName());
                    } else {
                        System.out.println("Failed to delete: " + file.getName());
                    }
                }
            }

        imageFrames.clear();
        System.out.println("All frames and PNG files have been deleted.");
    }
    

        imageFrames.clear();
        System.out.println("All frames and PNG files have been deleted.");
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 4) {
            System.out.println("Usage: java FloodFillFromImage <image-path> <start-x> <start-y> <method>");
            System.out.println("<method>: 'stack' or 'queue'");
            return;
        }

        String imagePath = args[0];
        int startX = Integer.parseInt(args[1]);
        int startY = Integer.parseInt(args[2]);
        String method = args[3];

        BufferedImage image = loadImage(imagePath);

        System.out.println("Starting Flood Fill...");
        Color fillColor = Color.PINK;  // Change the fill color here to any other color

        if (method.equalsIgnoreCase("stack")) {
            floodFillWithStack(image, startX, startY, fillColor);
        } else if (method.equalsIgnoreCase("queue")) {
            floodFillWithQueue(image, startX, startY, fillColor);
        } else {
            System.out.println("Invalid method. Use 'stack' or 'queue'.");
            return;
        }

        System.out.println("Flood Fill complete.");

        createAnimation();

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

