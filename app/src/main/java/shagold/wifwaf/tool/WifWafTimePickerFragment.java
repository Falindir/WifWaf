package shagold.wifwaf.tool;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class WifWafTimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private TextView timeText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // L'heure actuelle est l'heure par défaut
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String h;
        if(hourOfDay < 10)
            h = "0" + String.valueOf(hourOfDay);
        else
            h = String.valueOf(hourOfDay);

        String m;
        if(minute < 10)
            m = "0" + String.valueOf(minute);
        else
            m = String.valueOf(minute);

        String result = h + ":" + m + ":00";
        if(timeText != null)
            timeText.setText(result);
    }

    public void setTimeText(TextView timeText) {
        this.timeText = timeText;
    }
}
