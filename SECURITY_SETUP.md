# Secret cleanup / setup

## Ce am scos din fișierele versionate
- parola MySQL hardcodata
- cheia JWT hardcodata
- cheia Google Maps hardcodata

## Ce trebuie sa faci imediat
1. Schimba parola utilizatorului MySQL pe care l-ai folosit deja.
2. Genereaza o cheie JWT noua.
3. Revoca si regenereaza cheia Google Maps API.
4. Daca ai facut push cu valorile vechi, curata si istoricul git (nu doar ultimul commit).

## Backend local
Porneste backend-ul cu valori din argumente sau din variabile de mediu.

Exemplu PowerShell:

```powershell
Set-Location 'C:\Users\PASC\AndroidStudioProjects\proiectCalendarUMFST\backend'
powershell -ExecutionPolicy Bypass -File .\dev-run.ps1 -JavaHome "C:\Program Files\Android\Android Studio\jbr" -DbUser "calendar_user" -DbPass "PAROLA_TA_MYSQL"
```

Daca nu dai `-JwtSecret`, scriptul genereaza unul temporar pentru sesiunea curenta.

## Android local
1. Copiaza `app/secrets.properties.example` in `app/secrets.properties`
2. Pune cheia ta reala:

```ini
MAPS_API_KEY=CHEIA_TA_REALA
```

`app/secrets.properties` este ignorat de git.

## Important despre istoricul git
Chiar daca acum fisierele sunt curate, secretele vechi pot exista in commit-urile deja impinse.
Trebuie sa:
- rotesti secretele vechi;
- cureti istoricul repository-ului cu `git filter-repo` sau BFG Repo-Cleaner;
- faci push din nou dupa curatare.

