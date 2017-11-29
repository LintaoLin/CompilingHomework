package sssta.org.compiling;

public class Application extends android.app.Application {

    public static String fileName = "DrawF";

    public static Application mInstance;

    public Application() {
        mInstance = this;
    }

    public String getAppDirPath() {
        return getFilesDir().getAbsolutePath();
    }
}
