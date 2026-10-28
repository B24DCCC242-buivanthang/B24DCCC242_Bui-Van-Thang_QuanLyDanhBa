package com.example.th2_danh_ba;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_CONTACT = "EXTRA_CONTACT";
    public static final String EXTRA_POSITION = "EXTRA_POSITION";
    public static final String EXTRA_ACTION = "EXTRA_ACTION";
    public static final String ACTION_DELETE = "DELETE";
    public static final String ACTION_UPDATE = "UPDATE";
    public static final String EXTRA_EXISTING_PHONES = "EXISTING_PHONES";

    private View rootLayout;
    private View headerLayout;
    private View bottomLayout;
    private ImageView btnLogout;

    private TextView tvTotalContacts;
    private TextView tvFavoriteContacts;
    private RecyclerView rvContacts;
    private Button btnAddContact;

    private ArrayList<Contact> contactList;
    private ContactAdapter adapter;
    private DatabaseHelper dbHelper;

    // Launcher mở màn hình Thêm liên hệ và nhận kết quả trả về
    private final ActivityResultLauncher<Intent> addContactLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Contact newContact = (Contact) result.getData().getSerializableExtra(EXTRA_CONTACT);
                    if (newContact != null) {
                        // Lưu liên hệ mới vào SQLite Database
                        long newId = dbHelper.insertContact(newContact);
                        newContact.setId((int) newId);

                        contactList.add(newContact);
                        adapter.notifyDataSetChanged();
                        updateStatistics();
                        Toast.makeText(this, "Thêm liên hệ thành công và đã lưu CSDL!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    // Launcher mở màn hình Chi tiết liên hệ và nhận kết quả trả về (cập nhật/xóa)
    private final ActivityResultLauncher<Intent> detailContactLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    String action = data.getStringExtra(EXTRA_ACTION);
                    int position = data.getIntExtra(EXTRA_POSITION, -1);

                    if (ACTION_DELETE.equals(action)) {
                        if (position >= 0 && position < contactList.size()) {
                            Contact contactToDelete = contactList.get(position);
                            // Xóa khỏi SQLite Database
                            dbHelper.deleteContact(contactToDelete);

                            contactList.remove(position);
                            adapter.notifyDataSetChanged();
                            updateStatistics();
                            Toast.makeText(this, "Đã xóa liên hệ khỏi CSDL!", Toast.LENGTH_SHORT).show();
                        }
                    } else if (ACTION_UPDATE.equals(action)) {
                        Contact updatedContact = (Contact) data.getSerializableExtra(EXTRA_CONTACT);
                        if (position >= 0 && position < contactList.size() && updatedContact != null) {
                            // Cập nhật thông tin vào SQLite Database
                            dbHelper.updateContact(updatedContact);

                            contactList.set(position, updatedContact);
                            adapter.notifyDataSetChanged();
                            updateStatistics();
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        initViews();
        handleWindowInsets();
        initContactList();
        setupRecyclerView();
        setupEvents();
        updateStatistics();
    }

    private void initViews() {
        rootLayout = findViewById(R.id.rootLayout);
        headerLayout = findViewById(R.id.headerLayout);
        bottomLayout = findViewById(R.id.bottomLayout);
        btnLogout = findViewById(R.id.btnLogout);
        tvTotalContacts = findViewById(R.id.tvTotalContacts);
        tvFavoriteContacts = findViewById(R.id.tvFavoriteContacts);
        rvContacts = findViewById(R.id.rvContacts);
        btnAddContact = findViewById(R.id.btnAddContact);
    }

    /**
     * Hạ phần Header xuống dưới Status Bar và đệm đáy tránh phím điều hướng hệ thống
     */
    private void handleWindowInsets() {
        int padTopDefault = (int) (12 * getResources().getDisplayMetrics().density);
        int padBottomDefault = (int) (16 * getResources().getDisplayMetrics().density);

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
            if (bottomLayout != null) {
                bottomLayout.setPadding(
                        bottomLayout.getPaddingLeft(),
                        bottomLayout.getPaddingTop(),
                        bottomLayout.getPaddingRight(),
                        insets.bottom + padBottomDefault
                );
            }
            return windowInsets;
        });
    }

    /**
     * Tải danh sách liên hệ từ SQLite Database (đã có sẵn dữ liệu mẫu hoặc dữ liệu đã sửa trước đó)
     */
    private void initContactList() {
        contactList = dbHelper.getAllContacts();
    }

    private void setupRecyclerView() {
        adapter = new ContactAdapter(contactList, (contact, position) -> {
            // Mở màn hình Chi tiết liên hệ
            Intent intent = new Intent(MainActivity.this, ContactDetailActivity.class);
            intent.putExtra(EXTRA_CONTACT, contact);
            intent.putExtra(EXTRA_POSITION, position);
            detailContactLauncher.launch(intent);
        });
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        rvContacts.setAdapter(adapter);
    }

    private void setupEvents() {
        btnAddContact.setOnClickListener(v -> {
            // Lấy danh sách số điện thoại hiện có để kiểm tra trùng lặp
            ArrayList<String> existingPhones = new ArrayList<>();
            for (Contact c : contactList) {
                existingPhones.add(c.getPhone());
            }

            Intent intent = new Intent(MainActivity.this, AddContactActivity.class);
            intent.putStringArrayListExtra(EXTRA_EXISTING_PHONES, existingPhones);
            addContactLauncher.launch(intent);
        });

        // Nút Đăng xuất: cho phép đăng xuất để kiểm tra dữ liệu sau khi đăng nhập lại
        btnLogout.setOnClickListener(v -> confirmLogout());
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất không?\nMọi thông tin liên hệ đã chỉnh sửa đều đã được lưu vào cơ sở dữ liệu.")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    /**
     * Cập nhật khối thống kê: Tổng liên hệ và Số liên hệ yêu thích
     */
    private void updateStatistics() {
        int total = contactList.size();
        int favCount = 0;
        for (Contact contact : contactList) {
            if (contact.isFavorite()) {
                favCount++;
            }
        }
        tvTotalContacts.setText(String.valueOf(total));
        tvFavoriteContacts.setText(String.valueOf(favCount));
    }
}
