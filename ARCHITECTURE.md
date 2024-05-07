Må endres

# Arkitekturbeskrivelse

## Introduksjon
Dette dokumentet gir en oversikt over arkitekturen som er benyttet i appen. Det inkluderer beskrivelser av viktige objektorienterte prinsipper, designmønstre og teknologier som er brukt i løsningen.

## Objektorienterte prinsipper
Vi har lagt vekt på å følge viktige objektorienterte prinsipper som lav kobling og høy kohesjon i vår løsning.

- **Lav kobling**: Komponentene i systemet er løst koblet fra hverandre, noe som betyr at endringer i en komponent ikke har en uforholdsmessig stor innvirkning på andre komponenter. Dette oppnås gjennom bruk av grensesnitt og avhengighetsinjeksjon.
  
- **Høy kohesjon**: Hver komponent eller klasse har et klart definert ansvar og fokuserer på å oppfylle dette ansvaret på en effektiv måte. Dette bidrar til økt forståelse og vedlikeholdbarhet av koden.

## Designmønstre
Vi har benyttet oss av designmønstre som MVVM (Model-View-ViewModel) og UDF (Unified Data Flow) for å organisere og strukturere koden på en hensiktsmessig måte.

- **MVVM**: MVVM-mønsteret er brukt for å separere brukergrensesnittet (View) fra forretningslogikken (ViewModel) og data (Model). Dette gjør det enklere å teste og vedlikeholde koden, samtidig som det gir en mer skalerbar og fleksibel arkitektur.

- **UDF**: UDF (Unified Data Flow) er et mønster som fokuserer på å ha en enveis dataflyt gjennom applikasjonen. Dette bidrar til å redusere kompleksiteten og gjøre det lettere å forstå hvordan data flyter gjennom systemet.

## Løsningen for drift, vedlikehold og videreutvikling
For lesere som skal jobbe med drift, vedlikehold og videreutvikling av løsningen, gir vi følgende oversikt over teknologier, arkitektur og API-nivå som er brukt:

- **Teknologier**: Vi har benyttet oss av Kotlin som hovedspråk for utviklingen av Android-appen. I tillegg har vi brukt Android Jetpack-biblioteker som LiveData, ViewModel og Room for å implementere MVVM-arkitekturen. Retrofit er brukt for nettverkskommunikasjon, og Dagger Hilt for avhengighetsinjeksjon.

- **Arkitektur**: Appen følger en MVVM-arkitektur (Model-View-ViewModel) for å separere brukergrensesnittet fra forretningslogikken og data. Den har også en enveis dataflyt gjennom hele applikasjonen i tråd med UDF-mønsteret.

- **API-nivå**: Vi har valgt å målrette appen vår mot et lavt API-nivå, for eksempel API 21 (Android 5.0 Lollipop) eller nyere, for å sikre kompatibilitet med et bredt spekter av enheter og samtidig utnytte funksjonaliteter og forbedringer som er tilgjengelige i nyere versjoner av Android.
