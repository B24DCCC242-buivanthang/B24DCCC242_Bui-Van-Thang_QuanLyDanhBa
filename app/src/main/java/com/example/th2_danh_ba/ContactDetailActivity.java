package com.example.th2_danh_ba;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ContactDetailActivity extends AppCompatActivity {

    private View rootLayout;
    private View headerLayout;
    private ImageView btnHeaderBack;
    private FrameLayout layoutDetailAvatar;
    private TextView tvDetailAvatarLetter;
    private TextView tvDetailName;
    private TextView tvDetailPhone;
    private TextView tvDetailGroup;
    private TextView tvDetailGender;
    private TextView tvDetailNote;
    private CheckBox cbDetailFavorite;

    private Button btnCall;
    private Button btnBack;
    private Button btnDelete;

    private Contact contact;
    private int position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        initViews();
        handleWindowInsets();
        loadContactData();
        setupEvents();
    }

    private void initViews() {
        rootLayout = findViewById(R.id.rootLayout);
        headerLayout = findViewById(R.id.headerLayout);
        btnHeaderBack = findViewById(R.id.btnHeaderBack);
        layoutDetailAvatar = findViewById(R.id.layoutDetailAvatar);
        tvDetailAvatarLetter = findViewById(R.id.tvDetailAvatarLetter);
        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailPhone = findViewById(R.id.tvDetailPhone);
        tvDetailGroup = findViewById(R.id.tvDetailGroup);
        tvDetailGender = findViewById(R.id.tvDetailGender);
        tvDetailNote = findViewById(R.id.tvDetailNote);
        cbDetailFavorite = findViewById(R.id.cbDetailFavorite);

        btnCall = findViewById(R.id.btnCall);
        btnBack = findViewById(R.id.btnBack);
        btnDelete = findViewById(R.id.btnDelete);
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

    private void loadContactData() {
        Intent intent = getIntent();
        if (intent != null) {
            contact = (Contact) intent.getSerializableExtra(MainActivity.EXTRA_CONTACT);
            position = intent.getIntExtra(MainActivity.EXTRA_POSITION, -1);
        }

        if (contact != null) {
            tvDetailName.setText(contact.getName());
            tvDetailPhone.setText(contact.getPhone());
            tvDetailGroup.setText(contact.getGroup());
            tvDetailGender.setText(contact.getGender());

            if (contact.getNote() != null && !contact.getNote().trim().isEmpty()) {
                tvDetailNote.setText(contact.getNote());
            } else {
                tvDetailNote.setText("Chưa có ghi chú");
            }

            tvDetailAvatarLetter.setText(contact.getFirstLetter());

            // Thiết lập màu nền avatar
            GradientDrawable avatarBg = new GradientDrawable();
            avatarBg.setShape(GradientDrawable.OVAL);
            avatarBg.setColor(contact.getAvatarColor());
            layoutDetailAvatar.setBackground(avatarBg);

            cbDetailFavorite.setChecked(contact.isFavorite());
        }
    }

    private void setupEvents() {
        // Cập nhật trạng thái yêu thích khi tick CheckBox
        cbDetailFavorite.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (contact != null) {
                contact.setFavorite(isChecked);
            }
        });

        // 1. Nút Gọi điện - dùng Intent ngầm định (ACTION_DIAL)
        btnCall.setOnClickListener(v -> makePhoneCall());

        // 2. Nút Quay lại (trên Toolbar và nút Button)
        btnHeaderBack.setOnClickListener(v -> returnWithUpdatedData());
        btnBack.setOnClickListener(v -> returnWithUpdatedData());

        // 3. Nút Xóa liên hệ
        btnDelete.setOnClickListener(v -> confirmDeleteContact());
    }

    /**
     * Dùng Intent ngầm định ACTION_DIAL để mở ứng dụng gọi điện thoại
     */
    private void makePhoneCall() {
        if (contact == null || contact.getPhone() == null) {
            Toast.makeText(this, "Không tìm thấy số điện thoại!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        dialIntent.setData(Uri.parse("tel:" + contact.getPhone()));
        startActivity(dialIntent);
    }

    /**
     * Xác nhận trước khi xóa liên hệ
     */
    private void confirmDeleteContact() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete_title)
                .setMessage(R.string.confirm_delete_msg)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(MainActivity.EXTRA_ACTION, MainActivity.ACTION_DELETE);
                    resultIntent.putExtra(MainActivity.EXTRA_POSITION, position);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    /**
     * Quay lại MainActivity kèm dữ liệu đã cập nhật (ví dụ thay đổi trạng thái yêu thích)
     */
    private void returnWithUpdatedData() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(MainActivity.EXTRA_ACTION, MainActivity.ACTION_UPDATE);
        resultIntent.putExtra(MainActivity.EXTRA_CONTACT, contact);
        resultIntent.putExtra(MainActivity.EXTRA_POSITION, position);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        returnWithUpdatedData();
        super.onBackPressed();
    }
}
