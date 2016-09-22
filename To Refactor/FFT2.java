/*
 * Fast Fourier transform
 * Base: http://rosettacode.org/wiki/Fast_Fourier_transform#C
 * @autor Esau Peralta
 * @email esau.opr@gmail.com
 */

 import javax.swing.*;
 import java.awt.*;
 import java.awt.image.*;
 import java.io.*;
 import java.util.*;
 import java.util.StringTokenizer;
 import javax.imageio.*;
 import javax.imageio.stream.*;
 import java.lang.Math;

class  FFT2 {
    /* Two Matriz for real and imaginare values, the first index is for color RGB */
    private double [][][]Real;
    private double [][][]Imag;
    private int Width;
    private int Height;

    private static final int R = 0;
    private static final int G = 1;
    private static final int B = 2;

    /* Constructors */
    FFT2( BufferedImage image ) {
        setValues( image );
    }

    FFT2( PGM image ) {
        this( image.get_BufferedImage() );
    }

    FFT2( TIFF image ) {
        this( image.get_BufferedImage() );
    }

    FFT2( Dicom image ) {
        this( image.get_BufferedImage() );
    }

    /* Initial Real and imaginare values from image */
    private void setValues( BufferedImage image ) {
        this.Width = image.getWidth();
        this.Height = image.getHeight();

        if ( this.Width % 2 == 1 ) {
            this.Width --;
        }

        if ( this.Height % 2 == 1 ) {
            this.Height --;
        }

        Color color;

        this.Real = new double[3][ this.Height ][ this.Width ];
        this.Imag = new double[3][ this.Height ][ this.Width ];

        for (int i=0; i < this.Height; i++) {
            for(int j=0; j < this.Width; j++) {
                try{
                    /* BufferedImage contains image in RGB we will work with their components*/
                    color = new Color( image.getRGB(i,j) );
                    this.Real[R][i][j] = color.getRed();
                    this.Real[G][i][j] = color.getGreen();
                    this.Real[B][i][j] = color.getBlue();
                } catch (Exception e) {
                }
            }
        }
    }

    /* Set image to compute dft */
    public void setImage( BufferedImage image ) {
        setValues( image );
    }

    /* Return the real part of FDT */
    public double [][][] getReal() {
        return this.Real;
    }

    /* Return the imaginare part of FDT */
    public double [][][] getImag() {
        return this.Imag;
    }

    public void fft2() {
        System.out.println( this.Height + "" + this.Width );
        
        double [][] tmp_real = new double[3][this.Height];
        double [][] tmp_imag = new double[3][this.Height];

        /* fft for each row */
        for( int i = 0; i < this.Height; i++ ){
            FFT.fft( this.Real[R][i], this.Imag[R][i], this.Width);
            FFT.fft( this.Real[G][i], this.Imag[G][i], this.Width);
            FFT.fft( this.Real[B][i], this.Imag[B][i], this.Width);
        }

        /* fft for each columns */
        for( int i = 0; i < this.Width; i++ ){
            /* New array with values from the columns*/
            for( int j = 0; j < this.Height; j++ ){
                tmp_real[R][j] = this.Real[R][j][i];
                tmp_real[G][j] = this.Real[G][j][i];
                tmp_real[B][j] = this.Real[B][j][i];

                tmp_imag[R][j] = this.Real[R][j][i];
                tmp_imag[G][j] = this.Real[G][j][i];
                tmp_imag[B][j] = this.Real[B][j][i];
            }

            FFT.fft( tmp_real[R], tmp_imag[R], this.Height );
            FFT.fft( tmp_real[G], tmp_imag[G], this.Height );
            FFT.fft( tmp_real[B], tmp_imag[B], this.Height );

            for( int j = 0; j < this.Height; j++ ){
                this.Real[R][j][i] = tmp_real[R][j];
                this.Real[G][j][i] = tmp_real[G][j];
                this.Real[B][j][i] = tmp_real[B][j];

                this.Real[R][j][i] = tmp_imag[R][j];
                this.Real[G][j][i] = tmp_imag[G][j];
                this.Real[B][j][i] = tmp_imag[B][j];
            }
        }

    }

    public void ifft2() {
        /* Conjugate */
        for( int i = 0; i < this.Width; i++ ) {
            for( int j = 0; j < this.Height; j++){
                this.Imag[R][i][j] = - this.Imag[R][i][j];
                this.Imag[G][i][j] = - this.Imag[G][i][j];
                this.Imag[B][i][j] = - this.Imag[B][i][j];
            }
        }

        /* Apply fourier transform */
        fft2();

        /* conjugate and scale */
            /* Check if is the correct scale */
        double scale = this.Width + this.Height;
        for( int i = 0; i < this.Width; i++ ) {
            for( int j = 0; j < this.Height; j++){
                this.Imag[R][i][j] = -this.Imag[R][i][j] / scale;
                this.Imag[G][i][j] = -this.Imag[G][i][j] / scale;
                this.Imag[B][i][j] = -this.Imag[B][i][j] / scale;
            }
        }
    }

    /* Show a BufferedImage image */
    public void show_image( ) {
        JFrame jf = new JFrame();

        BufferedImage image = new BufferedImage( this.Width, this.Height, BufferedImage.TYPE_INT_RGB );

        for( int i = 0; i < this.Height; i++ ) {
            for( int j = 0; j < this.Width; j++ ) {
                try{
                    Color color = new Color( (int)Real[R][i][j], (int)Real[G][i][j], (int)Real[B][i][j] );
                    image.setRGB( i, j, color.getRGB() );
                }catch( Exception e ) {

                }
            }
        }

        jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        final Rectangle bounds = new Rectangle(0, 0, this.Width, this.Height );

        JPanel panel = new JPanel() {
            public void paintComponent(Graphics g) {
                Rectangle r = g.getClipBounds();
                ((Graphics2D)g).fill(r);

                if (bounds.intersects(r))
                    try {
                        g.drawImage(image, 0, 0, null);
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
            }
        };

        jf.getContentPane().add(panel);
        panel.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        jf.pack();
        jf.setVisible(true);
    }
}
