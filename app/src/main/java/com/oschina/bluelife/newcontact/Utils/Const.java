package com.oschina.bluelife.newcontact.Utils;

import com.oschina.bluelife.newcontact.model.Vcard;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

/**
 * Created by slomka.jin on 2016/10/20.
 */

public class Const {
    public final static String STAR="☆";
    public final static String CRAP="#";
    public final static int ICON_SIZE=320;

    public static String getStarAscii(){
        return STAR.charAt(0)+"";
    }

    public static String getQRContent(){
        Vcard vcard=getCard();
        Moshi moshi=new Moshi.Builder().build();
        JsonAdapter<Vcard> jsonAdapter=moshi.adapter(Vcard.class);
        return jsonAdapter.toJson(vcard);
    }
    public static Vcard getCard(){
        Vcard vcard=new Vcard();
        vcard.address="北京市宣武区广安门外大街2号";
        vcard.company="CCTV";
        vcard.email="email@cctv.com";
        vcard.linePhone="010-55555555";
        vcard.mobilePhone="15888888888";
        vcard.name="台长";
        vcard.place="总编辑";
        vcard.website="www.cctv.com";
        return vcard;
    }
}
