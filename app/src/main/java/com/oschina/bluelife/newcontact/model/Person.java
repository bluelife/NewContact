package com.oschina.bluelife.newcontact.model;

import com.oschina.bluelife.newcontact.Utils.Const;
import com.oschina.bluelife.newcontact.Utils.Format;

/**
 * Created by slomka.jin on 2016/10/20.
 */

public class Person {
    public String name;
    public String email;
    public String icon;
    public String phone;
    public String company;
    public String department;
    public String address;
    public String extra;
    public String place;
    public int connectCount;
    public String spell;
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
}
