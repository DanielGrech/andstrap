package {package_name}.jobs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;
import {package_name}.BuildConfig;
import {package_name}.api.IApiManager;
import {package_name}.api.IPersistenceManager;
import {package_name}.module.annotation.ForApplication;


import java.util.UUID;

import javax.inject.Inject;

import timber.log.Timber;

import static {package_name}.jobs.Constants.*;

/**
 * Base class for long running jobs in the app
 */
public abstract class BaseJob extends Job {

    protected static final int PRIORITY_LOW = 1;
    protected static final int PRIORITY_DEFAULT = 10;
    protected static final int PRIORITY_HIGH = 9000;

    private transient final String mToken;

    @Inject
    transient LocalBroadcastManager mLocalBroadcastManager;

    @Inject
    transient IApiManager mApiManager;

    @Inject
    transient IPersistenceManager mPersistenceManager;

    protected abstract Bundle runJob() throws Throwable;

    public BaseJob(Params params) {
        super(params);
        mToken = UUID.randomUUID().toString();
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        sendStartBroadcast();
        final Bundle results = runJob();
        sendFinishBroadcast(results);
    }

    @Override
    protected void onCancel() {
        sendErrorBroadcast(null);
        sendFinishBroadcast(null);
    }

    @Override
    protected boolean shouldReRunOnThrowable(final Throwable throwable) {
        // Override in subclasses to differentiate between errors.
        // For example, if this was an Authentication exception,
        // there would be much point in continuing unless we re-authenticate
        Timber.e(throwable, "Error executing job " + getClass().getSimpleName());

        return true;
    }

    @Override
    protected int getRetryLimit() {
        return BuildConfig.DEFAULT_JOB_RETRY_LIMIT;
    }

    private void sendStartBroadcast() {
        Intent intent = getIntentForAction(ACTION_API_START);
        broadcast(intent);
    }

    private void sendFinishBroadcast(Bundle results) {
        Intent intent = getIntentForAction(ACTION_API_FINISH);
        intent.putExtra(EXTRA_RESULTS, results);
        broadcast(intent);
    }

    private void sendErrorBroadcast(String errorMessage) {
        final Intent intent = getIntentForAction(ACTION_API_ERROR);
        intent.putExtra(EXTRA_ERROR_MESSAGE, errorMessage);
        broadcast(intent);
    }

    private void broadcast(Intent intent) {
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    public String getToken() {
        return mToken;
    }

    private Intent getIntentForAction(String action) {
        final Intent intent = new Intent(action);
        intent.putExtra(EXTRA_TOKEN, mToken);
        intent.putExtra(EXTRA_CLASS_NAME, getClass().getName());

        return intent;
    }
}

