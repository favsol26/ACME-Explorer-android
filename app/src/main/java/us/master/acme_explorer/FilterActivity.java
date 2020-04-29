package us.master.acme_explorer;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import us.master.acme_explorer.common.Constants;
import us.master.acme_explorer.common.Util;

public class FilterActivity extends AppCompatActivity {

    //    private static final String TAG = FilterActivity.class.getSimpleName();
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

        HashMap filterSaved = Util.getSharedPreferenceFilters(this);

        initView(filterSaved, Constants.dateStart, mTextViewDateStart);
        initView(filterSaved, Constants.dateEnd, mTextViewDateEnd);
        initView(filterSaved, Constants.minPrice, mEditTextMinPrice);
        initView(filterSaved, Constants.maxPrice, mEditTextMaxPrice);

//        Log.d(TAG, "onCreate: filter" + filterSaved.toString());
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        int minPrice = Integer.parseInt(getValue(mEditTextMinPrice.getText().toString()));
        int maxPrice = Integer.parseInt(getValue(mEditTextMaxPrice.getText().toString()));
        if (isInControl(minPrice, maxPrice) && isInControl(dateStartToFilter, dateEndToFilter)) {
            Intent intent = new Intent();
            intent.putExtra(Constants.minPrice, minPrice);
            intent.putExtra(Constants.maxPrice, maxPrice);
            intent.putExtra(Constants.dateStart, dateStartToFilter);
            intent.putExtra(Constants.dateEnd, dateEndToFilter);
            setResult(RESULT_OK, intent);
            finish();
        } else Toast.makeText(this,
                getString(R.string.filter_alert),
                Toast.LENGTH_LONG).show();
    }

    public void applyFilters(View view) {
        onBackPressed();
    }

    public void clearFilters(View view) {
        mEditTextMinPrice.setText("");
        mEditTextMaxPrice.setText("");
        dateStartToFilter = 0;
        dateEndToFilter = 0;
        onBackPressed();
    }

    public void pickOneDate(View view) {
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(
                this, (datePicker, year, month, day) -> {
            Calendar calendarDate = Calendar.getInstance();
            calendarDate.set(Calendar.YEAR, year);
            calendarDate.set(Calendar.MONTH, month);
            calendarDate.set(Calendar.DAY_OF_MONTH, day);
            if (calendarDate.getTimeInMillis() > (new Date().getTime() - 86400)) {
                if (view.getId() == R.id.my_imv_filter_departure_date)
                    updateTextView(Util.CalendarToLong(calendarDate), mTextViewDateStart);
                else updateTextView(Util.CalendarToLong(calendarDate), mTextViewDateEnd);
            } else Toast.makeText(this,
                    getResources().getString(R.string.date_message),
                    Toast.LENGTH_SHORT).show();
//            Log.d(TAG, "pickOneDate: " + Util.dateFormatter(calendarDate));
        }, yy, mm, dd);
        dialog.show();
    }

    private boolean isInControl(long min, long max) {
        if (max > 0)                 // if (min > 0) {
            if (min > 0)             //    if (max > 0) {
                return max > min;    //       return min < max;
            else return true;        //    } else return true;
        else return true;            // } else return true;
    }

    private void initView(HashMap filterSaved, String tag, TextView textView) {
        if ((long) filterSaved.get(tag) > 0) updateTextView((long) filterSaved.get(tag), textView);
    }

    private void initView(HashMap filterSaved, String tag, EditText mEditText) {
        if ((long) filterSaved.get(tag) > 0)
            mEditText.setText(String.valueOf(filterSaved.get(tag)));
    }

    private void updateTextView(long longDate, TextView mTextViewDate) {
        mTextViewDate.setTextColor(getResources().getColor(android.R.color.black));
        mTextViewDate.setText(Util.dateFormatter(longDate));
        if (mTextViewDate.getId() == R.id.my_txv_filter_date_start) dateStartToFilter = longDate;
        else dateEndToFilter = longDate;
    }

    private String getValue(String data) {
        return !Objects.equals(data, "") ? data : "0";
    }
}
