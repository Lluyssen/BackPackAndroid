package app.backpackandroid;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class Login extends Activity {

    HttpRequest httpRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        httpRequest = new HttpRequest(Login.this);

        final CircularProgressButton circularProgressButton = (CircularProgressButton) findViewById(R.id.LoginBtn);
        //Button LoginBtn = findViewById(R.id.LoginBtn);
        final EditText userEdit = (EditText) findViewById(R.id.UsernameInputLogin);
        final EditText passwdEdit = (EditText) findViewById(R.id.PasswordInputLogin);

        circularProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = userEdit.getText().toString();
                String password = passwdEdit.getText().toString();
                circularProgressButton.startAnimation();
                httpRequest.GetToken(user, password, circularProgressButton);
                //circularProgressButton.doneLoadingAnimation(Color.parseColor(""));
            }
        });

        /*LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = userEdit.getText().toString();
                String password = passwdEdit.getText().toString();
                httpRequest.GetToken(user, password);
            }
        });*/
    }

    public void RegisterClick(View v)
    {
        startActivity(new Intent(Login.this, Register.class));
    }
}
