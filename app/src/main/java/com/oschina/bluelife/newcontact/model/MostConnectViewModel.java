package com.oschina.bluelife.newcontact.model;

import com.oschina.bluelife.newcontact.R;
import com.oschina.bluelife.newcontact.Utils.Const;

import java.util.List;

/**
 * Created by slomka.jin on 2016/10/20.
 */

public class MostConnectViewModel implements ContactViewModel {
   private List<Person> persons;

    public MostConnectViewModel(List<Person> personList){
        persons=personList;
    }
    public List<Person> getPersons(){
        return persons;
    }
    @Override
    public String getSortCode() {
        return Const.STAR;
    }

    @Override
    public boolean selectable() {
        return false;
    }

    @Override
    public int getType() {
        return R.layout.contact_sub_list;
    }
}
