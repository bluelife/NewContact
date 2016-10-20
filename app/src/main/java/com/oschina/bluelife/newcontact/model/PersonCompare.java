package com.oschina.bluelife.newcontact.model;

import java.util.Comparator;

/**
 * Created by slomka.jin on 2016/10/20.
 */

public class PersonCompare implements Comparator<Person> {
    @Override
    public int compare(Person o1, Person o2) {
        return sort(o1,o2);
    }

    private int sort(Person person1,Person person2){
        int lhs_ascii = person1.getSpellFirstWord().toUpperCase().charAt(0);
        int rhs_ascii = person2.getSpellFirstWord().toUpperCase().charAt(0);

        if (lhs_ascii < 65 || lhs_ascii > 90)
            return 1;
        else if (rhs_ascii < 65 || rhs_ascii > 90)
            return -1;
        else
            return person1.spell.compareTo(person2.spell);
    }
    @Override
    public boolean equals(Object obj) {
        return false;
    }
}
