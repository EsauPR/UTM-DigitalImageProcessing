import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.StringTokenizer;
import javax.imageio.*;
import javax.imageio.stream.*;
import java.lang.Math;


public class Momentos_Invariantes_Hu {

    private BufferedImage DefaultImage;
    private BufferedImage Image;
    private int Width, Height;
    private int MaxRGB, MinRGB;


    Momentos_Invariantes_Hu( BufferedImage image ) {
        this.Width = image.getWidth();
        this.Height = image.getHeight();

        this.DefaultImage = new BufferedImage( image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB );
        setAsDefault( image );
        this.Image = new BufferedImage( image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB );
    }

    Momentos_Invariantes_Hu( PGM image ) {
        this( image.get_BufferedImage() );
    }

    Momentos_Invariantes_Hu( TIFF image ) {
        this( image.get_BufferedImage() );
    }

    Momentos_Invariantes_Hu( Dicom image ) {
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

    public rgbDobles [] hu(BufferedImage Original){
    /*
        ψ 1 = η 20 + η 02
        ψ 2 = (η 20 − η 02 ) 2 + 4η 211
        ψ 3 = (η 30 − 3η 12 ) 2 + (3η 21 − η 03 ) 2
        ψ 4 = (η 30 + η 12 ) 2 + (η 21 + η 03 ) 2
        ψ 5 = (η 30 − 3η 12 )(η 30 + η 12 ) (η 30 + η 12 ) 2 − 3(η 21 + η 03 ) 2 +
                (3η 21 − η 03 )(η 21 + η 03 )) (3η 30 + η 12 ) 2 − (η 21 + η 03 ) 2
        ψ 6 = (η 20 −η 02 ) (η 30 + η 12 ) 2 − (η 21 + η 03 ) 2 +(4η 11 )(η 30 +η 12 )(η 21 +η 03 )
        ψ 7 = (3η 21 − η 03 )(η 30 + η 12 ) (η 30 + η 12 ) 2 − 3(η 21 + η 03 ) 2 +
                (3η 12 − η 03 )(η 21 + η 03 ) (3η 30 + η 12 ) 2 − (η 21 + η 03 ) 2
                

        mpq = sum x sum y (x^p * y^q  f(x,y) )
        upq = sum x sum y (x - (X barra))^p * (y - (Y barra))^q * f(x,y)

        X barra = m10 /m00
        Y barra = m01 /m00

        npq = upq / m00^sigma
        sigma = (p+q)/2  +1 
    */
        int R, G, B, ROriginal, GOriginal, BOriginal;
        Color color;
        double xbarra = 0, ybarra = 0;
        double sigma = 0;
        rgbDobles [] momentosHu = new rgbDobles[7];

        int height = this.DefaultImage.getHeight();
        int width = this.DefaultImage.getWidth();
        int sumR=0, sumG=0, sumB=0;
        
       
        rgb init = new rgb(0,0,0);
        m m = new m(init,init,init,init,init,init,init,init,init,init);
        rgb F = new rgb(0,0,0);
        rgb sumX = new rgb(0,0,0);
        rgb sumX2 = new rgb(0,0,0);
        rgb sumY = new rgb(0,0,0);
        rgb sumY2 = new rgb(0,0,0);
        BufferedImage X = this.DefaultImage;
        BufferedImage Y = this.DefaultImage;
        //Obtener SUMATORIAS F
        for( int i = 0; i < this.DefaultImage.getHeight(); i++ ) {
            for( int j = 0; j < this.DefaultImage.getWidth(); j++ ) {
                try{
                   color = new Color( this.DefaultImage.getRGB(i,j) );
                    R = color.getRed();
                    G = color.getGreen();
                    B = color.getBlue();


                    if(R == 255 && G == 255 && B ==255){
                        color = new Color( Original.getRGB(i,j) );
                        ROriginal = color.getRed();
                        GOriginal = color.getGreen();
                        BOriginal = color.getBlue();
                        sumR = sumR + ROriginal;
                        sumG = sumG + GOriginal;
                        sumB = sumB + BOriginal;     
                    }
                }catch( Exception e ) {

                }
            }
        }

        ArrayList<Integer> arrayX = new ArrayList<Integer>();
        ArrayList<Integer> arrayY = new ArrayList<Integer>();
        ArrayList<Integer> arrayX2 = new ArrayList<Integer>();
        ArrayList<Integer> arrayY2 = new ArrayList<Integer>();

        for (int x=0; x < this.DefaultImage.getHeight(); x++) {
            for (int y=0; y < this.DefaultImage.getWidth(); y++) {
                    color = new Color( this.DefaultImage.getRGB(x,y) );
                    R = color.getRed();
                    G = color.getGreen();
                    B = color.getBlue();


                    if(R == 255 && G == 255 && B ==255){
                        arrayX.add(x); 
                        arrayY.add(y); 
                        arrayX2.add( x * x);
                        arrayY2.add( y * y);

                    }            
            }            
        }
      

        ArrayList<rgb> arrayF = new ArrayList<rgb>();
        rgb valor = new rgb(0,0,0);
        for (int x=0; x < this.DefaultImage.getHeight(); x++) {
            for (int y=0; y < this.DefaultImage.getWidth(); y++) {
                    color = new Color( this.DefaultImage.getRGB(x,y) );
                    R = color.getRed();
                    G = color.getGreen();
                    B = color.getBlue();
                    if(R == 255 && G == 255 && B ==255){
                        color = new Color( Original.getRGB(x,y) );
                        R = color.getRed();
                        G = color.getGreen();
                        B = color.getBlue();

                        valor.setR(R);
                        valor.setG(G);
                        valor.setB(B);
                        arrayF.add(valor); 

                    }                
            }            
        }

        //Calculo de M
        //M00

        F.setR(sumR);
        F.setG(sumG);
        F.setB(sumB);        

        m.setM00(F);

        //M10
        int auxR = 0, auxG=0, auxB=0;
        rgb aux = new rgb(0,0,0);
        for( int i = 0; i < arrayX.size(); i++ ) {

            /*
            System.out.println("======================");
            System.out.println("AX" + arrayX.get(i) + "F" + arrayF.get(i).getR() );
            System.out.println("AX" + arrayX.get(i) + "F" + arrayF.get(i).getG() );
            System.out.println("AX" + arrayX.get(i) + "F" + arrayF.get(i).getB() );
            System.out.println("======================");*/
            auxR = auxR + (arrayX.get(i) * arrayF.get(i).getR());
            auxG = auxG + (arrayX.get(i) * arrayF.get(i).getG());
            auxB = auxB + (arrayX.get(i) * arrayF.get(i).getB());
        }

        aux.setR(auxR);
        aux.setG(auxG);
        aux.setB(auxB);
        
        m.setM10(aux);

        //M01
        auxR = 0; auxG=0; auxB=0;
        aux.setR(0);
        aux.setG(0);
        aux.setB(0);

        for( int i = 0; i < arrayY.size() ; i++ ) {
            auxR = auxR + (arrayY.get(i) * arrayF.get(i).getR());
            auxG = auxG + (arrayY.get(i) * arrayF.get(i).getG());
            auxB = auxB + (arrayY.get(i) * arrayF.get(i).getB());
        }
        aux.setR(auxR);
        aux.setG(auxG);
        aux.setB(auxB);

        m.setM01(aux);

        //M11
        auxR = 0; auxG=0; auxB=0;
        aux.setR(0);
        aux.setG(0);
        aux.setB(0);

        for( int i = 0; i < arrayX.size() ; i++ ) {
            auxR = auxR + ( arrayX.get(i) * arrayY.get(i) * arrayF.get(i).getR());
            auxG = auxG + ( arrayX.get(i) * arrayY.get(i) * arrayF.get(i).getG());
            auxB = auxB + ( arrayX.get(i) * arrayY.get(i) * arrayF.get(i).getB());
        }
        aux.setR(auxR);
        aux.setG(auxG);
        aux.setB(auxB);

        m.setM11(aux);

        //M20
        auxR = 0; auxG=0; auxB=0;
        aux.setR(0);
        aux.setG(0);
        aux.setB(0);

        for( int i = 0; i < arrayX.size() ; i++ ) {
            auxR = auxR + ( arrayX2.get(i) *  arrayF.get(i).getR());
            auxG = auxG + ( arrayX2.get(i) *  arrayF.get(i).getG());
            auxB = auxB + ( arrayX2.get(i) *  arrayF.get(i).getB());
        }
        aux.setR(auxR);
        aux.setG(auxG);
        aux.setB(auxB);
        m.setM20(aux);

        //M02
        auxR = 0; auxG=0; auxB=0;
        aux.setR(0);
        aux.setG(0);
        aux.setB(0);
        for( int i = 0; i < arrayY.size() ; i++ ) {
            auxR = auxR + ( arrayY2.get(i)*  arrayF.get(i).getR());
            auxG = auxG + ( arrayY2.get(i) *  arrayF.get(i).getG());
            auxB = auxB + ( arrayY2.get(i) *  arrayF.get(i).getB());
        }
        aux.setR(auxR);
        aux.setG(auxG);
        aux.setB(auxB);
        m.setM02(aux);

        //M30
        auxR = 0; auxG=0; auxB=0;
        aux.setR(0);
        aux.setG(0);
        aux.setB(0);
        for( int i = 0; i < arrayY.size() ; i++ ) {
            auxR = auxR + ( (arrayX2.get(i) * arrayX.get(i)) *  arrayF.get(i).getR());
            auxG = auxG + ( (arrayX2.get(i) * arrayX.get(i)) *  arrayF.get(i).getG());
            auxB = auxB + ( (arrayX2.get(i) * arrayX.get(i)) *  arrayF.get(i).getB());
        }
        aux.setR(auxR);
        aux.setG(auxG);
        aux.setB(auxB);
        m.setM30(aux);

        //M03
        auxR = 0; auxG=0; auxB=0;
        aux.setR(0);
        aux.setG(0);
        aux.setB(0);
        for( int i = 0; i < arrayY.size(); i++ ) {
            auxR = auxR + ( (arrayY2.get(i) * arrayY.get(i)) *  arrayF.get(i).getR());
            auxG = auxG + ( (arrayY2.get(i) * arrayY.get(i)) *  arrayF.get(i).getG());
            auxB = auxB + ( (arrayY2.get(i) * arrayY.get(i)) *  arrayF.get(i).getB());
        }
        aux.setR(auxR);
        aux.setG(auxG);
        aux.setB(auxB);
        m.setM03(aux);

        //M12
        auxR = 0; auxG=0; auxB=0;
        aux.setR(0);
        aux.setG(0);
        aux.setB(0);
        for( int i = 0; i < arrayY.size() ; i++ ) {
            auxR = auxR + ( (arrayX.get(i) * arrayY2.get(i)) *  arrayF.get(i).getR());
            auxG = auxG + ( (arrayX.get(i) * arrayY2.get(i)) *  arrayF.get(i).getG());
            auxB = auxB + ( (arrayX.get(i) * arrayY2.get(i)) *  arrayF.get(i).getB());
        }
        aux.setR(auxR);
        aux.setG(auxG);
        aux.setB(auxB);
        m.setM12(aux);

        //M21
        auxR = 0; auxG=0; auxB=0;
        aux.setR(0);
        aux.setG(0);
        aux.setB(0);
        for( int i = 0; i < arrayY.size() ; i++ ) {
            auxR = auxR + ( (arrayX2.get(i) * arrayY.get(i)) *  arrayF.get(i).getR());
            auxG = auxG + ( (arrayX2.get(i) * arrayY.get(i)) *  arrayF.get(i).getG());
            auxB = auxB + ( (arrayX2.get(i) * arrayY.get(i)) *  arrayF.get(i).getB());
        }
        aux.setR(auxR);
        aux.setG(auxG);
        aux.setB(auxB);
        m.setM21(aux);

        //Obtener n--------------------------------------------------
        double xbarraR =0, ybarraR =0;
        double xbarraG =0, ybarraG =0;
        double xbarraB =0, ybarraB =0;

        rgb m00 = m.getM00();
        rgb m10 = m.getM10();
        rgb m01 = m.getM01();
        //Calcular Xbara y Ybarra
        xbarraR = m10.getR() / m00.getR() ; 
        xbarraG = m10.getG() / m00.getG(); 
        xbarraB = m10.getB() / m00.getB(); 

        ybarraR = m01.getR() / m00.getR(); 
        ybarraG = m01.getG() / m00.getG(); 
        ybarraB = m01.getB() / m00.getB();

        rgbDobles n11 = new rgbDobles(0,0,0);
        rgbDobles n20 = new rgbDobles(0,0,0);
        rgbDobles n02 = new rgbDobles(0,0,0);
        rgbDobles n30 = new rgbDobles(0,0,0);
        rgbDobles n03 = new rgbDobles(0,0,0);
        rgbDobles n21 = new rgbDobles(0,0,0);
        rgbDobles n12 = new rgbDobles(0,0,0);

        n N = new n(n11,n20,n02,n30,n03,n21,n12);

        double auxDR = 0, auxDG=0, auxDB=0;
        rgbDobles auxDoble = new rgbDobles(0,0,0);
        auxDoble.setR(0);
        auxDoble.setG(0);
        auxDoble.setB(0);

        auxDR = (m.getM11().getR() - ybarraR * m.getM10().getR()) / (m.getM00().getR()  *   m.getM00().getR() );
        auxDG = (m.getM11().getG() - ybarraG * m.getM10().getG()) / (m.getM00().getG()  *   m.getM00().getG() );
        auxDB = (m.getM11().getB() - ybarraB * m.getM10().getB()) / (m.getM00().getB()  *   m.getM00().getB() );
        auxDoble.setR(auxDR);
        auxDoble.setG(auxDG);
        auxDoble.setB(auxDB);
        N.setN11(auxDoble); 

        //N20
        auxDR = 0; auxDG=0; auxDB=0;
        

        auxDR = (m.getM20().getR() - xbarraR * m.getM10().getR()) / (m.getM00().getR()  *   m.getM00().getR() );
        auxDG = (m.getM20().getG() - xbarraG * m.getM10().getG()) / (m.getM00().getG()  *   m.getM00().getG() );
        auxDB = (m.getM20().getB() - xbarraB * m.getM10().getB()) / (m.getM00().getB()  *   m.getM00().getB() );
        auxDoble.setR(auxDR);
        auxDoble.setG(auxDG);
        auxDoble.setB(auxDB);
        N.setN20(auxDoble);
        //N02
        
        auxDR = (m.getM02().getR() - ybarraR * m.getM01().getR()) / (m.getM00().getR()  *   m.getM00().getR() );
        auxDG = (m.getM02().getG() - ybarraG * m.getM01().getG()) / (m.getM00().getG()  *   m.getM00().getG() );
        auxDB = (m.getM02().getB() - ybarraB * m.getM01().getB()) / (m.getM00().getB()  *   m.getM00().getB() );
        auxDoble.setR(auxDR);
        auxDoble.setG(auxDG);
        auxDoble.setB(auxDB);
        N.setN02(auxDoble);

        //N30
        auxDR = (m.getM30().getR() - 3 * xbarraR * m.getM20().getR() + 2 * Math.pow(xbarraR,2) * m.getM10().getR()) / Math.pow(m.getM00().getR() , 2.5);
        auxDG = (m.getM30().getG() - 3 * xbarraG * m.getM20().getG() + 2 * Math.pow(xbarraG,2) * m.getM10().getG()) / Math.pow(m.getM00().getG() , 2.5);
        auxDB = (m.getM30().getB() - 3 * xbarraB * m.getM20().getB() + 2 * Math.pow(xbarraB,2) * m.getM10().getB()) / Math.pow(m.getM00().getB() , 2.5);
        auxDoble.setR(auxDR);
        auxDoble.setG(auxDG);
        auxDoble.setB(auxDB);
        N.setN30(auxDoble);

         //N03
        auxDR = (m.getM03().getR() - 3 * ybarraR * m.getM02().getR() + 2 * Math.pow(ybarraR,2) * m.getM01().getR()) / Math.pow(m.getM00().getR() , 2.5);
        auxDG = (m.getM03().getG() - 3 * ybarraG * m.getM02().getG() + 2 * Math.pow(ybarraG,2) * m.getM01().getG()) / Math.pow(m.getM00().getG() , 2.5);
        auxDB = (m.getM03().getB() - 3 * ybarraB * m.getM02().getB() + 2 * Math.pow(ybarraB,2) * m.getM01().getB()) / Math.pow(m.getM00().getB() , 2.5);
        auxDoble.setR(auxDR);
        auxDoble.setG(auxDG);
        auxDoble.setB(auxDB);
        N.setN03(auxDoble);

         //N21
        auxDR = (m.getM21().getR() - 2 * xbarraR * m.getM11().getR() - ybarraR  * m.getM20().getR() + 2 * Math.pow(xbarraR,2) * m.getM01().getR()) / Math.pow(m.getM00().getR() , 2.5);
        auxDG = (m.getM21().getG() - 2 * xbarraG * m.getM11().getG() - ybarraR  * m.getM20().getG() + 2 * Math.pow(xbarraG,2) * m.getM01().getR()) / Math.pow(m.getM00().getG() , 2.5);
        auxDB = (m.getM21().getB() - 2 * xbarraB * m.getM11().getB() - ybarraR  * m.getM20().getB() + 2 * Math.pow(xbarraB,2) * m.getM01().getR()) / Math.pow(m.getM00().getB() , 2.5);
        auxDoble.setR(auxDR);
        auxDoble.setG(auxDG);
        auxDoble.setB(auxDB);
        N.setN21(auxDoble);
        
         //N12
        auxDR = (m.getM12().getR() - 2 * ybarraR * m.getM11().getR() - xbarraR  * m.getM02().getR() + 2 * Math.pow(ybarraR,2) * m.getM10().getR()) / Math.pow(m.getM00().getR() , 2.5);
        auxDG = (m.getM12().getG() - 2 * ybarraG * m.getM11().getG() - xbarraR  * m.getM02().getG() + 2 * Math.pow(ybarraG,2) * m.getM10().getG()) / Math.pow(m.getM00().getG() , 2.5);
        auxDB = (m.getM12().getB() - 2 * ybarraB * m.getM11().getB() - xbarraR  * m.getM02().getB() + 2 * Math.pow(ybarraB,2) * m.getM10().getB()) / Math.pow(m.getM00().getB() , 2.5);
        auxDoble.setR(auxDR);
        auxDoble.setG(auxDG);
        auxDoble.setB(auxDB);
        N.setN12(auxDoble);



        //Calculando los momentos de HU =================================================================
        momentosHu[0] = new rgbDobles();

        momentosHu[0].setR(N.getN20().getR() + N.getN02().getR());
        momentosHu[0].setG(N.getN20().getG() + N.getN02().getG());
        momentosHu[0].setB(N.getN20().getB() + N.getN02().getB());

        momentosHu[1] = new rgbDobles();
        momentosHu[1].setR( Math.pow((N.getN20().getR() - N.getN02().getR()), 2) + (4 * Math.pow(N.getN11().getR(), 2)) );
        momentosHu[1].setG( Math.pow((N.getN20().getG() - N.getN02().getG()), 2) + (4 * Math.pow(N.getN11().getG(), 2)) );
        momentosHu[1].setB( Math.pow((N.getN20().getB() - N.getN02().getB()), 2) + (4 * Math.pow(N.getN11().getB(), 2)) );

        momentosHu[2] = new rgbDobles();
        momentosHu[2].setR( Math.pow(N.getN30().getR()  - 3*N.getN12().getR()  , 2) + Math.pow(3*N.getN21().getR()  - N.getN03().getR()  , 2 ) );
        momentosHu[2].setG( Math.pow(N.getN30().getG()  - 3*N.getN12().getG()  , 2) + Math.pow(3*N.getN21().getG()  - N.getN03().getG()  , 2 ) );
        momentosHu[2].setB( Math.pow(N.getN30().getB()  - 3*N.getN12().getB()  , 2) + Math.pow(3*N.getN21().getB()  - N.getN03().getB()  , 2 ) );

        momentosHu[3] = new rgbDobles();
        momentosHu[3].setR( Math.pow(N.getN30().getR() + N.getN12().getR(),2) + Math.pow(N.getN21().getR() + N.getN03().getR(), 2) );
        momentosHu[3].setG( Math.pow(N.getN30().getG() + N.getN12().getG(),2) + Math.pow(N.getN21().getG() + N.getN03().getG(), 2) );
        momentosHu[3].setB( Math.pow(N.getN30().getB() + N.getN12().getB(),2) + Math.pow(N.getN21().getB() + N.getN03().getB(), 2) );

        momentosHu[4] = new rgbDobles();
        momentosHu[4].setR(  (N.getN30().getR() - 3 * N.getN12().getR() )*(N.getN30().getR() + N.getN12().getR() ) * ( Math.pow((N.getN30().getR() + N.getN12().getR() ), 2) - 3 * Math.pow(( N.getN21().getR() +  N.getN03().getR() ) ,2) )+(3* N.getN21().getR() -  N.getN03().getR() ) * ( N.getN21().getR() +  N.getN03().getR() ) * ( Math.pow(( N.getN30().getR() +  N.getN12().getR() ), 2) - Math.pow(( N.getN21().getR() +  N.getN03().getR() ), 2) )  );
        momentosHu[4].setG(  (N.getN30().getG() - 3 * N.getN12().getG() )*(N.getN30().getG() + N.getN12().getG() ) * ( Math.pow((N.getN30().getG() + N.getN12().getG() ), 2) - 3 * Math.pow(( N.getN21().getG() +  N.getN03().getG() ) ,2) )+(3* N.getN21().getG() -  N.getN03().getG() ) * ( N.getN21().getG() +  N.getN03().getG() ) * ( Math.pow(( N.getN30().getG() +  N.getN12().getG() ), 2) - Math.pow(( N.getN21().getG() +  N.getN03().getG() ), 2) )  );
        momentosHu[4].setB(  (N.getN30().getB() - 3 * N.getN12().getB() )*(N.getN30().getB() + N.getN12().getB() ) * ( Math.pow((N.getN30().getB() + N.getN12().getB() ), 2) - 3 * Math.pow(( N.getN21().getB() +  N.getN03().getB() ) ,2) )+(3* N.getN21().getB() -  N.getN03().getB() ) * ( N.getN21().getB() +  N.getN03().getB() ) * ( Math.pow(( N.getN30().getB() +  N.getN12().getB() ), 2) - Math.pow(( N.getN21().getB() +  N.getN03().getB() ), 2) )  );


        momentosHu[5] = new rgbDobles();
        momentosHu[5].setR( (N.getN20().getR() - N.getN02().getR() ) * (Math.pow(N.getN30().getR() + N.getN12().getR(),2) - Math.pow(N.getN21().getR() + N.getN03().getR() , 2 )) +(4 *N.getN11().getR() ) * (N.getN30().getR() + N.getN12().getR() ) * (N.getN21().getR() + N.getN03().getR()) );
        momentosHu[5].setG( (N.getN20().getG() - N.getN02().getG() ) * (Math.pow(N.getN30().getG() + N.getN12().getG(),2) - Math.pow(N.getN21().getG() + N.getN03().getG() , 2 )) +(4 *N.getN11().getG() ) * (N.getN30().getG() + N.getN12().getG() ) * (N.getN21().getG() + N.getN03().getG()) );
        momentosHu[5].setB( (N.getN20().getB() - N.getN02().getB() ) * (Math.pow(N.getN30().getB() + N.getN12().getB(),2) - Math.pow(N.getN21().getB() + N.getN03().getR() , 2 )) +(4 *N.getN11().getR() ) * (N.getN30().getR() + N.getN12().getR() ) * (N.getN21().getR() + N.getN03().getR()) );
        
        momentosHu[6] = new rgbDobles();
        momentosHu[6].setR( (3 *N.getN21().getR() - N.getN03().getR() ) * (N.getN30().getR() + N.getN12().getR() ) * ( Math.pow(N.getN30().getR() +N.getN12().getR() , 2) - 3 *(Math.pow(N.getN21().getR() + N.getN03().getR(),2))) + (3 * N.getN12().getR() - N.getN03().getR() ) * ( N.getN21().getR() + N.getN03().getR() ) * (Math.pow(3 * N.getN30().getR() + N.getN12().getR() , 2) - Math.pow(N.getN21().getR() + N.getN03().getR(),2) )  );
        momentosHu[6].setG( (3 *N.getN21().getG() - N.getN03().getG() ) * (N.getN30().getG() + N.getN12().getG() ) * ( Math.pow(N.getN30().getG() +N.getN12().getG() , 2) - 3 *(Math.pow(N.getN21().getG() + N.getN03().getG(),2))) + (3 * N.getN12().getG() - N.getN03().getG() ) * ( N.getN21().getG() + N.getN03().getG() ) * (Math.pow(3 * N.getN30().getG() + N.getN12().getG() , 2) - Math.pow(N.getN21().getG() + N.getN03().getG(),2) )  );
        momentosHu[6].setB( (3 *N.getN21().getB() - N.getN03().getB() ) * (N.getN30().getB() + N.getN12().getB() ) * ( Math.pow(N.getN30().getB() +N.getN12().getB() , 2) - 3 *(Math.pow(N.getN21().getB() + N.getN03().getB(),2))) + (3 * N.getN12().getB() - N.getN03().getB() ) * ( N.getN21().getB() + N.getN03().getB() ) * (Math.pow(3 * N.getN30().getB() + N.getN12().getB() , 2) - Math.pow(N.getN21().getB() + N.getN03().getB(),2) )  );
       
    
    return momentosHu;
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


