# SalseforceMFA

A desktop application and automated testing suite for generating Time-based One-Time Passwords (TOTP) and automating Salesforce login with Multi-Factor Authentication (MFA).

## 🎯 Features

- **Desktop GUI Application** - Intuitive Swing-based interface for generating and copying OTP codes
- **Multi-User Support** - Manage TOTP codes for multiple Salesforce users
- **RFC 6238 TOTP Algorithm** - Secure time-based OTP generation (30-second intervals)
- **Modern UI Design** - Beautiful card-based interface with gradients and rounded corners
- **Live Timer** - Visual countdown showing remaining seconds for current OTP
- **Autocomplete Search** - Keyboard-friendly user search with dropdown suggestions
- **One-Click Copy** - Copy OTP directly to clipboard with status feedback
- **Automated Testing** - Selenium-based automated Salesforce MFA login workflow

## 📋 Prerequisites

- **Java 8+** (Java 11+ recommended)
- **Maven 3.6+**
- **Chrome Browser** (for Selenium automation)
- **ChromeDriver** (compatible version for your Chrome browser)
- A valid Salesforce account with MFA enabled

## 🚀 Installation

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/SalseforceMFA.git
cd SalseforceMFA
```

### 2. Configure User Secrets
Edit `src/main/resources/users.properties` and add your Salesforce users with their TOTP secrets:

```properties
# Format: username=BASE32_ENCODED_SECRET
automation.user1=JBSWY3DPEHPK3PXP
automation.user2=KRSXG5DSMFZXGIDM
automation.admin=GEZDGNBVGY3TQOJQ
your.email@example.com=YOUR_BASE32_SECRET
```

> **Note**: TOTP secrets must be BASE32 encoded. You can obtain these from your Salesforce account's MFA setup page.

### 3. Build the Project
```bash
mvn clean install
```

## 💻 Usage

### Desktop GUI Application

#### Running the Application
```bash
mvn exec:java -Dexec.mainClass="TotpDesktopApp.TotpDesktopApp"
```

#### How to Use
1. Launch the application
2. Type or select a user from the dropdown list
3. The OTP code will automatically generate (6 digits)
4. Click **"📋 Copy OTP"** to copy to clipboard
5. The timer shows remaining seconds before code expires
6. Select a new user to generate a different code

**Keyboard Shortcuts:**
- `↓` Arrow Down - Navigate suggestions
- `↑` Arrow Up - Navigate suggestions
- `Enter` - Select highlighted user

---

### Automated Testing - Salesforce Login

#### Running the Automated Test
```bash
mvn test
```

Or directly run the test class:
```bash
mvn exec:java -Dexec.mainClass="TOTP.SalesforceLoginTest"
```

#### What It Does
1. Launches Chrome browser and navigates to Salesforce login
2. Enters username and password
3. Waits for MFA verification prompt
4. Generates OTP code using stored secret
5. Enters OTP and completes login
6. Verifies successful login by checking page title

#### Configuration
Edit [SalesforceLoginTest.java](src/test/java/TOTP/SalesforceLoginTest.java):
```java
String username = "your.email@example.com";
String password = "your-password";
String totpSecret = TotpSecretStore.getSecret(username);
```

---

## 📁 Project Structure

```
SalseforceMFA/
├── pom.xml                           # Maven configuration
├── README.md                          # This file
├── src/
│   ├── main/
│   │   ├── java/TotpDesktopApp/
│   │   │   ├── TotpDesktopApp.java           # Main GUI application
│   │   │   ├── RFC6238TOTP.java              # TOTP algorithm (RFC 6238)
│   │   │   ├── UserSecretStore.java          # Secret key manager
│   │   │   └── UserSearchField.java          # Autocomplete search component
│   │   └── resources/
│   │       └── users.properties              # User TOTP secrets
│   └── test/
│       ├── java/TOTP/
│       │   ├── SalesforceLoginTest.java      # Automated login test
│       │   ├── TOTPGenerator.java            # Test TOTP utility
│       │   └── TotpSecretStore.java          # Test secret manager
│       └── resources/
│           └── users.properties              # Test user secrets
└── target/                           # Build output
```

---

## 🔐 Security Notes

⚠️ **Important**: 
- **Never commit `users.properties` with real secrets to version control**
- Add `src/main/resources/users.properties` to `.gitignore`
- Store secrets in environment variables or secure vaults for production
- Use strong passwords with MFA enabled
- Keep credentials separate from source code

### Recommended .gitignore Entry
```
src/main/resources/users.properties
src/test/resources/users.properties
.env
*.properties    
```

---

## 📦 Dependencies

| Dependency | Version | Purpose |
|-----------|---------|---------|
| Selenium Java | 4.18.1 | Browser automation |
| TOTP (samstevens) | 1.7.1 | TOTP library |
| TOTP (de.taimos) | 1.0 | Alternative TOTP implementation |
| Apache Commons Net | 3.6 | Network utilities |

---

## 🛠️ How TOTP Works

This application implements **RFC 6238** Time-based One-Time Password (TOTP) algorithm:

1. **Time Counter**: Current Unix timestamp divided by 30 seconds
2. **HMAC-SHA1**: Hash the counter with your shared secret
3. **Dynamic Binary Code**: Extract 4 bytes from the hash using a dynamic offset
4. **6-Digit OTP**: Apply modulo to generate a 6-digit code
5. **Time-based Expiry**: Code expires every 30 seconds

**Example Timeline:**
```
00:00 - 00:30  → OTP Code A (30 seconds valid)
00:30 - 01:00  → OTP Code B (30 seconds valid)
01:00 - 01:30  → OTP Code C (30 seconds valid)
```

---

## 🐛 Troubleshooting

### Application won't start
- Ensure Java 8+ is installed: `java -version`
- Verify Maven is installed: `mvn -version`
- Check that `users.properties` exists and is properly formatted

### Users list is empty
- Verify `users.properties` is in `src/main/resources/`
- Check file format: `username=BASE32_SECRET` (no spaces around `=`)
- Rebuild project: `mvn clean install`

### OTP generation fails
- Ensure secrets are BASE32 encoded (only A-Z and 2-7 characters)
- Verify system time is synchronized (NTP)
- Test with known working secret

### Selenium test fails
- Ensure ChromeDriver version matches your Chrome browser version
- Download from: https://chromedriver.chromium.org/
- Update Selenium config with correct ChromeDriver path
- Verify Salesforce credentials are correct

---

## 🔄 Getting TOTP Secret from Salesforce

1. Log in to Salesforce
2. Click your profile icon → **Settings**
3. Navigate to **Security** → **Multi-Factor Authentication**
4. Enable authenticator app (Google Authenticator, Microsoft Authenticator, etc.)
5. Scan the QR code with your authenticator app
6. Copy the **Setup Key** (BASE32 secret)
7. Add to `users.properties`: `your.email@example.com=SETUP_KEY`

---

## 📝 Example Workflow

### Desktop App Example
```
1. Launch: mvn exec:java -Dexec.mainClass="TotpDesktopApp.TotpDesktopApp"
2. Type: "automation.user1"
3. Select from dropdown
4. See: "123456" displayed in 6 boxes
5. Click: "📋 Copy OTP"
6. Paste into Salesforce login
7. Status: "Copied!" message appears
```

### Automated Test Example
```bash
$ mvn test

[INFO] Running TOTP.SalesforceLoginTest
[INFO] Opening Chrome browser...
[INFO] Navigating to Salesforce login page
[INFO] Entering credentials...
[INFO] Waiting for MFA prompt...
[INFO] Generating OTP: 456789
[INFO] Entering OTP...
[INFO] Login successful with MFA
[INFO] BUILD SUCCESS
```

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

### Development Setup
1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## ⚠️ Disclaimer

This tool is for educational and authorized testing purposes only. Ensure you have permission from Salesforce and your organization before automating login procedures. The authors are not responsible for unauthorized access or misuse of this tool.

---

## 📧 Support

For issues, questions, or suggestions, please open an issue on GitHub or contact the project maintainer.

---

## 🚧 Future Enhancements

Planned features and improvements for upcoming releases:

### Database Connectivity
- [ ] **SQLite Integration** - Local embedded database for persistent user storage
- [ ] **MySQL/PostgreSQL Support** - Cloud-hosted database backend for enterprise deployments
- [ ] **User Credentials Storage** - Secure encrypted storage of usernames and TOTP secrets


### Excel/Spreadsheet Integration
- [ ] **XLS/XLSX Import** - Load users and TOTP secrets from Excel files
  - Column format: `Username | Email | TOTP_Secret | Status`
- [ ] **Bulk User Management** - Import multiple users at once from spreadsheet
- [ ] **Scheduled Sync** - Automatically sync user data from Excel file at intervals
- [ ] **CSV Support** - Alternative CSV format for user/key mapping

### UI/UX Improvements
- [ ] **Dark Mode** - Theme toggle for dark/light interface


### Security & Authentication
- [ ] **Login To App** - Add authentication to access the desktop app itself
- [ ] **Role-Based Access Control** - Different permissions for admin vs regular users

## 🎓 References

- [RFC 6238 - TOTP Algorithm](https://tools.ietf.org/html/rfc6238)
- [RFC 4648 - Base32 Encoding](https://tools.ietf.org/html/rfc4648)
- [Salesforce MFA Documentation](https://help.salesforce.com/s/articleView?id=sf.security_mfa_setup.htm)
- [Selenium WebDriver Documentation](https://www.selenium.dev/documentation/)
- [Java Swing Tutorial](https://docs.oracle.com/javase/tutorial/uiswing/)

---

**Last Updated**: February 2026  
**Version**: 0.0.1-SNAPSHOT

#

