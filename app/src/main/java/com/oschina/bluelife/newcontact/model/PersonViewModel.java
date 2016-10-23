package com.oschina.bluelife.newcontact.model;

import com.oschina.bluelife.newcontact.R;

/**
 * Created by slomka.jin on 2016/10/20.
 */

public class PersonViewModel implements ContactViewModel {

    private Person person;
    public PersonViewModel(Person people){
        person=people;
    }

    public Person getPerson(){
        return person;
    }

    @Override
    public int getType() {
        return R.layout.contact_list_item;
    }

    @Override
    public boolean selectable() {
        return true;
    }

    @Override
    public String getSortCode() {
        return person.getSpellFirstWord();
    }
}
