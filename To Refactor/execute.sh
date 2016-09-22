#!/bin/bash
#javac -classpath classes:lib/* Dicom.java PGM.java TIFF.java SpaceFilters.java ImageDft.java Main.java FFT.java FFT2.java CannyEdge.java AplicarFiltrosaIMG.java filtros_frec.java Detector_Bordes_Canny.java supresion_no_maximos.java Distancia_Radial_Normalizada.java
javac -cp classes:lib/* Dicom.java PGM.java TIFF.java SpaceFilters.java ImageDft.java Main.java FFT.java FFT2.java AplicarFiltrosaIMG.java filtros_frec.java Detector_Bordes_Canny.java Textura.java
java -cp lib/jai_codec-1.1.3-alpha.jar:lib/jai-core-1.1.3-alpha.jar:lib/dicom.jar:. Main
