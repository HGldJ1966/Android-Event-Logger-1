package com.arkada38.eventlogger.Model.Profile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ItemProfileList implements Serializable {
    public String header;
    public String alternativeHeader;
    public int type;// 0 - С промежутком, 1 - Моментальные, 2 - Дипольные
    public List<ItemProfileListItem> item = new ArrayList<>();

    public ItemProfileList(String header, int type) {
        this.header = header;
        this.type = type;
    }

    public ItemProfileList(String header, String alternativeHeader) {
        this.header = header;
        this.alternativeHeader = alternativeHeader;
        this.type = 2;
    }

    public ItemProfileList(String header) {
        this(header, 0);
    }
}
