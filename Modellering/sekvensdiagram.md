```mermaid    
sequenceDiagram %% Sekvensdiagram for å hente vindstyrke (eksempel)
        actor Bruker
        participant UI
        participant ViewModel
        participant Repository
        participant DataSource
        participant MET_API
        participant GRIB_API

        Bruker->>UI: Velger lokasjon og tidspunkt

        UI->>ViewModel: hentVindData(tid, sted)

        ViewModel->>ViewModel: hentHøyde(sted)

        alt høyde < 10 m
        
            ViewModel-->>Repository: hentVindData(tid, sted, met_api)
            Repository-->>DataSource: hentVindData(tid, sted, met_api)
            DataSource-->>MET_API: hentVindData(tid, sted)
            alt suksess
                MET_API-->>DataSource: data
                DataSource-->>ViewModel: data
            else feil
                DataSource-->>ViewModel: Feilmelding
            end

        else høyde > 10 m
            ViewModel-->>Repository: hentVindData(tid, sted, grib_api)
            Repository-->>DataSource: hentVindData(tid, sted, girb_api)
            DataSource-->>GRIB_API: hentVindData(tid, sted)
            alt suksess
                GRIB_API-->>DataSource: data
                DataSource-->>ViewModel: data
            else feil
                DataSource-->>ViewModel: Feilmelding
            end

        end
        alt suksess
            ViewModel-->>ViewModel: updateWind(data)
            UI-->>Bruker: Viser data
        else feil
            ViewModel-->>UI: showSnackbar(feilmelding)

        end
        