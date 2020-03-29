package com.example.final_project_template;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText eTUserNameL, eTPasswordL, eTUserNameR, eTPasswordR, eTConfirmPasswordR;
    CheckBox cBStayLoggedIn;
    Button btnLogIn, btnRegistration;

    final String databaseName = "databaseUser.db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eTUserNameL = findViewById(R.id.eTUserNameL);
        eTPasswordL = findViewById(R.id.eTPasswordL);
        eTUserNameR = findViewById(R.id.eTUserNameR);
        eTPasswordR = findViewById(R.id.eTPasswordR);
        eTConfirmPasswordR = findViewById(R.id.eTConfirmPasswordR);

        cBStayLoggedIn = findViewById(R.id.cBStayLoggedIn);

        btnLogIn = findViewById(R.id.btnLogIn);
        btnRegistration = findViewById(R.id.btnRegistration);
        btnLogIn.setOnClickListener(this);
        btnRegistration.setOnClickListener(this);

        if (firstAppStart()){
            createDatabase();
        }

        SharedPreferences prefStayLoggedIn = getSharedPreferences("loggedin", MODE_PRIVATE);
        if (prefStayLoggedIn.getBoolean("loggedin", false)){
            loadActivity();
        }
    }

    public void login(String username, String password){
        if (checkLogIn(username, password)) {
            if (stayLoggedIn()){
                setStayLoggedIn();
            }
            loadActivity();
        } else {
            Toast.makeText(getApplicationContext(), "Benutzername und Passwort nicht übereinstimmend!", Toast.LENGTH_SHORT).show();
        }
        eTPasswordL.setText("");
    }

    public void registration(String username, String password, String passwordC){
        if (checkUserNameIsOkay(username) && passwordConfirmation(password, passwordC)){
            createAccount(username, password);
        }else if (!checkUserNameIsOkay(username)){
            Toast.makeText(getApplicationContext(), "The username is already taken", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "The password is not matching", Toast.LENGTH_SHORT).show();
        }
        eTUserNameL.setText("");
        eTPasswordR.setText("");
        eTConfirmPasswordR.setText("");
    }

    // Methods for Log In
    public boolean checkLogIn(String username, String password){
        boolean okay = false;

        SQLiteDatabase databaseUser = getBaseContext().openOrCreateDatabase(databaseName, MODE_PRIVATE, null);
        Cursor cursorUser = databaseUser.rawQuery("SELECT password FROM user WHERE username = '" + username + "'", null);
        cursorUser.moveToFirst();

        if (cursorUser.getCount() > 0){
            if (cursorUser.getString(0).equals(password)){
                okay = true;
            }
        }
        cursorUser.close();
        databaseUser.close();
        return okay;
    }

    public boolean stayLoggedIn(){
        return cBStayLoggedIn.isChecked();
    }

    public void setStayLoggedIn(){
        SharedPreferences prefStayLoggedIn = getSharedPreferences("loggedin", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefStayLoggedIn.edit();
        editor.putBoolean("loggedin", true);
        editor.commit();
    }

    // Methods for registration

    public boolean checkUserNameIsOkay(String username){
        boolean okay = false;
        SQLiteDatabase databaseUser = getBaseContext().openOrCreateDatabase(databaseName, MODE_PRIVATE, null);
        Cursor cursorUser = databaseUser.rawQuery("SELECT COUNT(*) FROM user WHERE username = '" + username + "'", null);
        cursorUser.moveToFirst();
        if (cursorUser.getInt(0)==0){
            okay = true;
        }
        cursorUser.close();
        databaseUser.close();
        return okay;
    }

    public boolean passwordConfirmation(String password, String passwordC){
        boolean confirm = false;
        if (password.equals(passwordC)){
            confirm = true;
        }
        return confirm;
    }

    public void createAccount(String username, String password){
        SQLiteDatabase databaseUser = getBaseContext().openOrCreateDatabase(databaseName, MODE_PRIVATE, null);
        databaseUser.execSQL("INSERT INTO user VALUES('" + username + "', '" + password +"')");
        databaseUser.close();
    }

    // Methods for both
    public void loadActivity(){
        Intent intent = new Intent(this, SecondActivity.class);
        startActivity(intent);
        this.finish();
    }

    // Methods for first start
    public boolean firstAppStart(){
        boolean first = false;
        SharedPreferences sharedPreferences = getSharedPreferences("firstStart", MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        if (sharedPreferences.getBoolean("firstStart", false) == false){
            first = true;
            sharedPreferencesEditor.putBoolean("firstStart", true);
            sharedPreferencesEditor.commit();
        }
        return first;
    }

    public void createDatabase(){
        SQLiteDatabase databaseUser = getBaseContext().openOrCreateDatabase(databaseName, MODE_PRIVATE, null);
        databaseUser.execSQL("CREATE TABLE user(username TEXT, password TEXT)");
        databaseUser.close();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnLogIn:
                login(eTUserNameL.getText().toString(), eTPasswordL.getText().toString());
                break;
            case R.id.btnRegistration:
                registration(eTUserNameR.getText().toString(), eTPasswordR.getText().toString(), eTConfirmPasswordR.getText().toString());
                break;
        }
    }
}
