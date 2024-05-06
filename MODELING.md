# Sekvensdiagram 

```mermaid
sequenceDiagram
    actor Bruker
    participant UI
    participant ViewModel
    participant WeatherDataRepository
    participant DataSource
 
    
    
    Bruker->>UI: Velger sett med koordinater og høyde
    UI->>UI: Validererer info
   
    UI->>ViewModel: fetchData(lat, lon, alt, date, hour)
    ViewModel->>WeatherDataRepository: fetchData(lat, lon, alt, date, hour)
    WeatherDataRepository->>DataSource: fetchData(lat, lon, alt, date, hour)

    alt Sukksessful fetching
        DataSource -->> ViewModel: Data
        ViewModel -->> UI: Data
        
        alt Godkjent for oppskytning
            ViewModel -->> UI: Viser data og hake symbol
        else Ikke godkjent for oppskytning
            ViewModel -->> UI: Viser data og X symbol 
        end


    else Ikke suksessfull fetching
        DataSource -->> ViewModel: Feil
        ViewModel -->>UI: ShowSnackbar(Feilmelding)
    end
```
    
# Use Case 1

## Tekstlig beskrivelse av use case

**Primæraktør**: Brukeren: En person som ønsker å få en værmelding for et bestemt sted, tidspunkt og høyde for å sjekke om det er mulig å skyte opp en rakett. <br>
**Prebetingelse**: Brukeren må ha tilgang til applikasjonen, og UI-et må være klart til å motta brukerinput. <br>
**Postbetingelse**: Brukeren har blitt presentert med den ønskede informasjonen for det valgte stedet, tidspunktet og høyden, sammen med et resultat som indikerer om det er mulig eller ikke å skyte opp en rakett. <br>

**Hovedflyt**:
<ol>1. Bruker velger et sett med koordinater, tidspunkt og, hvis ønsket, høyde. Trykker på søk.</ol>
<ol>2. Systemet validerer koordinatene.</ol>
<ol>3. Systemet henter værdata.</ol>
<ol>4. Returnerer værdataene for de angitte opplysningene.</ol>
<ol>5. Viser et hake-symbol hvis det er mulig å skyte opp en rakett.</ol>

**Alternativ flyt**: <br>
<ol>2.1: Koordinatene blir ikke validert</ol>
<ol>2.2: Bruker skriver inn gyldige koordinater på nytt</ol>
<ol>3.1 Systemet mislykkes i å hente data</ol>
<ol>3.2 Returnerer feil</ol>
<ol>3.2 Viser snackbar til brukeren</ol>
<ol>5.1 Viser et X-merke hvis det ikke er mulig å skyte opp en rakett</ol>

# Aktivitetsdiagram
``` mermaid
flowchart TD
    style A fill:#f9f,stroke:#333,stroke-width:4px
    style Å fill:#f9f,stroke:#333,stroke-width:4px

    A[Start] --> B[Skriver inn koordinater og høyde]
    B -->C[Validerer søkekriterier]
    C --> D{ }
    D ----> |Søkekriterier ok| E[Henter værdata]
    D ---> |Søkekriterier ikke ok| F[Bruker får oppgitt at det er ugyldig] 
    F --> M{ }
    M --> |Bruker skriver nye koordinater og høyde| B
    M --> |Bruker velger å ikke prøve på nytt| Å
    E --> G[Viser data] --> H{ }
    H --> |Godkjent for utskytning| Å
    H --> |Ikke godkjent for utskytning| K{ }
    K --> |Bruker velger ny dato og tidspunkt| E
    K --> |Velger å ikke velge ny dato og tidspunkt|Å

    Å[SLUTT]
```
