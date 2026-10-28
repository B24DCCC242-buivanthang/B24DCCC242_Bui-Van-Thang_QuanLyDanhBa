# BÁO CÁO BÀI TẬP THỰC HÀNH 2: ỨNG DỤNG QUẢN LÝ DANH BẠ LIÊN HỆ

- **Họ và tên:** [Điền họ và tên của bạn]
- **Mã sinh viên (MSSV):** [Điền MSSV]
- **Lớp:** [Điền Lớp]
- **Link Repository Git:** `https://github.com/[Username]/[MSSV_HoTen_QuanLyDanhBa]`

---

## 1. TỔNG QUAN DỰ ÁN

Ứng dụng **Quản lý danh bạ liên hệ** được xây dựng trên nền tảng **Android (Java & XML)**, bao gồm 4 Activity chính:
1. **LoginActivity**: Màn hình đăng nhập tài khoản cố định (`admin` / `123456`).
2. **MainActivity**: Màn hình danh sách liên hệ, khối thống kê tổng số liên hệ và số liên hệ yêu thích, cùng nút thêm liên hệ.
3. **AddContactActivity**: Màn hình thêm liên hệ mới với các trường: Họ tên, Số điện thoại (10 số, bắt đầu bằng 0, không trùng), Nhóm, Giới tính, Yêu thích và Ghi chú.
4. **ContactDetailActivity**: Màn hình xem chi tiết liên hệ, cập nhật trạng thái yêu thích, thực hiện cuộc gọi qua Intent ngầm định (`ACTION_DIAL`), và xóa liên hệ.

Dữ liệu được quản lý trong bộ nhớ thông qua `ArrayList<Contact>` và truyền tải giữa các Activity thông qua `Intent` (kết hợp `ActivityResultLauncher` và `Serializable`).

---

## 2. NỘI DUNG THỰC HIỆN VÀ MÔ TẢ CHI TIẾT CÁC MÀN HÌNH

### 2.1. Màn hình 1: Đăng nhập (`LoginActivity`)

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
  - Tài khoản cố định được khai báo dưới dạng hằng số:
    ```java
    public static final String DEFAULT_USERNAME = "admin";
    public static final String DEFAULT_PASSWORD = "123456";
    ```
  - Khi người dùng nhấn nút Đăng nhập:
    - Kiểm tra bỏ trống: Nếu tên đăng nhập hoặc mật khẩu để trống, hiển thị thông báo: *"Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu"*.
    - Kiểm tra thông tin:
      - Nếu đúng tài khoản (`admin` / `123456`): Tạo `Intent` chuyển sang `MainActivity` và gọi `finish()` để người dùng không quay lại màn hình Login khi nhấn nút Back.
      - Nếu sai: Hiển thị thông báo *"Sai tên đăng nhập hoặc mật khẩu"* màu đỏ trên màn hình.

---

### 2.2. Màn hình 2: Danh sách liên hệ (`MainActivity`)

#### Mô tả chi tiết:
- **Giao diện (`activity_main.xml` & `item_contact.xml`)**:
  - **Header**: Thanh tiêu đề màu xanh teal với dòng chữ "Danh bạ của tôi".
  - **Khối thống kê**: Card bo góc màu xanh teal đậm hiển thị 2 cột số liệu:
    - `tvTotalContacts`: Tổng số liên hệ.
    - `tvFavoriteContacts`: Số liên hệ được đánh dấu yêu thích.
  - **Danh sách (`rvContacts`)**: Dùng `RecyclerView` kết hợp `ContactAdapter`. Mỗi dòng liên hệ gồm:
    - Avatar tròn với chữ cái đầu của Tên (được tạo động theo hàm `getFirstLetter()`), nền màu sắc sinh động.
    - Họ và tên, số điện thoại.
    - Badge nhóm bo góc màu xanh nhạt.
    - Biểu tượng ngôi sao vàng (`ivFavoriteStar`) nếu là liên hệ yêu thích.
  - Dòng hướng dẫn: `(nhấn vào một liên hệ để xem chi tiết)`.
  - **Nút Thêm liên hệ (`btnAddContact`)**: Cố định ở dưới cùng để mở màn hình thêm liên hệ.
- **Dữ liệu mẫu ban đầu**:
  - Khởi tạo 3 liên hệ mẫu trong `initSampleData()`:
    1. Nguyễn Văn An | 0901234567 | Bạn bè | Nam | Không yêu thích | ""
    2. Trần Thị Bình | 0912345678 | Gia đình | Nữ | Yêu thích | "Chị gái, hay gọi buổi tối"
    3. Lê Minh Châu | 0987654321 | Công việc | Khác | Không yêu thích | ""
- **Xử lý sự kiện**:
  - Hàm `updateStatistics()`: Tự động đếm `contactList.size()` và số liên hệ có `isFavorite() == true` để cập nhật lên giao diện.
  - Nhấn nút "Thêm liên hệ": Truyền danh sách các số điện thoại hiện có (`EXISTING_PHONES`) qua `AddContactActivity` bằng `addContactLauncher`.
  - Nhấn vào một liên hệ: Mở `ContactDetailActivity`, truyền đối tượng `Contact` và vị trí `position` bằng `detailContactLauncher`.
  - Khi nhận kết quả trả về: Cập nhật danh sách, thông báo cho adapter (`notifyDataSetChanged()`) và cập nhật lại thống kê.

---

### 2.3. Màn hình 3: Thêm liên hệ (`AddContactActivity`)

#### Mô tả chi tiết:
- **Giao diện (`activity_add_contact.xml`)**:
  - Thanh tiêu đề có nút mũi tên quay lại (`btnHeaderBack`) và tiêu đề "Thêm liên hệ".
  - `etName`: Họ và tên (bắt buộc nhập).
  - `etPhone`: Số điện thoại (`inputType="phone"`, giới hạn 10 số).
  - `spGroup`: Nhóm (Spinner gồm: Bạn bè, Gia đình, Công việc, Khác).
  - `rgGender`: Giới tính (RadioGroup ngang gồm: Nam [mặc định], Nữ, Khác).
  - `cbFavorite`: CheckBox "Thêm vào yêu thích".
  - `etNote`: Ghi chú nhiều dòng (`inputType="textMultiLine"`).
  - `btnSaveContact`: Nút "Lưu liên hệ".
- **Xử lý kiểm tra dữ liệu (Validation)**:
  - Kiểm tra họ tên: Báo lỗi bằng `setError` và Toast nếu để trống.
  - Kiểm tra định dạng số điện thoại: Sử dụng regex `^0\\d{9}$` để đảm bảo số điện thoại có đúng 10 chữ số và bắt đầu bằng số `0`.
  - Kiểm tra trùng số điện thoại: So sánh với danh sách `existingPhones` đã được truyền từ `MainActivity`. Nếu đã tồn tại, hiển thị thông báo lỗi và không cho lưu.
  - Khi dữ liệu hợp lệ: Đóng gói đối tượng `Contact` mới vào `Intent`, gọi `setResult(RESULT_OK, resultIntent)` và `finish()`.

---

### 2.4. Màn hình 4: Chi tiết liên hệ (`ContactDetailActivity`)

#### Mô tả chi tiết:
- **Giao diện (`activity_contact_detail.xml`)**:
  - Thanh tiêu đề với nút quay lại và tiêu đề "Chi tiết liên hệ".
  - Card chi tiết nền trắng bo góc:
    - Avatar tròn lớn với chữ cái đầu viết hoa.
    - Họ và tên, số điện thoại hiển thị màu xanh teal nổi bật.
    - Đường gạch ngang phân cách.
    - Hiển thị 2 cột: Nhóm và Giới tính.
    - Hiển thị Ghi chú.
    - `CheckBox` Yêu thích: Cho phép người dùng đánh dấu/hủy yêu thích trực tiếp tại đây.
  - **3 Nút chức năng**:
    1. **Nút Gọi điện (`btnCall`)**: Sử dụng Intent ngầm định:
       ```java
       Intent dialIntent = new Intent(Intent.ACTION_DIAL);
       dialIntent.setData(Uri.parse("tel:" + contact.getPhone()));
       startActivity(dialIntent);
       ```
       Mở ngay màn hình quay số điện thoại của hệ điều hành với số điện thoại đã được điền sẵn.
    2. **Nút Quay lại (`btnBack`)**: Đóng màn hình, trả về đối tượng `Contact` với trạng thái yêu thích mới để `MainActivity` đồng bộ và cập nhật thống kê.
    3. **Nút Xóa liên hệ (`btnDelete`)**: Hiển thị `AlertDialog` xác nhận trước khi xóa; nếu xác nhận, trả về hành động `ACTION_DELETE` kèm vị trí `position` để `MainActivity` xóa khỏi `ArrayList`.

---

## 3. HƯỚNG DẪN CHỤP ẢNH MINH HỌA VÀ ĐÓNG GÓI BÁO CÁO

Khi làm bài báo cáo Word/PDF (`MSSV_HoTen_BaoCao.docx`), bạn hãy chụp các ảnh màn hình ứng dụng đang chạy trên Emulator hoặc máy thật:
1. **Ảnh 1**: Màn hình Đăng nhập (chụp cả trường hợp nhập sai/bỏ trống báo lỗi đỏ và trường hợp sẵn sàng đăng nhập).
2. **Ảnh 2**: Màn hình Danh sách liên hệ ban đầu (với 3 liên hệ mẫu và khối thống kê 3 - 1).
3. **Ảnh 3**: Màn hình Thêm liên hệ (chụp trường hợp báo lỗi số điện thoại không hợp lệ/trùng lặp và trường hợp thêm thành công).
4. **Ảnh 4**: Màn hình Chi tiết liên hệ (chụp giao diện xem chi tiết và màn hình quay số điện thoại khi bấm nút "Gọi điện").
5. **Ảnh 5**: Màn hình sau khi xóa hoặc đổi trạng thái yêu thích để thấy khối thống kê được cập nhật tức thì.
