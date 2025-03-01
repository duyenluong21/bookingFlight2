package com.example.bookingflight.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bookingflight.R;
import com.example.bookingflight.model.User;

import org.mindrot.jbcrypt.BCrypt;

public class Editpassword extends AppCompatActivity {
    private EditText txOldPassword, txNewPassword, txConfirmPassword;
    private Button btnSave;
    private ImageView backButton;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editpassword);

        txOldPassword = findViewById(R.id.txOldPassword);
        txNewPassword = findViewById(R.id.txNewPassword);
        txConfirmPassword = findViewById(R.id.txConfirmPassword);
        backButton = findViewById(R.id.backButton);
        btnSave = findViewById(R.id.btnSave);

        currentUser = getIntent().getParcelableExtra("object_user");

        backButton.setOnClickListener(v -> {
            startActivity(new Intent(Editpassword.this, LoginProfile.class));
            finish();
        });

//        btnSave.setOnClickListener(v -> {
//            String oldPassword = txOldPassword.getText().toString().trim();
//            String newPassword = txNewPassword.getText().toString().trim();
//            String confirmPassword = txConfirmPassword.getText().toString().trim();
//
//            if (validateInput(oldPassword, newPassword, confirmPassword)) {
//                if (checkOldPassword(oldPassword)) {
//                    Intent intent = new Intent(Editpassword.this, VerifyEmail.class);
//                    intent.putExtra("new_password", newPassword);
//                    intent.putExtra("object_user", currentUser);
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(Editpassword.this, "Mật khẩu cũ không đúng.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    private boolean checkOldPassword(String oldPassword) {
        return currentUser != null && BCrypt.checkpw(oldPassword, currentUser.getPassword());
    }

    private boolean validateInput(String oldPassword, String newPassword, String confirmPassword) {
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu mới và xác nhận mật khẩu không khớp.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (oldPassword.equals(newPassword)) {
            Toast.makeText(this, "Mật khẩu mới phải khác mật khẩu cũ.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}