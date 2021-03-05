package com.dacaspex.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MysqlStorage {
    private final String DRIVER = "com.mysql.cj.jdbc.Driver";

    private final String host;
    private final String name;
    private final String username;
    private final String password;
    private final boolean isInitialised;

    protected Connection connection;

    public MysqlStorage(String host, String name, String username, String password) {
        this.host = host;
        this.name = name;
        this.username = username;
        this.password = password;
        this.isInitialised = false;
    }

    public void init() {
        if (isInitialised) return;

        try {
            String url = String.format(
                "jdbc:mysql://%s/%s?&%s&%s&%s",
                host,
                name,
                "useJDBCCompliantTimezoneShift=true",
                "&useLegacyDatetimeCode=false",
                "&serverTimezone=GMT"
            );

            Class.forName(DRIVER);
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
