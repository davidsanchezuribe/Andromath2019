package co.edu.eafit.andromath.linearsystems.gaussianelimination.util;

import java.math.BigDecimal;

import static co.edu.eafit.andromath.util.Constants.DECIMALS_QUANTITY;
import static co.edu.eafit.andromath.util.Constants.ROUNDING_MODE;

public class MatrixUtils {

    public static class MatrixMarks {
        public BigDecimal[][] matrixValues;
        public int matrixMarks[];
    }

    public static class LUMatrix {
        public BigDecimal L[][];
        public BigDecimal U[][];
    }

    public static BigDecimal[] regressiveSubstitution(BigDecimal[][] Ab) {

        int n = Ab.length;
        BigDecimal accumulator;

        BigDecimal solution[] = new BigDecimal[n];

        try {
            solution[n - 1] = (Ab[n - 1][n]).divide(Ab[n - 1][n - 1],
                    DECIMALS_QUANTITY, ROUNDING_MODE);

            for (int i = n - 2; i > -1; i--) {
                accumulator = BigDecimal.ZERO;

                for (int p = i + 1; p < n; p++)
                    accumulator = accumulator.add(
                            Ab[i][p].multiply(solution[p]));

                solution[i] = (Ab[i][n].subtract(accumulator)).divide(Ab[i][i],
                        DECIMALS_QUANTITY, ROUNDING_MODE);
            }
        } catch(ArithmeticException e) {
            // TODO: throw message.
            System.out.println("Division por cero");
        }

        return solution;
    }

    public static BigDecimal[] progressiveSubstitution(BigDecimal[][] Ab){

        int n = Ab.length;
        BigDecimal accumulator;

        BigDecimal solution[] = new BigDecimal[n];

        try {
            solution[0] = Ab[0][n].divide(Ab[0][0],
                    DECIMALS_QUANTITY, ROUNDING_MODE);

            for(int i = 1; i < n; i++){
                accumulator = BigDecimal.ZERO;

                for (int p = 0; p < i; p++)
                    accumulator = accumulator.add(
                            Ab[i][p].multiply(solution[p]));

                solution[i] = (Ab[i][n].subtract(accumulator)).
                        divide(Ab[i][i], DECIMALS_QUANTITY, ROUNDING_MODE);
            }
        } catch(ArithmeticException e) {
            // TODO: throw message.
            System.out.println("Zero division");
        }

        return solution;
    }

    public static BigDecimal[][] partialPivoting(BigDecimal[][] matrixValues, int k) {

        int n = matrixValues.length;
        BigDecimal largest = matrixValues[k][k].abs();

        int largestRow = k;

        for (int index = k + 1; index < n; index++) {
            if (matrixValues[index][k].abs().compareTo(largest) > 0) {
                largest = matrixValues[index][k].abs();
                largestRow = index;
            }
        }

        if (largest.compareTo(BigDecimal.ZERO) == 0) {
            return null; // There were an error.
        } else {
            if (largestRow != k)
                matrixValues = exchangeRows(
                        matrixValues, largestRow, k);

            return matrixValues;
        }
    }

    public static MatrixMarks totalPivoting(BigDecimal[][] matrixValues,
                                               int k, int[] marks) {

        int n = matrixValues.length;
        BigDecimal largest = BigDecimal.ZERO;

        int largestRow = k;
        int largestCol = k;

        for (int rowIndicator = k; rowIndicator < n; rowIndicator++) {

            for (int colIndicator = k; colIndicator < n; colIndicator++) {

                if (matrixValues[rowIndicator][colIndicator].
                        abs().compareTo(largest) > 0) {

                    largest = matrixValues[rowIndicator][colIndicator].abs();
                    largestRow = rowIndicator;
                    largestCol = colIndicator;
                }
            }
        }

        MatrixMarks matrixMarks = new MatrixMarks();

        if (largest.compareTo(BigDecimal.ZERO) == 0) {
            return null; // There were an error.

        } else {

            if (largestRow != k)
                matrixValues = exchangeRows(
                        matrixValues, largestRow, k);

            if (largestCol != k){
                matrixValues = exchangeCols(matrixValues, largestCol, k);
                marks = exchangeMatrixMarks(marks, largestCol, k);
            }

            matrixMarks.matrixValues = matrixValues;
            matrixMarks.matrixMarks = marks;

            return matrixMarks;
        }
    }

    private static BigDecimal[][] exchangeRows(BigDecimal[][] matrixValues,
                                        int firstRow, int secondRow) {

        BigDecimal row[] = matrixValues[firstRow];
        matrixValues[firstRow] = matrixValues[secondRow];
        matrixValues[secondRow] = row;

        return matrixValues;
    }

    private static BigDecimal[][] exchangeCols(BigDecimal[][] matrixValues,
                                          int firstCol, int secondCol){
        int n = matrixValues.length;
        BigDecimal colValue;

        for (int i = 0; i < n; i++) {
            colValue = matrixValues[i][firstCol];
            matrixValues[i][firstCol] = matrixValues[i][secondCol];
            matrixValues[i][secondCol] = colValue;
        }

        return matrixValues;
    }

    private static int[] exchangeMatrixMarks(int[] matrixMarks,
                                            int firstMark, int secondMark) {

        int markValue = matrixMarks[firstMark];
        matrixMarks[firstMark] = matrixMarks[secondMark];
        matrixMarks[secondMark] = markValue;

        return matrixMarks;
    }

    public static BigDecimal[] markAwareX(BigDecimal[] solution, int[] matrixMarks){

        int n = solution.length, marksIndex;
        BigDecimal result[] = new BigDecimal[n];

        for(int i = 0; i < n; i++) {
            marksIndex = matrixMarks[i];
            result[marksIndex] = solution[i];
        }

        return result;
    }

    public static BigDecimal[][] buildLMatrixPartially(int n) {

        BigDecimal identityMatrix[][] = new BigDecimal[n][n];

        for (int i = 0; i < n; i++) {
            identityMatrix[i][i] = BigDecimal.ONE;
            for (int j = 0; j < n; j++)
                if (j != i) identityMatrix[i][j] = BigDecimal.ZERO;
        }

        return identityMatrix;
    }

    public static BigDecimal[][] buildUMatrixPartially(int n) {

        BigDecimal identityMatrix[][] = new BigDecimal[n][n];

        for (int i = 0; i < n; i++) {
            identityMatrix[i][i] = BigDecimal.ONE;
            for (int j = 0; j < n; j++)
                if (j != i) identityMatrix[i][j] = BigDecimal.ZERO;
        }

        return identityMatrix;
    }

    public static BigDecimal[][] initializeZeroesMatrix(int n) {

        BigDecimal identityMatrix[][] = new BigDecimal[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++)
                identityMatrix[i][j] = BigDecimal.ZERO;
        }

        return identityMatrix;
    }

    public static BigDecimal[][] getAugmentedMatrix(BigDecimal[][] matrixValues,
                                                    BigDecimal[] independentVector) {

        BigDecimal augmentedMatrix[][] = new BigDecimal
                [matrixValues.length][matrixValues.length + 1];

        for (int i = 0; i < matrixValues.length; i++)
            for (int j = 0; j < matrixValues.length; j++)
                augmentedMatrix[i][j] = matrixValues[i][j];

        for (int i = 0; i < matrixValues.length; i++)
            augmentedMatrix[i][matrixValues.length] = independentVector[i];

        return augmentedMatrix;
    }

    public static BigDecimal[] getBVector(BigDecimal[][] matrixValues) {

        BigDecimal[] independentVector = new
                BigDecimal[matrixValues.length];

        for (int i = 0; i < independentVector.length; i++)
            independentVector[i] = matrixValues[i][matrixValues.length];

        return independentVector;
    }

    public static BigDecimal euclideanNorm(BigDecimal[] iterationValues,
                                     BigDecimal[] lastIteration) {

        int n = iterationValues.length;

        BigDecimal summation = BigDecimal.ZERO;

        for (int i = 0; i < n; i++)
            summation = summation.add((iterationValues[i].subtract(lastIteration[i]))
                    .multiply(iterationValues[i].subtract(lastIteration[i])));

        return new BigDecimal(Math.sqrt(summation.doubleValue()));
    }

    public static String matrixAsString(BigDecimal[][] augmentedMatrix) {

        StringBuilder matrixAsAString = new StringBuilder();

        for (int i = 0; i < augmentedMatrix.length; i++) {
            for (int j = 0; j < augmentedMatrix[0].length; j++) {
                matrixAsAString.append(augmentedMatrix[i][j]);
                matrixAsAString.append("|");
            }
        }

        matrixAsAString.append(augmentedMatrix.length);

        return matrixAsAString.toString();
    }
}