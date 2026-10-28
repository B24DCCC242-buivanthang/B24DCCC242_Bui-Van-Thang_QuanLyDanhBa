package com.example.th2_danh_ba;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    // Tài khoản cố định được yêu cầu trong đề bài
    public static final String DEFAULT_USERNAME = "admin";
    public static final String DEFAULT_PASSWORD = "123456";

    private View rootLayout;
    private EditText etUsername;
    private EditText etPassword;
    private TextView tvErrorMessage;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        handleWindowInsets();
        setupEvents();
    }

    private void initViews() {
        rootLayout = findViewById(R.id.rootLayout);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        btnLogin = findViewById(R.id.btnLogin);
    }

    /**
     * Hạ layout xuống dưới thanh Status Bar để tránh vướng trần màn hình
     */
    private void handleWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            return windowInsets;
        });
    }

    private void setupEvents() {
        btnLogin.setOnClickListener(v -> handleLogin());
    }

    private void handleLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Kiểm tra không để trống tên đăng nhập hoặc mật khẩu
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            tvErrorMessage.setText(R.string.err_empty_login);
            tvErrorMessage.setVisibility(View.VISIBLE);
            return;
        }

        // So sánh với tài khoản cố định
        if (DEFAULT_USERNAME.equals(username) && DEFAULT_PASSWORD.equals(password)) {
            tvErrorMessage.setVisibility(View.GONE);
            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

            // Chuyển sang MainActivity và gọi finish() để nút Back không quay lại LoginActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Hiển thị thông báo sai thông tin và ở lại màn hình đăng nhập
            tvErrorMessage.setText(R.string.err_wrong_login);
            tvErrorMessage.setVisibility(View.VISIBLE);
        }
    }
}
