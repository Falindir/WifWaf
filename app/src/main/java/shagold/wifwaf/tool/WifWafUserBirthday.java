package shagold.wifwaf.tool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

public class WifWafUserBirthday {

    private String value;

    public WifWafUserBirthday(String value) {
        this.value = value;
    }

    private String[] getValues() {
        String[] s = new String[1];

        if(value.contains("T"))
            s = value.split("T");
        else if(value.contains(" "))
            s = value.split(" ");
        else
            s[0] = value;

        return s;
    }

    public String getDate() {
        String[] values = getValues();
        String dt = values[0];
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        if(values.length > 1) {
            try {
                c.setTime(sdf.parse(dt));
                c.add(Calendar.DATE, 1);
                dt = sdf.format(c.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return dt;
    }
}
