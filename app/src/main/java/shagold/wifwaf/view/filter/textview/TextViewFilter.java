package shagold.wifwaf.view.filter.textview;

import android.widget.TextView;

import shagold.wifwaf.view.ValidateMessage;
import shagold.wifwaf.view.filter.Filter;

public abstract class TextViewFilter implements Filter<TextView, ValidateMessage> {

    @Override
    public ValidateMessage meetFilter(TextView text) {
        return new ValidateMessage();
    }

}
