package com.dacaspex.storage.event;

import org.joda.time.format.DateTimeFormat;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Mapper {
    public CalendarEvent mapRecordToCalendarEvent(ResultSet result) throws SQLException {
        return new CalendarEvent(
            result.getString("id"),
            result.getString("uuid"),
            DateTimeFormat
                .forPattern("yyyy-MM-dd HH:mm:ss")
                .parseDateTime(result.getString("date")),
            result.getString("header"),
            result.getString("url")
        );
    }
}
