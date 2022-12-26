package com.alpharelevant.idarenow.data.models.CustomModel;

import android.support.annotation.Nullable;

import java.util.Date;

/**
 * Created by NAUSIF on 07-Apr-18.
 */

public class EditProfile {

    public int profile_id;
    public String full_name;
    public String email;
    public String birthday;
    public String country_name;
    public Integer country_id;
    public Integer city_id;
    public String city_name;
    public Countries[] countries;
    public Cities[] cities;
}
