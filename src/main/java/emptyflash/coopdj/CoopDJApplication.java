package emptyflash.coopdj;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.app.Application;

public class CoopDJApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}