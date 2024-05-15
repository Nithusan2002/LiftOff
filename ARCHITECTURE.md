# Arkitekturbeskrivelse

## Introduksjon
Denne filen gir en oversikt over arkitekturen som vi har benyttet i Liftoff. Det inkluderer forklaringer av viktige objektorienterte prinsipper, designmønstre og teknologier som er brukt i appen vår.

## Objektorienterte prinsipper
Vi har som beskrevet i oppgaven, lagt vekt på å følge viktige objektorienterte prinsipper som lav kobling og høy kohesjon.

- **Lav kobling**: Komponentene vi har i systemet er løst koblet fra hverandre. Dette betyr at endringer i en komponent ikke har en stor innvirkning på andre komponenter i systemet. Dette oppnås gjennom bruk av grensesnitt og når vi tar i bruk et repository for å hente data og presentere det til View. 
  
- **Høy kohesjon**: Hvert komponent eller klasse har et klart definert ansvar og fokuserer kun på å oppfylle dette på best mulig måte. Dette sørger for økt forståelse og vedlikeholdbarhet av koden. Vi har organisert koden med ulike pakker for å følge Model-View-ViewModel mønsteret. Vi har delt inn i model, data, og ui pakker som der igjen er delt inn i mindre pakker der hver fil har et klart definert ansvar. 

## Designmønstre
Vi har benyttet oss av designmønstre som MVVM (Model-View-ViewModel) og UDF (Unified Data Flow) for å strukturere koden.

- **MVVM**: MVVM-mønsteret er brukt for å separere brukergrensesnittet (View) fra forretningslogikken (ViewModel) og data (Model). Dette gjør det enklere å teste og vedlikeholde koden, samtidig som det gir en mer skalerbar og fleksibel arkitektur. Ansvaret til View er å presentere state og muligjøre brukerinteraksjon i tillegg til å observere state i viewmodel. ViewModel sitt ansvar er å presentere state til view, og også oppdatere state. Dette innebærer blant annet å starte henting av data og også reagere på brukerinteraksjon som gjør at vi vil endre state. Model sitt ansvar er å hente og behandle data, og presentere denne dataen til viewmodel.

- **UDF**: UDF (Unified Data Flow) er et mønster som fokuserer på å ha en enveis dataflyt gjennom applikasjonen vår. Dette bidrar til å redusere kompleksiteten og gjøre det lettere å forstå hvordan data flyter gjennom systemet. Dette fungerer ved at tilstander flyter ned og hendelser flyter opp. Brukergrensesnittet genererer en hendelse og sender den oppover til state som deretter kan endre den, eller ikke, også sendes tilstanden ned igjen til UI. 

## Løsningen for drift, vedlikehold og videreutvikling
For brukere som skal jobbe med drift, vedlikehold og videreutvikling av løsningen, gir vi følgende oversikt over teknologier, arkitektur og API-nivå som er brukt:

- **Teknologier**: Vi har hovedsaklig benyttet oss av Kotlin som hovedspråk i appen og Jetpack Compose for å bygge UI-et. Vi bruker også en Google Maps SDK for å tilby brukerne bruk av kart i appen.

- **Arkitektur**: Appen følger en MVVM-arkitektur (Model-View-ViewModel) for å separere brukergrensesnittet fra forretningslogikken og data. Den har også en enveis dataflyt gjennom hele applikasjonen for å oppfylle kravet om UDF. 

- **API**: Vi har brukt API-ene LocationForecast og IsobaricGrib fra MET. Locationforecast går gjennom Ifi-proxyen og IsobaricGrib hentes via en backend-server for å parse data.

- **API-nivå**: Vi har valgt API-nivå 26 som minimum API-nivå. Dette valgte vi fordi det er det laveste API-nivået som appen kan kjøre med fordi vi bruker klasser fra biblioteker som krever minimum API-nivå 26. Klassene det gjelder er LocalDateTime og LocalDate. Ellers har vi prøvd å holde minimum API-nivå lavt for at appen skal være kompatibel med så mange enheter som mulig.

