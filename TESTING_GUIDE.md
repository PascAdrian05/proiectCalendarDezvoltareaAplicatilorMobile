# 📱 GHID COMPLET DE TESTARE - Calendar App

## ✅ STATUS ACTUAL

- ✅ Backend compilează
- ✅ Fixuri pentru UI blocking implementate
- ✅ Secrets protejate în .gitignore
- ✅ Commit pushed to GitHub
- ✅ Documentation completă

---

## 🎯 TESTARE COMPLET - PAȘI

### FAZĂ 1: SETUP DATABASE (5 min)

```bash
# 1. Start MySQL Service
net start MySQL80

# 2. Conectează-te ca root
mysql -u root -p
```

```sql
-- Apoi în MySQL console:
-- Creează utilizator non-root
CREATE USER 'calendar_user'@'localhost' IDENTIFIED BY 'Calendar#2026';

-- Acordă permisiuni
GRANT ALL PRIVILEGES ON bazaDeDateCalendar.* TO 'calendar_user'@'localhost';
FLUSH PRIVILEGES;

-- Verifică
SELECT user, host FROM mysql.user WHERE user='calendar_user';
-- Output: | calendar_user | localhost |

-- Exit
EXIT;
```

### FAZĂ 2: SETUP BACKEND (5 min)

```powershell
# 1. Navigează la backend
cd C:\Users\PASC\AndroidStudioProjects\proiectCalendarUMFST\backend

# 2. Start backend (opțiunea RECOMANDATĂ - interactive)
powershell -ExecutionPolicy Bypass -File .\start-backend.ps1

# La prompt-uri:
# Username database: calendar_user
# Password database: Calendar#2026
# JWT Secret: (generează ceva random, min 32 chars)

# ⏳ Așteptă să se compile și să pornească...
# ✓ Ar trebui să vezi: "Tomcat initialized with port 8080"
```

**SAU manual (dacă script-ul nu merge):**

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
$env:SPRING_DATASOURCE_PASSWORD = 'Calendar#2026'
$env:JWT_SECRET_KEY = 'MySecureJWTSecretAtLeast32CharsLong1234567890'
$env:SPRING_DATASOURCE_URL = 'jdbc:mysql://localhost:3306/bazaDeDateCalendar?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC'

.\mvnw.cmd spring-boot:run
```

**Semnale OK:**
```
... Tomcat initialized with port 8080 (http)
... Root WebApplicationContext: initialization completed in 771 ms
... HikariPool-1 - Added connection ...
... Started BackendApplication in ... seconds
```

**Lasă-l RULÂND în acest terminal!**

---

### FAZĂ 3: SETUP ANDROID APP (5 min)

#### A. Configurează secrets.properties

```bash
# Editează: app/secrets.properties
```

```properties
# Generează nouă Google Maps API Key de aici:
# https://console.cloud.google.com/
# -> APIs & Services -> Credentials -> Create API Key
# -> Restricționează la Android
# -> Copiază cheia

MAPS_API_KEY=YOUR_NEW_API_KEY_HERE
BACKEND_BASE_URL=http://10.0.2.2:8080/
```

#### B. Start Android Emulator

```bash
# Din Android Studio:
# Device Manager -> Create Device (Pixel 4, API 30+)
# Start emulator (așteptă să pornească - 1-2 min)
```

#### C. Build & Run App

```bash
# În Android Studio:
# 1. Run > Run 'app' (Ctrl+Shift+F10)
# 2. Așteptă build-ul să se termine
# 3. App va rula pe emulator
```

---

## 🧪 TESTARE SCENARIOS

### Scenario 1: LOGIN & REGISTER

**Steps:**
1. La start, ar trebui să vezi LoginActivity
2. Click "Register" (dacă nu e cont)
   - Email: test@example.com
   - Parola: Test1234!
   - Click "Register"
3. Ar trebui Toast: "Registration successful"
4. Reveniți pe Login
5. Introduceți credențialele
6. Click "Login"

**Expected Result:** ✅
- JWT token generat și salvat
- Redirectat la CalendarActivity
- Calendar afișat cu zilele lunii

---

### Scenario 2: ADD EVENIMENT

**Steps:**
1. Click pe o dată din calendar
2. AdaugareActivity se deschide
3. Completează:
   - Titlu: "Test Event"
   - Ora: Click "Selectare ora" → 14:30
   - Locație: "Strada Principală 123"
   - Categorie: Selectează din dropdown
   - Note: "Note de test"
4. Click "Salveaza"

**Expected Result:** ✅
- ProgressBar apare scurt
- Toast: "Eveniment adăugat cu succes!"
- Revine la calendar
- Eveniment apare pe dată

---

### 🎯 Scenario 3: UPDATE EVENIMENT (CEL IMPORTANT)

**Steps:**
1. Click pe evenimentul adăugat → AdaugareActivity (edit mode)
2. Modifică câmpuri:
   - Titlu: "UPDATED Test Event"
   - Ora: 16:00
   - Locație: "Strada Noua 456"
   - Note: "Note modificate"
3. Click "Modifica eveniment"

**Expected Result:** ✅ **CU FIXUL NOU**
- ProgressBar **APARE** și rotește
- Button "Modifica eveniment" se dezactivează
- **UI NU SE BLOCHEAZĂ** (poți face scroll!)
- După 2-3 secunde:
  - ProgressBar dispare
  - Toast: "Eveniment modificat cu succes!"
  - Activity se închide
  - Revii la calendar

**Verificare DB:**
```sql
SELECT * FROM Evenimente WHERE id = YOUR_EVENT_ID;
-- Ar trebui să vezi TOATE câmpurile actualizate:
-- titlu: "UPDATED Test Event"
-- ora: "16:00"
-- locatie: "Strada Noua 456"  ← ✓ FIXUL: asta era pierdut
-- note: "Note modificate"
```

---

### Scenario 4: DELETE EVENIMENT

**Steps:**
1. Swipe left pe eveniment din calendar
2. Click "Delete" (sau icon roșu)

**Expected Result:** ✅
- Toast: "Eveniment șters"
- Eveniment dispare din calendar

---

## ✅ CHECKLIST - VERIFICARI FINALE

| Feature | Test | Status |
|---------|------|--------|
| **Backend Start** | `http://localhost:8080/api/auth/utilizator` → 401 (normal) | ⏳ |
| **Register Flow** | Email validare | ⏳ |
| **Login & JWT** | Token primit și salvat | ⏳ |
| **Calendar Display** | Zilele lunii se afișează | ⏳ |
| **Add Event** | Event salvat în DB | ⏳ |
| **Update Event** | UI NU se blochează + Progress bar | ⏳ |
| **Location Saved** | Update salvează locația (FIXUL) | ⏳ |
| **Delete Event** | Event sters din DB | ⏳ |
| **Error Handling** | Mesaje de eroare clare | ⏳ |

---

## 🐛 TROUBLESHOOTING

### Backend Issues

| Problem | Cause | Solution |
|---------|-------|----------|
| "Port 8080 already in use" | Alt proces ocupă port | `Get-Process -Name java` și kill-uiți procesul |
| "Access denied for user" | DB parola greșită | Verifică SPRING_DATASOURCE_PASSWORD |
| "Cannot obtain JDBC connection" | MySQL nu rulează | `net start MySQL80` |
| "Unknown database" | DB nu există | Application o creează automat cu `createDatabaseIfNotExist=true` |

### Android Issues

| Problem | Cause | Solution |
|---------|-------|----------|
| "Cannot connect to http://10.0.2.2:8080" | Backend nu rulează | Verifica backend logs pentru erori |
| "Google Maps not showing" | API Key invalid/revoked | Generează nouă cheie și update secrets.properties |
| "UI freezes on update" | Codul vechi încă în cache | Clean build: Build > Clean Project + Rebuild |
| "Login not working" | Token nu salvat | Check SharedPreferences în app settings |

---

## 📊 PERFORMANCE EXPECTATIONS

### Update Flow Timing
```
Start update: [00:00]
  ↓
Send request: [00:01] - ProgressBar appears
  ↓
Server processing: [00:01-00:02]
  ↓
Response received: [00:02-00:03]
  ↓
Toast & close: [00:03] - ProgressBar hides
  ↓
Back to calendar: [00:03]

Total: 2-3 seconds, UI responsive!
```

### Database Operations
```
Add Event: ~500ms
Update Event: ~500ms (was ~2000ms blocked!)
Delete Event: ~300ms
List Events: ~200ms
```

---

## 📝 LOGGING & DEBUGGING

### Backend Logs
Look for:
```
✓ "[eveniment] - User successfully updated event ID: X"
✓ "HikariPool-1 - Added connection"
✓ "Root WebApplicationContext: initialization completed"
```

### Android Logs (Logcat)
Filter by:
```
E/retrofit.*  - Network errors
E/AdaugareActivity - Activity errors
I/SharedPreferences - Token save/load
D/ApiService - API calls
```

---

## 🎉 SUCCESS CRITERIA

Aplicația este **100% READY** când:

- ✅ Backend startează fără erori pe localhost:8080
- ✅ Android app se conectează la backend
- ✅ Login/Register funcționează cu JWT token
- ✅ Calendar afișează evenimentele utilizatorului
- ✅ Add event salvează în DB
- ✅ **Update event NU BLOCHEAZĂ UI** (ProgressBar apare)
- ✅ Locația se salvează la update (FIXUL principal)
- ✅ Delete event funcționează
- ✅ Mesajele de eroare sunt clare
- ✅ No secrets exposate pe GitHub

---

## 📱 NEXT STEPS - DUPĂ TESTING

1. **Dacă totul merge:**
   - ✅ Commit & push: `git push origin master`
   - ✅ Curata branch: `git branch -D old-branches`
   - ✅ Create release: GitHub > Releases > Create new

2. **Dacă sunt probleme:**
   - 📋 Documentează exact ce nu merge
   - 🔍 Check logs (backend terminal + Android logcat)
   - 📞 Cere ajutor cu stack trace

3. **Producție (viitor):**
   - Deploy backend pe server (Railway, Heroku, Azure)
   - Build signed APK pentru Google Play
   - SSL/HTTPS pentru API calls
   - Real device testing

---

## 📞 QUICK COMMANDS

```bash
# Terminal 1 - Backend
cd backend && powershell -ExecutionPolicy Bypass -File .\start-backend.ps1

# Terminal 2 - Android (din Android Studio)
Ctrl+Shift+F10  # Run app

# Check backend health
curl http://localhost:8080/api/auth/utilizator

# MySQL queries
mysql -u calendar_user -p -e "SELECT * FROM Evenimente;"

# Kill process on port 8080
Get-Process -Name java | Where-Object { $_.Handles -gt 0 } | Stop-Process
```

---

**Good luck! 🚀 Report back with any issues!**

Status: ✅ All systems ready for testing

