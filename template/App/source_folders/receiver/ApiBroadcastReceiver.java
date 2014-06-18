package {package_name}.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import {package_name}.R;
import {package_name}.jobs.BaseJob;

import java.util.HashSet;
import java.util.Set;

import static {package_name}.jobs.Constants.*;

/**
 * Catches broadcasts sent at different lifecycle events
 * of Api requests as executed by {@link {package_name}.jobs.BaseJob}
 */
public abstract class ApiBroadcastReceiver extends BroadcastReceiver {

    private Set<String> mAcceptableTokens = new HashSet<>();

    private Set<String> mAcceptableJobClasses = new HashSet<>();

    /**
     * Called when a new API request has started
     *
     * @param jobCls The class of the job
     * @param token  The kind of request being executed
     */
    protected abstract <T extends BaseJob> void onStart(Class<T> jobCls, String token);

    /**
     * Called when an API request has finished
     *
     * @param jobCls  The class of the job
     * @param token   The kind of request that finished
     * @param results Results passed back from the job
     */
    protected abstract <T extends BaseJob> void onFinish(Class<T> jobCls, String token,
                                                         Bundle results);

    /**
     * Called when there is an error with an API request
     *
     * @param jobCls   The class of the job
     * @param token    The kind of request which caused an error
     * @param errorMsg The human-readable error message representing the problem
     */
    protected abstract <T extends BaseJob> void onError(
            Class<T> jobCls, String token, String errorMsg);

    /**
     * Start listening for API events
     *
     * @param context
     */
    public void register(Context context) {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_API_START);
        filter.addAction(ACTION_API_FINISH);
        filter.addAction(ACTION_API_ERROR);
        LocalBroadcastManager.getInstance(context).registerReceiver(this, filter);
    }

    /**
     * Cease listening to API events
     *
     * @param context
     */
    public void unregister(Context context) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
    }

    /**
     * Accept broadcasts of type <code>token</code>
     *
     * @param token The kind of broadcast to accept
     */
    public void addAcceptableToken(String token) {
        mAcceptableTokens.add(token);
    }

    public <T extends BaseJob> void addAcceptableJobType(Class<T> cls) {
        mAcceptableJobClasses.add(cls.getName());
    }

    /**
     * Reference counter for the number of requests currently executing
     */
    private int mRunningCounter = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        final String token = intent.getStringExtra(EXTRA_TOKEN);
        final String className = intent.getStringExtra(EXTRA_CLASS_NAME);
        final Bundle results = intent.getBundleExtra(EXTRA_RESULTS);

        if (action != null && token != null &&
                (mAcceptableTokens.contains(token) || mAcceptableJobClasses.contains(className))) {

            Class<? extends BaseJob> cls = null;
            try {
                cls = (Class<? extends BaseJob>) Class.forName(className);
            } catch (ClassNotFoundException e) {
                cls = null;
            }
            switch (action) {
                case ACTION_API_START:
                    mRunningCounter++;
                    onStart(cls, action);
                    break;

                case ACTION_API_FINISH:
                    mRunningCounter--;
                    if (mRunningCounter < 0) {
                        mRunningCounter = 0;
                    }

                    onFinish(cls, action, results);
                    break;

                case ACTION_API_ERROR:
                    String errorMsg = intent.getStringExtra(EXTRA_ERROR_MESSAGE);
                    if (TextUtils.isEmpty(errorMsg)) {
                        errorMsg = context.getString(R.string.unknown_error);
                    }

                    onError(cls, action, errorMsg);

                    break;
            }
        }
    }

    /**
     * @return The number of currently executing requests
     */
    protected int getRunningCounter() {
        return mRunningCounter;
    }
}

