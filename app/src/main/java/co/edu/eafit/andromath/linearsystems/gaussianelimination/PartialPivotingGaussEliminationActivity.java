package co.edu.eafit.andromath.linearsystems.gaussianelimination;

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

import static co.edu.eafit.andromath.linearsystems.gaussianelimination.util.ViewUtils.getPxFromDp;
import static co.edu.eafit.andromath.util.Constants.DECIMALS_QUANTITY;
import static co.edu.eafit.andromath.util.Constants.MATRIX;
import static co.edu.eafit.andromath.util.Constants.ROUNDING_MODE;

public class PartialPivotingGaussEliminationActivity extends AppCompatActivity {

    LinearLayout linearLayoutSolutionStages;

    String solution;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partial_pivoting_gauss_elimination);
        Objects.requireNonNull(getSupportActionBar()).hide();

        linearLayoutSolutionStages = (LinearLayout) findViewById(
                R.id.linearLayoutSolutionStages);

        solution = getResources().getString(R.string.solution);

        Intent intent = getIntent();

        BigDecimal[][] matrixValues = (BigDecimal[][]) intent.
                getExtras().getSerializable(MATRIX);

        addMatrixSolution(partialPivotingGaussElimination(matrixValues));
    }

    private BigDecimal[] partialPivotingGaussElimination(BigDecimal[][] matrixValues) {

        int n = matrixValues.length;
        BigDecimal multiplier;

        for (int k = 0; k < n - 1 && matrixValues != null; k++) {

            matrixValues = MatrixUtils.partialPivoting(matrixValues, k);

            if (matrixValues != null) {

                for (int i = k + 1; i < n; i++) {

                    multiplier = matrixValues[i][k].divide(
                            matrixValues[k][k], DECIMALS_QUANTITY, ROUNDING_MODE);

                    for (int j = k; j < n + 1; j++) {
                        matrixValues[i][j] = matrixValues[i][j].
                                subtract(multiplier.multiply(matrixValues[k][j]));
                    }
                }

                addSolutionStage(k, matrixValues);
            }
        }

        return MatrixUtils.regressiveSubstitution(matrixValues);
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

    private void addMatrixSolution(BigDecimal[] systemSolved) {

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;

        TextView textViewSolution = getSolutionTextView(layoutParams);

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

    private TextView getSolutionTextView(LinearLayout.LayoutParams layoutParams) {

        TextView textViewSolution = new TextView(
                this.getApplicationContext());

        textViewSolution.setTextSize(20);
        textViewSolution.setTypeface(null, Typeface.BOLD);
        textViewSolution.setLayoutParams(layoutParams);

        textViewSolution.setPadding(0, getPxFromDp(15),
                0, getPxFromDp(5));

        textViewSolution.setText(solution);
        textViewSolution.setAllCaps(true);

        return textViewSolution;
    }
}
