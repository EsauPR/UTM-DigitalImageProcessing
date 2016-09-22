import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.StringTokenizer;
import javax.imageio.*;
import javax.imageio.stream.*;


public class Detector_Bordes_Canny {

    private BufferedImage DefaultImage;
    private BufferedImage Image;
    private int Width, Height;
    private int MaxRGB, MinRGB;


    Detector_Bordes_Canny( BufferedImage image ) {
        this.Width = image.getWidth();
        this.Height = image.getHeight();

        this.DefaultImage = new BufferedImage( image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB );
        setAsDefault( image );
        this.Image = new BufferedImage( image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB );
    }

    Detector_Bordes_Canny( PGM image ) {
        this( image.get_BufferedImage() );
    }

    Detector_Bordes_Canny( TIFF image ) {
        this( image.get_BufferedImage() );
    }

    Detector_Bordes_Canny( Dicom image ) {
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
                        //e.printStackTrace();
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

    public void D_B_Canny(){

        //Aplicando suavizado Gaussiano
        SpaceFilters ImgA = new SpaceFilters( this.DefaultImage );
        double [][][] ImgSuavizada = ImgA.weightyFilter( );

        //Seteando la nueva imagen
        int R, G, B;
        Color color;
        for( int i = 0; i < this.DefaultImage.getHeight(); i++ ) {
            for( int j = 0; j < this.DefaultImage.getWidth(); j++ ) {
                try{
                   //color = new Color( ImgA.getImage().getRGB(i,j) );
                    R = (int)ImgSuavizada[ i ][ j ][ 0 ];
                    G = (int)ImgSuavizada[ i ][ j ][ 1 ];
                    B = (int)ImgSuavizada[ i ][ j ][ 2 ];
                    color = new Color(R, G, B);
                    this.DefaultImage.setRGB(i, j, color.getRGB() );
                }catch( Exception e ) {

                }
            }
        }

        //Obteniendo magnitud de gradiente
        SpaceFilters ImgB = new SpaceFilters( this.DefaultImage );
        double [][][] ImgGradMag = ImgB.gradientFilter( );

        //Obteniendo direccion de gradiente
        SpaceFilters ImgC = new SpaceFilters( this.DefaultImage );
        double [][][] ImgGradDir = ImgC.gradientDirection( );

        //Obteniendo suprecion de no maximos
        double [][][] ImgSuprecion = supresion( ImgGradMag, ImgGradDir );

        double Th=75;
        double Tl=170;

        //Obteniendo Histeresis de humbral
        int [][][] ImgHysteresis = hysteresis( ImgSuprecion, Th, Tl );

        for( int i = 0; i < this.DefaultImage.getHeight(); i++ ) {
            for( int j = 0; j < this.DefaultImage.getWidth(); j++ ) {
                try{
                   //color = new Color( ImgA.getImage().getRGB(i,j) );
                    R = (int)ImgHysteresis[ i ][ j ][ 0 ];
                    G = (int)ImgHysteresis[ i ][ j ][ 1 ];
                    B = (int)ImgHysteresis[ i ][ j ][ 2 ];
                    color = new Color(R, G, B);
                    this.Image.setRGB(i, j, color.getRGB() );
                }catch( Exception e ) {

                }
            }
        }

    }

    /*
     *  Hysteresis
     *  @param double [][][]gn, double th, double tl
     *  @return int [][][]
    */
    public int[][][] hysteresis(double [][][] gn, double th, double tl ) {
        int rows = gn.length;
        int cols = gn[0].length;

        int [][][]edges = new int[ rows ][ cols ][3];

        int []posX = {1,  1, 1, 0,  0, -1, -1, -1};
        int []posY = {1, -1, 0, 1, -1, -1,  1,  0};

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                for(int color = 0; color < 3; color++) {
                    /* If gn has a hight value is edge */
                    if( gn[i][j][color] >= th ) {
                        edges[i][j][color] = 255;
                        /* Looking for neighbors candidares to be edge */
                        for (int k = 0; k < 8; k++) {
                            try{
                                if( gn[ i+posX[k] ][ j+posY[k] ][color] >= tl ) {
                                    edges[ i+posX[k] ][ j+posY[k] ][color] = 255;
                                }
                            }catch( Exception e ){
                            }
                        }
                    }
                }
            }
        }

        return edges;
    }

    /* Supresión de no máximos */
    public double [][][] supresion (double [][][] mag, double [][][] dir1 ) {

        int rows = mag[0].length;
        int cols = mag.length;
        System.out.println(rows);
        System.out.println(cols);

        double [][][] dir= new double[rows][cols][3];

        /*for(int i=0; i< rows; i++){
            for (int j = 0; j < cols; j++) {
               for(int color = 0; color < 3; color++) {

                  if((dir1[i][j][color]>=-22.5 && dir1[i][j][color]<22.5)|| (dir1[i][j][color]>=157.5 && dir1[i][j][color]<-157.5))
                      dir[i][j][color]=0;   //borde vertical

                  if((dir1 [i][j][color]>=22.5 && dir1[i][j][color]<67.5) || (dir1[i][j][color]>=-157.5 && dir1[i][j][color]< -112.5))
                      dir[i][j][color]= 45;

                  if((dir1[i][j][color]>=67.5 && dir1 [i][j][color]<112.5) || (dir1 [i][j][color]>= -112.5 && dir1[i][j][color]< -67.5))
                      dir[i][j][color]= 90;

                  if((dir1[i][j][color]>=112.5 && dir1[i][j][color]<157.5) || (dir1[i][j][color]>= -67.5 && dir1[i][j][color]< -22.5))
                      dir[i][j][color]= 135;
                  System.out.println(dir1[i][j][color]);
                  }

            }
        }*/

        for(int i=0; i< rows; i++){
            for (int j = 0; j < cols; j++) {
                for(int color = 0; color < 3; color++) {
                    if((dir1[i][j][color]>-22.5 && dir1[i][j][color]<=22.5)|| (dir1[i][j][color]>157.5 && dir1[i][j][color]<=180)  || (dir1[i][j][color]>=-180 && dir1[i][j][color]<=-157)){
                        dir[i][j][color]=0;
                    } else if((dir1 [i][j][color]>22.5 && dir1[i][j][color]<=67.5) || (dir1[i][j][color]>-157.5 && dir1[i][j][color]<= -112.5)){
                        dir[i][j][color]= 45;
                    } else if((dir1[i][j][color]>67.5 && dir1 [i][j][color]<=112.5) || (dir1 [i][j][color]> -112.5 && dir1[i][j][color]<= -67.5)){
                        dir[i][j][color]= 90;
                    } else if((dir1[i][j][color]>112.5 && dir1[i][j][color]<=157.5) || (dir1[i][j][color]> -67.5 && dir1[i][j][color]<= -22.5)){
                        dir[i][j][color]= 135;
                    }
                }
            }
        }

        double  [][][] gn = new double [ rows ][ cols ][3];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                for(int color = 0; color < 3; color++) {

                  if(i-1>0 && j-1 >0 && i+1 < rows && j+1< cols){
                        if (dir[i][j][color] == 0 ){ // Se trata de un borde vertical
                           if( mag[ i ][ j ][color] < mag[ i ][ j -1 ][color] || mag[ i ][ j ][color] < mag[ i ][ j+1 ][color]){
                               gn[i][j][color]=0;
                           }
                           else {
                            gn[i][j][color]=mag[i][j][color] ;
                           }
                        }
                        else if (dir[i][j][color] == 45){ // Se trata de un borde a 45
                           if( mag[ i ][ j ][color] < mag[ i -1 ][ j +1 ][color] || mag[ i ][ j ][color] < mag[ i +1][ j-1 ][color]){
                               gn[i][j][color]=0;
                           }
                           else {
                            gn[i][j][color]=mag[i][j][color] ;
                           }
                        }
                        else if (dir[i][j][color] == 90) { // Se trata de un borde horizontal
                           if( mag[ i ][ j ][color] < mag[ i -1][ j  ][color] || mag[ i ][ j ][color] < mag[ i +1][ j ][color]){
                               gn[i][j][color]=0;
                           }
                           else {
                            gn[i][j][color]=mag[i][j][color] ;
                           }
                        }
                        else if(dir[i][j][color] == 135) { //Se trata de un borde a -45
                           if( mag[ i ][ j ][color] < mag[ i -1][ j -1 ][color] || mag[ i ][ j ][color] < mag[ i+1 ][ j+1 ][color]){
                               gn[i][j][color]=0;
                           }
                           else {
                            gn[i][j][color]=mag[i][j][color] ;
                           }
                        }
                    }
                    else gn[i][j][color]=mag[i][j][color];


                }
            }
        }
        return gn;
    }

    /* Save image as png in te current directory */
    public void saveImage( String name ){
        try {
            File outputfile = new File( name + "png");
            ImageIO.write(this.Image, "png", outputfile);
        } catch (IOException e) {

        }
    }
}
