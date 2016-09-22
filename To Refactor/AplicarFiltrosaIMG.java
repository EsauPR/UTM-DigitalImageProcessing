import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.StringTokenizer;
import javax.imageio.*;
import javax.imageio.stream.*;
import java.lang.*;

public class AplicarFiltrosaIMG {

    private BufferedImage DefaultImage;
    private BufferedImage Image;
    private int Width, Height;
    private int MaxRGB, MinRGB;
    
    AplicarFiltrosaIMG( BufferedImage image ) {
            this.DefaultImage = new BufferedImage( image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB );
            setAsDefault( image );
            this.Image = new BufferedImage( 2*image.getWidth(), 2*image.getHeight(), BufferedImage.TYPE_INT_RGB );

    }

    AplicarFiltrosaIMG( PGM image ) {
        this( image.get_BufferedImage() );
    }

    AplicarFiltrosaIMG( TIFF image ) {
        this( image.get_BufferedImage() );
    }

    AplicarFiltrosaIMG( Dicom image ) {
        this( image.get_BufferedImage() );
    }

    /* Copy a BufferedImage to DefaultImage variable */
    private void setAsDefault( BufferedImage image ){
        int width = image.getWidth();
        int height = image.getHeight();

        for (int i=0; i < height; i++) {
            for(int j=0; j < width; j++) {
                try{
                    this.DefaultImage.setRGB(i, j, image.getRGB(i,j) );
                } catch (Exception e) {

                }

            }
        }
    }

    /* Set as default the transformed image */
    public void setAsDefault( ){
        setAsDefault( this.Image );
    }

    /* Show a BufferedImage image */
    private void show_image( final BufferedImage image ) {
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

    /* Show the transformed image */
    public void show_image() {
        show_image( this.Image );
    }

    /* Show the default image */
    public void show_default_image() {
        show_image( this.DefaultImage );
    }

    /* Return the default image */
    public BufferedImage getDefaultImage() {
        return this.DefaultImage;
    }

    /* Return the transformed image */
    public BufferedImage getImage() {
        return this.Image;
    }

    
    public void aplicarFiltros() {
        int M = this.DefaultImage.getHeight();
        int N = this.DefaultImage.getWidth();
        int P = 2*M;
        int Q = 2*N;
        int R, G, B;
        Color color;
        
        for (int i=0;i<P;i++ ) {
            for (int j=0;j<Q ;j++) {
                try{
                    R = 0;
                    G = 0;
                    B = 0;
                    color = new Color(R, G, B);
                    this.Image.setRGB(i,j,color.getRGB());
                }catch( Exception e ) {

                }
            }
        }
        
        for (int i=0;i<M;i++ ) {
            for (int j=0;j<N ;j++) {
                try{
                    color = new Color( this.DefaultImage.getRGB(i,j) );
                    R = color.getRed();
                    G = color.getGreen();
                    B = color.getBlue();
                    color = new Color(R, G, B);
                    this.Image.setRGB(i,j,color.getRGB());
                }catch( Exception e ) {

                }
            }
        }

        /*Multiplicar por (-1)^x+y para centrar la tranformada*/
        for (int i=0;i<P;i++ ) {
            for (int j=0;j<Q ;j++) {
                try{
                    this.Image.setRGB(i,j,(this.Image.getRGB(i,j))*((-1)^(i+j)));    
                }catch( Exception e ) {

                }
            }
        }

        ImageDft imgDFT = new ImageDft( this.Image );
        imgDFT.Dft();
        double [][][] Real = imgDFT.getReal();
        double [][][] Imag = imgDFT.getImag();

        filtros_frec H = new filtros_frec(P,Q);
        H.set_HPG_Filter(1);
        double [][] hh = H.getH();

        for (int i=0;i<P ;i++ ) {
            for (int j=0;j<Q ;j++ ) {
                Real[i][j][0] = Real[i][j][0]*hh[i][j];
                Real[i][j][1] = Real[i][j][1]*hh[i][j];
                Real[i][j][2] = Real[i][j][2]*hh[i][j];

                Imag[i][j][0] = Imag[i][j][0]*hh[i][j];
                Imag[i][j][1] = Imag[i][j][1]*hh[i][j];
                Imag[i][j][2] = Imag[i][j][2]*hh[i][j];
            }   
        }
        imgDFT.iDft();
        double [][][] RealInv = imgDFT.getReal();
        for (int i=0;i<P ;i++ ) {
            for (int j=0;j<Q ;j++ ) {
                RealInv[i][j][0] = RealInv[i][j][0]*((-1)^(i+j));
                RealInv[i][j][1] = RealInv[i][j][1]*((-1)^(i+j));
                RealInv[i][j][2] = RealInv[i][j][2]*((-1)^(i+j));
            }   
        }

        for (int i=0;i<P;i++ ) {
            for (int j=0;j<Q ;j++) {
                try{
                    //color = new Color( this.DefaultImage.getRGB(i,j) );
                    R = (int)RealInv[i][j][0];
                    G = (int)RealInv[i][j][1];
                    B = (int)RealInv[i][j][2];
                    color = new Color(R, G, B);
                    this.Image.setRGB(i,j,color.getRGB()*((-1)^(i+j)));
                }catch( Exception e ) {

                }
            }
        }
        /*for (int i=0;i<M ;i++ ) {
            for (int j=0;j<N ;j++ ) {
                this.Image.setRGB(i,j,(imageDft.getRGB(i,j) * hh[i][j]));
            }
        }*/

    }

    /* SAve image as png in te current directory */
    public void saveImage( String name ){
        try {
            File outputfile = new File( name + "png");
            ImageIO.write(this.Image, "png", outputfile);
        } catch (IOException e) {

        }
    }
}
