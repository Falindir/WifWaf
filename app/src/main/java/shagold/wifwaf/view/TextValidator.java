package shagold.wifwaf.view;

import android.widget.EditText;
import android.widget.TextView;

import shagold.wifwaf.view.filter.text.EditTextFilter;
import shagold.wifwaf.view.filter.textview.TextViewFilter;

public class TextValidator {

    public TextValidator () {}

    public ValidateMessage validate(EditText text, EditTextFilter[] filters) {
        for(EditTextFilter filter : filters) {
            ValidateMessage v = filter.meetFilter(text);
            if(!v.getValue())
                return v;
        }
        return new ValidateMessage();
    }

    public ValidateMessage validate(EditText text, EditTextFilter filter) {
        ValidateMessage v = filter.meetFilter(text);

        if(!v.getValue())
            return v;

        return new ValidateMessage();
    }

    public ValidateMessage validate(TextView text, TextViewFilter[] filters) {
        for(TextViewFilter filter : filters) {
            ValidateMessage v = filter.meetFilter(text);
            if(!v.getValue())
                return v;
        }
        return new ValidateMessage();
    }

    public ValidateMessage validate(TextView text, TextViewFilter filter) {
        ValidateMessage v = filter.meetFilter(text);

        if(!v.getValue())
            return v;

        return new ValidateMessage();
    }
}
