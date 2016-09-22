/*
 * Fast Fourier transform
 * Base: http://rosettacode.org/wiki/Fast_Fourier_transform#C
 * @autor Esau Peralta
 * @email esau.opr@gmail.com
 */

class  FFT {

    private static void fft_( double []Real_in, double []Imag_in, double []Real_out, double []Imag_out, int n, int step, int offset ){
        if( step < n ) {
            System.out.println(n + " " + step + " " + offset);
            fft_( Real_out, Imag_out, Real_in, Imag_in, n, step * 2, offset );
            fft_( Real_out , Imag_out, Real_in, Imag_in, n, step * 2, offset + step);

    		for (int i = 0; i < n; i += 2 * step) {
                System.out.println(offset + i + step);
                double angle = Math.PI * i / n;

                double t_real = (Math.cos( angle ) * Real_out[ offset + i + step ]) - (-Math.sin( angle ) * Imag_out[ offset + i + step ]);
                double t_imag = (Math.cos( angle ) * Imag_out[ offset + i + step ]) + (-Math.sin( angle ) * Real_out[ offset + i + step ]);

                Real_in[ offset + i / 2 ] = Real_out[ offset + i ] + t_real;
                Imag_in[ offset + i / 2 ] = Imag_out[ offset + i ] + t_imag;

                Real_in[ offset + (i+n) / 2 ] = Real_out[ offset + i ] - t_real;
                Imag_in[ offset + (i+n) / 2 ] = Imag_out[ offset + i ] - t_imag;
    		}
        }
    }

    /* Compute fft */
    public static void fft( double []Real, double []Imag, int n ) {

        double []Real_out = new double[n];
        double []Imag_out = new double[n];

        for (int i = 0; i < n; i++) {
            Real_out[i] = Real[i];
            Imag_out[i] = Imag[i];
        }
        fft_( Real, Imag, Real_out, Imag_out, n, 1, 0 );
    }

    /* Compute ifft*/
    public static void ifft( double []Real, double []Imag, int n ){
        //conjugate
        for (int i = 0; i < n; i++) {
            Imag[i] = -Imag[i];
        }

        //apply fourier transform
        double []Real_out = new double[n];
        double []Imag_out = new double[n];

        for (int i = 0; i < n; i++) {
            Real_out[i] = Real[i];
            Imag_out[i] = Imag[i];
        }
        fft_( Real, Imag, Real_out, Imag_out, n, 1, 0 );

        //conjugate
        for (int i = 0; i < n; i++) {
            Imag[i] = -Imag[i];
            //scale
            Real[i] /= n;
            Imag[i] /= n;
        }
    }

    /*
    //Ejecution example
    public static void main(String[] args) {
        double [] Real = {1.0, 1.0, 1.0, 1.0, 0, 0, 0, 0};
        double [] Imag = {0, 0, 0, 0, 0, 0, 0, 0};

        fft( Real, Imag, 8 );

        for( int i = 0; i < 8; i++ )
            System.out.print( "[" + Real[i] +","+ Imag[i] +"], " );
        System.out.println("");

        ifft( Real, Imag, 8 );

        for( int i = 0; i < 8; i++ )
            System.out.print( "[" + Real[i] +",0]" );
        System.out.println("");

    }
    */
}
