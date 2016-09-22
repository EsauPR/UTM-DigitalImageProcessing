import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.StringTokenizer;
import javax.imageio.*;
import javax.imageio.stream.*;
import java.lang.Math;

public class Textura{

    private BufferedImage DefaultImage;
    private BufferedImage Image;
    private int Width, Height;
    private int MaxRGB, MinRGB;


    Textura( BufferedImage image ) {
        this.Width = image.getWidth();
        this.Height = image.getHeight();

        this.DefaultImage = new BufferedImage( image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB );
        setAsDefault( image );
        this.Image = new BufferedImage( image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB );
    }

    Textura( PGM image ) {
        this( image.get_BufferedImage() );
    }

    Textura( TIFF image ) {
        this( image.get_BufferedImage() );
    }

    Textura( Dicom image ) {
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

    public double uniformidad(Textura Region ){
        Color color;
        int R,G,B;
        int[][] histogram = new int[256][3];

        /* Getting histogram */
        for( int i = 0; i < this.DefaultImage.getHeight(); i++ ) {
            for( int j = 0; j < this.DefaultImage.getWidth(); j++ ) {
                try{
                    Color region = new Color( Region.DefaultImage.getRGB(i,j) );
                    R= region.getRed();
                    G= region.getGreen();
                    B= region.getBlue();

                    if (R==255 && G==255 && B==255){
                        color = new Color( this.DefaultImage.getRGB(i,j) );
                        R = color.getRed();
                        G = color.getGreen();
                        B = color.getBlue();
                        histogram[R][0] ++;
                        histogram[G][1] ++;
                        histogram[B][2] ++;
                    }
                }catch( Exception e ) {

                }
            }
        }

        double suma=0;
        for( int i = 0; i < 256; i++ ) {
            suma += Math.pow((histogram[i][0]+histogram[i][1]+histogram[i][2])/(double)(3*(this.DefaultImage.getHeight() * this.DefaultImage.getWidth())),2);
        }

        return suma;

    }


    /* Return the standard deviation  for each color channel */
    public double[] standardDeviation( BufferedImage mask ) {
        double []mean = media( mask );

        double []sum = {0, 0, 0};

        for( int i = 0; i < this.DefaultImage.getHeight(); i++ ) {
            for( int j = 0; j < this.DefaultImage.getWidth(); j++ ) {
                try{
                    Color color = new Color( this.DefaultImage.getRGB(i,j) );
                    Color colorMask = new Color( this.DefaultImage.getRGB(i,j) );
                    int R = colorMask.getRed();
                    int G = colorMask.getGreen();
                    int B = colorMask.getBlue();
                    // Only the pixel of the object mask, 0 == background
                    if ( R != 0 && G == 0 && B == 0) {
                        sum[0] = Math.pow( color.getRed() - mean[0], 2 );
                        sum[1] = Math.pow( color.getGreen() - mean[1], 2 );
                        sum[2] = Math.pow( color.getBlue() - mean[2], 2 );
                    }
                }catch( Exception e ) {
                }
            }
        }

        for (int color = 0; color < 3; color ++) {
            sum[ color ] = 1.0 - 1.0 / ( 1.0 + sum[ color ]);
        }

        return sum;
    }

    /* Return the standard deviation from DRN for each color channel */
    public double[] media( BufferedImage image ) {

        double []sum = {0, 0, 0};
        // Pixeles de la región
        int contpx = 0;

        for( int i = 0; i < this.DefaultImage.getHeight(); i++ ) {
            for( int j = 0; j < this.DefaultImage.getWidth(); j++ ) {
                try{
                    Color color = new Color( this.DefaultImage.getRGB(i,j) );
                    Color colorMask = new Color( this.DefaultImage.getRGB(i,j) );
                    int R = colorMask.getRed();
                    int G = colorMask.getGreen();
                    int B = colorMask.getBlue();
                    // Only the pixel of the object mask, 0 == background
                    if ( R != 0 && G == 0 && B == 0) {
                        sum[0] = color.getRed();
                        sum[1] = color.getGreen();
                        sum[2] = color.getBlue();
                        contpx ++;
                    }
                }catch( Exception e ) {
                }
            }
        }

        for (int color = 0; color < 3; color ++) {
            sum[ color ] = sum[ color ] / contpx;
        }

        return sum;
    }

    //Metodo para calcular la compacidad e un objeto. Forma de llamada: ImagenDeContorno.(ImagenenDeRegion)
    public double[] Compacidad( Textura Region ){
        double []Area = { 0, 0, 0 };
        double []Perimetro = { 0, 0, 0};
        double []compacidad = { 0, 0, 0 };
        int R=0, G=1, B=2;
        //Vectores para los canales RGB del contorno y la Region del objeto
        int []rgbContorno = {0,0,0};
        int []rgbRegion = {0,0,0};
        //Iteramos para calcular el Area y Perimetro del Objeto
        for( int i = 0; i < this.Height; i++ ) {
            for( int j = 0; j < this.Width; j++ ) {
                try{
                    //Obtenemos los pixeles RGB de la imagen con el contorno del objeto
                    Color contorno = new Color( this.DefaultImage.getRGB(i,j) );
                    //Obtenemos los pixeles RGB de la imagen con la region objeto
                    Color region = new Color( Region.DefaultImage.getRGB(i,j) );

                    rgbContorno[ R ] = contorno.getRed();
                    rgbContorno[ G ] = contorno.getGreen();
                    rgbContorno[ B ] = contorno.getBlue();

                    rgbRegion[ R ] = region.getRed();
                    rgbRegion[ G ] = region.getGreen();
                    rgbRegion[ B ] = region.getBlue();

                    if ( rgbRegion[ R ] == 255 ){
                        Area[ R ] ++;
                    }
                    if ( rgbRegion[ G ] == 255 ){
                        Area[ G ] ++;
                    }
                    if ( rgbRegion[ B ] == 255 ){
                        Area[ B ] ++;
                    }
                    if ( rgbContorno[ R ] == 255 ){
                        Perimetro[ R ] ++;
                    }
                    if ( rgbContorno[ G ] == 255 ){
                        Perimetro[ G ] ++;
                    }
                    if ( rgbContorno[ B ] == 255 ){
                        Perimetro[ B ] ++;
                    }
                }catch( Exception e ) {

                }
            }
        }

        for(int i = 0 ; i < 3 ; i++ ){
            //System.out.println( "Area: "+Area[i]+"\nPerimetro: "+Perimetro[i] );
            compacidad[i] = Area[i] / ( ( Perimetro[i] * Perimetro[i] ) / (4 * 3.1416) ) ;
        }

        return compacidad;

    }

    //Entropia
    public double[] entropia(){
        Color color;
        int[][] histogram = new int[256][3];
        double[][] p = new double[256][3];
        int R=0, G=1, B=2;
        // Getting histogram

        SpaceFilters SFilter = new SpaceFilters( getDefaultImage( ) );
        histogram = SFilter.getHistogram( );

        double[] sumatoria = { 0, 0 ,0 };

        //Calcular el historial normalizado
        for(int i = 0; i < 256; i++) {
            p[i][ R ] = (double) histogram[i][R] / (this.DefaultImage.getWidth() * this.DefaultImage.getHeight());
            p[i][ G ] = (double) histogram[i][G] / (this.DefaultImage.getWidth() * this.DefaultImage.getHeight());
            p[i][ B ] = (double) histogram[i][B] / (this.DefaultImage.getWidth() * this.DefaultImage.getHeight());
        }

        //Calculamos Entropia
        for( int i = 0 ; i < 256 ; i++ ) {
            //Validamos Logaritmo para el caso Log(0)
            if( p[ i ][ R ] != 0 )
                sumatoria[ R ] += p[i][ R ]*( Math.log( p[i][ R ])/Math.log(2));
            if( p[ i ][ G ] != 0 )
                sumatoria[ G ] += p[i][ G ]*( Math.log( p[i][ G ])/Math.log(2));
            if( p[ i ][ B ] != 0 )
                sumatoria[ B ] += p[i][ B ]*( Math.log( p[i][ B ])/Math.log(2));
        }

        for( int i = 0; i < 3 ; i++ )
            sumatoria[i] = ( -1 ) * sumatoria[i];

        return sumatoria;

    }

    //Asimetria
    public double[] asimetria(){
        double[] mean = {0,0,0};
        Color color;
        int R = 0, G = 1, B =2;
        int[][] histogram = new int[ 256 ][ 3 ];
        double[][] p = new double[ 256 ][ 3 ];

        // Getting histogram
        SpaceFilters SFilter = new SpaceFilters( getDefaultImage( ) );
        histogram = SFilter.getHistogram( );

        double[] sumatoria = { 0, 0, 0 };

        //Calcular el historial normalizado
        for( int i = 0; i < 256; i++) {
            p[ i ][ R ] = (double) histogram[ i ][ R ] / (this.DefaultImage.getWidth() * this.DefaultImage.getHeight());
            p[ i ][ G ] = (double) histogram[ i ][ G ] / (this.DefaultImage.getWidth() * this.DefaultImage.getHeight());
            p[ i ][ B ] = (double) histogram[ i ][ B ] / (this.DefaultImage.getWidth() * this.DefaultImage.getHeight());
        }

        for( int i = 0 ; i < 256 ; i++ ) {

            sumatoria[ R ] += ( Math.pow( ( i  - mean[0] ), 3) * p[i][0] );
            sumatoria[ G ] += ( Math.pow( ( i  - mean[1] ), 3) * p[i][1] );
            sumatoria[ B ] += ( Math.pow( ( i  - mean[2] ), 3) * p[i][2] );

        }

        for( int i = 0; i < 3 ; i++ )
            sumatoria[i] = ( -1 ) * sumatoria[i];
        System.out.println( sumatoria[0] );
        return sumatoria;

    }
}
