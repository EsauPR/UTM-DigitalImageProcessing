import java.awt.*;
import java.awt.image.*;

public class filtros_frec{
    private double[][] H;
    private int Width;
    private int Height;

    filtros_frec(int P,int Q){
        this.H = new double[P][Q];
        this.Height = P;
        this.Width = Q;
    }

    public double [][] getH(){
        return this.H;
    } 

    /* High pass Gaussian filter */
    public void set_HPG_Filter( double d0 ) {
        double temp;

        for (int i = 0; i < this.Height; i++) {
            for (int j = 0; j < this.Width; j++) {
                temp = Math.sqrt( Math.pow( ( (double)i - ((double)this.Height / 2) ) , 2 ) + Math.pow( ( (double)j - ((double)this.Width / 2) ), 2) );
                temp = Math.pow( temp , 2) * - 1.0 / ( d0 * 2 );
                temp = Math.exp( temp );
                H[ i ][ j ] = 1.0 - temp;
            }
        }
    }

    public void set_butterWorthLP( double d0 , int n) {
        double temp;
        int z;
        for (int i = 0; i < this.Height; i++) {
            for (int j = 0; j < this.Width; j++) {
                temp = Math.sqrt( Math.pow( ( (double)i - ((double)this.Height / 2) ) , 2 ) + Math.pow( ( (double)j - ((double)this.Width / 2) ), 2) );
                z= 2 * n ;
                H[ i ][ j ] = 1 / (1 + Math.pow(temp / d0 , z ));
            }
        }
    }

    public void set_LPG_Filter( double d0 ) {
        double temp;

        for (int i = 0; i < this.Height; i++) {
            for (int j = 0; j < this.Width; j++) {
                temp = Math.sqrt( Math.pow( ( (double)i - ((double)this.Height / 2) ) , 2 ) + Math.pow( ( (double)j - ((double)this.Width / 2) ), 2) );
                temp = Math.pow( temp , 2) * - 1.0 / ( d0 * 2 );
                temp = Math.exp( temp );
                H[ i ][ j ] =  temp;
            }
        }
    }

}
