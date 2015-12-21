package shagold.wifwaf.tool;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;
import java.util.Calendar;

public class WifWafDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private TextView dateText;

    public void setDateText(TextView dateText) {
        this.dateText = dateText;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        int realMonth = month + 1;
        String result = year + "-" + realMonth + "-" + day;
        if(dateText != null)
            dateText.setText(result);
    }
}

