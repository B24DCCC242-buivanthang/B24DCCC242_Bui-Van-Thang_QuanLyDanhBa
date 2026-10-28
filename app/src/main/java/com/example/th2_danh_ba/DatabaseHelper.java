package com.example.th2_danh_ba;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "contacts_manager.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_CONTACTS = "contacts";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_GROUP = "contact_group";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_FAVORITE = "is_favorite";
    public static final String COLUMN_NOTE = "note";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng lưu trữ danh bạ
        String createTableQuery = "CREATE TABLE " + TABLE_CONTACTS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_PHONE + " TEXT NOT NULL UNIQUE, "
                + COLUMN_GROUP + " TEXT NOT NULL, "
                + COLUMN_GENDER + " TEXT NOT NULL, "
                + COLUMN_FAVORITE + " INTEGER DEFAULT 0, "
                + COLUMN_NOTE + " TEXT"
                + ");";
        db.execSQL(createTableQuery);

        // Chèn sẵn 3 liên hệ mẫu ban đầu theo đúng đề bài
        insertSampleContacts(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    /**
     * Chèn 3 liên hệ mẫu ban đầu khi cơ sở dữ liệu được tạo lần đầu
     */
    private void insertSampleContacts(SQLiteDatabase db) {
        insertSample(db, "Nguyễn Văn An", "0901234567", "Bạn bè", "Nam", 0, "");
        insertSample(db, "Trần Thị Bình", "0912345678", "Gia đình", "Nữ", 1, "Chị gái, hay gọi buổi tối");
        insertSample(db, "Lê Minh Châu", "0987654321", "Công việc", "Khác", 0, "");
    }

    private void insertSample(SQLiteDatabase db, String name, String phone, String group, String gender, int isFav, String note) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_GROUP, group);
        values.put(COLUMN_GENDER, gender);
        values.put(COLUMN_FAVORITE, isFav);
        values.put(COLUMN_NOTE, note);
        db.insert(TABLE_CONTACTS, null, values);
    }

    /**
     * Lấy toàn bộ danh sách liên hệ từ cơ sở dữ liệu SQLite
     */
    public ArrayList<Contact> getAllContacts() {
        ArrayList<Contact> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_CONTACTS,
                null,
                null,
                null,
                null,
                null,
                COLUMN_ID + " ASC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE));
                String group = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GROUP));
                String gender = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER));
                int isFav = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FAVORITE));
                String note = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE));

                Contact contact = new Contact(id, name, phone, group, gender, isFav == 1, note);
                list.add(contact);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return list;
    }

    /**
     * Thêm một liên hệ mới vào SQLite
     */
    public long insertContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, contact.getName());
        values.put(COLUMN_PHONE, contact.getPhone());
        values.put(COLUMN_GROUP, contact.getGroup());
        values.put(COLUMN_GENDER, contact.getGender());
        values.put(COLUMN_FAVORITE, contact.isFavorite() ? 1 : 0);
        values.put(COLUMN_NOTE, contact.getNote());

        return db.insert(TABLE_CONTACTS, null, values);
    }

    /**
     * Cập nhật thông tin liên hệ (bao gồm cả trạng thái yêu thích)
     */
    public int updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, contact.getName());
        values.put(COLUMN_PHONE, contact.getPhone());
        values.put(COLUMN_GROUP, contact.getGroup());
        values.put(COLUMN_GENDER, contact.getGender());
        values.put(COLUMN_FAVORITE, contact.isFavorite() ? 1 : 0);
        values.put(COLUMN_NOTE, contact.getNote());

        if (contact.getId() > 0) {
            return db.update(TABLE_CONTACTS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(contact.getId())});
        } else {
            return db.update(TABLE_CONTACTS, values, COLUMN_PHONE + " = ?", new String[]{contact.getPhone()});
        }
    }

    /**
     * Xóa liên hệ theo id hoặc số điện thoại
     */
    public int deleteContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (contact.getId() > 0) {
            return db.delete(TABLE_CONTACTS, COLUMN_ID + " = ?", new String[]{String.valueOf(contact.getId())});
        } else {
            return db.delete(TABLE_CONTACTS, COLUMN_PHONE + " = ?", new String[]{contact.getPhone()});
        }
    }

    /**
     * Kiểm tra số điện thoại đã tồn tại trong cơ sở dữ liệu chưa
     */
    public boolean isPhoneExists(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_CONTACTS,
                new String[]{COLUMN_ID},
                COLUMN_PHONE + " = ?",
                new String[]{phone},
                null,
                null,
                null
        );

        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) {
            cursor.close();
        }
        return exists;
    }
}
