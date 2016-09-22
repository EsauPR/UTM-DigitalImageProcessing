import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.StringTokenizer;
import javax.imageio.*;
import javax.imageio.stream.*;

public class PGM {
    private BufferedImage Image;
    private String Type;
    private int Max_gray_value;

    public PGM( String path ) {
        read_image( path );
    }

    public void read_image( String path ) {
        /*
            La Cabecera tiene el siguiente formato
            Primera linea => Tipo de imagen ( P2 o P5 )
            Número indefinido de lineas de comentarios iniciados por '#'
            Siguente linea dos enteros definiendo las dimenciones de la imagen
            Siguiente linea in entero definiendo el valor máximo de la escala de grises que toma la imagen;
            Si Tipo == P2
                Enteros separados por un espacio representando el valor de un pixel
            Si Tipo == P5
                Caracteres representando el valor del pixel que toma el valor de su ASCII
        */
        try {
            Scanner sc = new Scanner( new FileReader( path ) );
            StringTokenizer st;

            // Header
            this.Type = sc.nextLine();

            String str = sc.nextLine();

            // Skip Coments
            while( sc.hasNext() ) {
                if( str.charAt(0) != '#' )
                    break;
                str = sc.nextLine();
            }

            st = new StringTokenizer(str);

            int cols = Integer.parseInt( st.nextToken() );
            int rows = Integer.parseInt( st.nextToken() );
            this.Max_gray_value = sc.nextInt();

            this.Image = new BufferedImage( cols, rows, BufferedImage.TYPE_INT_RGB );

            if( this.Type.equals( "P2" ) ) {
                read_pgm_p2( sc, cols, rows );
            }
            else {
                sc.close();
                read_pgm_p5( path, cols, rows );
            }

        }catch(FileNotFoundException fe) {
	    	System.out.println("No se encontró el archivo.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void read_pgm_p2( Scanner sc, int cols, int rows ) {
        int px;
        for( int i=0; i < cols; i++ ) {
            for (int j=0; j < rows; j++) {
                px = sc.nextInt();
                this.Image.setRGB( i, j, ((255<<24) | (px << 16) | (px << 8) | px) );
            }
        }
    }

    private void read_pgm_p5( String path, int cols, int rows ) {
        try{
            FileImageInputStream fiis = new FileImageInputStream(new File(path));
            int px;
            String temp;
            for( int i=0; i<3; i++ )
                 temp = fiis.readLine();
            /* Falta saltar comentarios */
            for( int i = 0; i< rows; i++ )
                for( int j = 0; j < cols; j++ ){
                    px = fiis.readUnsignedByte();
                    this.Image.setRGB( j, i, ((255<<24) | (px << 16) | (px << 8) | px) );
                }
            fiis.close();
        }catch(IOException e){
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
