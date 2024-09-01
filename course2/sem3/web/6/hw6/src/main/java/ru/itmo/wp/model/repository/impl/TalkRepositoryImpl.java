package ru.itmo.wp.model.repository.impl;

import ru.itmo.wp.model.domain.Talk;
import ru.itmo.wp.model.repository.TalkRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TalkRepositoryImpl extends BaseRepositoryImpl<Talk> implements TalkRepository {
    @Override
    public void save(Talk talk) {
        saveImpl(talk,
                new Pair("sourceUserId", talk.getSourceUserId()),
                new Pair("targetUserId", talk.getTargetUserId()),
                new Pair("text", talk.getText()));
    }

    @Override
    public List<Talk> findAllByUserId(long id) {
        List<Talk> talks = findAllImpl();
        List<Talk> result = new ArrayList<>();
        for (Talk talk : talks) {
            if (talk.getSourceUserId() == id || talk.getTargetUserId() == id) {
                result.add(talk);
            }
        }
        return result;
    }

    @Override
    protected Talk toModel(ResultSetMetaData metaData, ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) {
            return null;
        }

        Talk talk = new Talk();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            switch (metaData.getColumnName(i)) {
                case "id" -> talk.setId(resultSet.getLong(i));
                case "sourceUserId" -> talk.setSourceUserId(resultSet.getLong(i));
                case "targetUserId" -> talk.setTargetUserId(resultSet.getLong(i));
                case "text" -> talk.setText(resultSet.getString(i));
                case "creationTime" -> talk.setCreationTime(resultSet.getTimestamp(i));
                default -> {
                    // No operations.
                }
            }
        }

        return talk;
    }

    @Override
    protected String getName() {
        return "Talk";
    }
}
