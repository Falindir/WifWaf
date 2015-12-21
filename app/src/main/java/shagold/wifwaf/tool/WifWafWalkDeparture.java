package shagold.wifwaf.tool;

import java.util.regex.Pattern;

public class WifWafWalkDeparture {

    private String value;

    public WifWafWalkDeparture(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    private String[] getValues() {
        String[] s = new String[0];

        if(value.contains("T"))
            s = value.split("T");
        else if(value.contains(" "))
            s = value.split(" ");
        else
            System.out.println("BUG type time : " + value);

        return s;
    }

    public String getDate() {
        return getValues()[0];
    }

    public String getTime() {
        String s = getValues()[1];
        if(s.contains("."))
            return s.split(Pattern.quote("."))[0];

        return s;
    }

    public String getFormattedDate() {
        String s = getDate();

        return s;
    }

    public String getFormattedTime() {
        String[] s = getTime().split(":");
        return s[0] + ":" + s[1];
    }
}
