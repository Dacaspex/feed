package com.dacaspex.storage.event;

import com.dacaspex.storage.MysqlStorage;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EventStorage extends MysqlStorage {
    private final Mapper mapper;

    public EventStorage(String host, String name, String username, String password) {
        super(host, name, username, password);
        this.mapper = new Mapper();
    }

    public List<CalendarEvent> getCalendarEventsBetween(List<String> sources, DateTime start, DateTime end) {
        init();

        List<CalendarEvent> events = new ArrayList<>();

        // If there are no sources selected, we early return with an empty list
        if (sources.size() == 0) {
            return events;
        }

        // Prepare the list of sources to include apostrophes, so that it behaves nicely with
        // MySQL. Then, pack it into a MySQL list
        sources.replaceAll(string -> "'" + string + "'");
        String inClause = String.format("(%s)", String.join(", ", sources));

        // Formatter to convert DateTime to MySQL date time
        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

        try {
            PreparedStatement statement = connection.prepareStatement(
                    String.format(
                            "" +
                                    "SELECT id, uuid, date, header, url " +
                                    "FROM calendar_events " +
                                    "WHERE source IN %s " +
                                    "AND date >= ? " +
                                    "AND date <= ?",
                            inClause
                    )
            );

            statement.setString(1, dtf.print(start));
            statement.setString(2, dtf.print(end));

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                events.add(mapper.mapRecordToCalendarEvent(result));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return events;
    }

    public CalendarEvent getCalendarEventByUuid(String uuid) {
        CalendarEvent event = null;
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "" +
                            "SELECT id, uuid, date, header, url " +
                            "FROM calendar_events " +
                            "WHERE uuid = ? "
            );

            statement.setString(1, uuid);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                event = mapper.mapRecordToCalendarEvent(result);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return event;
    }

    public void insertOrUpdateCalendarEvent(String uuid, String source, DateTime date, String header, String url) {
        init();

        CalendarEvent event = getCalendarEventByUuid(uuid);
        if (event == null) {
            // If null, it means that there is no event with this uuid in the database. Simply insert
            try {
                DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

                PreparedStatement statement = connection.prepareStatement("" +
                        "INSERT IGNORE INTO calendar_events " +
                        "(uuid, source, date, header, url) " +
                        "VALUES(?, ?, ?, ?, ?) "
                );

                int i = 1;
                statement.setString(i++, uuid);
                statement.setString(i++, source);
                statement.setString(i++, dtf.print(date));
                statement.setString(i++, header);
                statement.setString(i, url);

                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            // The event already exists in the database, update instead
            try {
                DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

                PreparedStatement statement = connection.prepareStatement("" +
                        "UPDATE calendar_events " +
                        "SET date = ?, header = ?, url = ?" +
                        "WHERE uuid = ? "
                );

                int i = 1;
                statement.setString(i++, dtf.print(date));
                statement.setString(i++, header);
                statement.setString(i++, url);
                statement.setString(i, uuid);

                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
