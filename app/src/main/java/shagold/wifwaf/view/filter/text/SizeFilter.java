package shagold.wifwaf.view.filter.text;

import android.widget.EditText;

import shagold.wifwaf.view.ErrorMessage;
import shagold.wifwaf.view.ValidateMessage;

/**
 * Created by jimmy on 29/11/15.
 */
public class SizeFilter extends EditTextFilter {

    private int max = 255;
    private int min = 1;

    public SizeFilter() {

    }

    public SizeFilter(int min, int max) {
        this.max = max;
        this.min = min;
    }

    @Override
    public ValidateMessage meetFilter(EditText text) {

        String s = text.getText().toString();

        if(min > s.length() || max < s.length())
            return new ValidateMessage(ErrorMessage.SIZE);
        else
            return new ValidateMessage();
    }

    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }
}
