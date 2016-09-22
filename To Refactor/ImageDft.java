/*
 * Discrete Fourier transform
 * Base: http://www.nayuki.io/page/how-to-implement-the-discrete-fourier-transform
 * @autor Esau Peralta
 * @email esau.opr@gmail.com
 */

import java.awt.*;
import java.awt.image.*;

public class ImageDft {

    /* Two Matriz for real and imaginare values, the last index is for color RGB */
    private double [][][]Real;
    private double [][][]Imag;
    private int Width;
    private int Height;

    private static final int R = 0;
    private static final int G = 1;
    private static final int B = 2;

    /* Constructors */
    ImageDft( BufferedImage image ) {
        setValues( image );
    }

    ImageDft( PGM image ) {
        this( image.get_BufferedImage() );
    }

    ImageDft( TIFF image ) {
        this( image.get_BufferedImage() );
    }

    ImageDft( Dicom image ) {
        this( image.get_BufferedImage() );
    }

    /* Initial Real and imaginare values from image */
    private void setValues( BufferedImage image ) {
        this.Width = image.getWidth();
        this.Height = image.getHeight();
        Color color;

        this.Real = new double[ this.Height ][ this.Width ][3];
        this.Imag = new double[ this.Height ][ this.Width ][3];

        for (int i=0; i < this.Width; i++) {
            for(int j=0; j < this.Height; j++) {
                try{
                    /* BufferedImage contains image in RGB we will work with their components*/
                    color = new Color( image.getRGB(i,j) );
                    this.Real[i][j][R] = color.getRed();
                    this.Real[i][j][G] = color.getGreen();
                    this.Real[i][j][B] = color.getBlue();
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

    public void Dft() {

        /* BufferedImage contains image in RGB we will work with their components*/
        double [][][]tmpReal = new double[ this.Height ][ this.Width ][3];
        double [][][]tmpImag = new double[ this.Height ][ this.Width ][3];

        /* Horizontal */
        for (int i = 0; i < this.Height; i++) { // For each row
            for (int k = 0; k < this.Width; k++) {  // For each element in the row
                tmpReal[i][k][R] = 0;
    			tmpImag[i][k][R] = 0;
                tmpReal[i][k][G] = 0;
    			tmpImag[i][k][G] = 0;
                tmpReal[i][k][B] = 0;
    			tmpImag[i][k][B] = 0;

                for (int t = 0; t < this.Width; t++) {  // For each element in the row
                    double angle = 2 * Math.PI * t * k / this.Width;

                    tmpReal[i][k][R] +=  this.Real[i][t][R] * Math.cos(angle) + this.Imag[i][t][R] * Math.sin(angle);
    				tmpImag[i][k][R] += -this.Real[i][t][R] * Math.sin(angle) + this.Imag[i][t][R] * Math.cos(angle);

                    tmpReal[i][k][G] +=  this.Real[i][t][G] * Math.cos(angle) + this.Imag[i][t][G] * Math.sin(angle);
    				tmpImag[i][k][G] += -this.Real[i][t][G] * Math.sin(angle) + this.Imag[i][t][G] * Math.cos(angle);

                    tmpReal[i][k][B] +=  this.Real[i][t][B] * Math.cos(angle) + this.Imag[i][t][B] * Math.sin(angle);
    				tmpImag[i][k][B] += -this.Real[i][t][B] * Math.sin(angle) + this.Imag[i][t][B] * Math.cos(angle);
    			}
    		}
        }

        /* Vertical */
        for (int i = 0; i < this.Width; i++) { // For each column
            for (int k = 0; k < this.Height; k++) {  // For each element in the column
                this.Real[k][i][R] = 0;
                this.Imag[k][i][R] = 0;
                this.Real[k][i][G] = 0;
                this.Imag[k][i][G] = 0;
                this.Real[k][i][B] = 0;
                this.Imag[k][i][B] = 0;

                for (int t = 0; t < this.Height; t++) {  // For each element in the column
                    double angle = 2 * Math.PI * t * k / this.Height;

                    this.Real[k][i][R] +=  tmpReal[t][i][R] * Math.cos(angle) + tmpImag[t][i][R] * Math.sin(angle);
                    this.Imag[k][i][R] += -tmpReal[t][i][R] * Math.sin(angle) + tmpImag[t][i][R] * Math.cos(angle);

                    this.Real[k][i][G] +=  tmpReal[t][i][G] * Math.cos(angle) + tmpImag[t][i][G] * Math.sin(angle);
                    this.Imag[k][i][G] += -tmpReal[t][i][G] * Math.sin(angle) + tmpImag[t][i][G] * Math.cos(angle);

                    this.Real[k][i][B] +=  tmpReal[t][i][B] * Math.cos(angle) + tmpImag[t][i][B] * Math.sin(angle);
                    this.Imag[k][i][B] += -tmpReal[t][i][B] * Math.sin(angle) + tmpImag[t][i][B] * Math.cos(angle);
                }
            }
        }

	}

    /* Compute de DFT inverse and stored in matriz Real */
    public void iDft(  ) {
        // Obtain Conjugate

        Dft();

        // Obtain conjugate
        // Scale
    }

}
