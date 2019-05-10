package co.edu.eafit.andromath.linearsystems;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.math.BigDecimal;
import java.util.Objects;

import co.edu.eafit.andromath.R;

import static co.edu.eafit.andromath.linearsystems.gaussianelimination.util.ViewUtils.getPxFromDp;
import static co.edu.eafit.andromath.util.Constants.MATRIX;

public class LinearSystemsLandingActivity extends AppCompatActivity {

    TableLayout matrix;
    LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linear_systems_landing);
        Objects.requireNonNull(getSupportActionBar()).hide();

        matrix = (TableLayout) findViewById(R.id.tableLayoutEquationSystem);

        mainLayout = (LinearLayout) findViewById(R.id.mainLayout);

        fillInitialMatrix(2, 3);
    }

    public void fillInitialMatrix(int initialRows, int initialColumns) {

        for (int i = 0; i < initialRows; i++) {

            TableRow rowMatrix = new TableRow(
                    this.getApplicationContext());

            for (int j = 0; j < initialColumns; j++)
                rowMatrix.addView(getCell(i, j),
                        getPxFromDp(60), getPxFromDp(60));

            matrix.addView(rowMatrix);
        }
    }

    public void addVariable(View v) {

        addColumn();
        addRow();
    }

    public void addColumn() {

        TableRow rowMatrix;

        for (int i = 0; i < matrix.getChildCount(); i++) {
            rowMatrix = (TableRow) matrix.getChildAt(i);

            rowMatrix.addView(getCell(i, ((TableRow) matrix.getChildAt(i))
                    .getChildCount()), getPxFromDp(60), getPxFromDp(60));
        }
    }

    public void addRow() {

        TableRow rowMatrix = new TableRow(
                this.getApplicationContext());

        int rowsQuantity = matrix.getChildCount();

        int colsQuantity = rowsQuantity == 0 ? 3 : ((TableRow)
                matrix.getChildAt(rowsQuantity - 1)).getChildCount();

        for (int i = 0; i < colsQuantity; i++)
            rowMatrix.addView(getCell(rowsQuantity, i),
                    getPxFromDp(60), getPxFromDp(60));

        matrix.addView(rowMatrix);
    }

    private EditText getCell(int row, int col) {

        EditText cell = new EditText(
                this.getApplicationContext());

        cell.setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_DECIMAL |
                InputType.TYPE_NUMBER_FLAG_SIGNED);

        cell.setHint(row + "," + col);

        cell.setPadding(0, getPxFromDp(10), 0, getPxFromDp(20));
        cell.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        cell.setAllCaps(true);

        return cell;
    }

    public void calculate(View v) {

        if (matrix.getChildCount() > 1) {

            Intent intent = new Intent(this,
                    LinearSystemElectionActivity.class);

            Bundle bundleMatrix = new Bundle();
            bundleMatrix.putSerializable(
                    MATRIX, getMatrixValues());
            intent.putExtras(bundleMatrix);

            startActivity(intent);
        }
    }

    private BigDecimal[][] getMatrixValues() {

        int rowsQuantity = matrix.getChildCount();
        int colsQuantity = ((TableRow) matrix.
                getChildAt(0)).getChildCount();

        BigDecimal matrixValues[][] = new BigDecimal[rowsQuantity][colsQuantity];

        for (int i = 0; i < rowsQuantity; i++) {

            for (int j = 0; j < colsQuantity; j++) {

                TableRow tableRow = (TableRow) matrix.getChildAt(i);
                EditText editText = (EditText) (tableRow).getChildAt(j);

                String value = editText.getText().toString().isEmpty()
                        ? "0" : editText.getText().toString();

                BigDecimal cellValue = new BigDecimal(value);

                matrixValues[i][j] = cellValue;
            }
        }

        return matrixValues;
    }
}