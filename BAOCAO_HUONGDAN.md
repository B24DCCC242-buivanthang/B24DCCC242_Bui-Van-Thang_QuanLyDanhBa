# BÁO CÁO BÀI TẬP THỰC HÀNH 2: ỨNG DỤNG QUẢN LÝ DANH BẠ LIÊN HỆ

- **Họ và tên:** [Điền họ và tên của bạn]
- **Mã sinh viên (MSSV):** [Điền MSSV]
- **Lớp:** [Điền Lớp]
- **Link Repository Git:** `https://github.com/[Username]/[MSSV_HoTen_QuanLyDanhBa]`

---

## 1. TỔNG QUAN DỰ ÁN

Ứng dụng **Quản lý danh bạ liên hệ** được xây dựng trên nền tảng **Android (Java & XML)**, bao gồm 4 Activity chính kết hợp cơ sở dữ liệu **SQLite** để lưu trữ bền vững:
1. **LoginActivity**: Màn hình đăng nhập tài khoản cố định (`admin` / `123456`).
2. **MainActivity**: Màn hình danh sách liên hệ, khối thống kê tổng số liên hệ và số liên hệ yêu thích, nút thêm liên hệ, và nút Đăng xuất.
3. **AddContactActivity**: Màn hình thêm liên hệ mới với các trường: Họ tên, Số điện thoại (10 số, bắt đầu bằng 0, không trùng), Nhóm, Giới tính, Yêu thích và Ghi chú.
4. **ContactDetailActivity**: Màn hình xem chi tiết liên hệ, cập nhật trạng thái yêu thích, thực hiện cuộc gọi qua Intent ngầm định (`ACTION_DIAL`), và xóa liên hệ.

Dữ liệu được lưu trữ lâu dài trong cơ sở dữ liệu **SQLite** (`contacts_manager.db`) thông qua lớp `DatabaseHelper`. Mỗi khi người dùng đăng nhập lại hoặc mở lại ứng dụng, toàn bộ các liên hệ đã thêm, sửa hoặc xóa đều được lưu giữ nguyên vẹn.

---

## 2. KIẾN TRÚC CƠ SỞ DỮ LIỆU SQLITE (`DatabaseHelper`)

- **Tên cơ sở dữ liệu**: `contacts_manager.db`
- **Bảng dữ liệu**: `contacts`
  - `id`: INTEGER PRIMARY KEY AUTOINCREMENT
  - `name`: TEXT NOT NULL (Họ và tên)
  - `phone`: TEXT NOT NULL UNIQUE (Số điện thoại)
  - `contact_group`: TEXT NOT NULL (Nhóm liên hệ)
  - `gender`: TEXT NOT NULL (Giới tính)
  - `is_favorite`: INTEGER DEFAULT 0 (1: Có yêu thích, 0: Không)
  - `note`: TEXT (Ghi chú)

- **Các thao tác CRUD tiêu biểu trong `DatabaseHelper.java`**:
  - `onCreate()`: Tự động khởi tạo bảng và chèn sẵn 3 liên hệ mẫu ban đầu (Nguyễn Văn An, Trần Thị Bình, Lê Minh Châu).
  - `getAllContacts()`: Lấy toàn bộ danh sách liên hệ đã lưu từ SQLite nạp vào `ArrayList<Contact>`.
  - `insertContact(Contact)`: Chèn liên hệ mới vào bảng.
  - `updateContact(Contact)`: Cập nhật thông tin và trạng thái yêu thích vào SQLite.
  - `deleteContact(Contact)`: Xóa liên hệ khỏi cơ sở dữ liệu.
  - `isPhoneExists(phone)`: Kiểm tra trùng lặp số điện thoại.

---

## 3. NỘI DUNG THỰC HIỆN VÀ MÔ TẢ CHI TIẾT CÁC MÀN HÌNH

### 3.1. Màn hình 1: Đăng nhập (`LoginActivity`)

#### Mô tả chi tiết:
- **Giao diện (`activity_login.xml`)**:
  - `ImageView`: Logo biểu tượng điện thoại tròn (`ic_phone_logo`).
  - `TextView`: Tiêu đề "Danh bạ" và phụ đề "Đăng nhập để tiếp tục".
  - `EditText` (`etUsername`): Nhập tên đăng nhập.
  - `EditText` (`etPassword`): Nhập mật khẩu (`inputType="textPassword"`).
  - `TextView` (`tvErrorMessage`): Hiển thị thông báo lỗi bằng chữ màu đỏ khi để trống hoặc sai tài khoản.
  - `Button` (`btnLogin`): Nút "Đăng nhập" bo tròn viền xanh teal.
  - `TextView`: Ghi chú tài khoản mẫu "Tài khoản mẫu: admin / 123456".
- **Xử lý mã nguồn (`LoginActivity.java`)**:
  - Xử lý Window Insets hạ layout an toàn dưới thanh Status Bar.
  - Tài khoản cố định: `admin` / `123456`.
  - Kiểm tra bỏ trống: Nếu để trống, báo lỗi *"Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu"*.
  - Kiểm tra thông tin:
    - Nếu đúng: Tạo `Intent` chuyển sang `MainActivity` và gọi `finish()`.
    - Nếu sai: Hiển thị thông báo *"Sai tên đăng nhập hoặc mật khẩu"* màu đỏ.

---

### 3.2. Màn hình 2: Danh sách liên hệ (`MainActivity`)

#### Mô tả chi tiết:
- **Giao diện (`activity_main.xml` & `item_contact.xml`)**:
  - **Header**: Thanh tiêu đề màu xanh teal có dòng chữ "Danh bạ của tôi" và icon **Đăng xuất** (`btnLogout`).
  - **Khối thống kê**: Card bo góc màu xanh teal đậm hiển thị 2 cột: Tổng số liên hệ và Số liên hệ yêu thích.
  - **Danh sách (`rvContacts`)**: Dùng `RecyclerView` kết hợp `ContactAdapter`. Mỗi dòng liên hệ gồm:
    - Avatar tròn với chữ cái đầu của Tên (tạo động theo hàm `getFirstLetter()`), nền màu sắc sinh động.
    - Họ và tên, số điện thoại.
    - Badge nhóm bo góc màu xanh nhạt.
    - Biểu tượng ngôi sao vàng (`ivFavoriteStar`) nếu là liên hệ yêu thích.
  - **Nút Thêm liên hệ (`btnAddContact`)**: Cố định ở dưới cùng để mở màn hình thêm liên hệ.
- **Tích hợp Cơ sở dữ liệu SQLite**:
  - Khởi tạo `DatabaseHelper dbHelper = new DatabaseHelper(this)`.
  - Tải dữ liệu từ database: `contactList = dbHelper.getAllContacts();`.
  - Khi thêm mới: Gọi `dbHelper.insertContact(newContact)` -> dữ liệu lưu ngay vào SQLite.
  - Khi xóa: Gọi `dbHelper.deleteContact(contactToDelete)` -> xóa khỏi SQLite.
  - Khi cập nhật yêu thích: Gọi `dbHelper.updateContact(updatedContact)` -> cập nhật trong SQLite.
  - Khối thống kê tự động cập nhật chính xác theo số liệu thực tế trong database.

---

### 3.3. Màn hình 3: Thêm liên hệ (`AddContactActivity`)

#### Mô tả chi tiết:
- **Giao diện (`activity_add_contact.xml`)**:
  - Thanh tiêu đề có nút mũi tên quay lại (`btnHeaderBack`) và tiêu đề "Thêm liên hệ" đã được xử lý Window Insets hạ dưới Status Bar.
  - `etName`: Họ và tên (bắt buộc nhập).
  - `etPhone`: Số điện thoại (`inputType="phone"`, 10 chữ số).
  - `spGroup`: Nhóm (Spinner gồm: Bạn bè, Gia đình, Công việc, Khác).
  - `rgGender`: Giới tính (RadioGroup ngang gồm: Nam [mặc định], Nữ, Khác).
  - `cbFavorite`: CheckBox "Thêm vào yêu thích".
  - `etNote`: Ghi chú nhiều dòng (`inputType="textMultiLine"`).
  - `btnSaveContact`: Nút "Lưu liên hệ".
- **Validation**:
  - Họ tên không được để trống.
  - Số điện thoại phải đúng 10 số và bắt đầu bằng số `0` (regex `^0\\d{9}$`).
  - Kiểm tra không trùng lặp số điện thoại đã có trong danh bạ.

---

### 3.4. Màn hình 4: Chi tiết liên hệ (`ContactDetailActivity`)

#### Mô tả chi tiết:
- **Giao diện (`activity_contact_detail.xml`)**:
  - Avatar tròn lớn chữ cái đầu, Họ và tên, Số điện thoại xanh teal.
  - Nhóm, Giới tính, Ghi chú.
  - `CheckBox` Yêu thích: Cho phép đổi trạng thái yêu thích ngay tại đây.
  - **Nút Gọi điện (`btnCall`)**: Dùng Intent ngầm định (`ACTION_DIAL`) với URI `tel:...` mở ứng dụng quay số.
  - **Nút Quay lại (`btnBack`)**: Đóng màn hình, trả về dữ liệu cập nhật trạng thái yêu thích về MainActivity để lưu vào SQLite.
  - **Nút Xóa liên hệ (`btnDelete`)**: Hộp thoại xác nhận `AlertDialog`; khi xóa sẽ gửi lệnh về MainActivity để xóa vĩnh viễn khỏi SQLite Database.

---

## 4. HƯỚNG DẪN CHỤP ẢNH MINH HỌA VÀ ĐÓNG GÓI BÁO CÁO

Khi làm bài báo cáo Word/PDF (`MSSV_HoTen_BaoCao.docx`), bạn hãy chụp các ảnh màn hình ứng dụng đang chạy:
1. **Ảnh 1**: Màn hình Đăng nhập (trường hợp báo lỗi sai/trống và trường hợp đăng nhập thành công).
2. **Ảnh 2**: Màn hình Danh sách liên hệ (với khối thống kê và danh sách từ SQLite).
3. **Ảnh 3**: Màn hình Thêm liên hệ (báo lỗi validation và lưu thành công).
4. **Ảnh 4**: Màn hình Chi tiết liên hệ và màn hình quay số điện thoại khi bấm nút "Gọi điện".
5. **Ảnh 5**: Thử nghiệm tính bền vững dữ liệu: Đăng xuất -> Đăng nhập lại -> Mọi dữ liệu đã thêm/sửa/xóa vẫn còn nguyên vẹn trong SQLite.
