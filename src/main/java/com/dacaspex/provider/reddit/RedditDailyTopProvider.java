package com.dacaspex.provider.reddit;

import com.dacaspex.provider.Provider;
import com.dacaspex.storage.article.ArticleStorage;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.models.TimePeriod;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.pagination.DefaultPaginator;
import org.joda.time.DateTime;

public class RedditDailyTopProvider implements Provider {
    private final String PLATFORM = "bot";
    private final String APP_ID = "com.dacaspex.feed";

    private final ArticleStorage articleStorage;
    private final String source;

    private final String version;
    private final String redditUsername;
    private final String redditPassword;
    private final String clientId;
    private final String clientSecret;
    private final String subreddit;
    private final int amount;

    public RedditDailyTopProvider(
            ArticleStorage articleStorage,
            String source,
            String version,
            String redditUsername,
            String redditPassword,
            String clientId,
            String clientSecret,
            String subreddit,
            int amount
    ) {
        this.articleStorage = articleStorage;
        this.source = source;
        this.version = version;
        this.redditUsername = redditUsername;
        this.redditPassword = redditPassword;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.subreddit = subreddit;
        this.amount = amount;
    }

    @Override
    public void invoke() {
        try {
            // Setup the reddit client
            UserAgent userAgent = new UserAgent(PLATFORM, APP_ID, version, redditUsername);

            Credentials credentials = Credentials.script(redditUsername, redditPassword, clientId, clientSecret);

            // https://github.com/square/okhttp/issues/1739
            // TODO: Catch exception to shutdown gracefully
            NetworkAdapter adapter = new OkHttpNetworkAdapter(userAgent);

            RedditClient redditClient = OAuthHelper.automatic(adapter, credentials);

            DefaultPaginator<Submission> paginator = redditClient
                    .subreddit(subreddit)
                    .posts()
                    .limit(amount)
                    .sorting(SubredditSort.TOP)
                    .timePeriod(TimePeriod.DAY)
                    .build();

            Listing<Submission> popular = paginator.next();

            popular.forEach(submission -> {
                articleStorage.insertOrUpdateArticle(
                        submission.getUniqueId(),
                        source,
                        submission.getTitle(),
                        "",
                        submission.getUrl(),
                        new DateTime(submission.getCreated())
                );
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
