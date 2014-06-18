package {package_name}.model;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class BaseModel implements Parcelable {
    @Override
    public int describeContents() {
        return hashCode();
    }
}
