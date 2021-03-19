package com.dacaspex.provider.reddit;

import com.dacaspex.provider.Provider;
import com.dacaspex.provider.RunnableType;
import com.dacaspex.storage.list.ListItem;
import com.dacaspex.storage.list.TemporaryRankedListStorage;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.pagination.DefaultPaginator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Provider for <a href="https://www.reddit.com/">reddit.com</a>. It fetches the
 * top posts from a subreddit, aggregated by 24h, weekly, monthly, etc. This provider
 * is built upon the <a href="https://github.com/mattbdean/JRAW">JRAW API</a>.
 */
public class RedditTopProvider implements Provider {
    private static final String PLATFORM = "bot";
    private static final String APP_ID = "com.dacaspex.feed";
    private static final String REDDIT_BASE_URL = "https://www.reddit.com";

    private final static Logger logger = LogManager.getLogger();

    private final String name;
    private final TemporaryRankedListStorage rankedListStorage;
    private final String source;

    private final String version;
    private final String redditUsername;
    private final String redditPassword;
    private final String clientId;
    private final String clientSecret;
    private final String subreddit;
    private final int amount;
    private final TimePeriod timePeriod;
    private final Sort sort;

    public RedditTopProvider(
        String name,
        TemporaryRankedListStorage rankedListStorage,
        String source,
        String version,
        String redditUsername,
        String redditPassword,
        String clientId,
        String clientSecret,
        String subreddit,
        int amount,
        TimePeriod timePeriod,
        Sort sort
    ) {
        this.name = name;
        this.rankedListStorage = rankedListStorage;
        this.source = source;
        this.version = version;
        this.redditUsername = redditUsername;
        this.redditPassword = redditPassword;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.subreddit = subreddit;
        this.amount = amount;
        this.timePeriod = timePeriod;
        this.sort = sort;
    }

    @Override
    public RunnableType getRunnableType() {
        return RunnableType.AD_HOC;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void execute() {
        try {
            // Setup the reddit client
            UserAgent userAgent = new UserAgent(PLATFORM, APP_ID, version, redditUsername);

            Credentials credentials = Credentials.script(redditUsername, redditPassword, clientId, clientSecret);

            // https://github.com/square/okhttp/issues/1739
            NetworkAdapter adapter = new OkHttpNetworkAdapter(userAgent);

            RedditClient redditClient = OAuthHelper.automatic(adapter, credentials);

            // JRAW has request/response logging enabled by default. We disable that here
            redditClient.setLogger(new NullHttpLogger());

            // Get the posts from reddit
            DefaultPaginator<Submission> paginator = redditClient
                .subreddit(subreddit)
                .posts()
                .limit(amount)
                .sorting(getSubredditSort(sort))
                .timePeriod(getTimePeriod(timePeriod))
                .build();

            Listing<Submission> popular = paginator.next();

            List<ListItem> list = new ArrayList<>();
            for (Submission submission : popular) {
                list.add(
                    new ListItem(
                        submission.getUniqueId(),
                        submission.getTitle(),
                        getRedditUrl(submission.getPermalink())
                    )
                );
            }

            rankedListStorage.addList(source, list);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private String getRedditUrl(String permalink) {
        return String.format("%s%s", REDDIT_BASE_URL, permalink);
    }

    private net.dean.jraw.models.TimePeriod getTimePeriod(TimePeriod timePeriod) {
        switch (timePeriod) {
            case HOURLY:
                return net.dean.jraw.models.TimePeriod.HOUR;
            case DAILY:
                return net.dean.jraw.models.TimePeriod.DAY;
            case WEEKLY:
                return net.dean.jraw.models.TimePeriod.WEEK;
            case MONTHLY:
                return net.dean.jraw.models.TimePeriod.MONTH;
            case YEARLY:
                return net.dean.jraw.models.TimePeriod.YEAR;
            case ALL_TIME:
                return net.dean.jraw.models.TimePeriod.ALL;
            default:
                throw new IllegalArgumentException(
                    String.format("Could not map %s to a valid time period", timePeriod.name())
                );
        }
    }

    private SubredditSort getSubredditSort(Sort sort) {
        switch (sort) {
            case HOT:
                return SubredditSort.HOT;
            case BEST:
                return SubredditSort.BEST;
            case NEW:
                return SubredditSort.NEW;
            case RISING:
                return SubredditSort.RISING;
            case CONTROVERSIAL:
                return SubredditSort.CONTROVERSIAL;
            case TOP:
                return SubredditSort.TOP;
            default:
                throw new IllegalArgumentException(
                    String.format("Could not map %s to a valid subreddit sort", sort.name())
                );
        }
    }
}
