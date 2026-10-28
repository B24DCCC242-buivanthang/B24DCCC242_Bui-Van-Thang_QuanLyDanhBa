package com.example.th2_danh_ba;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class AddContactActivity extends AppCompatActivity {

    private View rootLayout;
    private View headerLayout;
    private ImageView btnHeaderBack;
    private EditText etName;
    private EditText etPhone;
    private Spinner spGroup;
    private RadioGroup rgGender;
    private RadioButton rbMale;
    private RadioButton rbFemale;
    private RadioButton rbOther;
    private CheckBox cbFavorite;
    private EditText etNote;
    private Button btnSaveContact;

    private ArrayList<String> existingPhones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        initViews();
        handleWindowInsets();
        setupSpinner();
        loadExistingPhones();
        setupEvents();
    }

    private void initViews() {
        rootLayout = findViewById(R.id.rootLayout);
        headerLayout = findViewById(R.id.headerLayout);
        btnHeaderBack = findViewById(R.id.btnHeaderBack);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        spGroup = findViewById(R.id.spGroup);
        rgGender = findViewById(R.id.rgGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        rbOther = findViewById(R.id.rbOther);
        cbFavorite = findViewById(R.id.cbFavorite);
        etNote = findViewById(R.id.etNote);
        btnSaveContact = findViewById(R.id.btnSaveContact);
    }

    /**
     * Hạ Toolbar xuống dưới Status Bar để nút Back và tiêu đề không bị vướng trần
     */
    private void handleWindowInsets() {
        int padTopDefault = (int) (8 * getResources().getDisplayMetrics().density);

        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            if (headerLayout != null) {
                headerLayout.setPadding(
                        headerLayout.getPaddingLeft(),
                        insets.top + padTopDefault,
                        headerLayout.getPaddingRight(),
                        padTopDefault
                );
            }
            v.setPadding(0, 0, 0, insets.bottom);
            return windowInsets;
        });
    }

    private void setupSpinner() {
        String[] groups = new String[]{"Bạn bè", "Gia đình", "Công việc", "Khác"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                groups
        );
        spGroup.setAdapter(adapter);
    }

    private void loadExistingPhones() {
        existingPhones = getIntent().getStringArrayListExtra(MainActivity.EXTRA_EXISTING_PHONES);
        if (existingPhones == null) {
            existingPhones = new ArrayList<>();
        }
    }

    private void setupEvents() {
        btnHeaderBack.setOnClickListener(v -> finish());
        btnSaveContact.setOnClickListener(v -> handleSaveContact());
    }

    private void handleSaveContact() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        // 1. Kiểm tra họ và tên không được để trống
        if (TextUtils.isEmpty(name)) {
            etName.setError(getString(R.string.err_empty_name));
            etName.requestFocus();
            Toast.makeText(this, R.string.err_empty_name, Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Kiểm tra số điện thoại: gồm 10 chữ số và bắt đầu bằng 0
        if (TextUtils.isEmpty(phone) || !phone.matches("^0\\d{9}$")) {
            etPhone.setError(getString(R.string.err_invalid_phone));
            etPhone.requestFocus();
            Toast.makeText(this, R.string.err_invalid_phone, Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. Kiểm tra số điện thoại đã tồn tại trong danh bạ chưa
        if (existingPhones.contains(phone)) {
            etPhone.setError(getString(R.string.err_phone_exists));
            etPhone.requestFocus();
            Toast.makeText(this, R.string.err_phone_exists, Toast.LENGTH_SHORT).show();
            return;
        }

        // 4. Lấy các thông tin còn lại
        String group = spGroup.getSelectedItem().toString();

        String gender = "Nam";
        if (rbFemale.isChecked()) {
            gender = "Nữ";
        } else if (rbOther.isChecked()) {
            gender = "Khác";
        }

        boolean isFavorite = cbFavorite.isChecked();
        String note = etNote.getText().toString().trim();

        // Tạo đối tượng Contact mới
        Contact newContact = new Contact(name, phone, group, gender, isFavorite, note);

        // Trả kết quả về MainActivity
        Intent resultIntent = new Intent();
        resultIntent.putExtra(MainActivity.EXTRA_CONTACT, newContact);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
