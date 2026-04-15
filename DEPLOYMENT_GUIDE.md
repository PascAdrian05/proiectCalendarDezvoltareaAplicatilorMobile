# 🚀 Ghid de Deployment și Securitate

## ⚠️ SECURITATE CRITICĂ

### 1. Chei API Exponates - ACȚIUNE IMEDIATE NECESARE

**Detectate și REVOCATE:**
- ❌ Google Maps API Key: `AIzaSyBsXpC49dIaTU09WLA0aBT9r7x4U13IdpU` (REVOCATĂ)

**Acțiuni efectuate:**
1. ✅ Cheia Google Maps a fost revocată în Google Cloud Console
2. ✅ Fișierul `app/secrets.properties` actualizat cu placeholder
3. ✅ `.gitignore` configurat corect pentru a ignora secrets

### 2. Setup Securizat - Pași

#### A. Generează Google Maps API Key NOUĂ
```bash
1. Mergi pe https://console.cloud.google.com/
2. Selectează proiectul tău
3. API & Services → Credentials
4. Create Credentials → API Key
5. Restricționează cheia la Android Applications
6. Copiază cheia nouă
```

#### B. Configurează secrets.properties
```bash
# Windows - Edit local app/secrets.properties:
MAPS_API_KEY=YOUR_NEW_KEY_FROM_GOOGLE_CLOUD
BACKEND_BASE_URL=http://10.0.2.2:8080/
```

#### C. Setup Backend cu variabile de mediu
```bash
# Navigează la backend folder
cd backend

# Opțiunea 1: Folosind dev-run.ps1 (RECOMANDATĂ)
powershell -ExecutionPolicy Bypass -File .\dev-run.ps1 `
  -JavaHome "C:\Program Files\Android\Android Studio\jbr" `
  -DbUser "calendar_user" `
  -DbPass "YOUR_SECURE_PASSWORD" `
  -JwtSecret "YOUR_STRONG_JWT_SECRET_HERE"

# Opțiunea 2: Variabile de mediu globale (permanent)
$env:SPRING_DATASOURCE_PASSWORD = "YOUR_DB_PASSWORD"
$env:JWT_SECRET_KEY = "YOUR_JWT_SECRET_MIN_32_CHARS_STRONG"
$env:SPRING_DATASOURCE_URL = "jdbc:mysql://localhost:3306/bazaDeDateCalendar?..."
```

### 3. Database Setup

```sql
-- Conectează-te ca root MySQL
mysql -u root -p

-- Creează utilizator non-root
CREATE USER 'calendar_user'@'localhost' IDENTIFIED BY 'YOUR_SECURE_PASSWORD';

-- Acordă permisiuni la baza de date
GRANT ALL PRIVILEGES ON bazaDeDateCalendar.* TO 'calendar_user'@'localhost';
FLUSH PRIVILEGES;
```

### 4. Verificare Securitate Pre-Commit

```bash
# Verifică că nu ai secrete în git
git grep -n "AIzaSyBsXpC49dIaTU09WLA0aBT9r7x4U13IdpU"
git grep -n "root.*password"
git grep -n "SPRING_DATASOURCE_PASSWORD.*root"

# Șterge cached credentials
git rm --cached app/secrets.properties
git rm --cached backend/.env
git commit -m "Remove secrets from git history"
```

## 📋 Start Aplicației

### Backend (Terminal 1)
```powershell
cd C:\Users\PASC\AndroidStudioProjects\proiectCalendarUMFST\backend

# Start cu dev script
powershell -ExecutionPolicy Bypass -File .\dev-run.ps1 `
  -JavaHome "C:\Program Files\Android\Android Studio\jbr" `
  -DbUser "calendar_user" `
  -DbPass "YOUR_DB_PASSWORD"

# Backend va rula pe http://localhost:8080
```

### Android App (Terminal 2)
```bash
# Din Android Studio sau:
# Right-click proiect → Run

# Aplicația va conecta la backend via:
# http://10.0.2.2:8080 (emulator default)
# http://192.168.x.x:8080 (device actual)
```

## ✅ Testare Update Eveniment

1. **Login în aplicație**
   - Completează credențiale
   - Primești JWT token

2. **Adaugă eveniment**
   - Click data
   - Completează formularul
   - Save

3. **Modifică eveniment**
   - Swipe pe eveniment
   - Click update button
   - Modifică câmpuri
   - Save
   - **Verifică:** Progress bar apare, nu se blochează UI

4. **Verificare date în DB**
   ```sql
   SELECT * FROM Evenimente 
   WHERE utilizator_id = YOUR_USER_ID 
   ORDER BY data DESC;
   ```

## 🐛 Probleme Comune & Soluții

| Problemă | Cauză | Soluție |
|----------|-------|---------|
| "Port 8080 already in use" | Alt proces ocupă port | `Get-Process -Name java` și kill sau schimbă SERVER_PORT |
| "Access denied for user" | Parola DB greșită | Verifică SPRING_DATASOURCE_PASSWORD în dev-run.ps1 |
| "UI blochează pe update" | Main thread blocking | ✅ FIXAT - Retrofit folosește callbacks asyncrone |
| "Google Maps nu merge" | API Key greșită/revocată | ✅ Generează nouă cheie și actualizează secrets.properties |

## 🔐 Best Practices

✅ **DO:**
- Stochează secrets în variabile de mediu pe producție
- Folosește HTTPS în producție
- Rotează JWT secret periodic
- Validează input pe server (backend-side validation)
- Loghează tentativele de acces neautorizate

❌ **DON'T:**
- Niciodată nu hardcode secrets în cod
- Nu folosi root DB user în producție
- Nu trimit secrets prin email/chat
- Nu faci commit de .properties/sensitive files

## 📝 Git Workflow Securizat

```bash
# 1. Before commit - verify no secrets
git diff --staged | grep -i "password\|secret\|key" || echo "✓ No secrets detected"

# 2. Commit cu mesaj descriptiv
git add .
git commit -m "feat: Fixed update blocking issue + threading improvements"

# 3. Push la remote
git push origin main

# 4. After push - verify
git log --oneline -1
```

## 📊 Schimbare Efectuate

### Backend (Java/Spring Boot)
- ✅ `ServiceEvenimente.java` - Adăugat `setLocatie()` în update
- ✅ Actualizări trimit toate câmpurile corect

### Android App (Kotlin)
- ✅ `AdaugareActivity.java` - Retrofit callback pe background thread
- ✅ Adăugat ProgressBar pentru vizibilitate
- ✅ Button disabled durante requestului pentru evita multiple clicks
- ✅ Mesaje de eroare mai descriptive
- ✅ UI nu se mai blochează!

---

**Data ultimei actualizări:** 15-04-2026
**Status:** ✅ Gata pentru testing
**Next:** Deploy pe device/emulator real și test complet

