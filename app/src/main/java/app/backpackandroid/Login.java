package app.backpackandroid;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {

    HttpRequest httpRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        httpRequest = new HttpRequest(Login.this);

        //------------TEST---
        httpRequest.GetToken("user1", "user1");
        //-------------------

        Button LoginBtn = findViewById(R.id.LoginBtn);
        final EditText userEdit = (EditText) findViewById(R.id.UsernameInputLogin);
        final EditText passwdEdit = (EditText) findViewById(R.id.PasswordInputLogin);

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = userEdit.getText().toString();
                String password = passwdEdit.getText().toString();
                Toast.makeText(getApplicationContext(), "Loading...", Toast.LENGTH_SHORT).show();
                httpRequest.GetToken(user, password);
            }
        });
    }

    public void RegisterClick(View v)
    {
        startActivity(new Intent(Login.this, Register.class));
    }
}
