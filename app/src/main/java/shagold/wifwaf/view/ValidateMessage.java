package shagold.wifwaf.view;

/**
 * Created by jimmy on 29/11/15.
 */
public class ValidateMessage {

    private boolean value;
    private ErrorMessage error;

    public ValidateMessage() {
        this.value = true;
    }

    public ValidateMessage(ErrorMessage error) {
        this.value = false;
        this.error = error;
    }

    public boolean getValue() {
        return value;
    }

    public ErrorMessage getError() {
        return error;
    }
}
