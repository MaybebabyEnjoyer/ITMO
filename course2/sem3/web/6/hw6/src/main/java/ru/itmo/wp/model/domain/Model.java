package ru.itmo.wp.model.domain;

import java.util.Date;

public interface Model {
    long getId();
    void setId(long id);
    Date getCreationTime();
    void setCreationTime(Date creationTime);
}
