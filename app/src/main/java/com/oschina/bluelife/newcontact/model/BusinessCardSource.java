package com.oschina.bluelife.newcontact.model;

import com.oschina.bluelife.newcontact.Utils.Const;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HiWin10 on 2016/10/22.
 */

public class BusinessCardSource {
    private List<Vcard> vcards;
    private static BusinessCardSource businessCardSource;

    public static BusinessCardSource get(){
        if(businessCardSource==null)
            businessCardSource=new BusinessCardSource();
        return businessCardSource;
    }
    private BusinessCardSource(){
        vcards=new ArrayList<>();
        addCard(Const.getCard());
    }
    public void addCard(Vcard vcard){
        vcards.add(vcard);
    }
    public Vcard getCard(int index){
        return vcards.get(index);
    }
    public int getLastIndex(){
        return vcards.size()-1;
    }
}
