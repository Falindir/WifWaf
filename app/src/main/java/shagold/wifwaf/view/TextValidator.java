package shagold.wifwaf.view;

import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;

import shagold.wifwaf.view.filter.text.EditTextFilter;

/**
 * Created by jimmy on 07/11/15.
 */
public class TextValidator {

    public TextValidator () {

    }

    public ValidateMessage validate(EditText text, EditTextFilter[] filters) {

        for(EditTextFilter filter : filters) {
            ValidateMessage v = filter.meetFilter(text);
            if(!v.getValue())
                return v;
        }

        return new ValidateMessage();
    }

}
