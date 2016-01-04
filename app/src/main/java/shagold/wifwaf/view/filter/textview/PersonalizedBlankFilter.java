package shagold.wifwaf.view.filter.textview;

import android.widget.TextView;

import shagold.wifwaf.view.ErrorMessage;
import shagold.wifwaf.view.ValidateMessage;

public class PersonalizedBlankFilter extends TextViewFilter {

    private ErrorMessage type;

    public PersonalizedBlankFilter(ErrorMessage type) {
        this.type = type;
    }

    @Override
    public ValidateMessage meetFilter(TextView text) {
        String s = text.getText().toString();

        if(s.length() == 0)
            return new ValidateMessage(type);
        else
            return new ValidateMessage();
    }

    public ErrorMessage getType() {
        return type;
    }
}
