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
import static co.edu.eafit.andromath.util.Constants.NOTATION_FORMAT;
import static co.edu.eafit.andromath.util.Constants.VARIABLE;

public class MultipleRootsActivity extends AppCompatActivity {

    private static final String tag = MultipleRootsActivity.class.getSimpleName();

    EditText initialValueInput, derivativeInput, secondDerivativeInput, toleranceInput, iterationsInput;
    TextView function, results, iterations, xa, ya, dya, ddya, tolerance;
    Expression expressionF, expressionG, expressionGG;
    TableLayout procedure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_roots);
        Objects.requireNonNull(getSupportActionBar()).hide();

        initialValueInput = (EditText) findViewById(R.id.editTextInitialValue);
        derivativeInput = (EditText) findViewById(R.id.editTextDerivative);
        secondDerivativeInput = (EditText) findViewById(R.id.editTextSecondDerivative);
        toleranceInput = (EditText) findViewById(R.id.editTextTolerance);
        iterationsInput = (EditText) findViewById(R.id.editTextIterations);

        function = (TextView) findViewById(R.id.textViewFunction);
        results = (TextView) findViewById(R.id.textViewResult);

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

    public void multipleRoots(View v) {

        initialValueInput.setSelected(false);
        derivativeInput.setSelected(false);
        secondDerivativeInput.setSelected(false);
        toleranceInput.setSelected(false);
        iterationsInput.setSelected(false);
        results.setVisibility(View.VISIBLE);

        List<TableRow> tableIterations = new ArrayList<>();

        procedure.removeViews(1,
                procedure.getChildCount() - 1);

        Pair<String, Boolean> solution =
                MultipleRoot(tableIterations);

        if (solution != null) {
            results.setText(solution.first);
            createTableProcedure(tableIterations);
            procedure.setVisibility(solution.second ?
                    View.VISIBLE : View.INVISIBLE);
        } else {
            Messages.invalidEquation(tag,
                    getApplicationContext());
        }
    }

    public Pair<String, Boolean> MultipleRoot(List<TableRow> tableIterations) {

        String message;

        boolean displayProcedure = true;

        try {
            BigDecimal x0 = BigDecimal.valueOf(Double.parseDouble(initialValueInput.getText().toString()));
            BigDecimal tol = BigDecimal.valueOf(Double.parseDouble(toleranceInput.getText().toString()));
            BigDecimal x1;
            int niter = Integer.parseInt(iterationsInput.getText().toString());
            expressionG = new Expression(derivativeInput.getText().toString());
            expressionGG = new Expression(secondDerivativeInput.getText().toString());

            if (niter < 1) {
                message = INVALID_ITER.getMessage();
                displayProcedure = INVALID_ITER.isDisplayProcedure();
            }

            BigDecimal y = expressionF.with(VARIABLE, x0).eval();
            BigDecimal dy = expressionG.with(VARIABLE, x0).eval();
            BigDecimal ddy = expressionGG.with(VARIABLE, x0).eval();

            BigDecimal den = dy.pow(2).subtract(y.multiply(ddy));
            int count = 0;
            BigDecimal error = tol.add(BigDecimal.ONE);

            tableIterations.add(createProcedureIteration(count + 1, x0, y, dy, ddy, error));

            while (error.compareTo(tol) > 0 && y.compareTo(BigDecimal.ZERO) != 0 && den.compareTo(BigDecimal.ZERO) != 0 && count < niter) {
                //x1 = x0 - (y*dy)/den
                x1 = x0.subtract(y.multiply(dy).divide(den, 5, BigDecimal.ROUND_HALF_EVEN));
                y = expressionF.with(VARIABLE, x1).eval();
                dy = expressionG.with(VARIABLE, x1).eval();
                ddy = expressionGG.with(VARIABLE, x1).eval();
                den = dy.pow(2).subtract(y.multiply(ddy));
                error = x1.subtract(x0).abs();
                x0 = x1;
                count++;

                tableIterations.add(createProcedureIteration(count + 1, x0, y, dy, ddy, error));
            }

            if (y.compareTo(BigDecimal.ZERO) == 0) {
                message = "x = " + x0.toString() + " is a root";
                tableIterations.add(createProcedureIteration(count + 1, x0, y, dy, ddy, error));
                displayProcedure = true;
            } else if (error.compareTo(tol) < 0) {
                message = "x = " + x0.toString() + " is an approximated root\nwith E = " + error.toString();
                tableIterations.add(createProcedureIteration(count + 1, x0, y, dy, ddy, error));
                displayProcedure = true;
            } else if (den.compareTo(BigDecimal.ZERO) == 0) {
                //TODO: Buscar la explicacion de este caso
                message = "at x = " + x0.toString() + " the function behaves incorrectly";
            } else {
                message = "The method failed after " + niter + " iterations";
                displayProcedure = false;
            }
        } catch (Expression.ExpressionException e) {
            return null; // The equation is not valid.
        } catch (ArithmeticException | NumberFormatException e) {
            displayProcedure = OUT_OF_RANGE.isDisplayProcedure();
            message = OUT_OF_RANGE.getMessage();

        }

        return new Pair<>(message, displayProcedure);
    }

    private TableRow createProcedureIteration(int count, BigDecimal x, BigDecimal y,
                                              BigDecimal dy, BigDecimal ddy, BigDecimal error) {

        TableRow iterationResult = new TableRow(this);

        NumberFormat formatter = new DecimalFormat(NOTATION_FORMAT);
        formatter.setRoundingMode(RoundingMode.HALF_UP);
        formatter.setMinimumFractionDigits(3);

        iterations = new TextView(this);
        iterations.setPadding(15, 10, 15, 10);
        iterations.setGravity(Gravity.CENTER);
        iterations.setText(String.valueOf(count));

        xa = new TextView(this);
        xa.setPadding(15, 10, 15, 10);
        xa.setGravity(Gravity.CENTER);
        xa.setText(formatter.format(x));

        ya = new TextView(this);
        ya.setPadding(15, 10, 15, 10);
        ya.setGravity(Gravity.CENTER);
        ya.setText(formatter.format(y));

        dya = new TextView(this);
        dya.setPadding(15, 10, 15, 10);
        dya.setGravity(Gravity.CENTER);
        dya.setText(formatter.format(dy));

        ddya = new TextView(this);
        ddya.setPadding(15, 10, 15, 10);
        ddya.setGravity(Gravity.CENTER);
        ddya.setText(formatter.format(ddy));

        tolerance = new TextView(this);
        tolerance.setPadding(15, 10, 15, 10);
        tolerance.setGravity(Gravity.CENTER);
        tolerance.setText(formatter.format(error));

        iterationResult.addView(iterations);
        iterationResult.addView(xa);
        iterationResult.addView(ya);
        iterationResult.addView(dya);
        iterationResult.addView(ddya);
        iterationResult.addView(tolerance);

        return iterationResult;
    }

    private void createTableProcedure(List<TableRow> tableIterations) {

        for (TableRow tableRow : tableIterations) {
            procedure.addView(tableRow);
        }
    }
}
