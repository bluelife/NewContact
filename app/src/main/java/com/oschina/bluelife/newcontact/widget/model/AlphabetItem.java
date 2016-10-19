package com.oschina.bluelife.newcontact.widget.model;

/**
 * Created by slomka.jin on 2016/10/19.
 */

public class AlphabetItem {

    public int position;
    public String word;
    public boolean isActive;

    public AlphabetItem(int pos, String word, boolean isActive) {
        this.position = pos;
        this.word = word;
        this.isActive = isActive;
    }
}