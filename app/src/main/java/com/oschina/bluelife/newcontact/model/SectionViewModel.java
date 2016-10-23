package com.oschina.bluelife.newcontact.model;

import com.oschina.bluelife.newcontact.R;

/**
 * Created by HiWin10 on 2016/10/20.
 */

public class SectionViewModel implements ContactViewModel {
    private String label;
    private String sortCode;
    private int count;

    public SectionViewModel(String label,String sortCode){
        this.label=label;
        this.sortCode=sortCode;
    }
    public void plus(){
        count++;
    }
    public void minus(){
        count--;
    }
    public int getCount(){
        return count;
    }
    public void setCount(int total){
        count=total;
    }
    @Override
    public String getSortCode() {
        return sortCode;
    }
    public void setLabel(String label){
        this.label=label;
    }
    public String getLabel(){
        return label;
    }
    @Override
    public int getType() {
        return R.layout.contact_list_section_item;
    }

    @Override
    public boolean selectable() {
        return false;
    }
}
