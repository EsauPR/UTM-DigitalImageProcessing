import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.StringTokenizer;
import javax.imageio.*;
import javax.imageio.stream.*;
import java.lang.Math;

public class RegionDescriptor {
    private BufferedImage DefaultImage;
    private BufferedImage Image;
    private int Width, Height;
    private int MaxRGB, MinRGB;

    public Vector DRN;

    RegionDescriptor( BufferedImage image ) {
        this.Width = image.getWidth();
        this.Height = image.getHeight();

        this.DefaultImage = new BufferedImage( image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB );
        setAsDefault( image );
        this.Image = new BufferedImage( image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB );
    }

    RegionDescriptor( PGM image ) {
        this( image.get_BufferedImage() );
    }

    RegionDescriptor( TIFF image ) {
        this( image.get_BufferedImage() );
    }

    RegionDescriptor( Dicom image ) {
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

    //Recibe el centro de gravedad de la imagen (x,y)
    public Vector Distancia_Radial_Normalizada(int x,int y){
        Color color;
        int R,G,B;
        Vector Vec =new Vector();

        //Encontrar el vector distancia
        for( int i = 0; i < this.DefaultImage.getHeight(); i++ ) {
            for( int j = 0; j < this.DefaultImage.getWidth(); j++ ) {
                try{
                    color = new Color( this.DefaultImage.getRGB(i,j) );
                    R = color.getRed();
                    G = color.getGreen();
                    B = color.getBlue();
                    if (R==255 && G==255 && B==255  ){
                        int  x1=i;
                        int y1=j;

                        double dist=Math.sqrt((double)((x-x1)*(x-x1)+(y-y1)*(y-y1)));
                        Vec.addElement(dist);
                        //System.out.println(dist);
                    }
                }catch( Exception e ) {
                }
            }
        }
        //Encontrar el maximo del vector distancia
        double max_vec=0;
        for (int i=0; i< Vec.size(); i++){
            if((double)Vec.elementAt(i)>max_vec ){
                max_vec=(double)Vec.elementAt(i);
            }
        }

        Vector Vec2 = new Vector();

        //Normalizar el vector distancia
        for (int i=0; i< Vec.size(); i++){
            Vec2.addElement((double)Vec.elementAt(i)/max_vec);
        }

        //for (int i=0; i<Vec2.size();i++ ) {
        //    System.out.println(i+"\t"+Vec2.elementAt(i));
        //}

        this.DRN = Vec2;

        return Vec2;

    }

    /* Return the arithmetic mean from DRN for each color channel */
    public double mean() {
        double sum = 0;

        for (int i = 0; i < this.DRN.size(); i++) {
            sum += (double)this.DRN.elementAt( i );
        }
        sum /= this.DRN.size();

        return sum;
    }

    /* Return the standard deviation from DRN for each color channel */
    public double standardDeviation() {
        double mean = mean();
        double sum = 0;

        for (int i = 0; i < this.DRN.size(); i++) {
                sum += Math.pow( (double)this.DRN.elementAt( i ) - mean, 2 );
        }
        sum = Math.sqrt( sum / this.DRN.size() );

        return sum;
    }

    /* Return the number of zero Crossings from DRN for each color channel */
    public int zeroCrossings() {
        double mean = mean();
        int cont = 0;

        for (int i = 0; i < this.DRN.size(); i++) {
            if ( (double)this.DRN.elementAt( i ) > mean ) {
                cont++;
            }
        }
        return cont;
    }

    /* Return the area index from DRN for each color channel */
    public double areaIndex() {
        double mean = mean();
        double sum = 0;

        for (int i = 0; i < this.DRN.size(); i++) {
            if ( (double)this.DRN.elementAt( i ) - mean > 0 ) {
                sum += (double)this.DRN.elementAt( i ) - mean;
            }
        }
        sum /= this.DRN.size() * mean;

        return sum;
    }

    /* Return the Roughness index from DRN for each color channel */
    public double RoughnessIndex() {
        double mean = mean();
        double sum = 0;

        for (int i = 0; i < this.DRN.size() - 1; i++) {
            sum += (double)this.DRN.elementAt( i ) - (double)this.DRN.elementAt( i + 1 );
        }
        sum /= this.DRN.size() * mean;

        return sum;
    }

}
