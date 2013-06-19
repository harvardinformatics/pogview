package pogvue.math;

/**
 * FFT - Fast Fourier Transform Program
 * by Tracy Hammond
 * Algorithm used from Applied Numerical Analysis, 
 * Fifth Edition; Curtis F. Gerald, Patrick O. Wheatley
 * Addison Wesley 1994
 */
public class FFT  {
  private double[] YREAL;
  private double[] YIMAG;
  private double[] XREAL;
  private double[] AFFT;  
  private double[] BFFT;
  private double[] COSX;
  private double[] SINX;
  private int[] POW;
  
  public FFT(String[] datapoints) {
    try {
      // put the given values in to an array
      YREAL = makeRealArray(datapoints);
      YIMAG = makeImaginaryArray(datapoints);
		
      // make array of x values
      XREAL = makeXArray(Integer.parseInt(datapoints[0]));

      // the given values function are starting values for 
      // Fourier Results
      AFFT = YREAL;  
      BFFT = YIMAG;
		
      // make cos(x) array and sin(x) array
      COSX = makeCosXArray(XREAL);
      SINX = makeSinXArray(XREAL);
		
      // generate power array
      POW = makePowerArray(Integer.parseInt(datapoints[0]));
      
      // actual fft calculation done here
      calculateABValues(Integer.parseInt(datapoints[0]));
      
      // unscramble the vertices according to POW values
      unscramble();
		
    } catch (Exception e) {
      System.out.println("Exception " + e.toString());
      e.printStackTrace();
      System.exit(-1);
    }
		
    for(int i = 0; i < AFFT.length; i++) {
      System.out.println("A" + i + " = " + AFFT[i] + "  B" + i + " = " + BFFT[i]);
    }
    System.exit(0);
  }
	
  /**
   * Method: makeRealArray
   * Input: string array of datapoints
   * Output: double array of real y values
   */
  private double[] makeRealArray(String[] datapoints) {

    double[] RealArray = new double[Integer.parseInt(datapoints[0])];
    for(int i = 0; i < Integer.parseInt(datapoints[0]); i++)
      {
	RealArray[i] = Double.valueOf(datapoints[2*i + 1]).doubleValue();
      }
    return RealArray;
  }
  
  /**
   * Method: makeImaginaryArray
   * Input: String array of datapoints
   * Output: double array of imaginary y values
   */	
  private double[] makeImaginaryArray(String[] datapoints) {
    double[]ImaginaryArray = new double[Integer.parseInt(datapoints[0])];
    for (int i=0; i < Integer.parseInt(datapoints[0]); i++) {
      ImaginaryArray[i] = Double.valueOf(datapoints[2*(i+1)]).doubleValue();
    }
    return ImaginaryArray;
  }
  
	
  /**
   * Method: makeXArray
   * Input: number of datapoints
   * Output: Array of X Values
   * This is not necessary, but helpful in the readability of 
   * the program.
   * It also allows retrieval of X values if necessary
   */
  private double[] makeXArray(int ArrayLength) {
    double[] XArray = new double[ArrayLength];
    for (int i = 0; i < ArrayLength; i++) {
      XArray[i] = 8 * i / ArrayLength + 2;
      //XArray[i] = 2 * i * Math.PI / ArrayLength;
    }
    return XArray;
  }
	
  /**
   * Method: makeCosXArray
   * Input: double array of x values
   * Output: double array of cos(x) values
   */
  private double[] makeCosXArray(double[] XArray) {
    double[] CosXArray = new double[XArray.length];
    for (int i = 0; i < XArray.length; i++) {
      CosXArray[i] = Math.cos(XArray[i]);
    }
    return CosXArray;
  }
	
  /**
   * Method: makeSinXArray
   * Input: double array of x values
   * Output: double array of sin x values
   */
  private double[] makeSinXArray(double[] XArray) {
    double[] SinXArray = new double[XArray.length];
    for (int i = 0; i < XArray.length; i++){
      SinXArray[i] = Math.sin(XArray[i]);
    }
    return SinXArray;
  }
  
  /**
   * Method: makePowerArray
   * Input: number of datapoints
   * Output: Power of 2 Integer Array 
   * This create something an array of Zn* with the first 
   * element zero
   */
  private int[] makePowerArray(int ArrayLength){
    int[] PowerArray = new int[ArrayLength];
    
    // initialize to zero
    for (int i = 0; i < ArrayLength; i++)
      {
	PowerArray[i] = 0;
      }
    
    // reevaluate elements
    int TotalNumberOfRuns =  (int) (Math.log(ArrayLength)/Math.log(2));
    for (int i = 1; i <= TotalNumberOfRuns; i++) {
      for (int j = 1; j <= Math.pow(2, i - 1); j++){
	PowerArray[j] = 2 * PowerArray[j];
	//System.out.println(j + " " + PowerArray[j]);
      }
      for (int j = 0; j < Math.pow(2, i - 1); j++) {
	PowerArray[j + (int) Math.pow(2, i - 1)] = PowerArray[j] + 1;
	//System.out.println(j + " " + PowerArray[j]);
      }
    }
		
    //for (int i = 0; i < PowerArray.length; i++)
    //	System.out.println(PowerArray[i]);
    return PowerArray;
	}
	
  /**
   * Method: recalculateABValues
   * Input: None, as all necessary variables are in global
   * Output: none
   * Sets A and B arrays to Fourier series values
   */
  private void calculateABValues(int NumberOfData) {
    int stage = 1;
    int NumberOfSets = 1;
    int cycleLength = NumberOfData/2;
    int k = 0;
    double[] TEMPA = AFFT;
    double[] TEMPB = BFFT;
    
    do {
      for(int setNumber = 1; setNumber <= NumberOfSets; setNumber++){
	for (int i = 0; i < NumberOfData/NumberOfSets; i++){
	  int j = (i % cycleLength) + (setNumber - 1) * cycleLength * 2;
	  int l = POW[(int)(k/cycleLength)];
	  if (j > AFFT.length || l > AFFT.length || j+cycleLength > AFFT.length)
	    System.out.println("HELP");

	  TEMPA[k] = AFFT[j] + COSX[l]*AFFT[j+cycleLength] - SINX[l]*BFFT[j+cycleLength];
	  TEMPB[k] = BFFT[j] + COSX[l]*BFFT[j+cycleLength] - SINX[l]*AFFT[j+cycleLength];

	  k++;
	}
      }
      AFFT = TEMPA;
      BFFT = TEMPB;
      stage++;
      NumberOfSets *= 2;
      cycleLength /= 2;
      k = 0;														  
    } while (stage <= (int)(Math.log(NumberOfData)/Math.log(2))); 
  }
	
  /**
   * Method: unscramble
   * Input: none
   * Output: none
   * Function: Rearranges the A and B values
   */
  private void unscramble() {
    double[] tempA = AFFT;
    double[] tempB = BFFT;
    for (int i = 0; i < AFFT.length; i++){
      AFFT[i] = tempA[POW[i]];
      BFFT[i] = tempB[POW[i]];
    }
  }
  
  
  /**
   * Method: main
   * Input: command-line arguments
   * the 0th argument is the number of datapoints
   * each following argument is the yreal value followed by
   * the yimaginary value
   * the number of datapoints must be a power of 2
   */
  public static void main (String[] args){
    // hopefully a string of arguments will be entered.
    try{
      // if no arguments, print usage
      if (args.length == 0){
	String[] data1 = {"32", "3.804", "0", "6.503", 
			  "0", "7.496", "0", "6.094", "0", 
			  "3.003", "0", "-0.105", "0", "-1.589", 
			  "0", "-0.721", "0", "1.806", "0", 
			  "4.350", "0", "5.2555", "0", "3.878", 
			  "0", "0.893", "0", "-2.048", "0", 
			  "-3.280", "0", "-2.088", "0", "3.746",
			  "0", "5.115", "0", "4.156", "0", 
			  "1.593", "0", "-0.941", "0", "-1.821", 
			  "0", "-0.329", "0", "2.799", "0", 
			  "5.907", "0", "7.338", "0", "6.380", 
			  "0", "3.709", "0", "0.992", "0", 
			  "-.0116", "0", "1.047", "0", "3.802", "0"};
	FFT alg = new FFT(data1);
	printUsage();
      }
			
      // make sure is a positive integer entered
      if (Integer.parseInt(args[0]) <= 0){
	printUsage();
      }
			
      // make sure number is a power of 2
      int i = 0;
      do {
	if(Math.pow(2.0,(double) i) < Integer.parseInt(args[0]))
	  i++;
	if(Math.pow(2.0,(double) i) == Integer.parseInt(args[0]))
	  break;
	if(Math.pow(2.0,(double) i) > Integer.parseInt(args[0]))
	  printUsage();			
      } while (true);
      
      // make sure number entered equals the number of
      // datapoints
      if ((args.length - 1)/2 != Integer.parseInt(args[0])){
	printUsage();
      }
    } catch (Exception e){
      // probably error in input
      printUsage();
    }
		
    // all is good.
    // start the program (Fast Fourier Transform)
    FFT alg = new FFT(args);
    
  }
	
  private static void printUsage() {
    System.out.println("Fast Fourier Transform");
    System.out.println("Returns the fft of a list of data points");
    System.out.println("--usage");
    System.out.println("        fft number datapoints");
    System.out.println();
    System.out.println("Number must be the number of datapoints");
    System.out.println("Number must be power of 2");
    System.out.println("Datapoints must be written as: y1real y1imaginary y2real y2imaginary...");
    System.out.println("X values are considered to be standardized on [0,2*pi]");
    System.out.println();
    System.exit(0);
  }
}

