package us.master.acme_explorer;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Date;

import us.master.acme_explorer.common.Constants;
import us.master.acme_explorer.common.Util;

public class FilterActivity extends AppCompatActivity {

    private static final String TAG = FilterActivity.class.getSimpleName();
    private TextView mTextViewDateStart, mTextViewDateEnd;
    private EditText mEditTextMaxPrice, mEditTextMinPrice;
    private Calendar calendar = Calendar.getInstance();
    private long dateStartToFilter = 0, dateEndToFilter = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        setTitle(getString(R.string.filter_trips));
        mTextViewDateEnd = findViewById(R.id.my_txv_filter_date_end);
        mTextViewDateStart = findViewById(R.id.my_txv_filter_date_start);
        mEditTextMaxPrice = findViewById(R.id.my_edt_max_price);
        mEditTextMinPrice = findViewById(R.id.my_edt_min_price);
        Log.d(TAG, "onCreate: filter");
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra(Constants.minPrice, mEditTextMinPrice.getText().toString());
        intent.putExtra(Constants.maxPrice, mEditTextMaxPrice.getText().toString());
        intent.putExtra(Constants.dateStart, dateStartToFilter);
        intent.putExtra(Constants.dateEnd, dateEndToFilter);
        Log.d(TAG, "onBackPressed: " + intent.toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    public void applyFilters(View view) {
        onBackPressed();
    }

    public void getPickOneDate(View view) {
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                this, (datePicker, year, month, day) -> {
            Calendar calendarDate = Calendar.getInstance();
            calendarDate.set(Calendar.YEAR, year);
            calendarDate.set(Calendar.MONTH, month);
            calendarDate.set(Calendar.DAY_OF_MONTH, day);
            //TODO enable control of departure date greater than arrival date
            if (calendarDate.getTimeInMillis() > (new Date().getTime() - 86400)) {

                if (view.getId() == R.id.my_imv_filter_departure_date) {
                    mTextViewDateStart.setTextColor(
                            getColor(android.R.color.black));
                    mTextViewDateStart.setText(Util.dateFormatter(calendarDate));
                    dateStartToFilter = Util.CalendarToLong(calendarDate);

                } else {
                    mTextViewDateEnd.setTextColor(
                            getColor(android.R.color.black));
                    mTextViewDateEnd.setText(Util.dateFormatter(calendarDate));
                    dateEndToFilter = Util.CalendarToLong(calendarDate);
                }

            } else
                Toast.makeText(this,
                        getResources().getString(R.string.date_message),
                        Toast.LENGTH_SHORT).show();

        }, yy, mm, dd);
        dialog.show();
    }
}
