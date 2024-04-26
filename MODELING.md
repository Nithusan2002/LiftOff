# Sequencediagram 

```mermaid
sequenceDiagram
    actor User
    participant UI
    participant ViewModel
    participant Repository
    participant DataSource
    participant LocationForecast
    participant IsobaricGRIB

    User->>UI: Choosing location, time and altitide

    UI->>ViewModel: fetchLocationForecast(lat, lon, alt, time)
    UI->>ViewModel: fetchIsobaricGrib(time)
    
    
    alt LocationForecast Success
        ViewModel->>Repository: fetchLocationForecast(lat, lon, alt, time)
        Repository->>DataSource: fetchLocationForecast(lat, lon, alt, time)
        DataSource->>LocationForecast: fetchLocationForecast(lat, lon, alt, time)

        LocationForecast-->>DataSource: Data
        DataSource-->>ViewModel: Data
        
    else LocationForecast Error
        LocationForecast-->>DataSource: Error
        DataSource-->>ViewModel: Error
    end

    alt IsobaricGRIB Success
        IsobaricGRIB-->>DataSource: Returns IsobaricGRIB data
        DataSource-->>ViewModel: Data
    else IsobaricGRIB Error
        IsobaricGRIB-->>DataSource: Error
        DataSource-->>ViewModel: Error
    end

    alt Success
        UI-->>User: Show data 

    else Error
        ViewModel-->>UI: Show snackbar

    end
```
    
# Use Case

## Textual description of the use case

**Primary actor**: User: A person who wants to obtain a weather forecast for a specific location, time, and altitude to check if it is possible to launch a rocket. <br>
**Precondition**: The user must have access to the application, and the UI must be ready to receive user input. <br>
**Postcondition**: The user has been presented with the desired information for the selected location, time, and altitude, along with a result indicating whether it is possible or not to launch a rocket. <br>

**Main flow**:
1. LOREM IPSUM
2. LOREM IPSUM
3. LOREM IPSUM
4. LOREM IPSUM
5. LOREM IPSUM

**Alternative flow**: <br>
X.1: LOREM IPSUM <br>
X.2: LOREM IPSUM <br>
X.3: LOREM IPSUM <br>
