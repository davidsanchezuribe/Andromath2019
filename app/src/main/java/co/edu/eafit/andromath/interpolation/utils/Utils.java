package co.edu.eafit.andromath.interpolation.utils;

import co.edu.eafit.andromath.linearsystems.gaussianelimination.PartialPivotingGaussEliminationActivity;

/**
 * Created by User on 5/28/2017.
 */

public class Utils {

    public static class MatrixMarks{
        public double Ab[][];
        public int marks[];
    }

    public static class LU{
        public double L[][];
        public double U[][];
    }

    public static class LUMarks extends LU{
        public int marks[];
    }

    public static LUMarks LUGaussPivotingModified(double[][] a){
        int n = a.length;
        MatrixMarks mm;
        LUMarks mlu = new LUMarks();
        mlu.L = constructIdentity(n);
        int marks[] = new int[n];
        double mult;
        for(int i = 0; i < n; i++){
            marks[i] = i;
        }
        for(int k = 0; k < n-1; k++){
            mm = partialPivotWithMarks(a,marks,k);
            a = mm.Ab;
            marks = mm.marks;
            for (int i = k+1; i < n; i++){
                a[i][k] = a[i][k]/a[k][k];
                /*
                mult = a[i][k]/a[k][k];
                mlu.L[i][k] = mult;
                //Instead of working with A and deconstructing it, we simple clear a normally
                //and insert into L, thus, giving us U as well, simplifying the method.
                */
                for(int j = k+1; j < n; j++){
                    a[i][j] = a[i][j] - a[i][k]*a[k][j];
                }
            }
        }
        for(int j = 0; j < n-1; j++){
            for(int i = j+1; i < n; i++){
                mlu.L[i][j] = a[i][j];
            }
        }
        mlu.U = a;
        mlu.marks = marks;
        return mlu;
    }

    public static LU LUGauss(double[][] a){
        int n = a.length;
        LU mlu = new LU();
        mlu.L = constructIdentity(n);
        //mlu.U = constructIdentity(n);
        double mult;
        for (int k = 0; k < n-1; k++){
            for (int i = k+1; i < n; i++){
                mult = a[i][k]/a[k][k];
                mlu.L[i][k] = mult;
                for (int j = k; j < n; j++){
                    a[i][j] = a[i][j] - mult*a[k][j];
                }
            }
        }
        mlu.U = a;
        return mlu;
    }

    public static LU LUDoolitle(double[][] a){
        int n = a.length;
        LU mlu = new LU();
        mlu.L = constructIdentity(n);
        mlu.U = constructIdentity(n);
        double s1, s2, s3;
        for(int k = 0; k < n; k++){
            s1 = 0;
            for (int p = 0; p < k; p++){
                s1 = s1 + mlu.L[k][p] * mlu.U[p][k];
            }
            //mlu.L[k][k] = 1;
            mlu.U[k][k] = a[k][k] - s1;
            for(int i = k+1; i < n; i++){
                s2 = 0;
                for(int p = 0; p < k; p++){
                    s2 = s2 + mlu.L[i][p] * mlu.U[p][k];
                }
                mlu.L[i][k] = (a[i][k] - s2)/mlu.U[k][k];
            }
            for(int j = k+1; j < n; j++){
                s3 = 0;
                for(int p = 0; p < k; p++){
                    s3 = s3 + mlu.L[k][p] * mlu.U[p][j];
                }
                mlu.U[k][j] = (a[k][j] - s3)/mlu.L[k][k];
            }
        }
        return mlu;
    }

    public static LU LUCroult(double[][] a){
        int n = a.length;
        LU mlu = new LU();
        mlu.L = constructIdentity(n);
        mlu.U = constructIdentity(n);
        double s1, s2, s3;
        for(int k = 0; k < n; k++){
            s1 = 0;
            for (int p = 0; p < k; p++){
                s1 = s1 + mlu.L[k][p] * mlu.U[p][k];
            }
            //mlu.L[k][k] = 1;
            mlu.L[k][k] = a[k][k] - s1;
            for(int i = k+1; i < n; i++){
                s2 = 0;
                for(int p = 0; p < k; p++){
                    s2 = s2 + mlu.L[i][p] * mlu.U[p][k];
                }
                mlu.L[i][k] = (a[i][k] - s2)/mlu.U[k][k];
            }
            for(int j = k+1; j < n; j++){
                s3 = 0;
                for(int p = 0; p < k; p++){
                    s3 = s3 + mlu.L[k][p] * mlu.U[p][j];
                }
                mlu.U[k][j] = (a[k][j] - s3)/mlu.L[k][k];
            }
        }
        return mlu;
    }

    public static LU LUCholesky(double[][] a){
        int n = a.length;
        LU mlu = new LU();
        mlu.L = constructIdentity(n);
        mlu.U = constructIdentity(n);
        double s1, s2, s3;
        for(int k = 0; k < n; k++){
            s1 = 0;
            for (int p = 0; p < k; p++){
                s1 = s1 + mlu.L[k][p] * mlu.U[p][k];
            }
            //mlu.L[k][k] = 1;
            mlu.L[k][k] = Math.sqrt(a[k][k] - s1);
            mlu.U[k][k] = mlu.L[k][k];
            for(int i = k+1; i < n; i++){
                s2 = 0;
                for(int p = 0; p < k; p++){
                    s2 = s2 + mlu.L[i][p] * mlu.U[p][k];
                }
                mlu.L[i][k] = (a[i][k] - s2)/mlu.U[k][k];
            }
            for(int j = k+1; j < n; j++){
                s3 = 0;
                for(int p = 0; p < k; p++){
                    s3 = s3 + mlu.L[k][p] * mlu.U[p][j];
                }
                mlu.U[k][j] = (a[k][j] - s3)/mlu.L[k][k];
            }
        }
        return mlu;
    }

    public static double norm(double[] x1, double[] x0){
        int n = x1.length;
        double sum = 0;
        for(int i = 0; i < n; i++){
            sum = sum + Math.pow(x1[i]-x0[i], 2);
        }
        return Math.sqrt(sum);
    }

    public static double[] doJacobiRelaxed(double[][] a, double[] b, double[] x0, double tol, double lambda, int niter){
        int count = 0;
        double disp = tol + 1;
        double x1[];
        while (disp > tol && count < niter){
            x1 = calculateNewJacobiRelaxed(a,b,x0, lambda);
            disp = norm(x1, x0);
            x0 = x1;
            count++;
        }
        if (disp < tol){
            return x0;
        } else {
            return null;
        }
    }

    public static double[] calculateNewJacobiRelaxed(double[][] a, double b[], double[] x0, double lambda){
        int n = a.length;
        double sum, x1[] = new double[x0.length];
        for (int i = 0; i < n; i++){
            sum = 0;
            for (int j = 0; j < n; j++){
                if(j != i){
                    sum = sum + a[i][j] * x0[j];
                }
            }
            x1[i] = (b[i] - sum)/a[i][i];
            x1[i] = lambda*x1[i] + (1-lambda)*x0[i];
        }
        return x1;
    }

    public static double[] doJacobi(double[][] a, double[] b, double[] x0, double tol, int niter){
        int count = 0;
        double disp = tol + 1;
        double x1[];
        while (disp > tol && count < niter){
            x1 = calculateNewJacobi(a,b,x0);
            disp = norm(x1, x0);
            x0 = x1;
            count++;
        }
        if (disp < tol){
            return x0;
        } else {
            return null;
        }
    }

    public static double[] calculateNewJacobi(double[][] a, double b[], double[] x0){
        int n = a.length;
        double sum, x1[] = new double[x0.length];
        for (int i = 0; i < n; i++){
            sum = 0;
            for (int j = 0; j < n; j++){
                if(j != i){
                    sum = sum + a[i][j] * x0[j];
                }
            }
            x1[i] = (b[i] - sum)/a[i][i];
        }
        return x1;
    }

    public static double[] doGaussSeidelRelaxed(double[][] a, double[] b, double[] x0, double tol, double lambda, int niter){
        int count = 0;
        double disp = tol + 1;
        double x1[];
        while (disp > tol && count < niter){
            x1 = calculateNewGaussSeidelRelaxed(a,b,x0, lambda);
            disp = norm(x1, x0);
            x0 = x1;
            count++;
        }
        if (disp < tol){
            return x0;
        } else {
            return null;
        }
    }

    public static double[] calculateNewGaussSeidelRelaxed(double[][] a, double b[], double[] x0, double lambda){
        int n = a.length;
        double sum, x1[] = new double[x0.length];
        for (int i = 0; i < n; i++){
            x1[i] = x0[i];
        }
        for (int i = 0; i < n; i++){
            sum = 0;
            for (int j = 0; j < n; j++){
                if(j != i){
                    sum = sum + a[i][j] * x1[j];
                }
            }
            x1[i] = (b[i] - sum)/a[i][i];
            x1[i] = lambda*x1[i] + (1-lambda)*x0[i];
        }
        return x1;
    }

    public static double[] doGaussSeidel(double[][] a, double[] b, double[] x0, double tol, int niter){
        int count = 0;
        double disp = tol + 1;
        double x1[];
        while (disp > tol && count < niter){
            x1 = calculateNewGaussSeidel(a,b,x0);
            disp = norm(x1, x0);
            x0 = x1;
            count++;
        }
        if (disp < tol){
            return x0;
        } else {
            return null;
        }
    }

    public static double[] calculateNewGaussSeidel(double[][] a, double b[], double[] x0){
        int n = a.length;
        double sum, x1[] = new double[x0.length];
        for (int i = 0; i < n; i++){
            x1[i] = x0[i];
        }
        for (int i = 0; i < n; i++){
            sum = 0;
            for (int j = 0; j < n; j++){
                if(j != i){
                    sum = sum + a[i][j] * x1[j];
                }
            }
            x1[i] = (b[i] - sum)/a[i][i];
        }
        return x1;
    }

    public static double[][] constructIdentity(int n){
        double a[][] = new double[n][n];
        for (int i = 0; i < n; i++){
            a[i][i] = 1;
        }
        return a;
    }

    public static double[][] augmentMatrix(double[][] a, double[] b){
        double Ab[][] = new double[a.length][a.length+1];
        for (int i=0; i < a.length; i++){
            for(int j = 0; j < a.length; j++){
                Ab[i][j] = a[i][j];
            }
        }
        for (int i = 0; i < a.length; i++){
            Ab[i][a.length] = b[i];
        }
        return Ab;
    }

    public static double[] progressiveSubstitution(double[][] Ab){
        int n = Ab.length;
        double x[] = new double[n];
        double sum;
        x[0] = Ab[0][n]/Ab[0][0];
        for(int i = 1; i < n; i++){
            sum = 0;
            for (int p = 0; p < i; p++){
                sum = sum + Ab[i][p]*x[p];
            }
            x[i] = (Ab[i][n] - sum)/Ab[i][i];
        }
        return x;
    }

    public static double[] regressiveSubstitution(double[][] Ab){
        int n = Ab.length;
        double x[] = new double[n];
        double sum;
        x[n-1] = Ab[n-1][n]/Ab[n-1][n-1];
        for (int i = n - 2; i > -1; i--){
            sum = 0;
            for (int p = i+1; p < n; p++){
                sum = sum + Ab[i][p]*x[p];
            }
            x[i] = (Ab[i][n] - sum)/Ab[i][i];
        }
        return x;
    }

    public static double[][] partialPivot(double[][] Ab, int k) {
        int n = Ab.length;
        double largest = Math.abs(Ab[k][k]);
        int largestrow = k;
        for(int s = k+1; s < n; s++){
            if(Math.abs(Ab[s][k]) > largest){
                largest = Math.abs(Ab[s][k]);
                largestrow = s;
            }
        }
        if(largest == 0.0f){
            //TODO: Fix this and throw error instead, add error handling
            return Ab;
        } else {
            if (largestrow != k){
                Ab = exchangeRows(Ab, largestrow, k);
            }
            return Ab;
        }
    }

    public static MatrixMarks partialPivotWithMarks(double[][] A, int[] marks, int k){
        MatrixMarks r = new MatrixMarks();
        int n = A.length;
        double largest = Math.abs(A[k][k]);
        int largestrow = k;
        for(int s = k+1; s < n; s++){
            if(Math.abs(A[s][k]) > largest){
                largest = Math.abs(A[s][k]);
                largestrow = s;
            }
        }
        if(largest == 0.0f){
            //TODO: Fix this and throw error instead, add error handling
        } else {
            if (largestrow != k){
                A = exchangeRows(A, largestrow, k);
                marks = exchangeMarks(marks, largestrow, k);
            }
        }
        r.Ab = A;
        r.marks = marks;
        return r;
    }

    public static MatrixMarks totalPivot(double[][] Ab, int k, int[] marks){
        int n = Ab.length;
        double largest = 0;
        int largestrow = k;
        int largestcolumn = k;
        for (int r = k; r < n; r++){
            for (int s = k; s < n; s++){
                if(Math.abs(Ab[r][s]) > largest){
                    largest = Math.abs(Ab[r][s]);
                    largestrow = r;
                    largestcolumn = s;
                }
            }
        }
        MatrixMarks mm = new MatrixMarks();
        if (largest == 0){
            //TODO: This is wrong, needs to be fixed with exception throwing.
            mm.Ab = Ab;
            mm.marks = marks;
            return mm;
        }else{
            if(largestrow != k){
                Ab = exchangeRows(Ab, largestrow, k);
            }
            if(largestcolumn != k){
                Ab = exchangeColumns(Ab, largestcolumn, k);
                marks = exchangeMarks(marks, largestcolumn, k);
            }
            mm.Ab = Ab;
            mm.marks = marks;
            return mm;
        }
    }

    public static double[][] exchangeRows(double[][] Ab, int r1, int r2){
        double t[] = Ab[r1];
        Ab[r1] = Ab[r2];
        Ab[r2] = t;
        return Ab;
    }

    public static double[][] exchangeColumns(double[][] Ab, int c1, int c2){
        int n = Ab.length;
        double t;
        for(int i = 0; i < n; i++){
            t = Ab[i][c1];
            Ab[i][c1] = Ab[i][c2];
            Ab[i][c2] = t;
        }
        return Ab;
    }

    public static int[] exchangeMarks(int[] marks, int m1, int m2){
        int t = marks[m1];
        marks[m1] = marks[m2];
        marks[m2] = t;
        return marks;
    }

    public static double[] markAwareX(double[] x, int[] marks){
        int n = x.length;
        int j;
        double result[] = new double[n];
        for(int i = 0; i < n; i++){
            j = marks[i];
            result[j] = x[i];
        }
        return result;
    }

}