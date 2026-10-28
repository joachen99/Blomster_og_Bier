~** Gruppe 8 **

Gruppemedlemmer:
-Sondre
-Håkon
-Evina
-Synnøve
-Joachim
-Marianne

For å kjøre denne appen må du:
1. Last ned prosjektfilene og åpne prosjektet i Android Studio
2. Opprett en emulator eller koble til en Android-enhet
3. Kjør gradle sync og gradle build
4. Kjør appen

For å benytte seg av appen kreves API-nøkler fra Frost. Disse nøklene ligger allerede eksplisitt i FrostDatasource som snakker med Frost API v0, men det er mulig å bytte de ut med signe egne fra: https://frost.met.no/auth/requestCredentials.html
Appen krever kun tilgang på internett for å kjøre.

Appen benytter seg av flere biblioteker som er viktige for funksjonaliteten:
Compose og AndroidX for generell oppbygning, brukergrensesnitt og utseende på appen
Ktor og Kotlinx Serialization brukes for å snakke med API og serializere JSON-respons.
Mapbox brukes for å vise kart.
Io.github.dautovicharis:charts brukes for å vise grafer for klimastatistikk.
Coil brukes for å laste og vise bilder
