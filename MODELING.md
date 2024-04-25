# Sekvensdiagram 

```mermaid
sequenceDiagram
    actor Bruker
    participant UI
    participant ViewModel
    participant Repository
    participant DataSource
    participant LocationForecast
    participant IsobaricGRIB

    Bruker->>UI: Velger lokasjon, tidspunkt og høyde

    UI->>ViewModel: fetchLocationForecast(lat, lon, alt, time)
    UI->>ViewModel: fetchIsobaricGrib(time)
    
    
    alt LocationForecast suksess
        ViewModel->>Repository: fetchLocationForecast(lat, lon, alt, time)
        Repository->>DataSource: fetchLocationForecast(lat, lon, alt, time)
        DataSource->>LocationForecast: fetchLocationForecast(lat, lon, alt, time)

        LocationForecast-->>DataSource: data
        DataSource-->>ViewModel: data
        
    else LocationForecast feil
        LocationForecast-->>DataSource:Feil
        DataSource-->>ViewModel: Feil
    end

    alt IsobaricGRIB suksess
        IsobaricGRIB-->>DataSource: Returnerer IsobaricGRIB data
        DataSource-->>ViewModel: Data
    else IsobaricGRIB feil
        IsobaricGRIB-->>DataSource: Feil
        DataSource-->>ViewModel: Feil
    end

    alt suksess
        UI-->>Bruker: Viser data 

    else Feil
        ViewModel-->>UI: Viser snackbar

    end
```
    
# Use Case

## Tekstlig beskrivelse av use case
**Navn**: Reserver bil
**Primæraktør**: Kundebehandler
**Sekundæraktør**: -
**Prebetingelse**: Ingen
**Postbetingelse**: Leiekontrakt for spesifisert bil og kunde med gitte utleiedatoer er opprettet

**Hovedflyt**:
1. Kundebehandler velger tidsintervall (hentedato og returdato)
2. Systemet returnerer en liste over tilgjengelige biler innenfor de spesifiserte datoene
3. Kundebehandler velger én av bilene.
4. Systemet ber om kundenr og finner kunden i systemet
5. Systemet bekrefter at bilen er reservert for den gitte perioden

**Alternativ flyt punkt 2*:
2.1: Det finnes ingen tilgjengelige biler i valgt tidsintervall.
2.2. Systemet opplyser om at det ikke er tilgjengelige biler innenfor oppgitt tidsintervall. 2.3. Kundebehandler
oppgir et nytt tidsintervall (steg 1) eller avslutter bruksmønsteret
