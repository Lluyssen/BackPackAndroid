package app.backpackandroid;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class TutoPage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tuto_page);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);

        Button LoginBtn = findViewById(R.id.Tuto1Btn);
        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TutoPage.this, Tuto1.class));
            }
        });

        Button LoginBtn1 = findViewById(R.id.Tuto2Btn);
        LoginBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TutoPage.this, Tuto2.class));
            }
        });

        Button LoginBtn2 = findViewById(R.id.Tuto3Btn);
        LoginBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TutoPage.this, Tuto3.class));
            }
        });
    }
}
