package app.backpackandroid;

import android.os.Bundle;
import android.app.Activity;
import android.view.Window;

public class Tuto3 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuto3);
    }

}
