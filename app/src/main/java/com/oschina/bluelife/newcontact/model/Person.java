package com.oschina.bluelife.newcontact.model;

import com.oschina.bluelife.newcontact.Utils.Const;
import com.oschina.bluelife.newcontact.Utils.Format;

/**
 * Created by slomka.jin on 2016/10/20.
 */

public class Person {
    public String id;
    public String rowId;
    public String name;
    public String email;
    public String emailLabel;
    public String icon;
    public String phone;
    public String phoneLabel;
    public String homePhone;

    public String company;
    public String department;
    public String address;
    public String extra;
    public String place;
    public String title;
    public int connectCount;
    public String spell;
    public String url;
    public Person(){

    }
    public Person(String name,String email,String icon){
        this.name=name;
        this.email=email;
        this.icon=icon;
        spell=Format.getPingYin(name);
    }

    public String getSpellFirstWord(){
        String word=spell.substring(0,1).toUpperCase();
        return Format.isAlphabet(word)?word: Const.CRAP;

    }
    @Override
    public String toString(){
        return name+","+phone+","+email+","+company+","+department+","+title+" note:"+extra+" address:"+address+" url:"+url
                +" icon:"+icon;
    }
}
