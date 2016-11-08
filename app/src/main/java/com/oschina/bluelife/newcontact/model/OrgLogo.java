package com.oschina.bluelife.newcontact.model;

import com.google.auto.value.AutoValue;
import com.squareup.sqldelight.RowMapper;

/**
 * Created by slomka.jin on 2016/11/7.
 */

@AutoValue
public abstract class OrgLogo implements OrgLogoModel {
    public static final Factory<OrgLogo> FACTORY = new Factory<>(new Creator<OrgLogo>() {
        @Override public OrgLogo create(long _id, long contactId, String image,String name) {
            return new AutoValue_OrgLogo(_id, contactId,image, name);
        }
    });

    public static final RowMapper<OrgLogo> SELECT_ALL_MAPPER = FACTORY.select_allMapper();
}
