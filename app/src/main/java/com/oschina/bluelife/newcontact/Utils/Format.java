package com.oschina.bluelife.newcontact.Utils;

/**
 * Created by HiWin10 on 2016/10/19.
 */

public class Format {
    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
    public static boolean isValidPhone(CharSequence target){
        if(target==null)
            return false;
        return target.length()==11;
    }
}
