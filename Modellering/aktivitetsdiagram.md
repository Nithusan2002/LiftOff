```mermaid
    flowchart TD
    %% Eksempel på å hente data ved å velge tid og sted
    Start((Start))
    
    StartValg{Start med å velge}
    VelgSted([Velg sted])
    VelgTid([Velg tid])
    
    Kart([Peker på sted på kart])
    TidTilSted{Gå til stedvalg}
    StedTilTid{Gå til tidvalg}
    
    riktigTid{Er tid innenfor \n dataprognosene?}
    apiTilgjengelig{Er API tilgjengelig?}
    
    HentData([Hent data])
    VisData([Vis data])
    TidFeilmelding([Vis feilmelding om tid])
    
    Slutt((Slutt))
    
    Start --> StartValg
    StartValg --Velg sted--> VelgSted --> Kart --> StedTilTid
    StartValg --Velg tid--> VelgTid --> TidTilSted
    
    TidTilSted --> VelgSted
    StedTilTid --> VelgTid
    
    VelgTid --> riktigTid
    riktigTid --JA--> apiTilgjengelig
    apiTilgjengelig --JA--> HentData
    apiTilgjengelig --NEI--> TidFeilmelding
    riktigTid --NEI--> TidFeilmelding
    
    TidFeilmelding --> VelgTid
    
    HentData --> VisData
    VisData --> Slutt
