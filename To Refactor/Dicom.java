import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.imageio.stream.*;
import edu.ucla.rip.imageio.*;

public class Dicom {

    private BufferedImage Image;
    private Hashtable Header;
    private int Instance;

    public Dicom( String path ) {
        try {
            File f = new File(path);

            Iterator readers = ImageIO.getImageReadersByFormatName("dcm");
            ImageReader reader = (ImageReader)readers.next();
            reader.setInput(new FileImageInputStream(f), true);

            // get the header as a hashtable
            this.Header = ((DICOMImageReader)reader).getDICOMHashtable();

            // Image body
            this.Image = ((DICOMImageReader)reader).read(0, null);

            // let's retrieve the instance number dicom tag (0020,0013)
            this.Instance = Integer.parseInt((String)DICOMElement.getValueFromHash(this.Header, 0x00200013));

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
