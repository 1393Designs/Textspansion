package com.designs_1393.textspansion.utils;

import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Tokens {

    public static String replace(String input, Context ctx) {
        // Date and Time
        input = input.replace("%tttt",
                android.text.format.DateFormat.getLongDateFormat(ctx).format(new Date()) + " " +
                        android.text.format.DateFormat.getTimeFormat(ctx).format(new Date())
        );
        input = input.replace("%ttt",
                android.text.format.DateFormat.getMediumDateFormat(ctx).format(new Date()) + " " +
                        android.text.format.DateFormat.getTimeFormat(ctx).format(new Date())
        );
        input = input.replace("%t",
                android.text.format.DateFormat.getDateFormat(ctx).format(new Date()) + " " +
                        android.text.format.DateFormat.getTimeFormat(ctx).format(new Date())
        );

        // Date
        input = input.replace("%xxxx",
                android.text.format.DateFormat.getLongDateFormat(ctx).format(new Date())
        );
        input = input.replace("%xxx",
                android.text.format.DateFormat.getMediumDateFormat(ctx).format(new Date())
        );
        input = input.replace("%x",
                android.text.format.DateFormat.getDateFormat(ctx).format(new Date())
        );

        // Time
        input = input.replace("%X",
                android.text.format.DateFormat.getTimeFormat(ctx).format(new Date())
        );

        // Custom
        if(input.contains("%MMMM")) { // Month as long text (January)
            DateFormat newDF = new SimpleDateFormat("MMMM");
            input = input.replace("%MMMM", newDF.format(new Date()));
        }

        if(input.contains("%MMM")) { // Month as text (Jan)
            DateFormat newDF = new SimpleDateFormat("MMM");
            input = input.replace("%MMM", newDF.format(new Date()));
        }

        if(input.contains("%M")) { // Month as int
            DateFormat newDF = new SimpleDateFormat("M");
            input = input.replace("%M", newDF.format(new Date()));
        }

        if(input.contains("%cccc")) { // Day of the week as a long string (Saturday)
            DateFormat newDF = new SimpleDateFormat("cccc");
            input = input.replace("%cccc", newDF.format(new Date()));
        }

        if(input.contains("%ccc")) { // Day of the week as a string (Sat)
            DateFormat newDF = new SimpleDateFormat("ccc");
            input = input.replace("%ccc", newDF.format(new Date()));
        }

        if(input.contains("%d")) { // Date of the month
            DateFormat newDF = new SimpleDateFormat("d");
            input = input.replace("%d", newDF.format(new Date()));
        }

        if(input.contains("%yy")) { //Truncated year '11'
            DateFormat newDF = new SimpleDateFormat("yy");
            input = input.replace("%yy", newDF.format(new Date()));
        }

        if(input.contains("%y")) { //Full year '2011'
            DateFormat newDF = new SimpleDateFormat("y");
            input = input.replace("%y", newDF.format(new Date()));
        }

        if(input.contains("%h")) { // Hour of the day using AM/PM
            DateFormat newDF = new SimpleDateFormat("h");
            input = input.replace("%h", newDF.format(new Date()));
        }

        if(input.contains("%k")) { // Hour of the day using 1-24
            DateFormat newDF = new SimpleDateFormat("k");
            input = input.replace("%k", newDF.format(new Date()));
        }

        if(input.contains("%mm")) { //Minute in the hour
            DateFormat newDF = new SimpleDateFormat("mm");
            input = input.replace("%mm", newDF.format(new Date()));
        }

        if(input.contains("%ss")) { // Second in the minute
            DateFormat newDF = new SimpleDateFormat("ss");
            input = input.replace("%ss", newDF.format(new Date()));
        }

        return input;
    }
}
