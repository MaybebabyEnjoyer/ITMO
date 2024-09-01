package ru.itmo.wp.model.repository.impl;

import ru.itmo.wp.model.domain.Event;
import ru.itmo.wp.model.domain.Type;
import ru.itmo.wp.model.repository.EventRepository;

import java.sql.*;

public class EventRepositoryImpl extends BaseRepositoryImpl<Event> implements EventRepository {
    @Override
    public void safe(Event event) {
        saveImpl(event,
                new Pair("userId", event.getUserId()),
                new Pair("type", event.getType().toString()));
    }

    @Override
    protected Event toModel(ResultSetMetaData metaData, ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) {
            return null;
        }

        Event event = new Event();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            switch (metaData.getColumnName(i)) {
                case "id" -> event.setId(resultSet.getLong(i));
                case "userId" -> event.setUserId(resultSet.getLong(i));
                case "type" -> event.setType(Type.valueOf(resultSet.getString(i)));
                case "creationTime" -> event.setCreationTime(resultSet.getTimestamp(i));
                default -> {
                    // No operations.
                }
            }
        }

        return event;
    }

    @Override
    protected String getName() {
        return "Event";
    }
}
