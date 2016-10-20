package com.oschina.bluelife.newcontact.model;

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
    public String getSortCode() {
        return person.getSpellFirstWord();
    }
}
