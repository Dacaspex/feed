package com.dacaspex.storage.article;

import com.dacaspex.storage.MysqlStorage;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Let methods throw exceptions and let the callers handle with them
 */
public class ArticleStorage extends MysqlStorage {
    public ArticleStorage(String host, String name, String username, String password) {
        super(host, name, username, password);
    }

    public List<Article> getArticles(List<String> sources, DateTime after) {
        init();

        List<Article> articles = new ArrayList<>();

        // If there are no sources selected, we early return with an empty list
        if (sources.size() == 0) {
            return articles;
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
                        "SELECT id, uuid, source, title, body, link, published_at " +
                        "FROM articles " +
                        "WHERE source IN %s " +
                        "AND published_at >= ?",
                    inClause
                )
            );

            statement.setString(1, dtf.print(after));

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                articles.add(getArticleFromResult(result));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return articles;
    }

    public Article getArticleByUuid(String uuid) {
        init();

        Article article = null;
        try {
            PreparedStatement statement = connection.prepareStatement(
                "" +
                    "SELECT id, uuid, source, title, body, link, published_at " +
                    "FROM articles " +
                    "WHERE uuid = ? "
            );

            statement.setString(1, uuid);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                article = getArticleFromResult(result);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return article;
    }

    public void insertOrUpdateArticle(String uuid, String source, String title, String body, String link, DateTime publishedAt) {
        init();

        Article article = getArticleByUuid(uuid);
        if (article == null) {
            // If null, it means that there is no article with this uuid in the database. Simply insert
            try {
                DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

                PreparedStatement statement = connection.prepareStatement("" +
                    "INSERT IGNORE INTO articles " +
                    "(uuid, source, title, body, link, published_at) " +
                    "VALUES(?, ?, ?, ?, ?, ?) "
                );

                statement.setString(1, uuid);
                statement.setString(2, source);
                statement.setString(3, title);
                statement.setString(4, body);
                statement.setString(5, link);
                statement.setString(6, dtf.print(publishedAt));

                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            // The article already exists in the database, update instead
            try {
                DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

                PreparedStatement statement = connection.prepareStatement("" +
                    "UPDATE articles " +
                    "SET source = ?, title = ?, body = ?, link = ?, published_at = ?" +
                    "WHERE uuid = ? "
                );

                statement.setString(1, source);
                statement.setString(2, title);
                statement.setString(3, body);
                statement.setString(4, link);
                statement.setString(5, dtf.print(publishedAt));
                statement.setString(6, uuid);

                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private Article getArticleFromResult(ResultSet result) throws SQLException {
        return new Article(
            result.getInt("id"),
            result.getString("uuid"),
            result.getString("source"),
            result.getString("title"),
            result.getString("body"),
            result.getString("link"),
            DateTimeFormat
                .forPattern("yyyy-MM-dd HH:mm:ss")
                .parseDateTime(result.getString("published_at"))
        );
    }
}
