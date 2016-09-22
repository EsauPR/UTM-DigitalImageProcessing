// Compile : javac -classpath classes:lib/* tiff.java
// Execute : java -cp lib/jai_codec-1.1.3-alpha.jar:lib/jai-core-1.1.3-alpha.jar:. tiff

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.media.jai.PlanarImage;
import com.sun.media.jai.codec.ByteArraySeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.SeekableStream;

public class TIFF {

    private BufferedImage Image;

    TIFF( String path ) {
        read_image( path );
    }

    public void read_image( String path ) {
        try {
            FileInputStream in = new FileInputStream(path);
            FileChannel channel = in.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate((int)channel.size());
            channel.read(buffer);

            Image image = null;
            SeekableStream stream = new ByteArraySeekableStream( buffer.array() );
            String[] names = ImageCodec.getDecoderNames(stream);
            ImageDecoder dec = ImageCodec.createImageDecoder(names[0], stream, null);
            RenderedImage im = dec.decodeAsRenderedImage();
            image = PlanarImage.wrapRenderedImage(im).getAsBufferedImage();
            this.Image = (BufferedImage) image;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public BufferedImage get_BufferedImage() {
        return this.Image;
    }

    public void show_image(  ) {
        final BufferedImage image = this.Image;
        JFrame jf = new JFrame();

        jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        final Rectangle bounds = new Rectangle(0, 0, image.getWidth(), image.getHeight());

        JPanel panel = new JPanel() {
            public void paintComponent(Graphics g) {
                Rectangle r = g.getClipBounds();
                ((Graphics2D)g).fill(r);

                if (bounds.intersects(r))
                    try {
                        g.drawImage(image, 0, 0, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        };

        jf.getContentPane().add(panel);
        panel.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        jf.pack();
        jf.setVisible(true);
    }

}
