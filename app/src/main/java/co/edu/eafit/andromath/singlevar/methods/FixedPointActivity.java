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

public class FixedPointActivity extends AppCompatActivity {

    private static final String tag = FixedPointActivity.class.getSimpleName();

    EditText approximatedXInput, gFunctionInput, toleranceInput, iterationsInput;
    TextView function, result, iterations, tolerance, x, fx;
    Expression expressionF, expressionG;
    TableLayout procedure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixed_point);
        Objects.requireNonNull(getSupportActionBar()).hide();

        approximatedXInput = (EditText) findViewById(R.id.editTextApproximatedX);
        gFunctionInput = (EditText) findViewById(R.id.editTextGFunction);
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

        expressionF = new Expression(intent.
                getStringExtra(EQUATION));

        procedure.setStretchAllColumns(true);
    }

    public void fixedPoint(View v) {

        approximatedXInput.setSelected(false);
        gFunctionInput.setSelected(false);
        toleranceInput.setSelected(false);
        iterationsInput.setSelected(false);
        result.setVisibility(View.VISIBLE);

        List<TableRow> tableIterations = new ArrayList<>();

        procedure.removeViews(1,
                procedure.getChildCount() - 1);

        Pair<String, Boolean> solution =
                fixedPoint(tableIterations);

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
    private Pair<String, Boolean> fixedPoint(List<TableRow> tableIterations) {

        String message;

        boolean displayProcedure;

        try {
            BigDecimal xa = BigDecimal.valueOf(Double.parseDouble(approximatedXInput.getText().toString()));
            BigDecimal tol = BigDecimal.valueOf(Double.parseDouble(toleranceInput.getText().toString()));
            BigDecimal xn;
            int niter = Integer.parseInt(iterationsInput.getText().toString());
            expressionG = new Expression(gFunctionInput.getText().toString());

            BigDecimal y = expressionF.with(VARIABLE, xa).eval();
            int count = 0;
            BigDecimal error = tol.add(BigDecimal.ONE);

            tableIterations.add(createProcedureIteration(count, xa, y, error));

            if (niter < 1) {
                message = INVALID_ITER.getMessage();
                displayProcedure = INVALID_ITER.isDisplayProcedure();
            } else if (y.compareTo(BigDecimal.ZERO) == 0) {
                message = X_ROOT.getMessage();
                displayProcedure = X_ROOT.isDisplayProcedure();
            } else {
                //Method Begins
                while (y.compareTo(BigDecimal.ZERO) != 0 && error.compareTo(tol) > 0 && count < niter) {
                    xn = expressionG.with(VARIABLE, xa).eval();
                    y = expressionF.with(VARIABLE, xn).eval();
                    error = xn.subtract(xa).abs();
                    xa = xn;
                    count++;

                    tableIterations.add(createProcedureIteration(count, xa, y, error));
                }
                if (y.compareTo(BigDecimal.ZERO) == 0) {
                    tableIterations.add(createProcedureIteration(count, xa, y, error));
                    message = "x = " + xa.toString() + " is a root";
                    displayProcedure = true;
                } else if (error.compareTo(tol) < 0) {
                    tableIterations.add(createProcedureIteration(count, xa, y, error));
                    message = "x = " + xa.toString() + " is an approximated root\nwith E = " + error.toString();
                    displayProcedure = true;
                } else {
                    message = "The method failed after "
                            + niter + " iterations";
                    displayProcedure = false;
                }
            }
        } catch (Expression.ExpressionException e) {
            return null; // The equation is not valid.
        } catch (ArithmeticException | NumberFormatException e) {
            displayProcedure = OUT_OF_RANGE.isDisplayProcedure();
            message = OUT_OF_RANGE.getMessage();
        }

        return new Pair<>(message, displayProcedure);
    }

    private TableRow createProcedureIteration(int count, BigDecimal xa,
                                              BigDecimal y, BigDecimal Error) {
        TableRow iterationResult = new TableRow(this);

        NumberFormat formatter = new DecimalFormat(NOTATION_FORMAT);
        formatter.setRoundingMode(RoundingMode.HALF_UP);
        formatter.setMinimumFractionDigits(3);

        iterations = new TextView(this);
        iterations.setPadding(15, 10, 15, 10);
        iterations.setGravity(Gravity.CENTER);
        iterations.setText(String.valueOf(count));

        x = new TextView(this);
        x.setPadding(15, 10, 15, 10);
        x.setGravity(Gravity.CENTER);
        x.setText(formatter.format(xa));

        fx = new TextView(this);
        fx.setPadding(15, 10, 15, 10);
        fx.setGravity(Gravity.CENTER);
        fx.setText(formatter.format(y));

        tolerance = new TextView(this);
        tolerance.setPadding(15, 10, 15, 10);
        tolerance.setGravity(Gravity.CENTER);
        tolerance.setText(formatter.format(Error));

        iterationResult.addView(iterations);
        iterationResult.addView(x);
        iterationResult.addView(fx);
        iterationResult.addView(tolerance);

        return iterationResult;
    }

    private void createTableProcedure(List<TableRow> tableIterations) {

        for (TableRow tableRow : tableIterations) {
            procedure.addView(tableRow);
        }
    }
}