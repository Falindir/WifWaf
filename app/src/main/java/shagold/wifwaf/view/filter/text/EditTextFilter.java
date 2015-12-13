package shagold.wifwaf.view.filter.text;

import android.widget.EditText;

import shagold.wifwaf.view.ValidateMessage;
import shagold.wifwaf.view.filter.Filter;

/**
 * Created by jimmy on 29/11/15.
 */
public abstract class EditTextFilter implements Filter<EditText, ValidateMessage> {

    @Override
    public ValidateMessage meetFilter(EditText text) {
        return new ValidateMessage();
    }
}
