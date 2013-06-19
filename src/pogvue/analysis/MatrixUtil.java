package pogvue.analysis;

import pogvue.datamodel.Sequence;
import pogvue.util.Format;
import pogvue.io.FastaFile;

import java.io.*;
import java.util.Hashtable;
import java.util.StringTokenizer;

public final class MatrixUtil {

    private static final Hashtable dinuc  = new Hashtable();

    static {
	dinuc.put("AA", 0);
	dinuc.put("AC", 1);
	dinuc.put("AG", 2);
	dinuc.put("AT", 3);
	dinuc.put("CA", 4);
	dinuc.put("CC", 5);
	dinuc.put("CG", 6);
	dinuc.put("CT", 7);
	dinuc.put("GA", 8);
	dinuc.put("GC", 9);
	dinuc.put("GG", 10);
	dinuc.put("GT", 11);
	dinuc.put("TA", 12);
	dinuc.put("TC", 13);
	dinuc.put("TG", 14);
	dinuc.put("TT", 15);
    }
    private static final Hashtable factorial = new Hashtable();

    public static double[][] readMatrix(DataInputStream din) {
	String line;
	double[][] mat = null;

	int init = 0;
	int i    = 0;

	double tot = 0.0;
	double[] rowtot = null;

	int num = 0;

	try {
	    while ((line = din.readLine()) != null) {
		if (line.equals("")) {
		    
		    return normalize(mat,num,num);
		}

		StringTokenizer str = new StringTokenizer(line);

		num = str.countTokens();
		
		if (init == 0) {
		    mat  = new double[num][num];
		    rowtot = new double[num];

		    init = 1;
		}

		int j = 0;

		while (str.hasMoreTokens()) {
		    double val = Double.parseDouble(str.nextToken());
		    mat[i][j] = val;
		    tot += val;
		    rowtot[i] += val;
		    j++;
		}
		i++;
	    }

	} catch (IOException e) {
	    System.out.println("Exception e " + e);
	}
	return normalize(mat,num,num);
    }

    private static double[][] normalize(double[][] mat,int row, int col) {
	int i = 0;
	double tot = 0;
	double[] rowtot = new double[row];

	while (i < row) {
	    int j = 0;
	    int sum = 0;

	    while (j < col) {
		sum += mat[i][j];
		j++;
	    }

	    j = 0;

	    while (j < col) {
		mat[i][j] /= sum;
		j++;
	    }

	    
	    i++;
	}
	//	print_matrix("Norm",mat,row,col);

	return mat;
    }
    private static double[][] matrix_multiply(double[][] mat1, double[][] mat2, int row, int col) {

	double[][] mat = new double[row][col];

	int i = 0;

	while (i < row) {

	    int j = 0;
	    
	    while (j < col) {
		int jj  = 0;
		int val = 0;
		
		while (jj < row) {
		    mat[i][j] += mat1[i][jj]*mat2[jj][j];
		    jj++;
		}
		
		j++;
	    }

	    i++;
	}

	return mat;
    }

    private static void print_matrix(String label, double[][] mat, int row, int col) {

	int i = 0;

	while (i < row) {
	    int j = 0;

	    if (label != null) {
		Format.print(System.out,"%10s ",label);
		Format.print(System.out,"%4d ",i);
	    }

	    while (j < col) {
		Format.print(System.out,"%10.8f ",mat[i][j]);
		j++;
	    }

	    System.out.println();
	    i++;
	}
	System.out.println();
    }

    private static double[][] copy_matrix(double[][] mat,int row, int col) {

	double[][] newmat = new double[row][col];

	int i = 0;
	
	while (i < row) {
	    int j = 0;
	    while (j < col) {
		newmat[i][j] = mat[i][j];
		j++;
	    }
	    i++;
	}

	return newmat;
    }

    private static double[][] matrix_exponent(double[][] mat,int row, int col) {

	int kk = 20;
	int k  = 1;

	double[][] tmpmat;
        copy_matrix(mat, row, col);

        double[][] endmat = new double[row][col];

	int i = 0;
	
	while (i < row) {
	    endmat[i][i] = 1;
	    i++;
	}

	double[][] multmat = copy_matrix(endmat,row,col);

	while (k < kk) {
	    tmpmat = copy_matrix(mat,row,col);

	    if (k > 1) {
		multmat = matrix_multiply(multmat,tmpmat,row,col);
	    } else {
		multmat = copy_matrix(mat,row,col);
	    }

	    tmpmat = copy_matrix(multmat,row,col);

	    double multiplier = get_factorial(k);

	    multiplier = 1.0/multiplier;

	    constant_multiply(multiplier,tmpmat,row,col);

	    endmat = matrix_add(tmpmat,endmat,row,col);

	    k++;
	}

	return endmat;
    }

    private static void constant_multiply(double c, double[][] mat,int row, int col) {

	int i = 0;

	while (i < row) {
	    int j = 0;
	    while (j < col) {

		mat[i][j] *= c;
		j++;

	    }

	    i++;
	}

	
    }

    private static double get_factorial(int num) {

	if (MatrixUtil.factorial.get(new Integer(num)) == null) {

	    if (num == 0) {
		MatrixUtil.factorial.put(0, 1);
	    } else {
		double i = 1;
		double fac = 1;

		while (i <= num) {
		    fac *= i;
		    i++;
		}

		MatrixUtil.factorial.put(num, fac);
	    }
	}
	
	double val = (Double) MatrixUtil.factorial.get(new Integer(num));

	return val;
    }

    private static double[][]  matrix_add(double[][] mat1, double[][] mat2,int row, int col) {

	int i = 0;

	double[][] newmat = new double[row][col];

	while (i < row) {
	    int j = 0;

	    while (j < col) {
		newmat[i][j] = mat1[i][j] + mat2[i][j];
		j++;
	    }
	    i++;
	}
	return newmat;
    }

    public static double[][] get_matrix(double dist,double gc,boolean meth) {

	double trans_cpg;
	double tranv_cpg = 1.2e-8;

	trans_cpg = 0.5e-7;
	trans_cpg = 0.3e-8;

	double trans = 1.2e-8;
	double tranv = 5.5e-9;

	
	double[][] tm_cpg = new double[4][4];
	double[][] tm     = new double[4][4];

	double[] pi = get_pi(gc,meth);

	tm[0][1] =  tranv; // A -> C
	tm[0][2] =  trans; // A -> G 
	tm[0][3] =  tranv; // A -> T 
	
	tm[1][0] =  tranv; // C -> A
	tm[1][2] =  tranv; // C -> G
	tm[1][3] =  trans; // C -> T
	
	tm[2][0] =  trans; // G -> A
	tm[2][1] =  tranv; // G -> C 
	tm[2][3] =  tranv; // G -> T
	
	tm[3][0] =  tranv; // T -> A
	tm[3][1] =  trans; // T -> C
	tm[3][2] =  tranv; // T -> G
	
	tm[0][0] = (1);
	tm[1][1] = (1);
	tm[2][2] = (1);
	tm[3][3] = (1);
	
	tm_cpg[0][1] =  tranv_cpg; // A -> C
	tm_cpg[0][2] =  trans_cpg; // A -> G 
	tm_cpg[0][3] =  tranv_cpg; // A -> T 
	
	tm_cpg[1][0] =  tranv_cpg; // C -> A
	tm_cpg[1][2] =  tranv_cpg; // C -> G
	tm_cpg[1][3] =  trans_cpg; // C -> T
	
	tm_cpg[2][0] = trans_cpg;  // G -> A
	tm_cpg[2][1] = tranv_cpg;  // G -> C 
	tm_cpg[2][3] = tranv_cpg;  // G -> T
	
	tm_cpg[3][0] = tranv_cpg; // T -> A
	tm_cpg[3][1] = trans_cpg; // T -> C
	tm_cpg[3][2] = tranv_cpg; // T -> G
	
	tm_cpg[0][0] = 1;
	tm_cpg[1][1] = 1;
	tm_cpg[2][2] = 1;
	tm_cpg[3][3] = 1;
	

	double[][] dinuc = new double[16][16];

	// AA AC AG AT CA CC CG CT GA GC GG GT TA TC TG TT
	// 0  1  2  3  4  5  6  7  8  9  10 11 12 13 14 15
	//                  CpG 	

	int i1 = 0;
	int i2;

	while (i1 < 4) {
	    i2 = 0;

	    while (i2 < 4) {

		int j1 = 0;
		int j2;

		while (j1 < 4) {

		    j2 = 0;

		    while (j2 < 4) {

			int row = i1*4 + i2;
			int col = j1*4 + j2;

			dinuc[row][col] = tm[i1][j1] * tm[i2][j2];

			if (meth) {
			    if (i1 == 1 && i2 == 2) {
				dinuc[row][col] = tm_cpg[i1][j1] * tm_cpg[i2][j2];
		
			    }
			}
			j2++;
		    }
		    j1++;
		}
		i2++;
	    }
	    i1++;
	}
    

	// Multiply by the composition

	int i = 0;

	while (i < 16) {

	    int j = 0;

	    while ( j < 16) {
		dinuc[i][j] *= pi[j];
		j++;
	    }
	    i++;
	}


	i = 0;

	// Make sure off diags sum to 1 and rows sum to 0

	double tot = 0.0;

	double[] rowtot = new double[16];

	while (i < 16) {
	    int j = 0;

	    while (j < 16) {

		if (j != i) {
		    tot += dinuc[i][j];
		    rowtot[i] += dinuc[i][j];
		}

		j++;
	    }
	    i++;
	}

	i = 0;

	while (i < 16) {
	    int j = 0;

	    while (j < 16) {

		if (i != j) {
		    dinuc[i][j] = dinuc[i][j] * 16.0 / tot;
		} else {
		    dinuc[i][j] = -1 * rowtot[i] * 16.0/tot;
		}

		j++;
	    }
	    i++;
	}
	print_matrix("dinuc",dinuc,16,16);

	double[][] tmp1 = copy_matrix(dinuc,16,16);

	constant_multiply(dist,tmp1,16,16);

	print_matrix("mult",tmp1,16,16);

	double[][] tmp  = matrix_exponent(tmp1,16,16);

	return tmp;
    }

    public static double[] get_pi(double gc, boolean meth) {
	double[] pi = new double[16];

	if (meth) {
	    pi[0] = 0.0974;
	    pi[1] = 0.0504;
	    pi[2] = 0.0690;
	    pi[3] = 0.0772;
	    pi[4] = 0.0724;
	    pi[5] = 0.0518;
	    pi[6] = 0.1020;
	    pi[7] = 0.0690;
	    pi[8] = 0.0588;
	    pi[9] = 0.0426;
	    pi[10] = 0.0520;
	    pi[11] = 0.0508;
	    pi[12] = 0.0656;
	    pi[13] = 0.0588;
	    pi[14] = 0.0728;
	    pi[15] = 0.0972;
	} else {
	    int i = 0;

	    while (i < 4) {
		int j = 0;

		while (j < 4) {
		    double p_i;
		    double p_j;

		    if (i == 1 || i == 2) {
			p_i = (gc/100.0)/2.0;
		    } else {
			p_i = (1.0-gc/100.0)/2.0;
		    }

		    if (j == 1 || j == 2) {
			p_j = (gc/100.0)/2.0;
		    } else {
			p_j = (1.0-gc/100.0)/2.0;
		    }

		    pi[4*i+j] = p_i*p_j;

		    j++;
		}
		i++;
	    }
	}
	return pi;
    }


    public static double get_dist_cg(double[][] mat1, double[][] mat2,String seq1, String seq2,double[] pi1, double[] pi2) {

	int i = 0;
	
	double p = 0.0;


	while (i < seq1.length()-1) {
	    String c1 = seq1.substring(i,i+2);
	    String c2 = seq2.substring(i,i+2);

	    //	    System.out.println("C " + c1 + " " + c2 + " " + dinuc.get(c1) + " " + dinuc.get(c2));

	    if (dinuc.containsKey(c1) && dinuc.containsKey(c2)) {
		int num1 = (Integer) dinuc.get(c1);
		int num2 = (Integer) dinuc.get(c2);
		
		double comp1 = pi1[num1];
		double comp2 = pi2[num1];
		
		double prob1 = mat1[num1][num2];
		double prob2 = mat2[num1][num2];
		
		//	System.out.println("Comp " + comp1 + " " + comp2 + " " + prob1 + " " +prob2);

		p += Math.log((comp1 * prob1)/(comp2 * prob2)) / Math.log(10);
	    }
	    i++;
	}
	return p;
    }
    
    private static int count_motifs(String str1,String motif) {
	int num = 0;

	int i = 0;

	while (str1.indexOf(motif,i) != -1) {
	    i = str1.indexOf(motif,i)+1;
	    num++;
	}

	return num;
    }

    private static int count_conserved_motifs(String str1,String str2,String motif) {
	int num = 0;

	int i = 0;

	while (str1.indexOf(motif,i) != -1) {
	    i = str1.indexOf(motif,i);

	    if (str2.substring(i,i+motif.length()).equals(motif)) {
		num++;
	    }
	    i++;
	}

	return num;
    }
    
    public static void main(String[] args) {
	try {
	    File file1 = new File(args[0]);
	    
	    BufferedInputStream bis1    = new BufferedInputStream(new FileInputStream(file1));
	    DataInputStream     dataIn1 = new DataInputStream(bis1);
	    
	    double [][] matrix2 = MatrixUtil.readMatrix(dataIn1);
	    double [][] matrix1 = MatrixUtil.readMatrix(dataIn1);
	    
	    double[] pi2 = MatrixUtil.get_pi(41.0,true);
	    double[] pi1 = MatrixUtil.get_pi(41.0,false);
	    

	    MatrixUtil.print_matrix("Meth  ",matrix2,16,16);
	    MatrixUtil.print_matrix("Unmeth",matrix1,16,16);


			FastaFile ff = new FastaFile(args[1],"File");
			Sequence[] s = ff.getSeqsAsArray();
	    
	    int window = 500;
	    
	    String str1  = s[0].getSequence();
	    String str2  = "";
	    
	    int j = 1;
	    
	    while (j < s.length) {
		if (s[j].getName().equals("dog")) {
		    str2 = s[j].getSequence();
		}
		j++;
	    }
	    
	    int i = 0;
	    
	    while (i < str1.length() - 500) {
		
		String s1 = str1.substring(i,i+500);
		String s2 = str2.substring(i,i+500);
		
		double loglike = MatrixUtil.get_dist_cg(matrix1,matrix2,s1,s2,pi1,pi2);

		int    num1     = MatrixUtil.count_motifs(s1,"CG");
		int    num2     = MatrixUtil.count_motifs(s2,"CG");
		int    numc     = MatrixUtil.count_conserved_motifs(s1,s2,"CG");

		System.out.println("Log " + i + " " + num1 + " " + num2 + " " + numc + " " + loglike);
		
		i += 100;
	    }
	    
	    //while (i < 1) {
	    //    double[][] matrix = Matrix.get_matrix(i,41.0,false);
	    
	    //    Matrix.print_matrix(String.valueOf(i),matrix,matrix.length,matrix[0].length);
	    
	    //    i+= 0.1;
	    //}
	} catch (IOException e) {
	    System.out.println("Exception " + e);
	}
	
    }
}
