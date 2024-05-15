# Introduksjon

Denne filen gir en god oversikt og beskrivelse av modellene vi har utviklet for appen vår Liftoff. Modellene og diagrammene er laget ved hjelp av Mermaid, som er et verktøy som lar oss visualisere modellene på en god måte. 

# Sekvensdiagram 

```mermaid
sequenceDiagram
    actor User
    participant UI
    participant ViewModel
    participant WeatherDataRepository
    participant DataSources
    
    User->>UI: Velger sett med koordinater og høyde
   
    UI->>ViewModel: fetchData(lat, lon, alt, date, hour)
    ViewModel->>WeatherDataRepository: fetchData(lat, lon, alt, date, hour)
    WeatherDataRepository->>DataSources: fetchData(lat, lon, alt, date, hour)

    alt Suksessrik fetching
        DataSources -->> WeatherDataRepository: Data
        WeatherDataRepository -->> ViewModel: Data
        ViewModel -->> UI: Data
        
        alt Godkjent for oppskytning
            ViewModel -->> UI: Viser data og hake symbol
        else Ikke godkjent for oppskytning
            ViewModel -->> UI: Viser data og X symbol 
        end

    else Ikke suksessrik fetching
        DataSources -->> WeatherDataRepository: Feil
        WeatherDataRepository -->> ViewModel: Feil
        ViewModel -->>UI: ShowSnackbar(Feilmelding)
    end
```
    
# Use Case

<img width="422" alt="image" src="https://media.github.uio.no/user/10213/files/928d49ee-d4e4-45f6-a573-9f6d303ac8e1">

## Tekstlig beskrivelse av use case

**Primæraktør**: Brukeren: En person som ønsker å få en værmelding for et bestemt sted, tidspunkt og høyde for å sjekke om det er mulig å skyte opp en rakett. <br>
**Prebetingelse**: Brukeren må ha tilgang til applikasjonen, og UI-et må være klart til å motta brukerinput. <br>
**Postbetingelse**: Brukeren har blitt presentert med den ønskede informasjonen for det valgte stedet, tidspunktet og høyden, sammen med et resultat som indikerer om det er mulig eller ikke å skyte opp en rakett. <br>

**Hovedflyt**:
<ol>1. Bruker velger et sett med koordinater og høyde hvis ønsket. Trykker på søk.</ol>
<ol>2. Systemet henter værprognose.</ol>
<ol>3. Returnerer værdataene for de angitte søkekriteriene og presenterer dem for bruker.</ol>
<ol>4. Viser et hake-ikon for å vise at værforholdet er egnet for oppskyting.</ol>

**Alternativ flyt**: <br>
<ol>2.1 Systemet mislykkes i å hente data</ol>
<ol>2.2 Returnerer feilmelding</ol>
<ol>2.3 Viser snackbar til brukeren</ol>
<ol>4.1 Viser et X-ikon hvis en eller flere verdier er mer enn 20% over grenseverdiene.</ol>
<ol>4.1.1 Viser en gul varseltrekant hvis en eller flere verdier er mindre enn 20% over grenseverdiene.</ol>


# Aktivitetsdiagram
``` mermaid
flowchart TD
    style Start fill:#f9f,stroke:#333,stroke-width:4px
    style Slutt fill:#f9f,stroke:#333,stroke-width:4px
    
    Start((Start)) 
    Start --> B[Skriver inn koordinater og høyde]
    B --> E[Henter værdata]
    E --> P{ }
    P ---> |Suksessrik fetching| G[Viser data] --> H{ }
    P --> |Ikke suksessrik fetching| T[Viser feilmelding]
    T --> Q{ } --> Avslutter --> Slutt
    Q --> |Prøver igjen| Start
    H --> |Godkjent for utskytning| Slutt
    H ---> |Ikke godkjent for utskytning| K{ }
    K --> |Bruker velger ny dato og tidspunkt| E
    K --> |Bruker velger nye koordinater og høyde|B 
    K ----> |Velger å ikke sette nye kriterier|Slutt

    Slutt((Slutt))
```
