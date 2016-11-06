package com.oschina.bluelife.newcontact.model;

import java.util.Comparator;

/**
 * Created by HiWin10 on 2016/11/3.
 */

public class TimesCompare implements Comparator<Person> {
    @Override
    public int compare(Person o1, Person o2) {
        return o2.connectCount-o1.connectCount;
    }
}
