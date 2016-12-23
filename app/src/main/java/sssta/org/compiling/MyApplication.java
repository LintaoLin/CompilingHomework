package sssta.org.compiling;

import android.app.Application;

/**
 * Created by mac on 16/12/23.
 */

public class MyApplication extends Application {

    public static String fileName = "DrawF";

    public static MyApplication     mInstance;

    public MyApplication(){
        mInstance = this;
    }


    public String getAppDirPath() {
        return getFilesDir().getAbsolutePath();
    }
}
