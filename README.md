# 🏦 P2P Lending Platform (Nền tảng Cho vay Ngang hàng)

> **Hệ thống kết nối tài chính vi mô an toàn, minh bạch và tự động hóa.**

[![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-green?style=flat-square&logo=spring)](https://spring.io/projects/spring-boot)
[![Security](https://img.shields.io/badge/Spring_Security-6-blue?style=flat-square&logo=springsecurity)](https://spring.io/projects/spring-security)
[![Database](https://img.shields.io/badge/MySQL-8.0-00618a?style=flat-square&logo=mysql)](https://www.mysql.com/)
[![Frontend](https://img.shields.io/badge/Thymeleaf-Bootstrap_5-purple?style=flat-square)](https://www.thymeleaf.org/)

## 📖 Giới thiệu (Overview)

**P2P Lending Platform** là một ứng dụng Fintech mô phỏng mô hình kinh tế chia sẻ trong tài chính, kết nối trực tiếp **Người vay (Borrower)** và **Nhà đầu tư (Lender)** mà không qua trung gian ngân hàng truyền thống.

Dự án được xây dựng nhằm giải quyết các bài toán cốt lõi của một hệ thống tài chính:
1.  **Đánh giá tín dụng (Credit Scoring):** Tự động hóa việc phân loại rủi ro người vay.
2.  **Quản lý dòng tiền (Cash Flow):** Đảm bảo tính toàn vẹn của giao dịch tiền tệ.
3.  **Bảo mật đa lớp (Multi-layer Security):** Kết hợp linh hoạt giữa Stateful và Stateless.

---

## 🛠️ Kiến trúc Kỹ thuật & Công nghệ (Tech Stack & Architecture)

Dự án áp dụng kiến trúc **Layered Architecture** chặt chẽ, đảm bảo Separation of Concerns (SoC).

### Backend (Core)
* **Framework:** Spring Boot 3.x (Java 17).
* **ORM:** Spring Data JPA & Hibernate.
* **Security:** Spring Security 6 (Cấu hình nâng cao).
* **Database:** MySQL 8.0.
* **Build Tool:** Maven.
* **Utilities:** Lombok, JJWT (Java JWT).

### Frontend (Hybrid View)
* **Server-side Rendering:** Thymeleaf (Tối ưu cho SEO và Admin Dashboard).
* **Styling:** Bootstrap 5.
* **Integration:** Tích hợp chặt chẽ với Spring Security Session.

### Điểm nhấn Kiến trúc (Technical Highlights)
Đây là những kỹ thuật chuyên sâu được áp dụng trong dự án:

1.  **Hybrid Security Architecture :**
    * **Luồng Web (Stateful):** Sử dụng `Session` & `Cookies` cho giao diện quản trị và người dùng trên trình duyệt (Thymeleaf), giúp trải nghiệm mượt mà (không cần lưu token thủ công).
    * **Luồng API (Stateless):** Sử dụng `JWT (JSON Web Token)` cho các endpoint `/api/**`, sẵn sàng mở rộng cho Mobile App hoặc 3rd-party integration.
    * Cơ chế `SecurityFilterChain` kép để xử lý riêng biệt hai luồng này.

2.  **DTO Pattern & Data Mapping :**
    * Sử dụng DTO (Data Transfer Object) cho toàn bộ các lớp giao tiếp (Controller <-> Service).
    * **Lợi ích:** Ngăn chặn lỗi `LazyInitializationException` của Hibernate, ẩn thông tin nhạy cảm (như password hash, thông tin cá nhân của Borrower đối với Lender), và decouple cấu trúc DB khỏi API.

3.  **Transaction Management & Data Integrity :**
    * Sử dụng `@Transactional` ở cấp độ Service để đảm bảo tính ACID (Atomicity, Consistency, Isolation, Durability).
    * Đặc biệt quan trọng trong luồng `Invest` (Đầu tư) và `Repayment` (Trả nợ): Tiền chỉ được trừ khi mọi trạng thái liên quan đã cập nhật thành công.

---

## 🚀 Tính năng Nghiệp vụ (Key Features)

Hệ thống phân quyền chặt chẽ cho 3 vai trò: **Borrower**, **Lender**, và **Admin**.

### 1. Phân hệ Người vay (Borrower)
* **Hồ sơ tín dụng thông minh:**
    *Hệ thống tự động tính toán **Điểm rủi ro (Risk Score)** và **Hạng rủi ro (Risk Category A/B/C)** dựa trên thu nhập và chỉ số DTI (Debt-to-Income) ngay khi cập nhật hồ sơ .
* **Quy trình vay vốn:**
    * Tạo yêu cầu vay (Loan Request) chỉ khi hồ sơ đã được duyệt lãi suất.
    * Theo dõi trạng thái khoản vay (`PENDING` -> `APPROVED` -> `FUNDED`).
* **Quản lý trả nợ:**
    * Xem lịch trả nợ (Repayment Schedule) chi tiết.
    * Thanh toán từng kỳ hạn bằng Ví điện tử nội bộ.
    * **Logic chặt chẽ:** Bắt buộc trả nợ theo **thứ tự tuần tự** (Kỳ 1 -> Kỳ 2...), không được trả nhảy cóc .

### 2. Phân hệ Nhà đầu tư (Lender)
* **Thẩm định & Đầu tư:**
    * Xem danh sách các khoản vay đã được Admin phê duyệt (`APPROVED`).
    * Xem thông tin Người vay dưới dạng ẩn danh (chỉ thấy Hạng rủi ro, Mục đích vay) để bảo mật .
    * Thực hiện đầu tư: Trừ tiền ví Lender -> Cộng tiền ví Borrower -> Kích hoạt trạng thái `FUNDED` .

### 3. Phân hệ Quản trị (Admin)
* **Kiểm soát rủi ro:**
    * Xem xét hồ sơ chi tiết của Borrower.
    * **Quyền quyết định:** Gán lãi suất (`Interest Rate`) thủ công dựa trên đánh giá rủi ro của hệ thống .
* **Phê duyệt khoản vay:**
    * Duyệt (`APPROVE`) hoặc Từ chối (`REJECT`) các yêu cầu vay vốn .

### 4. Hệ thống Core (System)
* **Ví điện tử (Wallet Simulation):** Giả lập số dư, nạp/rút và lịch sử biến động số dư (Transaction History) .
* **Tự động hóa:** Tự động sinh lịch trả nợ (gốc + lãi) ngay khi khoản vay được giải ngân thành công .
* **Vòng đời khoản vay:** Tự động chuyển trạng thái sang `COMPLETED` khi Borrower hoàn tất nghĩa vụ trả nợ .

---

## 🗄️ Thiết kế Cơ sở dữ liệu (Database Design)

Hệ thống sử dụng mô hình quan hệ chuẩn (Relational Model) với các thực thể chính:

| Thực thể (Entity) | Mô tả | Quan hệ nổi bật |
| :--- | :--- | :--- |
| **User** | Người dùng hệ thống (chung) | N-N với `Role` |
| **BorrowerProfile** | Hồ sơ tín dụng (Thu nhập, DTI...) | 1-1 với `User` |
| **LoanRequest** | Khoản vay | N-1 với `User` (Borrower/Lender) |
| **Investment** | Lịch sử đầu tư | N-1 với `User` & `LoanRequest` |
| **RepaymentSchedule** | Lịch trả nợ chi tiết | N-1 với `LoanRequest` |
| **Transaction** | Nhật ký dòng tiền (Audit log) | N-1 với `User` (From/To) |

---

## ⚙️ Hướng dẫn Cài đặt & Chạy (Installation)

### Yêu cầu tiên quyết
* JDK 17+
* MySQL 8.0+
* Maven

### Các bước triển khai
1.  **Clone dự án:**
 
    git clone https://github.com/Meovjp/P2P-Lending-Platform.git


2.  **Cấu hình Database:**
    * Cách 1 : Import file SQL có sẵn dữ liệu  `spring_pj`.
    * Cách 2  :
         * Tạo database trống tên `spring_pj`
         * Mở file `src/main/resources/application.properties`.
         * Cập nhật `spring.datasource.username` và `password` của bạn.
         * Đảm bảo `spring.jpa.hibernate.ddl-auto=update` để hệ thống tự tạo bảng.

3.  **Chạy ứng dụng:**
    ```bash
    mvn spring-boot:run
    ```
    *Lần chạy đầu tiên, hệ thống sẽ tự động nạp dữ liệu mẫu (Admin, Lender có tiền, Borrower) thông qua class `DataInitializer`.*

4.  **Truy cập:**
    * **Web UI:** `http://localhost:8080` (Đăng nhập bằng tài khoản demo bên dưới).
    * **API Documentation:** `http://localhost:8080/swagger-ui.html` (Nếu đã tích hợp Swagger).

### Tài khoản Demo (Data Initializer)
| Vai trò | Username | Password | Ghi chú |
| :--- | :--- | :--- | :--- |
| **Lender** | `api_lender` | `password123` | Số dư ví: 1 Tỷ VND |
| **Borrower** | `api_borrower` | `password123` | Số dư ví: 0 VND |
| **Admin** | `api_admin` | `password123` | (Tùy chọn tạo thêm) |

---

## 📞 Liên hệ & Đóng góp

Dự án này được phát triển bởi **Nguyễn Minh Trường ** như một phần của đồ án thực tập Java Backend.
Mọi đóng góp hoặc câu hỏi xin vui lòng liên hệ qua [Nguyenminhtruong0905@gmail.com] .

---
*© 2025 P2P Lending Project. *
