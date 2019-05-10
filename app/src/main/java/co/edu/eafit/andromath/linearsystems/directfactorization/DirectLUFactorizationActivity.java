package co.edu.eafit.andromath.linearsystems.directfactorization;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;

import co.edu.eafit.andromath.R;
import co.edu.eafit.andromath.linearsystems.gaussianelimination.util.MatrixUtils;

import static co.edu.eafit.andromath.linearsystems.gaussianelimination.util.MatrixUtils.buildLMatrixPartially;
import static co.edu.eafit.andromath.linearsystems.gaussianelimination.util.MatrixUtils.buildUMatrixPartially;
import static co.edu.eafit.andromath.linearsystems.gaussianelimination.util.MatrixUtils.initializeZeroesMatrix;
import static co.edu.eafit.andromath.linearsystems.gaussianelimination.util.ViewUtils.getPxFromDp;
import static co.edu.eafit.andromath.util.Constants.DECIMALS_QUANTITY;
import static co.edu.eafit.andromath.util.Constants.MATRIX;
import static co.edu.eafit.andromath.util.Constants.ROUNDING_MODE;
import static co.edu.eafit.andromath.util.Constants.SELECTION;

public class DirectLUFactorizationActivity extends AppCompatActivity {

    LinearLayout linearLayoutSolutionStages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_lufactorization);
        Objects.requireNonNull(getSupportActionBar()).hide();

        linearLayoutSolutionStages = (LinearLayout) findViewById(
                R.id.linearLayoutSolutionStages);

        Intent intent = getIntent();

        BigDecimal[][] augmentedMatrix = (BigDecimal[][]) intent.
                getExtras().getSerializable(MATRIX);

        int methodSelection = (int) intent.getExtras().
                getSerializable(SELECTION);

        directLUFactorization(augmentedMatrix, methodSelection);
    }

    private void directLUFactorization(BigDecimal[][] augmentedMatrix,
                                       int methodSelection) {

        try {
            MatrixUtils.LUMatrix luMatrix;

            switch (methodSelection) {
                default:
                case 0:
                    luMatrix = LUDoolitle(augmentedMatrix);
                    break;
                case 1:
                    luMatrix = LUCholesky(augmentedMatrix);
                    break;
                case 2:
                    luMatrix = LUCrout(augmentedMatrix);
                    break;
            }

            BigDecimal z[] = MatrixUtils.progressiveSubstitution(MatrixUtils.
                    getAugmentedMatrix(luMatrix.L, MatrixUtils.getBVector(augmentedMatrix)));

            addZSolutionStage(z);

            BigDecimal x[] = MatrixUtils.regressiveSubstitution(
                    MatrixUtils.getAugmentedMatrix(luMatrix.U, z));

            addXSolutionStage(x);

        } catch(NumberFormatException e) {
            System.out.println("There were an error");
        }
    }

    public MatrixUtils.LUMatrix LUDoolitle(BigDecimal[][] augmentedMatrix) {

        int n = augmentedMatrix.length;

        MatrixUtils.LUMatrix luMatrix = new MatrixUtils.LUMatrix();
        luMatrix.L = buildLMatrixPartially(n);
        luMatrix.U = initializeZeroesMatrix(n);

        BigDecimal s1, s2, s3;

        for (int k = 0; k < n; k++) {
            s1 = BigDecimal.ZERO;

            for (int p = 0; p < k; p++)
                s1 = s1.add(luMatrix.L[k][p].
                        multiply(luMatrix.U[p][k]));

            luMatrix.U[k][k] = augmentedMatrix[k][k].subtract(s1);

            for (int i = k + 1; i < n; i++) {
                s2 = BigDecimal.ZERO;

                for (int p = 0; p < k; p++)
                    s2 = s2.add(luMatrix.L[i][p].
                            multiply(luMatrix.U[p][k]));

                luMatrix.L[i][k] = (augmentedMatrix[i][k].subtract(s2)).
                        divide(luMatrix.U[k][k], DECIMALS_QUANTITY, ROUNDING_MODE);
            }

            for (int j = k + 1; j < n; j++) {
                s3 = BigDecimal.ZERO;

                for (int p = 0; p < k; p++)
                    s3 = s3.add(luMatrix.L[k][p].
                            multiply(luMatrix.U[p][j]));

                luMatrix.U[k][j] = (augmentedMatrix[k][j].subtract(s3)).
                        divide(luMatrix.L[k][k], DECIMALS_QUANTITY, ROUNDING_MODE);
            }

            addSolutionStage(k, luMatrix.L, "L");
            addSolutionStage(k, luMatrix.U, "U");
        }

        return luMatrix;
    }

    public MatrixUtils.LUMatrix LUCholesky(BigDecimal[][] augmentedMatrix) {

        int n = augmentedMatrix.length;

        MatrixUtils.LUMatrix luMatrix = new MatrixUtils.LUMatrix();
        luMatrix.L = initializeZeroesMatrix(n);
        luMatrix.U = initializeZeroesMatrix(n);

        BigDecimal s1, s2, s3;

        for (int k = 0; k < n; k++) {
            s1 = BigDecimal.ZERO;

            for (int p = 0; p < k; p++)
                s1 = s1.add(luMatrix.L[k][p].
                        multiply(luMatrix.U[p][k]));

            luMatrix.L[k][k] = BigDecimal.valueOf(Math.sqrt(
                    augmentedMatrix[k][k].subtract(s1).doubleValue()));

            luMatrix.U[k][k] = luMatrix.L[k][k];

            for (int i = k + 1; i < n; i++) {
                s2 = BigDecimal.ZERO;

                for (int p = 0; p < k; p++)
                    s2 = s2.add(luMatrix.L[i][p].
                            multiply(luMatrix.U[p][k]));

                luMatrix.L[i][k] = (augmentedMatrix[i][k].subtract(s2)).divide(
                        luMatrix.U[k][k], DECIMALS_QUANTITY, ROUNDING_MODE);
            }

            for (int j = k + 1; j < n; j++) {
                s3 = BigDecimal.ZERO;

                for (int p = 0; p < k; p++)
                    s3 = s3.add(luMatrix.L[k][p].
                            multiply(luMatrix.U[p][j]));

                luMatrix.U[k][j] = (augmentedMatrix[k][j].subtract(s3)).divide(
                        luMatrix.L[k][k], DECIMALS_QUANTITY, ROUNDING_MODE);
            }

            addSolutionStage(k, luMatrix.L, "L");
            addSolutionStage(k, luMatrix.U, "U");
        }

        return luMatrix;
    }

    public MatrixUtils.LUMatrix LUCrout(BigDecimal[][] augmentedMatrix) {

        int n = augmentedMatrix.length;

        MatrixUtils.LUMatrix luMatrix = new MatrixUtils.LUMatrix();
        luMatrix.L = initializeZeroesMatrix(n);
        luMatrix.U = buildUMatrixPartially(n);

        BigDecimal s1, s2, s3;

        for (int k = 0; k < n; k++) {
            s1 = BigDecimal.ZERO;

            for (int p = 0; p < k; p++)
                s1 = s1.add(luMatrix.L[k][p].
                        multiply(luMatrix.U[p][k]));

            luMatrix.L[k][k] = augmentedMatrix[k][k].subtract(s1);

            for (int i = k + 1; i < n; i++) {
                s2 = BigDecimal.ZERO;

                for (int p = 0; p < k; p++)
                    s2 = s2.add(luMatrix.L[i][p].
                            multiply(luMatrix.U[p][k]));

                luMatrix.L[i][k] = (augmentedMatrix[i][k].subtract(s2)).divide(
                        luMatrix.U[k][k], DECIMALS_QUANTITY, ROUNDING_MODE);
            }

            for (int j = k + 1; j < n; j++) {
                s3 = BigDecimal.ZERO;

                for (int p = 0; p < k; p++)
                    s3 = s3.add(luMatrix.L[k][p].
                            multiply(luMatrix.U[p][j]));

                luMatrix.U[k][j] = (augmentedMatrix[k][j].subtract(s3)).divide(
                        luMatrix.L[k][k], DECIMALS_QUANTITY, ROUNDING_MODE);
            }

            addSolutionStage(k, luMatrix.L, "L");
            addSolutionStage(k, luMatrix.U, "U");
        }

        return luMatrix;
    }

    public void addSolutionStage(int k, BigDecimal[][] matrix, String matrixType) {

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;

        HorizontalScrollView horizontalScrollView = new
                HorizontalScrollView(this.getApplicationContext());
        horizontalScrollView.setLayoutParams(layoutParams);
        horizontalScrollView.setHorizontalScrollBarEnabled(false);

        TextView textViewStage = getStageTextView(k, layoutParams, matrixType);

        TableLayout tableLayoutStage = new TableLayout(
                this.getApplicationContext());

        for (int i = 0; i < matrix.length; i++) {

            TableRow tableRowMatrix = new TableRow(
                    this.getApplicationContext());

            for (int j = 0; j < matrix[0].length; j++) {
                TextView textViewCell = getCellTextView(matrix[i][j]);

                tableRowMatrix.addView(textViewCell,
                        TableRow.LayoutParams.WRAP_CONTENT,
                        getPxFromDp(35));
            }

            tableLayoutStage.addView(tableRowMatrix);
        }

        linearLayoutSolutionStages.addView(textViewStage);
        horizontalScrollView.addView(tableLayoutStage);
        linearLayoutSolutionStages.addView(horizontalScrollView);
    }

    private TextView getStageTextView(int k, LinearLayout.
            LayoutParams layoutParams, String matrixType) {

        TextView textViewStage = new TextView(
                this.getApplicationContext());

        textViewStage.setTextSize(20);
        textViewStage.setLayoutParams(layoutParams);
        textViewStage.setTypeface(null, Typeface.BOLD);

        textViewStage.setPadding(0, getPxFromDp(15),
                0, getPxFromDp(5));

        textViewStage.setText("STAGE #" + (k + 1) + " - " + matrixType + " SOLUTION");
        textViewStage.setAllCaps(true);

        return textViewStage;
    }

    private TextView getCellTextView(BigDecimal cellValue) {

        TextView textViewCell = new TextView(
                this.getApplicationContext());

        textViewCell.setText(String.valueOf(cellValue.
                setScale(3, ROUNDING_MODE).doubleValue()));
        textViewCell.setPadding(20, 10, 20, 10);
        textViewCell.setGravity(Gravity.CENTER);

        return textViewCell;
    }

    private void addZSolutionStage(BigDecimal[] systemSolved) {

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;

        TextView textViewSolution = getZSolutionTextView(layoutParams);

        TextView[] systemSolution = new TextView[systemSolved.length];

        linearLayoutSolutionStages.addView(textViewSolution);

        for (int i = 0; i < systemSolution.length; i++) {

            systemSolution[i] = new TextView(
                    this.getApplicationContext());

            systemSolution[i].setText("Z" + (i + 1) + " = " + systemSolved[i].
                    setScale(DECIMALS_QUANTITY, ROUNDING_MODE).doubleValue());

            systemSolution[i].setPadding(0,
                    getPxFromDp(10), 0, getPxFromDp(5));
            systemSolution[i].setGravity(Gravity.CENTER);

            linearLayoutSolutionStages.addView(systemSolution[i]);
        }
    }

    private TextView getZSolutionTextView(LinearLayout.LayoutParams layoutParams) {

        TextView textViewSolution = new TextView(
                this.getApplicationContext());

        textViewSolution.setTextSize(20);
        textViewSolution.setTypeface(null, Typeface.BOLD);
        textViewSolution.setLayoutParams(layoutParams);

        textViewSolution.setPadding(0, getPxFromDp(15),
                0, getPxFromDp(5));

        textViewSolution.setText("Z SOLUTION");
        textViewSolution.setAllCaps(true);

        return textViewSolution;
    }

    private void addXSolutionStage(BigDecimal[] systemSolved) {

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;

        TextView textViewSolution = getXSolutionTextView(layoutParams);

        TextView[] systemSolution = new TextView[systemSolved.length];

        linearLayoutSolutionStages.addView(textViewSolution);

        for (int i = 0; i < systemSolution.length; i++) {

            systemSolution[i] = new TextView(
                    this.getApplicationContext());

            systemSolution[i].setText("X" + (i + 1) + " = " + systemSolved[i].
                    setScale(DECIMALS_QUANTITY, ROUNDING_MODE).doubleValue());

            systemSolution[i].setPadding(0,
                    getPxFromDp(10), 0, getPxFromDp(5));
            systemSolution[i].setGravity(Gravity.CENTER);

            linearLayoutSolutionStages.addView(systemSolution[i]);
        }
    }

    private TextView getXSolutionTextView(LinearLayout.LayoutParams layoutParams) {

        TextView textViewSolution = new TextView(
                this.getApplicationContext());

        textViewSolution.setTextSize(20);
        textViewSolution.setTypeface(null, Typeface.BOLD);
        textViewSolution.setLayoutParams(layoutParams);

        textViewSolution.setPadding(0, getPxFromDp(15),
                0, getPxFromDp(5));

        textViewSolution.setText("X SOLUTION");
        textViewSolution.setAllCaps(true);

        return textViewSolution;
    }
}