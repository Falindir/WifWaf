package shagold.wifwaf.view.filter.text;

import android.widget.EditText;

import shagold.wifwaf.view.ErrorMessage;
import shagold.wifwaf.view.ValidateMessage;

/**
 * Created by jimmy on 29/11/15.
 */
public class NumberFilter  extends EditTextFilter {

    @Override
    public ValidateMessage meetFilter(EditText text) {

        String s = text.getText().toString();

        for (char c : s.toCharArray())
            if (!Character.isDigit(c))
                return new ValidateMessage();

        return new ValidateMessage(ErrorMessage.NUMBER);
    }

}
