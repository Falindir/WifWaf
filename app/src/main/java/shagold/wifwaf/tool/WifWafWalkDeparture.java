package shagold.wifwaf.tool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

    public boolean isTooLate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar current = Calendar.getInstance();
        Calendar dep = Calendar.getInstance();

        Date d = current.getTime();
        String currentS = sdf.format(d);
        String depS = getDate();

        try {
            current.setTime(sdf.parse(currentS));
            dep.setTime(sdf.parse(depS));
            return dep.before(current);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }
}
