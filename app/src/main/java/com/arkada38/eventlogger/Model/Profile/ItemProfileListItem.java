package com.arkada38.eventlogger.Model.Profile;

import java.io.Serializable;
import java.util.Date;

public class ItemProfileListItem implements Serializable {
    public String note;
    public Date startDate, endDate;

    public ItemProfileListItem() {}

    public ItemProfileListItem(Date startDate) {
        this.startDate = startDate;
    }
}
