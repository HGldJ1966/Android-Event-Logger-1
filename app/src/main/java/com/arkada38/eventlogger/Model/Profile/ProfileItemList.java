package com.arkada38.eventlogger.Model.Profile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProfileItemList implements Serializable {
    public String header;
    public List<ItemProfileList> item = new ArrayList<>();
}
