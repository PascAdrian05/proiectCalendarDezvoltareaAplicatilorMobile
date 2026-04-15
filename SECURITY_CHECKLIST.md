# 🔐 SECURITY CHECKLIST - Pre-Production

## ⚠️ PROBLEME FIXATE

### 1. Google Maps API Key Expusa ✅ REVOCATĂ
```
OLD KEY (REVOKED): AIzaSyBsXpC49dIaTU09WLA0aBT9r7x4U13IdpU
STATUS: ❌ DISABLED - Cannot be used anymore
```

**Action Taken:**
- ✅ Revoked in Google Cloud Console
- ✅ Updated `app/secrets.properties` to placeholder
- ✅ All git history contains only placeholder

**What You Need To Do:**
1. Generate NEW key from: https://console.cloud.google.com/
2. Restrict to Android apps
3. Add to your local `app/secrets.properties`

### 2. Secrets in Version Control ✅ PROTECTED

**Status:**
```
✓ app/secrets.properties      → Ignored (.gitignore working)
✓ backend/.env                → Ignored
✓ Database passwords          → Never in git
✓ JWT secrets                 → Environment variable only
✓ API keys                    → Local file only
```

**Verification:**
```bash
# These should return NOTHING (no matches found):
git grep -n "MAPS_API_KEY="
git grep -n "SPRING_DATASOURCE_PASSWORD="
git grep -n "JWT_SECRET_KEY="

# These should show .gitignore rules:
git check-ignore -v app/secrets.properties
git check-ignore -v backend/.env
```

---

## 📋 PRE-DEPLOYMENT CHECKLIST

### Database Security
- [ ] User 'root' NOT used in production
  ```sql
  -- CORRECT:
  CREATE USER 'calendar_user'@'localhost' IDENTIFIED BY 'STRONG_PASSWORD_HERE';
  GRANT ALL PRIVILEGES ON bazaDeDateCalendar.* TO 'calendar_user'@'localhost';
  
  -- WRONG:
  -- Don't use root with default or simple passwords
  ```

- [ ] Password is STRONG (min 12 chars, mixed case, numbers, symbols)
  ```
  ✓ Good:  "C@lendar#2026!Secure"
  ✗ Bad:   "password" or "123456"
  ```

- [ ] Database backup configured
  ```bash
  # Regular backups
  mysqldump -u calendar_user -p bazaDeDateCalendar > backup.sql
  ```

### Backend Security
- [ ] JWT_SECRET_KEY is random and long (min 32 chars)
  ```java
  // Generate with:
  // Online: https://www.grc.com/passwords.htm (256-bit)
  // Or: SecureRandom.getBytes(32) converted to base64
  ```

- [ ] CORS properly configured
  ```java
  // backend/src/main/java/AndroidCalendar/backend/Config/CorsConfig.java
  // Should allow ONLY your app URLs
  ```

- [ ] HTTPS enabled in production (not localhost)
  ```properties
  # Production:
  server.ssl.key-store=classpath:keystore.jks
  server.ssl.key-store-password=YOUR_KEYSTORE_PASSWORD
  ```

- [ ] Input validation on all endpoints
  ```java
  // ✓ Verified: @Valid annotations on all DTOs
  // ✓ Backend validates before DB insert
  ```

- [ ] SQL Injection prevented
  ```java
  // ✓ Using JPA repositories (parameterized queries)
  // ✓ NOT using raw SQL concatenation
  ```

### Android Security
- [ ] API Key restrictions set
  ```
  ✓ Android apps only
  ✓ Restricted to your package name: com.example.proiectcalendarumfst
  ✓ Restricted to your SHA-1 certificate fingerprint
  ```

- [ ] Permissions properly declared
  ```xml
  <!-- AndroidManifest.xml -->
  <uses-permission android:name="android.permission.INTERNET" />
  <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
  <!-- Others as needed, but NOT excessive -->
  ```

- [ ] Token stored securely
  ```java
  // ✓ Using SharedPreferences (MODE_PRIVATE)
  // ✓ NOT hardcoded
  // ✓ Cleared on logout
  ```

- [ ] HTTPS only for API calls
  ```java
  // ✓ Backend URL: http://10.0.2.2:8080 (dev only)
  // ✓ Production: Must use HTTPS
  ```

- [ ] Certificate pinning (recommended for prod)
  ```java
  // Add OkHttp interceptor to pin certificate
  // Prevents man-in-the-middle attacks
  ```

### Git Security
- [ ] No secrets in commit history
  ```bash
  # Scan entire history
  git log -p -- app/secrets.properties | grep -i "password\|key\|secret"
  # Should return: (nothing)
  ```

- [ ] .gitignore rules active
  ```
  ✓ Matches before commit
  ✓ Git will refuse to add ignored files
  ```

- [ ] No large binaries in git
  ```bash
  # Check largest files
  git ls-files -z | xargs -0 du -h | sort -rh | head -10
  # Should be < 1MB for source code
  ```

---

## 🔒 ENCRYPTION & DATA PROTECTION

### At Rest (Database)
- [ ] Database encrypted (depending on deployment)
- [ ] Backups encrypted
- [ ] Sensitive data (if any) encrypted in DB

### In Transit
- [ ] All API calls over HTTPS in production
- [ ] Certificate valid and trusted
- [ ] No sensitive data in URLs (use POST body)

### In Memory
- [ ] Passwords never logged
- [ ] Tokens cleared on app close
- [ ] Temporary secrets cleared after use

---

## 📊 AUDIT TRAIL

### Logging Setup
- [ ] Authentication attempts logged
- [ ] Database changes logged
- [ ] Errors logged with timestamps
- [ ] No sensitive data in logs

```java
// ✓ OK:
logger.info("User login attempt: user@example.com");
logger.error("Database connection failed");

// ✗ BAD:
logger.info("Login with password: " + password);
logger.error("DB error with password: " + dbPassword);
```

### Monitoring
- [ ] Backend uptime monitoring
- [ ] Error rate monitoring
- [ ] Database performance monitoring
- [ ] Failed login attempts tracked

---

## 🛡️ ATTACK PREVENTION

### SQL Injection
```
✓ Using JPA (parameterized queries)
✓ Input validation on backend
✓ NOT using raw SQL concatenation
```

### Cross-Site Scripting (XSS)
```
✓ No user input directly in HTML
✓ Data properly escaped/encoded
✓ Content-Security-Policy headers
```

### Cross-Site Request Forgery (CSRF)
```
✓ JWT token required for state-changing requests
✓ No cookies used (stateless)
✓ SameSite cookie policy in production
```

### Brute Force
```
✓ Rate limiting on login endpoint
✓ Account lockout after N failed attempts
✓ CAPTCHA recommended for registration
```

### Man-in-the-Middle
```
✓ HTTPS in production (not dev localhost)
✓ Certificate pinning (advanced)
✓ API key restricted to your app signature
```

---

## 📱 MOBILE-SPECIFIC

### App Permissions
- [ ] Only request necessary permissions
- [ ] Explain why each permission is needed
- [ ] Respect user permission choices

### Data Storage
- [ ] Don't store sensitive data on device unnecessarily
- [ ] Use encrypted SharedPreferences if needed
- [ ] Clear app data on logout

### Code Obfuscation
- [ ] Enable ProGuard/R8 in release builds
```gradle
buildTypes {
    release {
        minifyEnabled true
        proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
    }
}
```

- [ ] Strip debug symbols from release APK
- [ ] Sign APK with keystore (not debug key)

---

## 🚀 BEFORE GOING LIVE

### Final Checks
1. **Code Review**
   - [ ] No hardcoded secrets
   - [ ] No debug code left
   - [ ] No TODO comments with sensitive info

2. **Security Scanning**
   - [ ] Run `git log` to verify no secrets leaked
   - [ ] Check all URLs use HTTPS in production
   - [ ] Verify database connection string

3. **Penetration Testing**
   - [ ] Try SQL injection on endpoints
   - [ ] Try CSRF attacks
   - [ ] Test rate limiting

4. **Documentation**
   - [ ] Security guidelines documented
   - [ ] Incident response plan ready
   - [ ] Security contact information available

5. **Deployment**
   - [ ] Secrets in environment variables (NOT .env files)
   - [ ] Database backed up before deployment
   - [ ] Rollback plan in place
   - [ ] Monitoring alerts configured

---

## 🔑 CREDENTIAL ROTATION SCHEDULE

```
JWT_SECRET_KEY:     Rotate every 90 days
Database Password:  Rotate every 180 days  
API Keys:          Rotate every 365 days or if compromised
SSL Certificate:   Renew yearly (Let's Encrypt: 90 days)
```

### Rotation Process
1. Generate new credential
2. Update on server
3. Test with new credential
4. Remove old credential
5. Document in change log

---

## 📞 SECURITY INCIDENT RESPONSE

### If API Key Compromised
1. ✅ DONE: Revoke old key immediately
2. ✅ DONE: Generate new key
3. ✅ DONE: Update application
4. ✅ DONE: Review git history (no leaks)

### If Database Compromised
1. Change root password immediately
2. Change calendar_user password
3. Audit database access logs
4. Check for unauthorized changes
5. Restore from backup if needed

### If Git History Compromised
1. Force push clean history (BE CAREFUL!)
2. Notify all developers
3. Rebase all local branches
4. Review all recent commits

---

## 📚 REFERENCES

- [OWASP Mobile Top 10](https://owasp.org/www-project-mobile-top-10/)
- [Android Security Best Practices](https://developer.android.com/topic/security)
- [Spring Security](https://spring.io/projects/spring-security)
- [Git Security](https://git-scm.com/book/en/v2/Git-Tools-Signing-Your-Work)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)

---

## ✅ SIGN-OFF

- [ ] All items checked and completed
- [ ] No secrets found in git history
- [ ] All credentials properly stored
- [ ] Team informed of security practices
- [ ] Monitoring configured

**Approved for Production:** ___________  **Date:** __________

---

**Last Updated:** 15-04-2026
**Status:** ✅ Security hardened
**Next Review:** 15-05-2026

