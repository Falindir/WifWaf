package shagold.wifwaf.view;

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
