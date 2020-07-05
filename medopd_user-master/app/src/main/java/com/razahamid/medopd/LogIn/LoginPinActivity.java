package com.razahamid.medopd.LogIn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.razahamid.medopd.Home.MainActivity;
import com.razahamid.medopd.R;

import java.util.Random;

public class LoginPinActivity extends AppCompatActivity implements View.OnClickListener {

    private enum PinMode {
        REGISTER,
        VERIFY_REGISTER,
        LOGIN
    }

    private ConstraintLayout mainLayout;
    private ImageView ivPinOne, ivPinTwo, ivPinThree, ivPinFour;
    private TextView tvPINTitle, tvPINMsg;

    private String enteredPIN = "";
    private String oldPIN;
    private String registerPIN = "";
    private PinMode pinMode;
    private int incorrectCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login_pin);

        initView();
        initBackground();
        updatePinUI();
        initPINMode();
        initPINText();
    }

    private void initBackground(){
        Random random = new Random();
        Drawable bgResource;
        switch (random.nextInt(2)){
            case 0:
                bgResource = ContextCompat.getDrawable(this, R.drawable.bg_one);
                break;
            case 1:
            default:
                bgResource = ContextCompat.getDrawable(this, R.drawable.bg_two);
                break;
        }
        mainLayout.setBackground(bgResource);
    }

    private void initPINText() {
        String msg;
        switch (pinMode) {
            case REGISTER:
                msg = "REGISTER PIN";
                break;
            case VERIFY_REGISTER:
                msg = "VERIFY PIN";
                break;
            case LOGIN:
                msg = "ENTER YOUR PIN";
                break;
            default:
                msg = "NOT INITIALIZED";
        }
        tvPINTitle.setText(msg);
    }

    private void initPINMode() {
        SharedPreferences preferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        boolean hasPIN = preferences.contains("PIN");
        if (hasPIN) {
            oldPIN = preferences.getString("PIN", null);
        }
        if (oldPIN != null && !oldPIN.isEmpty()) {
            pinMode = PinMode.LOGIN;
        } else {
            pinMode = PinMode.REGISTER;
        }
    }

    private void initView() {
        findViewById(R.id.btn_one).setOnClickListener(this);
        findViewById(R.id.btn_two).setOnClickListener(this);
        findViewById(R.id.btn_three).setOnClickListener(this);
        findViewById(R.id.btn_four).setOnClickListener(this);
        findViewById(R.id.btn_five).setOnClickListener(this);
        findViewById(R.id.btn_six).setOnClickListener(this);
        findViewById(R.id.btn_seven).setOnClickListener(this);
        findViewById(R.id.btn_eight).setOnClickListener(this);
        findViewById(R.id.btn_nine).setOnClickListener(this);
        findViewById(R.id.btn_zero).setOnClickListener(this);
        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.btn_forgot).setOnClickListener(this);

        ivPinOne = findViewById(R.id.iv_pin_one);
        ivPinTwo = findViewById(R.id.iv_pin_two);
        ivPinThree = findViewById(R.id.iv_pin_three);
        ivPinFour = findViewById(R.id.iv_pin_four);
        tvPINTitle = findViewById(R.id.tv_pin_title);
        tvPINMsg = findViewById(R.id.tv_pin_msg);
        mainLayout = findViewById(R.id.main_layout);
    }

    private void updatePinUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvPINMsg.setText("");
                if (enteredPIN.length() >= 4) {
                    ivPinFour.setImageResource(R.drawable.shape_circle);
                } else {
                    ivPinFour.setImageResource(R.drawable.shape_circle_outline_2dp);
                }
                if (enteredPIN.length() >= 3) {
                    ivPinThree.setImageResource(R.drawable.shape_circle);
                } else {
                    ivPinThree.setImageResource(R.drawable.shape_circle_outline_2dp);
                }
                if (enteredPIN.length() >= 2) {
                    ivPinTwo.setImageResource(R.drawable.shape_circle);
                } else {
                    ivPinTwo.setImageResource(R.drawable.shape_circle_outline_2dp);
                }
                if (enteredPIN.length() >= 1) {
                    ivPinOne.setImageResource(R.drawable.shape_circle);
                } else {
                    ivPinOne.setImageResource(R.drawable.shape_circle_outline_2dp);
                }
            }
        });
    }

    private void verifyOrRegisterPIN() {
        switch (pinMode) {
            case REGISTER:
                onRegisterPIN();
                break;
            case VERIFY_REGISTER:
                onVerifyPIN();
                break;
            case LOGIN:
                onLoginPIN();
                break;
        }
    }

    private void onRegisterPIN() {
        this.registerPIN = this.enteredPIN;
        this.pinMode = PinMode.VERIFY_REGISTER;
        this.enteredPIN = "";
        updatePinUI();
        initPINText();
    }

    private void onVerifyPIN() {
        if (!registerPIN.isEmpty() && (registerPIN.equals(enteredPIN))) {
            onPINRegisterSuccess(enteredPIN);
        } else if (incorrectCount <= 3) {
            incorrectCount++;
            enteredPIN = "";
            updatePinUI();
            onPINError();
        } else {
            enteredPIN = "";
            registerPIN = "";
            pinMode = PinMode.REGISTER;
            updatePinUI();
            initPINText();
        }
    }

    private void onLoginPIN() {
        if (!oldPIN.isEmpty() && (oldPIN.equals(enteredPIN))) {
            onPINLoginSuccess();
        } else {
            onPINError();
        }
    }

    private void onPINRegisterSuccess(String pin) {
        SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
        preferences.edit().putString("PIN", pin).apply();
        onPINLoginSuccess();
    }

    private void onPINLoginSuccess() {
        startActivity(new Intent(this, MainActivity.class));
    }

    private void onPINError() {
        tvPINMsg.setText(R.string.pin_not_matching);
    }

    private void onPinKeysEntered(String val) {
        if (enteredPIN.length() < 4) {
            this.enteredPIN += val;
        }
        updatePinUI();
        if (enteredPIN.length() == 4) {
            verifyOrRegisterPIN();
        }
    }

    private void onBackPinEntered() {
        if (enteredPIN.length() > 0) {
            enteredPIN = enteredPIN.substring(0, enteredPIN.length() - 1);
        }
        updatePinUI();
    }

    private void onForgotPressed(){
        Intent intent = new Intent(this,LogInActivity.class);
        intent.putExtra("IS_FORGOT_PIN",true);
        SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
        preferences.edit().remove("PIN").apply();
        FirebaseAuth.getInstance().signOut();
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_one:
                onPinKeysEntered("1");
                break;
            case R.id.btn_two:
                onPinKeysEntered("2");
                break;
            case R.id.btn_three:
                onPinKeysEntered("3");
                break;
            case R.id.btn_four:
                onPinKeysEntered("4");
                break;
            case R.id.btn_five:
                onPinKeysEntered("5");
                break;
            case R.id.btn_six:
                onPinKeysEntered("6");
                break;
            case R.id.btn_seven:
                onPinKeysEntered("7");
                break;
            case R.id.btn_eight:
                onPinKeysEntered("8");
                break;
            case R.id.btn_nine:
                onPinKeysEntered("9");
                break;
            case R.id.btn_zero:
                onPinKeysEntered("0");
                break;
            case R.id.btn_back:
                onBackPinEntered();
                break;
            case R.id.btn_forgot:
                onForgotPressed();
                break;
        }

    }
}
