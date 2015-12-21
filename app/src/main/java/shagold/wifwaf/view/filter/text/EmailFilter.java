package shagold.wifwaf.view.filter.text;

import android.widget.EditText;

import java.util.regex.Pattern;

import shagold.wifwaf.view.ErrorMessage;
import shagold.wifwaf.view.ValidateMessage;

public class EmailFilter extends EditTextFilter {

    @Override
    public ValidateMessage meetFilter(EditText text) {
        String s = text.getText().toString();

        boolean b = Pattern.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", s);

        if(!b)
            return new ValidateMessage(ErrorMessage.EMAIL);
        else
            return new ValidateMessage();
    }
}
