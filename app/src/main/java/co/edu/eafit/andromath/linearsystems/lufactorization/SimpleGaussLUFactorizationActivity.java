package co.edu.eafit.andromath.linearsystems.lufactorization;

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
import java.util.Objects;

import co.edu.eafit.andromath.R;
import co.edu.eafit.andromath.linearsystems.gaussianelimination.util.MatrixUtils;

import static co.edu.eafit.andromath.linearsystems.gaussianelimination.util.MatrixUtils.buildLMatrixPartially;
import static co.edu.eafit.andromath.linearsystems.gaussianelimination.util.MatrixUtils.getBVector;
import static co.edu.eafit.andromath.linearsystems.gaussianelimination.util.ViewUtils.getPxFromDp;
import static co.edu.eafit.andromath.util.Constants.DECIMALS_QUANTITY;
import static co.edu.eafit.andromath.util.Constants.MATRIX;
import static co.edu.eafit.andromath.util.Constants.ROUNDING_MODE;

public class SimpleGaussLUFactorizationActivity extends AppCompatActivity {

    LinearLayout linearLayoutSolutionStages;

    String solution;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_gauss_lufactorization);
        Objects.requireNonNull(getSupportActionBar()).hide();

        linearLayoutSolutionStages = (LinearLayout) findViewById(
                R.id.linearLayoutSolutionStages);

        solution = getResources().getString(R.string.solution);

        Intent intent = getIntent();

        BigDecimal[][] augmentedMatrix = (BigDecimal[][]) intent.
                getExtras().getSerializable(MATRIX);

        simpleGaussLUFactorization(augmentedMatrix);
    }

    private void simpleGaussLUFactorization(BigDecimal[][] augmentedMatrix) {

        int n = augmentedMatrix.length;
        BigDecimal multiplier;

        MatrixUtils.LUMatrix luMatrix = new MatrixUtils.LUMatrix();

        luMatrix.L = buildLMatrixPartially(n);

        for (int k = 0; k < n - 1; k++) {

            for (int i = k + 1; i < n; i++) {

                multiplier = augmentedMatrix[i][k].divide(augmentedMatrix[k][k],
                        DECIMALS_QUANTITY, ROUNDING_MODE);
                luMatrix.L[i][k] = multiplier;

                for (int j = k; j < n; j++)
                    augmentedMatrix[i][j] = augmentedMatrix[i][j].subtract(
                            multiplier.multiply(augmentedMatrix[k][j]));
            }

            addSolutionStage(k, augmentedMatrix);
        }

        showLMatrixSolution(luMatrix.L);

        luMatrix.U = augmentedMatrix;

        BigDecimal z[] = MatrixUtils.progressiveSubstitution(MatrixUtils.
                getAugmentedMatrix(luMatrix.L, getBVector(augmentedMatrix)));

        addZSolutionStage(z);

        BigDecimal x[] = MatrixUtils.regressiveSubstitution(
                MatrixUtils.getAugmentedMatrix(luMatrix.U, z));

        addXSolutionStage(x);
    }

    public void addSolutionStage(int k, BigDecimal[][] matrix) {

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;

        HorizontalScrollView horizontalScrollView = new
                HorizontalScrollView(this.getApplicationContext());
        horizontalScrollView.setLayoutParams(layoutParams);
        horizontalScrollView.setHorizontalScrollBarEnabled(false);

        TextView textViewStage = getStageTextView(k, layoutParams);

        TableLayout tableLayoutStage = new TableLayout(
                this.getApplicationContext());

        for (int i = 0; i < matrix.length; i++) {

            TableRow tableRowMatrix = new TableRow(
                    this.getApplicationContext());

            for (int j = 0; j < matrix.length; j++) {
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
            LayoutParams layoutParams) {

        TextView textViewStage = new TextView(
                this.getApplicationContext());

        textViewStage.setTextSize(20);
        textViewStage.setLayoutParams(layoutParams);
        textViewStage.setTypeface(null, Typeface.BOLD);

        textViewStage.setPadding(0, getPxFromDp(15),
                0, getPxFromDp(5));

        textViewStage.setText("STAGE #" + (k + 1));
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

    private void showLMatrixSolution(BigDecimal[][] lMatrix) {

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;

        HorizontalScrollView horizontalScrollView = new
                HorizontalScrollView(this.getApplicationContext());
        horizontalScrollView.setLayoutParams(layoutParams);
        horizontalScrollView.setHorizontalScrollBarEnabled(false);

        TextView textViewLSolution = getLSolutionTextView(layoutParams);

        TableLayout tableLayoutStage = new TableLayout(
                this.getApplicationContext());

        for (int i = 0; i < lMatrix.length; i++) {

            TableRow tableRowMatrix = new TableRow(
                    this.getApplicationContext());

            for (int j = 0; j < lMatrix.length; j++) {
                TextView textViewCell = getCellTextView(lMatrix[i][j]);

                tableRowMatrix.addView(textViewCell,
                        TableRow.LayoutParams.WRAP_CONTENT,
                        getPxFromDp(35));
            }

            tableLayoutStage.addView(tableRowMatrix);
        }

        linearLayoutSolutionStages.addView(textViewLSolution);
        horizontalScrollView.addView(tableLayoutStage);
        linearLayoutSolutionStages.addView(horizontalScrollView);
    }

    private TextView getLSolutionTextView(LinearLayout.LayoutParams layoutParams) {

        TextView textViewLMatrixSolution = new TextView(
                this.getApplicationContext());

        textViewLMatrixSolution.setTextSize(20);
        textViewLMatrixSolution.setLayoutParams(layoutParams);
        textViewLMatrixSolution.setTypeface(null, Typeface.BOLD);

        textViewLMatrixSolution.setPadding(0, getPxFromDp(15),
                0, getPxFromDp(5));

        textViewLMatrixSolution.setText("L MATRIX");
        textViewLMatrixSolution.setAllCaps(true);

        return textViewLMatrixSolution;
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
