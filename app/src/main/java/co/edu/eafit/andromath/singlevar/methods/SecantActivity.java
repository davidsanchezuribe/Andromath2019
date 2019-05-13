package co.edu.eafit.andromath.singlevar.methods;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import co.edu.eafit.andromath.evalex.Expression;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import co.edu.eafit.andromath.R;
import co.edu.eafit.andromath.util.Messages;

import static co.edu.eafit.andromath.util.Constants.EQUATION;
import static co.edu.eafit.andromath.util.Constants.ErrorCodes.INVALID_ITER;
import static co.edu.eafit.andromath.util.Constants.ErrorCodes.OUT_OF_RANGE;
import static co.edu.eafit.andromath.util.Constants.ErrorCodes.X_ROOT;
import static co.edu.eafit.andromath.util.Constants.NOTATION_FORMAT;
import static co.edu.eafit.andromath.util.Constants.VARIABLE;

public class SecantActivity extends AppCompatActivity {

    private static final String tag = SecantActivity.class.getSimpleName();

    EditText x0ValueInput, x1ValueInput, toleranceInput, iterationsInput;
    TextView function, result, x1a, x2a, fx1, fx2, tolerance, iterations;
    Expression expression;
    TableLayout procedure;

    private List<TableRow> tableIterations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secant);
        Objects.requireNonNull(getSupportActionBar()).hide();

        x0ValueInput = (EditText) findViewById(R.id.editTextX0Value);
        x1ValueInput = (EditText) findViewById(R.id.editTextX1Value);
        toleranceInput = (EditText) findViewById(R.id.editTextTolerance);
        iterationsInput = (EditText) findViewById(R.id.editTextIterations);

        function = (TextView) findViewById(R.id.textViewFunction);
        result = (TextView) findViewById(R.id.textViewResult);

        procedure = (TableLayout) findViewById(R.id.tableLayoutProcedure);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        String equation = "f(x) = " + intent.
                getStringExtra(EQUATION);
        function.setText(equation);

        expression = new Expression(intent.
                getStringExtra(EQUATION));

        procedure.setStretchAllColumns(true);
    }

    public void secant(View v) {

        x0ValueInput.setSelected(false);
        x1ValueInput.setSelected(false);
        toleranceInput.setSelected(false);
        iterationsInput.setSelected(false);
        result.setVisibility(View.VISIBLE);

        tableIterations = new ArrayList<>();

        procedure.removeViews(1,
                procedure.getChildCount() - 1);

        Pair<String, Boolean> solution =
                secant(tableIterations);

        if (solution != null) {
            result.setText(solution.first);
            createTableProcedure(tableIterations);
            procedure.setVisibility(solution.second ?
                    View.VISIBLE : View.INVISIBLE);
        } else {
            Messages.invalidEquation(tag,
                    getApplicationContext());
        }
    }

    /**
     * @return Pair<String, Boolean>
     *     String parameter is the message.
     *     Boolean parameter is a flag to show the procedure.
     */
    public Pair<String, Boolean> secant(List<TableRow> tableIterations) {

        String message;

        boolean displayProcedure;

        try {
            BigDecimal x0 = BigDecimal.valueOf(Double.
                    parseDouble(x0ValueInput.getText().toString()));

            BigDecimal x1 = BigDecimal.valueOf(Double.
                    parseDouble(x1ValueInput.getText().toString()));

            BigDecimal tol = BigDecimal.valueOf(Double.
                    parseDouble(toleranceInput.getText().toString()));

            BigDecimal x2;

            int niter = Integer.parseInt(iterationsInput.getText().toString());

            BigDecimal y0 = expression.with(VARIABLE, x0).eval();

            if (niter < 1) {
                message = INVALID_ITER.getMessage();
                displayProcedure = INVALID_ITER.isDisplayProcedure();
            } else if (y0.compareTo(BigDecimal.ZERO) == 0) {
                message = X_ROOT.getMessage();
                displayProcedure = X_ROOT.isDisplayProcedure();
            } else {
                BigDecimal y1 = expression.with(VARIABLE, x1).eval();
                int count = 0;
                BigDecimal error = tol.add(BigDecimal.ONE);
                BigDecimal den = y1.subtract(y0);

                tableIterations.add(createProcedureIteration(count, x0, x1, y0, y1, error));
                while (error.compareTo(tol) > 0 && y1.compareTo(BigDecimal.ZERO) != 0 && den.compareTo(BigDecimal.ZERO) != 0 && count < niter) {
                    //x2 = x1 - (y1*(x1-x0)/den)
                    x2 = x1.subtract(y1.multiply(x1.subtract(x0)).divide(den, BigDecimal.ROUND_HALF_EVEN));
                    error = x2.subtract(x1).abs();
                    x0 = x1;
                    y0 = y1;
                    x1 = x2;
                    y1 = expression.with(VARIABLE, x1).eval();
                    den = y1.subtract(y0);
                    count++;

                    tableIterations.add(createProcedureIteration(count, x0, x1, y0, y1, error));
                }

                if (y1.compareTo(BigDecimal.ZERO) == 0) {
                    message = "x = " + x1.toString() + " is a root";
                    displayProcedure = true;
                } else if (error.compareTo(tol) < 0) {
                    message = "x = " + x1.toString() + " is an approximated root\n" +
                            "with E = " + error.toString();
                    displayProcedure = true;
                } else if (den.compareTo(BigDecimal.ZERO) == 0) {
                    message = "There are possibly multiple roots";
                    displayProcedure = false;
                } else {
                    message = "The method failed after "
                            + count + " iterations";
                    displayProcedure = true;
                }
            }
        } catch (Expression.ExpressionException e) {
            return null; // The equation is not valid.
        } catch (ArithmeticException | NumberFormatException e) {
            displayProcedure = OUT_OF_RANGE.isDisplayProcedure();
            message = OUT_OF_RANGE.getMessage();
        }

        return new Pair(message, displayProcedure);
    }

    private TableRow createProcedureIteration(int count, BigDecimal x1, BigDecimal x2,
                                              BigDecimal y1, BigDecimal y2, BigDecimal error) {

        TableRow iterationResult = new TableRow(this);

        NumberFormat formatter = new DecimalFormat(NOTATION_FORMAT);
        formatter.setRoundingMode(RoundingMode.HALF_UP);
        formatter.setMinimumFractionDigits(3);

        iterations = new TextView(this);
        iterations.setPadding(15, 10, 15, 10);
        iterations.setGravity(Gravity.CENTER);
        iterations.setText(String.valueOf(count));

        x1a = new TextView(this);
        x1a.setPadding(15, 10, 15, 10);
        x1a.setGravity(Gravity.CENTER);
        x1a.setText(formatter.format(x1));

        fx1 = new TextView(this);
        fx1.setPadding(15, 10, 15, 10);
        fx1.setGravity(Gravity.CENTER);
        fx1.setText(formatter.format(y1));

        x2a = new TextView(this);
        x2a.setPadding(15, 10, 15, 10);
        x2a.setGravity(Gravity.CENTER);
        x2a.setText(formatter.format(x2));

        fx2 = new TextView(this);
        fx2.setPadding(15, 10, 15, 10);
        fx2.setGravity(Gravity.CENTER);
        fx2.setText(formatter.format(y2));

        tolerance = new TextView(this);
        tolerance.setPadding(15, 10, 15, 10);
        tolerance.setGravity(Gravity.CENTER);
        tolerance.setText(formatter.format(error));

        iterationResult.addView(iterations);
        iterationResult.addView(x1a);
        iterationResult.addView(fx1);
        iterationResult.addView(x2a);
        iterationResult.addView(fx2);
        iterationResult.addView(tolerance);

        return iterationResult;
    }

    private void createTableProcedure(List<TableRow> tableIterations) {

        for (TableRow tableRow : tableIterations) {
            procedure.addView(tableRow);
        }
    }
}