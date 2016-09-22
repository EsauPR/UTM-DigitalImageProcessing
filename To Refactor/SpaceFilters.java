import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.StringTokenizer;
import javax.imageio.*;
import javax.imageio.stream.*;
import java.lang.Math;

public class SpaceFilters {

    private BufferedImage DefaultImage;
    private BufferedImage Image;
    private int Width, Height;
    private int MaxRGB, MinRGB;


    SpaceFilters( BufferedImage image ) {
        this.Width = image.getWidth();
        this.Height = image.getHeight();

        this.DefaultImage = new BufferedImage( image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB );
        setAsDefault( image );
        this.Image = new BufferedImage( image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB );
    }

    SpaceFilters( PGM image ) {
        this( image.get_BufferedImage() );
    }

    SpaceFilters( TIFF image ) {
        this( image.get_BufferedImage() );
    }

    SpaceFilters( Dicom image ) {
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

    /* Set the default image to negative image */
    public void toNegative() {
        int R, G, B;
        Color color;

        for( int i = 0; i < this.DefaultImage.getHeight(); i++ ) {
            for( int j = 0; j < this.DefaultImage.getWidth(); j++ ) {
                try{
                    color = new Color( this.DefaultImage.getRGB(i,j) );
                    R = 255 - color.getRed();
                    G = 255 - color.getGreen();
                    B = 255 - color.getBlue();
                    color = new Color(R, G, B);
                    this.Image.setRGB(i, j, color.getRGB() );
                }catch( Exception e ) {

                }
            }
        }
    }

    /* Gamma transformation */
    public void gammaTransformation( double gamma ){
        int R, G, B;
        int maxR, minR, maxG, minG, maxB, minB;
        Color color;
        double scale;

        scale = Math.pow( 255.0, gamma ) / 256;

        /* Scaling image */
        for( int i = 0; i < this.DefaultImage.getHeight(); i++ ) {
            for( int j = 0; j < this.DefaultImage.getWidth(); j++ ) {
                try{
                    color = new Color( this.DefaultImage.getRGB(i,j) );

                    R = (int)(Math.pow( (double)(color.getRed()), gamma ) / scale);
                    G = (int)(Math.pow( (double)(color.getGreen()), gamma ) / scale);
                    B = (int)(Math.pow( (double)(color.getBlue()), gamma ) / scale);

                    color = new Color( R, G, B);
                    this.Image.setRGB(i, j, color.getRGB() );
                }catch( Exception e ) {

                }
            }
        }

    }

    public int [][] getHistogram(){
        Color color;
        int R,G,B;
        int[][] histogram = new int[256][3];

        /* Getting histogram */
        for( int i = 0; i < this.DefaultImage.getHeight(); i++ ) {
            for( int j = 0; j < this.DefaultImage.getWidth(); j++ ) {
                try{
                    color = new Color( this.DefaultImage.getRGB(i,j) );
                    R = color.getRed();
                    G = color.getGreen();
                    B = color.getBlue();
                    histogram[R][0] ++;
                    histogram[G][1] ++;
                    histogram[B][2] ++;
                }catch( Exception e ) {

                }
            }
        }
        return histogram;

    }

    /* Histogram Equalization */
    public void histogramEqualization() {
        Color color;
        int R,G,B;
        int[][] histogram = new int[256][3];

        /* Getting histogram */
        for( int i = 0; i < this.DefaultImage.getHeight(); i++ ) {
            for( int j = 0; j < this.DefaultImage.getWidth(); j++ ) {
                try{
                    color = new Color( this.DefaultImage.getRGB(i,j) );
                    R = color.getRed();
                    G = color.getGreen();
                    B = color.getBlue();
                    histogram[R][0] ++;
                    histogram[G][1] ++;
                    histogram[B][2] ++;
                }catch( Exception e ) {

                }
            }
        }

        /* Histogram equalization */
        for( int i = 0, sumR = 0, sumG = 0, sumB = 0; i < 256; i++ ) {
            sumR += histogram[i][0];
            histogram[i][0] = (int)Math.ceil(( 255.0 * sumR / (double)(this.DefaultImage.getHeight() * this.DefaultImage.getWidth())) - 0.5);

            sumG += histogram[i][1];
            histogram[i][1] = (int)Math.ceil(( 255.0 * sumG / (double)(this.DefaultImage.getHeight() * this.DefaultImage.getWidth())) - 0.5);

            sumB += histogram[i][2];
            histogram[i][2] = (int)Math.ceil(( 255.0 * sumB / (double)(this.DefaultImage.getHeight() * this.DefaultImage.getWidth())) - 0.5);
        }

        /* Setting Histogram to image */
        for( int i = 0; i < this.DefaultImage.getHeight(); i++ ) {
            for( int j = 0; j < this.DefaultImage.getWidth(); j++ ) {
                try{
                    color = new Color( this.DefaultImage.getRGB(i,j) );
                    R = color.getRed();
                    G = color.getGreen();
                    B = color.getBlue();
                    color = new Color( histogram[R][0], histogram[G][1], histogram[B][2] );
                    this.Image.setRGB( i, j, color.getRGB() );
                }catch( Exception e ) {

                }
            }
        }
    }

    /* Verifica Dimensiones de la máscara */
    private boolean checkMaskDimension( int size ) {
        if( size % 2 != 1 || size < 0 )
            return false;
        return true;
    }

    /* Median filter, input => mask dimensions ( width, height ) */
    public void medianFilter( int maskSize ) {
        if( !checkMaskDimension( maskSize ) ) {
            System.out.println( "Invalid mask dimensions" );
            return;
        }

        int[] valuesRGB = new int[ maskSize*maskSize ];

        for( int i = 0; i < this.DefaultImage.getHeight(); i++ ) {
            for( int j = 0; j < this.DefaultImage.getWidth(); j++ ) {
                int cont = 0;
                for( int offsetY = -maskSize; offsetY < maskSize; offsetY++ ) {
                    for( int offsetX = -maskSize; offsetX < maskSize; offsetX++ ) {
                        try{
                            valuesRGB[ cont ] = this.DefaultImage.getRGB( i + offsetY, j + offsetX );
                            cont++;
                        }catch( Exception e ) {
                        }
                    }
                }
                Arrays.sort( valuesRGB, 0, cont );
                try{
                    this.Image.setRGB( i, j, valuesRGB[ cont / 2 ] );
                }catch( Exception e ) {

                }
            }
        }
    }
	/*Max Filter input: None, Output: duble[][][]*/
	int [] getMax( int [][]Neighborhood){

		int R = 0, G = 1, B = 2;
		int max[] = {0,0,0};
		max[R] = Neighborhood[0][R];
		max[G] = Neighborhood[0][G];
		max[B] = Neighborhood[0][B];
		for( int i=0; i<9 ; i++){
			if( max[R] < Neighborhood[i][R] )
				max[R] = Neighborhood[i][R];
			if( max[G] < Neighborhood[i][G] )
				max[G] = Neighborhood[i][G];
			if( max[B] < Neighborhood[i][B] )
				max[B] = Neighborhood[i][B];
		}
		return max;
	}


	public double [][][] maxFilter( ) {

        double [][][] L = new double [ this.Height ][ this.Width ][ 3 ];

        /* canal color */
        int R = 0, G = 1, B = 2;

        /* Computing gradient */
        for( int i = 0; i < this.DefaultImage.getHeight(); i++ ) {
            for( int j = 0; j < this.DefaultImage.getWidth(); j++ ) {

                int [][] tmp = new int[9][3];
                /* Getting values around the pixel */
                for( int offsetY = -1, cont = 0; offsetY <= 1; offsetY++ ) {
                    for( int offsetX = -1; offsetX <= 1; offsetX++, cont++ ) {
                        /* The invalid positions are catched*/
                        try{
                            Color color = new Color( this.DefaultImage.getRGB( i + offsetY, j + offsetX ) );
                            tmp[ cont ][ R ] = color.getRed();
                            tmp[ cont ][ G ] = color.getGreen();
                            tmp[ cont ][ B ] = color.getBlue();
                        }catch( Exception e ) {
                        }
                    }
                }

                /* Multipliying by de mask */
				int []maximos = getMax(tmp);


                L[ i ][ j ][ R ] += maximos[ R ] ;
                L[ i ][ j ][ G ] += maximos[ G ] ;
                L[ i ][ j ][ B ] += maximos[ B ] ;

            }
        }

        /*  Setting into image*/
        int []rgb = {0,0,0};
        for( int i = 0; i < this.Height; i++ ) {
            for( int j = 0; j < this.Width; j++ ) {
                try{
                    Color color = new Color( this.DefaultImage.getRGB(i,j) );
                    rgb[R] = color.getRed();
                    rgb[G] = color.getGreen();
                    rgb[B] = color.getBlue();

                    color = new Color( (int)(L[i][j][R] ), (int)(L[i][j][G] ), (int)(L[i][j][B] ) );
                    this.Image.setRGB( i, j, color.getRGB() );
                }catch( Exception e ) {
                }
            }
        }


        return L;

    }
	/*End MAX Filter*/


	/*Min Filter input: None, Output: duble[][][]*/
	public int [] getMin( int [][]Neighborhood){

		int R = 0, G = 1, B = 2;
		int max[] = {0,0,0};
		max[R] = Neighborhood[0][R];
		max[G] = Neighborhood[0][G];
		max[B] = Neighborhood[0][B];
		for( int i=0; i<9 ; i++){
			if( max[R] > Neighborhood[i][R] )
				max[R] = Neighborhood[i][R];
			if( max[G] > Neighborhood[i][G] )
				max[G] = Neighborhood[i][G];
			if( max[B] > Neighborhood[i][B] )
				max[B] = Neighborhood[i][B];
		}
		return max;
	}


	public double [][][] minFilter( ) {

        double [][][] L = new double [ this.Height ][ this.Width ][ 3 ];

        /* canal color */
        int R = 0, G = 1, B = 2;

        /* Computing gradient */
        for( int i = 0; i < this.DefaultImage.getHeight(); i++ ) {
            for( int j = 0; j < this.DefaultImage.getWidth(); j++ ) {

                int [][] tmp = new int[9][3];
                /* Getting values around the pixel */
                for( int offsetY = -1, cont = 0; offsetY <= 1; offsetY++ ) {
                    for( int offsetX = -1; offsetX <= 1; offsetX++, cont++ ) {
                        /* The invalid positions are catched*/
                        try{
                            Color color = new Color( this.DefaultImage.getRGB( i + offsetY, j + offsetX ) );
                            tmp[ cont ][ R ] = color.getRed();
                            tmp[ cont ][ G ] = color.getGreen();
                            tmp[ cont ][ B ] = color.getBlue();
                        }catch( Exception e ) {
                        }
                    }
                }

                /* Multipliying by de mask */
				int []maximos = getMin(tmp);


                L[ i ][ j ][ R ] += maximos[ R ] ;
                L[ i ][ j ][ G ] += maximos[ G ] ;
                L[ i ][ j ][ B ] += maximos[ B ] ;

            }
        }

        /*  Setting into image*/
        int []rgb = {0,0,0};
        for( int i = 0; i < this.Height; i++ ) {
            for( int j = 0; j < this.Width; j++ ) {
                try{
                    Color color = new Color( this.DefaultImage.getRGB(i,j) );
                    rgb[R] = color.getRed();
                    rgb[G] = color.getGreen();
                    rgb[B] = color.getBlue();

                    color = new Color( (int)(L[i][j][R] ), (int)(L[i][j][G] ), (int)(L[i][j][B] ) );
                    this.Image.setRGB( i, j, color.getRGB() );
                }catch( Exception e ) {
                }
            }
        }


        return L;

    }
	/*End Min Filter*/

	/*Box Filter input: None, Output: duble[][][]*/
	public double [][][] boxFilter( ) {

        /*Mask for Filter */
        int [] mask =	{1, 1, 1, 1, 1, 1, 1, 1, 1};
		int promedio = 0;
		for( int x = 0 ; x < 9 ; x++ ){
			promedio += mask[ x ];
		}
		double peso = (double)1/promedio;

        double [][][] L = new double [ this.Height ][ this.Width ][ 3 ];

        /* canal color */
        int R = 0, G = 1, B = 2;

        /* Computing gradient */
        for( int i = 0; i < this.DefaultImage.getHeight(); i++ ) {
            for( int j = 0; j < this.DefaultImage.getWidth(); j++ ) {

                int [][] tmp = new int[9][3];
                /* Getting values around the pixel */
                for( int offsetY = -1, cont = 0; offsetY <= 1; offsetY++ ) {
                    for( int offsetX = -1; offsetX <= 1; offsetX++, cont++ ) {
                        /* The invalid positions are catched*/
                        try{
                            Color color = new Color( this.DefaultImage.getRGB( i + offsetY, j + offsetX ) );
                            tmp[ cont ][ R ] = color.getRed();
                            tmp[ cont ][ G ] = color.getGreen();
                            tmp[ cont ][ B ] = color.getBlue();
                        }catch( Exception e ) {
                        }
                    }
                }

                /* Multipliying by de mask */
                for( int k = 0; k < 9; k++ ) {
                    L[ i ][ j ][ R ] += (mask[ k ] * tmp[ k ][ R ]) * peso;
                    L[ i ][ j ][ G ] += (mask[ k ] * tmp[ k ][ G ]) * peso;
                    L[ i ][ j ][ B ] += (mask[ k ] * tmp[ k ][ B ]) * peso;
                }
            }
        }

        /*  Setting into image*/
        int []rgb = {0,0,0};
        for( int i = 0; i < this.Height; i++ ) {
            for( int j = 0; j < this.Width; j++ ) {
                try{
                    Color color = new Color( this.DefaultImage.getRGB(i,j) );
                    rgb[R] = color.getRed();
                    rgb[G] = color.getGreen();
                    rgb[B] = color.getBlue();

                    color = new Color( (int)(L[i][j][R] ), (int)(L[i][j][G] ), (int)(L[i][j][B] ) );
                    this.Image.setRGB( i, j, color.getRGB() );
                }catch( Exception e ) {
                }
            }
        }


        return L;

    }
	/*End box Filter*/
	/*Filtro Promedio pesado input: None, Output: duble[][][]*/
	public double [][][] weightyFilter( ) {

        /*Mask for Filter */
        int [] mask =	{1, 2, 1, 2, 4, 2, 1, 2, 1};
		int promedio = 0;
		for( int x = 0 ; x < 9 ; x++ ){
			promedio += mask[ x ];
		}
		double peso = (double)1/promedio;

        double [][][] L = new double [ this.Height ][ this.Width ][ 3 ];

        /* canal color */
        int R = 0, G = 1, B = 2;

        /* Computing gradient */
        for( int i = 0; i < this.DefaultImage.getHeight(); i++ ) {
            for( int j = 0; j < this.DefaultImage.getWidth(); j++ ) {

                int [][] tmp = new int[9][3];
                /* Getting values around the pixel */
                for( int offsetY = -1, cont = 0; offsetY <= 1; offsetY++ ) {
                    for( int offsetX = -1; offsetX <= 1; offsetX++, cont++ ) {
                        /* The invalid positions are catched*/
                        try{
                            Color color = new Color( this.DefaultImage.getRGB( i + offsetY, j + offsetX ) );
                            tmp[ cont ][ R ] = color.getRed();
                            tmp[ cont ][ G ] = color.getGreen();
                            tmp[ cont ][ B ] = color.getBlue();
                        }catch( Exception e ) {
                        }
                    }
                }

                /* Multipliying by de mask */
                for( int k = 0; k < 9; k++ ) {
                    L[ i ][ j ][ R ] += (mask[ k ] * tmp[ k ][ R ]) * peso;
                    L[ i ][ j ][ G ] += (mask[ k ] * tmp[ k ][ G ]) * peso;
                    L[ i ][ j ][ B ] += (mask[ k ] * tmp[ k ][ B ]) * peso;
                }
            }
        }

        /*  Setting into image*/
        int []rgb = {0,0,0};
        for( int i = 0; i < this.Height; i++ ) {
            for( int j = 0; j < this.Width; j++ ) {
                try{
                    Color color = new Color( this.DefaultImage.getRGB(i,j) );
                    rgb[R] = color.getRed();
                    rgb[G] = color.getGreen();
                    rgb[B] = color.getBlue();

                    color = new Color( (int)(L[i][j][R] ), (int)(L[i][j][G] ), (int)(L[i][j][B] ) );
                    this.Image.setRGB( i, j, color.getRGB() );
                }catch( Exception e ) {
                }
            }
        }


        return L;

    }
	/*End box Filter*/


    /* Gradient Filter  */
    public double [][][] gradientFilter( ) {
        /* Mask for Gradient */
        int [] maskX = {-1, 0, 1, -2, 0, 2, -1, 0, 1 };
        int [] maskY = {-1, -2, -1, 0, 0, 0, 1, 2, 1 };

        /* canal color */
        int R = 0, G = 1, B = 2;

        double [][][] GX = new double [ this.Height ][ this.Width ][ 3 ];
        double [][][] GY = new double [ this.Height ][ this.Width ][ 3 ];

        /* Computing gradient */
        for( int i = 0; i < this.DefaultImage.getHeight(); i++ ) {
            for( int j = 0; j < this.DefaultImage.getWidth(); j++ ) {

                int [][] tmp = new int[9][3];

                /* Getting values around the pixel */
                for( int offsetY = -1, cont = 0; offsetY <= 1; offsetY++ ) {
                    for( int offsetX = -1; offsetX <= 1; offsetX++, cont++ ) {
                        /* The invalid positions are catched*/
                        try{
                            Color color = new Color( this.DefaultImage.getRGB( i + offsetY, j + offsetX ) );
                            tmp[ cont ][ R ] = color.getRed();
                            tmp[ cont ][ G ] = color.getGreen();
                            tmp[ cont ][ B ] = color.getBlue();
                        }catch( Exception e ) {
                        }
                    }
                }

                /* Multipliying by de mask */
                for( int k = 0; k < 9; k++ ) {
                    GX[ i ][ j ][ R ] += maskX[ k ] * tmp[ k ][ R ];
                    GX[ i ][ j ][ G ] += maskX[ k ] * tmp[ k ][ G ];
                    GX[ i ][ j ][ B ] += maskX[ k ] * tmp[ k ][ B ];

                    GY[ i ][ j ][ R ] += maskY[ k ] * tmp[ k ][ R ];
                    GY[ i ][ j ][ G ] += maskY[ k ] * tmp[ k ][ G ];
                    GY[ i ][ j ][ B ] += maskY[ k ] * tmp[ k ][ B ];
                }
            }
        }

        /* Gradient magnitude, result in GX*/
        for( int i = 0; i < this.DefaultImage.getHeight(); i++ ) {
            for( int j = 0; j < this.DefaultImage.getWidth(); j++ ) {
                GX[ i ][ j ][R] = Math.pow( (double)( GX[i][j][R] * GX[i][j][R] + GY[i][j][R] * GY[i][j][R] ), 0.5 );
                GX[ i ][ j ][G] = Math.pow( (double)( GX[i][j][G] * GX[i][j][G] + GY[i][j][G] * GY[i][j][G] ), 0.5 );
                GX[ i ][ j ][B] = Math.pow( (double)( GX[i][j][B] * GX[i][j][B] + GY[i][j][B] * GY[i][j][B] ), 0.5 );
            }
        }

        /* Setting into image */
        for( int i = 0; i < this.Height; i++ ) {
            for( int j = 0; j < this.Width; j++ ) {
                try{
                    Color color = new Color( (int)GX[i][j][R], (int)GX[i][j][G], (int)GX[i][j][B] );
                    this.Image.setRGB( i, j, color.getRGB() );
                }catch( Exception e ) {
                }
            }
        }

        /* !!!!! Check if is neccesary to scale the imagen  !!!!*/
        return GX;
    }

    /* Direccion Gradiente  */
    public double [][][] gradientDirection( ) {
        /* Mask for Gradient */
        int [] maskX = {-1, 0, 1, -2, 0, 2, -1, 0, 1 };
        int [] maskY = {-1, -2, -1, 0, 0, 0, 1, 2, 1 };

        /* canal color */
        int R = 0, G = 1, B = 2;

        double [][][] GX = new double [ this.Height ][ this.Width ][ 3 ];
        double [][][] GY = new double [ this.Height ][ this.Width ][ 3 ];

        /* Computing gradient */
        for( int i = 0; i < this.DefaultImage.getHeight(); i++ ) {
            for( int j = 0; j < this.DefaultImage.getWidth(); j++ ) {

                int [][] tmp = new int[9][3];

                /* Getting values around the pixel */
                for( int offsetY = -1, cont = 0; offsetY <= 1; offsetY++ ) {
                    for( int offsetX = -1; offsetX <= 1; offsetX++, cont++ ) {
                        /* The invalid positions are catched*/
                        try{
                            Color color = new Color( this.DefaultImage.getRGB( i + offsetY, j + offsetX ) );
                            tmp[ cont ][ R ] = color.getRed();
                            tmp[ cont ][ G ] = color.getGreen();
                            tmp[ cont ][ B ] = color.getBlue();
                        }catch( Exception e ) {
                        }
                    }
                }

                /* Multipliying by de mask */
                for( int k = 0; k < 9; k++ ) {
                    GX[ i ][ j ][ R ] += maskX[ k ] * tmp[ k ][ R ];
                    GX[ i ][ j ][ G ] += maskX[ k ] * tmp[ k ][ G ];
                    GX[ i ][ j ][ B ] += maskX[ k ] * tmp[ k ][ B ];

                    GY[ i ][ j ][ R ] += maskY[ k ] * tmp[ k ][ R ];
                    GY[ i ][ j ][ G ] += maskY[ k ] * tmp[ k ][ G ];
                    GY[ i ][ j ][ B ] += maskY[ k ] * tmp[ k ][ B ];
                }
            }
        }

        double aRadR, aRadG, aRadB;
        double [][][] alfa = new double [ this.Height ][ this.Width ][ 3 ];
        double [][][] direccion = new double [ this.Height ][ this.Width ][ 3 ];
        /* Gradient magnitude, result in GX*/
        for( int i = 0; i < this.DefaultImage.getHeight(); i++ ) {
            for( int j = 0; j < this.DefaultImage.getWidth(); j++ ) {
                aRadR = Math.atan2(GX[i][j][R] * GX[i][j][R], GY[i][j][R] * GY[i][j][R]);
                aRadG = Math.atan2(GX[i][j][G] * GX[i][j][G], GY[i][j][G] * GY[i][j][G]);
                aRadB = Math.atan2(GX[i][j][B] * GX[i][j][B], GY[i][j][B] * GY[i][j][B]);

                alfa[i][j][0] = Math.toDegrees(aRadR);
                alfa[i][j][1] = Math.toDegrees(aRadG);
                alfa[i][j][2] = Math.toDegrees(aRadB);

                direccion[i][j][0] = 90 - alfa[i][j][0];
                direccion[i][j][1] = 90 - alfa[i][j][1];
                direccion[i][j][2] = 90 - alfa[i][j][2];
            }
        }

        return direccion;
    }

    /* Laplacian filter */
    public double [][][] laplacianFilter( int typeMask ) {
        if( typeMask > 3 || typeMask < 1 ) {
            System.out.println( "Invalid Mask type" );
            //return 1;
        }
        /*Three Mask for Gradient */
        int [][] mask =    {   {0, 1, 0, 1, -4, 1, 0, 1, 0},
                                {1, 1, 1, 1, -8, 1, 1, 1, 1,},
                                {-1, -1, -1, -1, 8, -1, -1, -1, -1}
                            };
        double [][][] L = new double [ this.Height ][ this.Width ][ 3 ];

        /* canal color */
        int R = 0, G = 1, B = 2;

        /* Computing gradient */
        for( int i = 0; i < this.DefaultImage.getHeight(); i++ ) {
            for( int j = 0; j < this.DefaultImage.getWidth(); j++ ) {

                int [][] tmp = new int[9][3];
                /* Getting values around the pixel */
                for( int offsetY = -1, cont = 0; offsetY <= 1; offsetY++ ) {
                    for( int offsetX = -1; offsetX <= 1; offsetX++, cont++ ) {
                        /* The invalid positions are catched*/
                        try{
                            Color color = new Color( this.DefaultImage.getRGB( i + offsetY, j + offsetX ) );
                            tmp[ cont ][ R ] = color.getRed();
                            tmp[ cont ][ G ] = color.getGreen();
                            tmp[ cont ][ B ] = color.getBlue();
                        }catch( Exception e ) {
                        }
                    }
                }

                /* Multipliying by de mask */
                for( int k = 0; k < 9; k++ ) {
                    L[ i ][ j ][ R ] += mask[ typeMask - 1 ][ k ] * tmp[ k ][ R ];
                    L[ i ][ j ][ G ] += mask[ typeMask - 1 ][ k ] * tmp[ k ][ G ];
                    L[ i ][ j ][ B ] += mask[ typeMask - 1 ][ k ] * tmp[ k ][ B ];
                }
            }
        }

        /*  Setting into image
            L must be added to the image to see changes, but L es the result of the Laplacian
         */
        int []rgb = {0,0,0};
        for( int i = 0; i < this.Height; i++ ) {
            for( int j = 0; j < this.Width; j++ ) {
                try{
                    Color color = new Color( this.DefaultImage.getRGB(i,j) );
                    rgb[R] = color.getRed();
                    rgb[G] = color.getGreen();
                    rgb[B] = color.getBlue();

                    color = new Color( (int)(L[i][j][R] + rgb[R]), (int)(L[i][j][G] + rgb[G]), (int)(L[i][j][B] + rgb[B]) );
                    this.Image.setRGB( i, j, color.getRGB() );
                }catch( Exception e ) {
                }
            }
        }


        return L;

    }

    public void gradientLaplacianFilter(){
        double [][][] magnitudGradiente = new double [ this.Height ][ this.Width ][ 3 ];
        double [][][] laplaciano =  new double [ this.Height ][ this.Width ][ 3 ];
        double [][][] mask =  new double [ this.Height ][ this.Width ][ 3 ];

        /* canal color */
        int R = 0, G = 1, B = 2;

        //Obtener la magnitud del gradiente
        magnitudGradiente = gradientFilter();

        //Suavizar la imagen del gradiente
        //magnitudGradiente = suavizado(magnitudGradiente);


        //Multiplicar la magnitud suavizada del gradient por el laplaciano
        laplaciano = laplacianFilter(1);

         for( int i = 0; i < this.DefaultImage.getHeight(); i++ ) {
            for( int j = 0; j < this.DefaultImage.getWidth(); j++ ) {

                mask[ i ][ j ][R] = (double)( magnitudGradiente[i][j][R] * laplaciano[i][j][R] );
                mask[ i ][ j ][G] = (double)( magnitudGradiente[i][j][G] * laplaciano[i][j][G] );
                mask[ i ][ j ][B] = (double)( magnitudGradiente[i][j][B] * laplaciano[i][j][B] );

            }
        }

        /*  Setting into image
            mask  must be added to the image to see changes.
         */
        int []rgb = {0,0,0};
        for( int i = 0; i < this.Height; i++ ) {
            for( int j = 0; j < this.Width; j++ ) {
                try{
                    Color color = new Color( this.DefaultImage.getRGB(i,j) );
                    rgb[R] = color.getRed();
                    rgb[G] = color.getGreen();
                    rgb[B] = color.getBlue();

                    color = new Color( (int)(mask[i][j][R] + rgb[R]), (int)(mask[i][j][G] + rgb[G]), (int)(mask[i][j][B] + rgb[B]) );
                    this.Image.setRGB( i, j, color.getRGB() );
                }catch( Exception e ) {
                }
            }
        }

    }


public void otsu( ){
    //Calcular el historial normalizado
    Color color;
    int R,G,B;
    int[][] histogram = new int[256][3];
    double[][] histogramNormalizado = new double[256][3];
    int nivelIntensidad = 255;
    /* Getting histogram */
     for( int i = 0; i < this.DefaultImage.getHeight(); i++ ) {
            for( int j = 0; j < this.DefaultImage.getWidth(); j++ ) {
                try{
                    color = new Color( this.DefaultImage.getRGB(i,j) );
                    R = color.getRed();
                    G = color.getGreen();
                    B = color.getBlue();
                    histogram[R][0] ++;
                    histogram[G][1] ++;
                    histogram[B][2] ++;
                }catch( Exception e ) {

                }
            }
        }

    //1.- Calcular el historial normalizado
    for(int i = 0; i < 256; i++) {
        histogramNormalizado[i][0] = (double) histogram[i][0] / (this.DefaultImage.getWidth() * this.DefaultImage.getHeight());
        histogramNormalizado[i][1] = (double) histogram[i][1] / (this.DefaultImage.getWidth() * this.DefaultImage.getHeight());
        histogramNormalizado[i][2] = (double) histogram[i][2] / (this.DefaultImage.getWidth() * this.DefaultImage.getHeight());
    }
    //2.- Calcular la suma acumulada
    //Valores para almacenar la suma acumulada

    double[] sumR = new double [256];
    double[] sumG = new double [256];
    double[] sumB = new double [256];
    double[][] pI = new double [256][3];

    double pr = 0;
    double pg = 0;
    double pb = 0;
    for (int i = 0; i < 256; i++) {
        pr = 0;
        pg = 0;
        pb = 0;
        for (int j = 0; j <= i; j++ ) {
            pr = pr +  histogramNormalizado[j][0];
            pg = pg +  histogramNormalizado[j][1];
            pb = pb +  histogramNormalizado[j][2];
        }

        pI[i][0] = pr;
        pI[i][1] = pg;
        pI[i][2] = pb;
    }

    //3.- Calcular la media acumulada
    double[][] media = new double [256][3];
    double mr,mg,mb;

    for (int i = 0; i < 256 ; i++) {
        mr = 0;
        mg = 0;
        mb = 0;

        for (int j = 0; j <=i ; j++) {
            mr = mr * pI[j][0];
            mg = mg * pI[j][1];
            mb = mb * pI[j][2];
        }

        media [i][0] = mr;
        media [i][1] = mg;
        media [i][2] = mb;
    }

  //4 Calcular la media global
     double mediaGlobalR = 0;
     double mediaGlobalG = 0;
     double mediaGlobalB = 0;


     for (int i = 0; i < 256; i++) {
         mediaGlobalR = mediaGlobalR + media[i][0];
         mediaGlobalG = mediaGlobalG + media[i][1];
         mediaGlobalB = mediaGlobalB + media[i][2];
     }
 // 5.- Calcular la varianza entre clases

    double[][] sigma2B = new double[256][3];
    for (int i = 0; i < 256 ; i ++) {
        sigma2B [i][0] = ((mediaGlobalR * pI[i][0] - media[i][0]) * (mediaGlobalR * pI[i][0] - media[i][0])) / ( pI[i][0] * (1 - pI[i][0]));
        sigma2B [i][1] = ((mediaGlobalG * pI[i][1] - media[i][1]) * (mediaGlobalG * pI[i][1] - media[i][1])) / ( pI[i][1] * (1 - pI[i][1]));
        sigma2B [i][2] = ((mediaGlobalB * pI[i][2] - media[i][2]) * (mediaGlobalB * pI[i][2] - media[i][2])) / ( pI[i][2] * (1 - pI[i][2]));
    }

 // 6.- Obtener el valor de umbral de Otsu, K ∗ , como el valor de k que
    int numkr = 0;
    int numkg = 0;
    int numkb = 0;

    double maxr = 0;
    double maxg = 0;
    double maxb = 0;

    int[] kr = new int [256];
    int[] kg = new int [256];
    int[] kb = new int [256];

 //Obtener el maximo
    for (int i = 0; i < 256; i++) {

        if(maxr <= sigma2B[i][0])
            maxr = sigma2B[i][0];

        if(maxg <= sigma2B[i][1])
            maxg = sigma2B[i][1];

        if(maxb <= sigma2B[i][2])
            maxb =  sigma2B[i][2];
    }
 //cuantas veces se encuentra el max y guardar la posicion
    for (int i = 0; i< 256; i++ ) {
        if (sigma2B[i][0] == maxr){

            kr[numkr] = i;
            numkr ++;

        }
        if (sigma2B[i][1] == maxg){

            kg[numkg] = i;
            numkg ++;

        }
        if (sigma2B[i][2] == maxb){

            kb[numkb] = i;
            numkb ++;

        }

    }
 //Verificar si k fue el unico, sino promediar
    int temp = 0;

    int k0 = 0; //R
    int k1 = 0; //G
    int k2 = 0; //B


    if (numkr > 1){
      //Hay que promediar los krs
      for (int i = 0; i < numkr ; i++) {
          temp = temp + kr[i];
        }
        k0 = temp / numkr;
    }

    else {
        k0 = kr[0];
    }
    temp = 0;

    if (numkg > 1){
      //Hay que promediar los krs
      for (int i = 0; i < numkg ; i++) {
          temp = temp + kg[i];
        }
        k1 = temp / numkg;
    }

    else {
        k1 = kg[0];
    }

    temp = 0;

    if (numkb > 1){
      //Hay que promediar los krs
      for (int i = 0; i < numkb ; i++) {
          temp = temp + kb[i];
        }
        k2 = temp / numkb;
    }

    else {
        k2 = kb[0];
    }
 //7- Obtener la imagen umbralizada(segmentada)
    /*g(x,y) = 1 if f(x,y) <= k
             = 0 de otra forma
              */
    int []rgb = {0,0,0};
    int gxr, gxg, gxb;
        for( int i = 0; i < this.DefaultImage.getHeight(); i++ ) {
            for( int j = 0; j < this.DefaultImage.getWidth(); j++ ) {
                try{
                    color = new Color( this.DefaultImage.getRGB(i,j) );
                    rgb[0] = color.getRed();
                    rgb[1] = color.getGreen();
                    rgb[2] = color.getBlue();

                    if (rgb[0] <= k0)
                        gxr = 255;
                    else
                        gxr = 0;

                    if (rgb[1] <= k1)
                        gxg = 255;
                    else
                        gxg = 0;

                    if (rgb[2] <= k2)
                        gxb = 255;
                    else
                        gxb = 0;

                    color = new Color( (int)(gxr), (int)(gxg), (int)(gxb) );


                    this.Image.setRGB( i, j, color.getRGB() );
                }catch( Exception e ) {
                }
            }
        } }

    /* Save image as png in te current directory */
    public void saveImage( String name ){
        try {
            File outputfile = new File( name + "png");
            ImageIO.write(this.Image, "png", outputfile);
        } catch (IOException e) {

        }
    }
}
