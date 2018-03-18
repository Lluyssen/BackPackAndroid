package app.backpackandroid;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        Button LoginBtn = findViewById(R.id.RegisterBtn);
        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EditText UserNameInput = (EditText) findViewById(R.id.UsernameInput);
                EditText PassWordInput = (EditText) findViewById(R.id.PasswordInput);
                EditText PassWordConfirmedInput = (EditText) findViewById(R.id.PasswordConfirmedInput);
                EditText EmailInput =(EditText) findViewById(R.id.EmailInput);

                if (UserNameInput.getText().toString() == null || PassWordInput.getText().toString() == null
                        || PassWordConfirmedInput.getText().toString() == null || EmailInput.getText().toString() == null) {
                    Toast.makeText(getApplicationContext(), "Wrong Argument", Toast.LENGTH_LONG).show();
                    System.out.println("Error");
                }
                if (PassWordInput.getText().toString() != PassWordConfirmedInput.getText().toString())
                    return;
                startActivity(new Intent(Register.this, MapsActivity.class));
            }
        });
    }
}
