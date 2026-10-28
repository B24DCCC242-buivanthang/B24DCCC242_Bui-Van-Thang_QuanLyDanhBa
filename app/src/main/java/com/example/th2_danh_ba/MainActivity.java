package com.example.th2_danh_ba;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
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

    private TextView tvTotalContacts;
    private TextView tvFavoriteContacts;
    private RecyclerView rvContacts;
    private Button btnAddContact;

    private ArrayList<Contact> contactList;
    private ContactAdapter adapter;

    // Launcher mở màn hình Thêm liên hệ và nhận kết quả trả về
    private final ActivityResultLauncher<Intent> addContactLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Contact newContact = (Contact) result.getData().getSerializableExtra(EXTRA_CONTACT);
                    if (newContact != null) {
                        contactList.add(newContact);
                        adapter.notifyDataSetChanged();
                        updateStatistics();
                        Toast.makeText(this, "Thêm liên hệ thành công!", Toast.LENGTH_SHORT).show();
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
                            contactList.remove(position);
                            adapter.notifyDataSetChanged();
                            updateStatistics();
                            Toast.makeText(this, "Đã xóa liên hệ!", Toast.LENGTH_SHORT).show();
                        }
                    } else if (ACTION_UPDATE.equals(action)) {
                        Contact updatedContact = (Contact) data.getSerializableExtra(EXTRA_CONTACT);
                        if (position >= 0 && position < contactList.size() && updatedContact != null) {
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

        initViews();
        initSampleData();
        setupRecyclerView();
        setupEvents();
        updateStatistics();
    }

    private void initViews() {
        tvTotalContacts = findViewById(R.id.tvTotalContacts);
        tvFavoriteContacts = findViewById(R.id.tvFavoriteContacts);
        rvContacts = findViewById(R.id.rvContacts);
        btnAddContact = findViewById(R.id.btnAddContact);
    }

    /**
     * Khởi tạo 3 liên hệ mẫu theo đúng yêu cầu đề bài
     */
    private void initSampleData() {
        contactList = new ArrayList<>();
        contactList.add(new Contact(
                "Nguyễn Văn An",
                "0901234567",
                "Bạn bè",
                "Nam",
                false,
                ""
        ));
        contactList.add(new Contact(
                "Trần Thị Bình",
                "0912345678",
                "Gia đình",
                "Nữ",
                true,
                "Chị gái, hay gọi buổi tối"
        ));
        contactList.add(new Contact(
                "Lê Minh Châu",
                "0987654321",
                "Công việc",
                "Khác",
                false,
                ""
        ));
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
