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

**MVVM**: MVVM-mønsteret er brukt for å separere brukergrensesnittet (View) fra forretningslogikken (ViewModel) og data (Model). Dette gjør det enklere å teste og vedlikeholde koden, samtidig som det gir en mer skalerbar og fleksibel arkitektur. Ansvaret til View er å presentere state og muligjøre brukerinteraksjon i tillegg til å observere state i viewmodel. ViewModel sitt ansvar er å presentere state til view, og også oppdatere state. Dette innebærer blant annet å starte henting av data og også reagere på brukerinteraksjon som gjør at vi vil endre state. Model sitt ansvar er å hente og behandle data, og presentere denne dataen til viewmodel.

**UDF**: UDF (Unified Data Flow) er et mønster som fokuserer på å ha en enveis dataflyt gjennom applikasjonen vår. Dette bidrar til å redusere kompleksiteten og gjøre det lettere å forstå hvordan data flyter gjennom systemet. Dette fungerer ved at tilstander flyter ned og hendelser flyter opp. Brukergrensesnittet genererer en hendelse og sender den oppover til state som deretter kan endre den, eller ikke, også sendes tilstanden ned igjen til UI. 

## Løsningen for drift, vedlikehold og videreutvikling
For lesere som skal jobbe med drift, vedlikehold og videreutvikling av løsningen, gir vi følgende oversikt over teknologier, arkitektur og API-nivå som er brukt:

- **Teknologier**: Vi har benyttet oss av Kotlin som hovedspråk for utviklingen av Android-appen og Jetpack Compose for å bygge UI-en. //TODO er det noe mer som skal her?

- **Arkitektur**: Appen følger en MVVM-arkitektur (Model-View-ViewModel) for å separere brukergrensesnittet fra forretningslogikken og data. Den har også en enveis dataflyt gjennom hele applikasjonen i tråd med UDF-mønsteret.

- **API-nivå**: Vi har valgt API-nivå 26 som minimum API-nivå. Dette valgte vi fordi det er det laveste API-nivået som appen kan kjøre med fordi vi bruker klasser fra biblioteker som krever minimum API-nivå 26. Klassene det gjelder er LocalDateTime og LocalDate. Ellers har vi prøvd å holde minimum API-nivå lavt for at appen skal være kompatibel med så mange enheter som mulig.

