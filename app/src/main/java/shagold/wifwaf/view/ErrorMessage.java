package shagold.wifwaf.view;

/**
 * Created by jimmy on 29/11/15.
 */
public enum ErrorMessage {

    BLANK {
        @Override
        public String toString() {
            return "Required field";
        }
    },

    NUMBER {
        @Override
        public String toString() {
            return "This field has to be a number";
        }
    },

    SIZE {
        @Override
        public String toString() {
            return "Wrong text size";
        }
    },

    EMAIL {
        @Override
        public String toString() {
            return "Wrong email typo";
        }
    }
}