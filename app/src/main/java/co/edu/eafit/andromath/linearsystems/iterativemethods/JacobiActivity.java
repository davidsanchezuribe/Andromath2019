package co.edu.eafit.andromath.linearsystems.iterativemethods;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Objects;

import co.edu.eafit.andromath.R;
import co.edu.eafit.andromath.linearsystems.gaussianelimination.util.MatrixUtils;

import static co.edu.eafit.andromath.linearsystems.gaussianelimination.util.ViewUtils.getPxFromDp;
import static co.edu.eafit.andromath.util.Constants.DECIMALS_QUANTITY;
import static co.edu.eafit.andromath.util.Constants.MATRIX;
import static co.edu.eafit.andromath.util.Constants.NORMAL_METHOD;
import static co.edu.eafit.andromath.util.Constants.NOTATION_FORMAT;
import static co.edu.eafit.andromath.util.Constants.RELAXED_METHOD;
import static co.edu.eafit.andromath.util.Constants.ROUNDING_MODE;

public class JacobiActivity extends AppCompatActivity {

    TableLayout matrix;
    LinearLayout mainLayout;
    EditText editTextLambda, editTextTolerance, editTextIterations;

    TableLayout resultsMatrix;

    BigDecimal[][] augmentedMatrix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jacobi);
        Objects.requireNonNull(getSupportActionBar()).hide();

        mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        matrix = (TableLayout) findViewById(R.id.tableLayoutEquationSystem);

        editTextLambda = (EditText) findViewById(R.id.editTextLambda);
        editTextTolerance = (EditText) findViewById(R.id.editTextTolerance);
        editTextIterations = (EditText) findViewById(R.id.editTextIterations);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;

        HorizontalScrollView horizontalScrollView = new
                HorizontalScrollView(this.getApplicationContext());
        horizontalScrollView.setLayoutParams(layoutParams);
        horizontalScrollView.setHorizontalScrollBarEnabled(false);

        resultsMatrix = new TableLayout(this.getApplicationContext());
        horizontalScrollView.addView(resultsMatrix);
        mainLayout.addView(horizontalScrollView);

        Intent intent = getIntent();

        augmentedMatrix = (BigDecimal[][]) intent.
                getExtras().getSerializable(MATRIX);

        setInitialValueFields(augmentedMatrix.length);
    }

    private void setInitialValueFields(int variableQuantity) {

        TableRow rowMatrix = new TableRow(
                this.getApplicationContext());

        for (int i = 0; i < variableQuantity; i++)
            rowMatrix.addView(getCell(i),
                    getPxFromDp(60), getPxFromDp(60));

        matrix.addView(rowMatrix);
    }

    private EditText getCell(int indexVariable) {

        EditText cell = new EditText(
                this.getApplicationContext());

        cell.setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_DECIMAL |
                InputType.TYPE_NUMBER_FLAG_SIGNED);

        cell.setHint("X" + indexVariable);

        cell.setPadding(0, getPxFromDp(10), 0, getPxFromDp(20));
        cell.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        cell.setAllCaps(true);

        return cell;
    }

    public void execute(View v) {

        resultsMatrix.removeAllViews(); // Remove all existing views.

        addResultsMatrixLegends(getInitialValues().length);

        String lambdaValue = editTextLambda.getText().toString();
        String iterationsValue = editTextIterations.getText().toString();
        String toleranceValue = editTextTolerance.getText().toString();

        BigDecimal lambda = lambdaValue.isEmpty() ?
                BigDecimal.ONE : new BigDecimal(lambdaValue);

        int iterations = Integer.parseInt(iterationsValue);

        BigDecimal tolerance = new BigDecimal(toleranceValue);

        int choice = lambdaValue.isEmpty()
                ? NORMAL_METHOD : RELAXED_METHOD;

        switch (choice) {
            default:
            case NORMAL_METHOD: {
                jacobi(augmentedMatrix, getInitialValues(),
                        tolerance, BigDecimal.ONE, iterations);
                break;
            }
            case RELAXED_METHOD: {
                jacobi(augmentedMatrix, getInitialValues(),
                        tolerance, lambda, iterations);
                break;
            }
        }
    }

    private void addResultsMatrixLegends(int variablesQuantity) {

        TableRow tableRowMatrix = new TableRow(
                this.getApplicationContext());

        TextView iterationCell = getHeaderTextView("ITER");
        tableRowMatrix.addView(iterationCell, TableRow.
                LayoutParams.WRAP_CONTENT, getPxFromDp(35));

        for (int i = 0; i < variablesQuantity; i++) {
            TextView textViewCell = getHeaderTextView("X" + (i + 1));

            tableRowMatrix.addView(textViewCell, TableRow.
                    LayoutParams.WRAP_CONTENT, getPxFromDp(35));
        }

        TextView errorCell = getHeaderTextView("ERROR");
        tableRowMatrix.addView(errorCell, TableRow.
                LayoutParams.WRAP_CONTENT, getPxFromDp(35));

        resultsMatrix.addView(tableRowMatrix);
    }

    private BigDecimal[] getInitialValues() {

        TableRow tableRowInitialValues =
                (TableRow) matrix.getChildAt(0);

        int initialValuesQuantity = tableRowInitialValues.getChildCount();

        BigDecimal[] initialValues = new BigDecimal[initialValuesQuantity];

        for (int i = 0; i < initialValuesQuantity; i++) {

            EditText editTextInitialValues = (EditText)
                    tableRowInitialValues.getChildAt(i);

            initialValues[i] = new BigDecimal(
                    editTextInitialValues.getText().toString());
        }

        return initialValues;
    }

    private BigDecimal[] jacobi(BigDecimal[][] augmentedMatrix, BigDecimal[] initialValues,
                                BigDecimal tolerance, BigDecimal lambda, int iterations) {

        int iteration = 0;
        BigDecimal[] iterationValues;
        BigDecimal error = tolerance.add(BigDecimal.ONE);

        while (error.compareTo(tolerance) > 0 && iteration < iterations) {
            iterationValues = calculateJacobi(augmentedMatrix, initialValues, lambda);
            error = MatrixUtils.euclideanNorm(iterationValues, initialValues);
            initialValues = iterationValues;
            iteration++;

            addJacobiStep(iteration, iterationValues, error);
        }

        if (error.compareTo(tolerance) < 0) {
            return initialValues;
        } else {
            return null;
        }
    }

    private BigDecimal[] calculateJacobi(BigDecimal[][] augmentedMatrix,
                                         BigDecimal[] initialValues, BigDecimal lambda) {

        BigDecimal summation;

        int n = augmentedMatrix.length;

        BigDecimal[] b = MatrixUtils.getBVector(augmentedMatrix);
        BigDecimal[] iterationValues = new BigDecimal[initialValues.length];

        for (int i = 0; i < n; i++) {
            summation = BigDecimal.ZERO;

            for (int j = 0; j < n; j++)
                if (i != j)
                    summation = summation.add(augmentedMatrix[i][j]
                            .multiply(initialValues[j]));

            iterationValues[i] = (b[i].subtract(summation)).divide(
                    augmentedMatrix[i][i], DECIMALS_QUANTITY, ROUNDING_MODE);

            iterationValues[i] = lambda.multiply(iterationValues[i])
                    .add((BigDecimal.ONE.subtract(lambda)).multiply(initialValues[i]));
        }

        return iterationValues;
    }

    private void addJacobiStep(int iteration, BigDecimal[] systemSolved, BigDecimal error) {

        TableRow tableRowMatrix = new TableRow(
                this.getApplicationContext());

        TextView iterationCell = getHeaderTextView(iteration);
        tableRowMatrix.addView(iterationCell, TableRow.
                LayoutParams.WRAP_CONTENT, getPxFromDp(35));

        for (int j = 0; j < systemSolved.length; j++) {
            TextView textViewCell = getHeaderTextView(systemSolved[j]);

            tableRowMatrix.addView(textViewCell, TableRow.
                    LayoutParams.WRAP_CONTENT, getPxFromDp(35));
        }

        NumberFormat formatter = new DecimalFormat(NOTATION_FORMAT);
        formatter.setRoundingMode(RoundingMode.HALF_UP);
        formatter.setMinimumFractionDigits(3);

        TextView errorCell = getCellTextView(formatter.format(error));
        tableRowMatrix.addView(errorCell, TableRow.
                LayoutParams.WRAP_CONTENT, getPxFromDp(35));

        resultsMatrix.addView(tableRowMatrix);
    }

    private TextView getHeaderTextView(BigDecimal cellValue) {

        TextView textViewCell = new TextView(
                this.getApplicationContext());

        textViewCell.setText(String.valueOf(cellValue.
                setScale(10, ROUNDING_MODE).doubleValue()));
        textViewCell.setPadding(20, 10, 20, 10);
        textViewCell.setGravity(Gravity.CENTER);

        return textViewCell;
    }

    private TextView getHeaderTextView(int cellValue) {

        TextView textViewCell = new TextView(
                this.getApplicationContext());

        textViewCell.setText(String.valueOf(cellValue));
        textViewCell.setPadding(20, 10, 20, 10);
        textViewCell.setGravity(Gravity.CENTER);

        return textViewCell;
    }

    private TextView getCellTextView(String cellValue) {

        TextView textViewCell = new TextView(
                this.getApplicationContext());

        textViewCell.setText(cellValue);
        textViewCell.setPadding(20, 10, 20, 10);
        textViewCell.setGravity(Gravity.CENTER);

        return textViewCell;
    }

    private TextView getHeaderTextView(String cellValue) {

        TextView textViewCell = new TextView(
                this.getApplicationContext());

        textViewCell.setText(cellValue);
        textViewCell.setTypeface(null, Typeface.BOLD);
        textViewCell.setPadding(20, 10, 20, 10);
        textViewCell.setGravity(Gravity.CENTER);

        return textViewCell;
    }
}