import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class GifSequenceWriter {
    private final ImageWriter writer;
    private final ImageWriteParam params;
    private final ImageOutputStream imageOutputStream;

    public GifSequenceWriter(ImageOutputStream out, int imageType, int timeBetweenFramesMS, boolean loopContinuously) throws IOException {
        writer = ImageIO.getImageWritersBySuffix("gif").next();
        params = writer.getDefaultWriteParam();
        imageOutputStream = out;

        writer.setOutput(out);
        writer.prepareWriteSequence(null);
    }

    public void writeToSequence(BufferedImage img) throws IOException {
        writer.writeToSequence(new javax.imageio.IIOImage(img, null, null), params);
    }

    public void close() throws IOException {
        writer.endWriteSequence();
        imageOutputStream.close();
    }
}
