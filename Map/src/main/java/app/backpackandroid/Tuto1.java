package app.backpackandroid;

import android.os.Bundle;
import android.app.Activity;
import android.view.Window;

public class Tuto1 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_tuto1);
    }
}
