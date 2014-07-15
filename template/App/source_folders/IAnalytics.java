package {package_name};

import android.app.Activity;

public interface IAnalytics {

    public void timing(String label, long time);

    public void click(String label);

    public void event(String name);

    public void networkRequest(String label);

    public void networkFailure(String label);

    public void activityStart(Activity act);

    public void activityStop(Activity act);
}
