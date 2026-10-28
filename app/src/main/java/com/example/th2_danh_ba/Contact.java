package com.example.th2_danh_ba;

import java.io.Serializable;

public class Contact implements Serializable {
    private String name;
    private String phone;
    private String group;
    private String gender;
    private boolean isFavorite;
    private String note;

    public Contact() {
    }

    public Contact(String name, String phone, String group, String gender, boolean isFavorite, String note) {
        this.name = name;
        this.phone = phone;
        this.group = group;
        this.gender = gender;
        this.isFavorite = isFavorite;
        this.note = note;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    /**
     * Lấy chữ cái đầu của Tên (từ cuối cùng trong họ tên)
     * Ví dụ: "Nguyễn Văn An" -> "A", "Trần Thị Bình" -> "B", "Lê Minh Châu" -> "C"
     */
    public String getFirstLetter() {
        if (name == null || name.trim().isEmpty()) {
            return "?";
        }
        String trimmed = name.trim();
        String[] parts = trimmed.split("\\s+");
        String lastWord = parts[parts.length - 1];
        if (!lastWord.isEmpty()) {
            return lastWord.substring(0, 1).toUpperCase();
        }
        return "?";
    }

    /**
     * Sinh màu nền avatar đại diện dựa trên chữ cái đầu của tên
     */
    public int getAvatarColor() {
        String letter = getFirstLetter();
        char c = letter.charAt(0);
        int code = (int) c;
        int[] colors = new int[] {
                0xFF00897B, // Teal
                0xFFD84315, // Cam đỏ
                0xFF5E35B1, // Tím
                0xFF1976D2, // Xanh dương
                0xFF388E3C, // Xanh lá
                0xFFC2185B  // Hồng đậm
        };
        return colors[Math.abs(code) % colors.length];
    }
}
