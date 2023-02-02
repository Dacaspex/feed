package com.dacaspex.provider.reddit;

import com.dacaspex.collector.ItemCollector;
import com.dacaspex.collector.models.ListItem;
import com.dacaspex.collector.models.OrderedList;
import com.dacaspex.provider.AbstractProvider;
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
public class RedditTopProvider extends AbstractProvider {
    private static final String REDDIT_BASE_URL = "https://www.reddit.com";
    private final static Logger logger = LogManager.getLogger();

    private final RedditTopProviderSettings settings;

    public RedditTopProvider(String id, ItemCollector itemCollector, RedditTopProviderSettings settings) {
        super(id, itemCollector);
        this.settings = settings;
    }

    @Override
    public void execute() {
        try {
            // Set up the reddit client
            UserAgent userAgent = new UserAgent(settings.platform, settings.appId, settings.version, settings.username);
            Credentials credentials = Credentials.script(
                settings.username,
                settings.password,
                settings.clientId,
                settings.clientSecret
            );

            // https://github.com/square/okhttp/issues/1739
            NetworkAdapter adapter = new OkHttpNetworkAdapter(userAgent);

            RedditClient redditClient = OAuthHelper.automatic(adapter, credentials);

            // JRAW has request/response logging enabled by default. We disable that here
            redditClient.setLogger(new NullHttpLogger());

            // Get the posts from reddit
            DefaultPaginator<Submission> paginator = redditClient
                .subreddit(settings.subReddit)
                .posts()
                .limit(settings.amount)
                .sorting(getSubredditSort(settings.sort))
                .timePeriod(getTimePeriod(settings.timePeriod))
                .build();

            Listing<Submission> popular = paginator.next();

            List<ListItem> items = new ArrayList<>();
            for (Submission submission : popular) {
                items.add(new ListItem(submission.getTitle(), getRedditUrl(submission.getPermalink())));
            }

            itemCollector.addOrderedList(new OrderedList(id, items));
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
