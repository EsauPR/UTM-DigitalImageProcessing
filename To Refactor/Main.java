import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.imageio.*;
import javax.imageio.stream.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.Vector;

public class Main {

    public static void main(String[] args) throws FileNotFoundException, IOException {

        if( args.length > 2 ) {
        }

        Dicom image_dcm = new Dicom( "img/dicom.dcm" );
        //image_dcm.show_image();

        PGM image_pmg_p2 = new PGM( "img/img2_p2.pgm" );
        //image_pmg_p2.show_image();

        PGM image_Drn = new PGM( "img/img1.pgm" );
        //image_pmg_p2.show_image();

        TIFF image_tiff_frut = new TIFF( "img/frutasGrises.tif" );
        //image_tiff.show_image();

        PGM image_pmg_p5 = new PGM( "img/img2_p5.pgm" );
        //image_pmg_p5.show_image();

        TIFF image_tiff = new TIFF( "img/img3.tif" );

        image_tiff_frut.show_image();

        FFT2 fft2 = new FFT2( image_tiff_frut );

        fft2.fft2();
        fft2.show_image();

        //image_tiff.show_image();

        //   T    E    X    T    U    R    A
        //   Estadisticos de primer orden

        //   3.- Uniformidad (Luis).
        //Textura T= new Textura(image_pmg_p5);
        //double Uniformidad=T.uniformidad();
        //System.out.println("Uniformidad:  "+Uniformidad);


        //SpaceFilters FiltroOtsu = new SpaceFilters( image_pmg_p5 );
        //SpaceFilters Drn= new SpaceFilters( image_Drn );
        //Drn.show_default_image();

        //Vector Vec =new Vector();

        //ec=Drn.Distancia_Radial_Normalizada(127,127);


        //for (int i=0; i<Vec.size();i++ ) {
        //   System.out.println(i+"\t"+Vec.elementAt(i));
        //}
        //SpaceFilters FiltroOtsu = new SpaceFilters( image_pmg_p5 );
        //FiltroOtsu.show_default_image();

        //Img.toNegative();
        //Img.gammaTransformation( 2 );
        //Img.histogramEqualization();
        //FiltroOtsu.otsu();
        //FiltroOtsu.show_image();
        //Img.gradientFilter();
        //Img.laplacianFilter( 1 );
        //Detector_Bordes_Canny Img = new Detector_Bordes_Canny( image_tiff_frut );
        //Img.show_default_image();

        //Img.D_B_Canny();
        //Img.show_image();
        //Detector_Bordes_Canny Img = new Detector_Bordes_Canny( image_pmg_p5 );
        //Img.show_default_image();

        //Img.D_B_Canny();
        //Img.show_image();

        //ImageDft imgDFT = new ImageDft( image_pmg_p5 );
        //imgDFT.Dft();

    }

}
