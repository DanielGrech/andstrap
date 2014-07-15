package {package_name};

import android.app.Activity;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class Analytics implements IAnalytics {

    private final Tracker mTracker;

    private final GoogleAnalytics mGoogleAnalytics;

    public Analytics(GoogleAnalytics ga) {
        mGoogleAnalytics = ga;
        mTracker = mGoogleAnalytics.newTracker(R.xml.analytics);
    }

    @Override
    public void timing(String label, long time) {
        sendTiming(label, time);
    }

    @Override
    public void click(String label) {
        send(Category.UI_EVENT, Action.CLICK, label);
    }

    @Override
    public void event(String name) {
        send(Category.APP_EVENT, Action.APP_EVENT, name);
    }

    @Override
    public void networkRequest(String label) {
        send(Category.NETWORK_EVENT, Action.REQUEST, label);
    }

    @Override
    public void networkFailure(String label) {
        send(Category.NETWORK_EVENT, Action.FAILURE, label);
    }

    @Override
    public void activityStart(Activity act) {
        mGoogleAnalytics.reportActivityStart(act);
    }

    @Override
    public void activityStop(Activity act) {
        mGoogleAnalytics.reportActivityStop(act);
    }

    private void send(String category, String action, String label) {
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build());
    }

    private void sendTiming(String label, long value) {
        mTracker.send(new HitBuilders.TimingBuilder()
                .setCategory(Category.APP_EVENT)
                .setLabel(label)
                .setVariable(label)
                .setValue(value)
                .build());
    }

    public static class Category {

        static final String UI_EVENT = "UI_Event";

        static final String APP_EVENT = "app_event";

        static final String NETWORK_EVENT = "Network_Event";
    }

    public static class Action {

        static final String CLICK = "click";

        static final String APP_EVENT = "app_event";

        static final String REQUEST = "request";

        static final String FAILURE = "failure";
    }

}
