# Use-case diagram

```mermaid
flowchart LR
    Bruker(["<br/>Bruker"])
    MET(["<br/>MET Frost API<br/>(klimadata)"])

    subgraph System["Blomster & Bier"]
        UC1(["Se tiltak for hagen<br/>mot styrtregn"])
        UC2(["Se anbefalte planter<br/>for lokalt klima"])
        UC3(["Se klimastatistikk<br/>for valgt sted"])
        UC0(["Velg lokasjon"])

        UC2 -. "«include»" .-> UC0
        UC3 -. "«include»" .-> UC0
    end

    Bruker --- UC1
    Bruker --- UC2
    Bruker --- UC3
    Bruker --- UC0

    UC2 --- MET
    UC3 --- MET

    classDef actor fill:#fff,stroke:#333,stroke-width:2px,color:#000
    classDef usecase fill:#e8f4ff,stroke:#1f6feb,stroke-width:1.5px,color:#000
    classDef shared fill:#f5f5f5,stroke:#666,stroke-width:1.5px,color:#000
    class Bruker,MET actor
    class UC1,UC2,UC3 usecase
    class UC0 shared
```
Diagrammet er valg for å illustrere de viktigste use-cases for vår app. Her har vi valgt å inkludere 4 use cases:

0. Velg lokasjon
1. Se tiltak for hagen mot styrtregn
2. Se klimastatistikk for valgt sted
3. Se anbefalte planter for lokalt klima
    


# Use Case 0 – Velg lokasjon (seksvensdiagram)

```mermaid
sequenceDiagram
    actor Bruker
    participant Kart as MapScreen
    participant MapVM as MapViewModel
    participant WeatherRepo as WeatherRepository
    participant Frost as MET Frost API
    participant ClimateVM as ClimateStatisticsViewModel
    participant PlantVM as PlantViewModel

    Bruker->>Kart: 1. Åpner MapScreen
    Bruker->>Kart: 2. Trykker på kartet eller søker på adresse
    Kart->>MapVM: Plasser pin på valgt punkt
    MapVM->>WeatherRepo: 3. Hent stedsnavn for koordinat
    WeatherRepo->>Frost: Forespør stedsnavn
    Frost-->>WeatherRepo: Stedsnavn
    WeatherRepo-->>MapVM: Stedsnavn
    MapVM-->>Kart: Viser pin og stedsnavn

    Bruker->>Kart: 4. Trykker «Last inn data for [stedsnavn]»
    Kart->>ClimateVM: 5. Hent klimadata for lokasjon
    Kart->>PlantVM: 5. Hent anbefalte planter for lokasjon

    ClimateVM->>WeatherRepo: Be om klimadata
    WeatherRepo->>Frost: Forespør klimadata
    Frost-->>WeatherRepo: Klimadata
    WeatherRepo-->>ClimateVM: Klimadata

    PlantVM->>WeatherRepo: Be om klimadata
    WeatherRepo->>Frost: Forespør klimadata
    Frost-->>WeatherRepo: Klimadata
    WeatherRepo-->>PlantVM: Klimadata

    Kart-->>Bruker: Viser snarveier til klimastatistikk, planter, bier og tiltak
```

Use case 0 (inkludert i Use case 2 og 3): Velg lokasjon
1.	Brukeren åpner kartskjermen
2.	Brukeren velger et sted ved å trykke på kartet eller søke på en adresse
3.	Systemet plasserer en pin på valgt punkt og henter stedsnavn fra MET Frost
4.	Brukeren bekrefter valget ved å trykke «Last inn data for [stedsnavn]»
5.	Systemet henter klimadata for det valgte stedet i bakgrunnen og åpner opp snarveier til de andre skjermene (klimastatistikk, planteskjerm, tiltak mot styrtregn og tiltak for bier)

Vi har valgt å bruke sekvensdiagrammer for å illustrere de viktige use-cases (illustrert ovenfor). Dette er for å gi et bedre innblikk i hvordan programmet håndterer disse.


# Use Case 1 – Se tiltak for hagen mot styrtregn (seksvensdiagram)

```mermaid
sequenceDiagram
    actor Bruker
    participant Tiltak as Tiltaksskjerm
    participant ActionsVM as ActionsViewModel
    participant RecRepo as RecommendationRepository
    participant LokalData as Lokal datakilde (tiltaksfil)
    participant Detalj as Detaljskjerm

    Bruker->>Tiltak: 1. Åpner tiltaksskjermen
    Tiltak->>ActionsVM: Be om tiltak
    ActionsVM->>RecRepo: 2. Hent forhåndsdefinerte tiltak
    RecRepo->>LokalData: Les inn tiltaksfil
    LokalData-->>RecRepo: Liste av tiltak
    RecRepo-->>ActionsVM: Liste av tiltak
    ActionsVM-->>Tiltak: 3. Viser tiltakene som kort med bilde og tittel
    Tiltak-->>Bruker: Liste av tiltakskort

    Bruker->>Tiltak: 4. Trykker på et tiltakskort
    Tiltak->>Detalj: Åpne valgt tiltak
    Detalj-->>Bruker: 5. Viser beskrivelse og fremgangsmåte
```

Use case 1: Se tiltak for hagen mot styrtregn
1.	Brukeren åpner tiltaksskjermen 
2.	Systemet leser inn de forhåndsdefinerte tiltakene fra appens lokale data
3.	Systemet viser tiltakene som en liste av kort med bilde og tittel
4.	Brukeren trykker på et tiltakskort
5.	Systemet viser en detaljskjerm med beskrivelse og fremgangsmåte for det valgte tiltaket




# Use Case 2 - Se klimastatistikk for valgt sted (seksvensdiagram)

```mermaid
sequenceDiagram
    actor Bruker
    participant Kart as MapScreen
    participant ClimateVM as ClimateStatisticsViewModel
    participant ClimateRepo as ClimateProfileRepository
    participant WeatherRepo as WeatherRepository
    participant Frost as MET Frost API
    participant KlimaSkjerm as Klimastatistikkskjerm

    Note over Bruker, Kart: 1. Bruker velger lokasjon (se Use case 0)

    Kart->>ClimateVM: Last inn klimastatistikk for valgt lokasjon
    ClimateVM->>ClimateRepo: 2. Be om normal sommernedbør (1961–1990 og 1991–2020), <br/> styrtregn per år og total årsnedbør
    ClimateRepo->>WeatherRepo: Hent klimadata
    WeatherRepo->>Frost: Forespør klimadata
    Frost-->>WeatherRepo: Klimadata
    WeatherRepo-->>ClimateRepo: Klimadata
    ClimateRepo->>ClimateRepo: 3. Beregn trender og regresjonslinjer
    ClimateRepo-->>ClimateVM: Klimadata med trender

    Bruker->>KlimaSkjerm: 4. Navigerer til klimastatistikkskjermen
    KlimaSkjerm->>ClimateVM: Hent gjeldende klimastatistikk
    ClimateVM-->>KlimaSkjerm: Klimadata og trender
    KlimaSkjerm-->>Bruker: Viser klimastatistikk for valgt lokasjon

    Bruker->>KlimaSkjerm: 5. Bytter mellom linje- og søylediagram <br/> og blar mellom grafer
    KlimaSkjerm-->>Bruker: Oppdatert visning av graf
```

Use case 2: Se klimastatistikk for valgt sted
1.	Brukeren velger lokasjon (se Use case 0)
2.	Systemet henter klimadata fra MET Frost for det valgte stedet: normal sommernedbør for to perioder, antall timer styrtregn per år og total årsnedbør de siste årene
3.	Systemet beregner trender og regresjonslinjer for nedbør og styrtregn
4.	Brukeren navigerer til klimastatistikkskjermen, og systemet viser klimastatistikk for valgt lokasjon
5.	Brukeren kan bytte mellom linje- og søylediagram og bla mellom grafene for sommernedbør, styrtregn og årsnedbør



# Use Case 3 - Se anbefalte planter for lokalt klima (seksvensdiagram)
```mermaid
sequenceDiagram
    actor Bruker
    participant Kart as Kartskjerm
    participant PlantVM as PlantViewModel
    participant ClimateRepo as ClimateProfileRepository
    participant WeatherRepo as WeatherRepository
    participant Frost as MET Frost API
    participant PlanteData as Lokal plantedata
    participant PlanteSkjerm as PlantScreen

    Note over Bruker, Kart: 1. Bruker velger lokasjon (se Use case 0)

    Kart->>PlantVM: Last inn anbefalte planter for valgt lokasjon
    PlantVM->>ClimateRepo: Hent anbefalte planter for lokasjon
    ClimateRepo->>WeatherRepo: 2. Be om sommernedbør for 1961–1990 og 1991–2020
    WeatherRepo->>Frost: Forespør sommernedbør
    Frost-->>WeatherRepo: Sommernedbør for begge perioder
    WeatherRepo-->>ClimateRepo: Sommernedbør for begge perioder
    ClimateRepo->>ClimateRepo: 3. Sammenligner periodene og <br/> klassifiserer klimaet som tørrere, våtere eller stabilt
    ClimateRepo->>PlanteData: 4. Be om planter som passer klimatrenden
    PlanteData-->>ClimateRepo: Filtrert liste av planter
    ClimateRepo-->>PlantVM: Klimatrend og anbefalte planter

    Bruker->>PlanteSkjerm: 5. Navigerer til Plantscreen
    PlanteSkjerm->>PlantVM: Hent anbefalte planter
    PlantVM-->>PlanteSkjerm: Stedsnavn, klimatrend og planteliste
    PlanteSkjerm-->>Bruker: Viser stedsnavn, klimatrend og planteliste

    Bruker->>PlanteSkjerm: 6. Utvider et plantekort
    PlanteSkjerm-->>Bruker: Viser detaljert informasjon om planten
```

Use case 3: Se anbefalte planter for lokalt klima
1.	Brukeren velger lokasjon (se Use case 0)
2.	Systemet henter normaler for sommernedbør for periodene 1961–1990 og 1991–2020 fra MET Frost
3.	Systemet sammenligner periodene og klassifiserer klimaet på stedet som tørrere, våtere eller stabilt
4.	Systemet velger ut planter som passer til den beregnede klimatrenden
5.	Brukeren navigerer til planteskjermen, og systemet viser stedsnavn, klimatrend og en liste med anbefalte planter
6.	Brukeren kan utvide et plantekort for å se mer informasjon om en plante



# Klassediagram for use-cases og sekvensdiagrammer

```mermaid
classDiagram
    direction LR

    %% =====================
    %% UI-laget (skjermer)
    %% =====================
    class MapScreen {
        <<Composable>>
    }
    class ClimateScreen {
        <<Composable>>
    }
    class PlantScreen {
        <<Composable>>
    }
    class ActionsScreen {
        <<Composable>>
    }
    class ActionsInfoScreen {
        <<Composable>>
    }

    %% =====================
    %% ViewModel-laget
    %% =====================
    class MapViewModel {
        -mapState: MapUiState
        +placePin(long, lat)
        +getName(long, lat)
        +confirmSelection(long, lat)
        +clearLocation()
        +markDataLoaded()
    }
    class MapUiState {
        +location: String?
        +pinLongitude: Double?
        +pinLatitude: Double?
        +selectedLongitude: Double?
        +selectedLatitude: Double?
        +screenState: MapScreenState
    }
    class MapScreenState {
        <<enum>>
        NoSelection
        PinPlaced
        DataLoaded
    }

    class AddressSearchViewModel {
        +onQueryChanged(query)
        +clearResults()
    }
    class Address {
        +addressText: String
        +lat: Double
        +lon: Double
    }

    class ClimateStatisticsViewModel {
        -climateStatisticsUiState: ClimateStatisticsUiState
        +loadClimateStatistics(long, lat)
        +setLocationName(name)
    }
    class ClimateStatisticsUiState {
        +earlySummerPrecipitation: Int?
        +lateSummerPrecipitation: Int?
        +yearlyHeavyRain: List
        +yearlyPrecipitation: List
        +summerPrecipitationTrend: ClimateSignal
        +heavyRainTrend: ClimateSignal
        +location: String?
    }

    class PlantViewModel {
        -uiState: PlantUiState
        +loadPlants(long, lat)
        +setLocationName(name)
    }
    class PlantUiState {
        +plants: List~PlantEntry~
        +climateSignal: ClimateSignal?
        +location: String?
    }

    class ActionsViewModel {
        -actionsUiState: ActionsUiState
        +getMoreActionInfo()
    }
    class ActionsUiState {
        +action: List~ActionInfo~
    }

    %% =====================
    %% Domain / Repository
    %% =====================
    class ClimateProfileRepository {
        +getEarlySummerPrecipitation(long, lat)
        +getLateSummerPrecipitation(long, lat)
        +getHeavyRain(long, lat)
        +getYearlyPrecipitation(long, lat)
        +calculateSummerPrecipitationTrend(long, lat)
        +calculateHeavyRainTrend(long, lat)
        +fetchPlants(long, lat)
    }

    class RecommendationRepository {
        +getActions(): List~ActionInfo~
        +getCurrentRecommendedPlants(long, lat)
    }

    class WeatherRepository {
        <<interface>>
        +getName(long, lat)
        +getSummerPrecipitation(...)
        +getYearlyPrecipitation(...)
        +getYearlyHeavyRain(...)
    }
    class WeatherRepositoryImplementation

    %% =====================
    %% DataSource-laget
    %% =====================
    class FrostDataSource {
        <<interface>>
        +fetchObservations(...)
        +fetchWeatherstation(...)
        +fetchClimateNormals(...)
        +getLocation(long, lat)
    }
    class FrostDataSourceImplementation

    class ActionsDataSource {
        +fetchActions(): String
    }

    class PlantDatabase {
        <<object>>
        +getPlantsForSignals(signal): List~PlantEntry~
    }

    %% =====================
    %% Modellklasser
    %% =====================
    class ActionInfo {
        +id: String
        +title: String
        +description: String
        +procedure: String
        +img: String
    }
    class PlantEntry {
        +id: String
        +name: String
        +latinName: String
        +climateSignals: List~ClimateSignal~
        +goodForBees: Boolean
    }
    class ClimateSignal {
        <<enum>>
        DRY_SUMMER
        WET_SUMMER
        HEAVY_RAIN
        STABLE
    }

    %% =====================
    %% Eksternt system
    %% =====================
    class MET_Frost_API {
        <<external>>
    }

    %% =====================
    %% Relasjoner
    %% =====================

    %% Skjerm bruker ViewModel
    MapScreen --> MapViewModel
    MapScreen --> AddressSearchViewModel
    MapScreen --> ClimateStatisticsViewModel : trigger lasting
    MapScreen --> PlantViewModel : trigger lasting
    ClimateScreen --> ClimateStatisticsViewModel
    PlantScreen --> PlantViewModel
    ActionsScreen --> ActionsViewModel
    ActionsInfoScreen --> ActionsViewModel

    %% ViewModel eier UiState
    MapViewModel *-- MapUiState
    MapUiState --> MapScreenState
    AddressSearchViewModel ..> Address : produserer
    ClimateStatisticsViewModel *-- ClimateStatisticsUiState
    PlantViewModel *-- PlantUiState
    ActionsViewModel *-- ActionsUiState

    %% ViewModel bruker repositories
    MapViewModel --> WeatherRepository
    ClimateStatisticsViewModel --> ClimateProfileRepository
    PlantViewModel --> ClimateProfileRepository
    ActionsViewModel --> RecommendationRepository

    %% Repository-avhengigheter
    ClimateProfileRepository --> WeatherRepository
    ClimateProfileRepository ..> PlantDatabase : filtrerer planter
    RecommendationRepository --> ActionsDataSource
    WeatherRepositoryImplementation ..|> WeatherRepository
    WeatherRepositoryImplementation --> FrostDataSource

    %% DataSource-avhengigheter
    FrostDataSourceImplementation ..|> FrostDataSource
    FrostDataSourceImplementation --> MET_Frost_API : HTTP

    %% Modellbruk
    ActionsUiState --> ActionInfo
    PlantUiState --> PlantEntry
    PlantUiState --> ClimateSignal
    PlantEntry --> ClimateSignal
    ClimateStatisticsUiState --> ClimateSignal
    ClimateProfileRepository ..> ClimateSignal : returnerer
```
Vi har valgt å inkludere et klassediagrm for å gi en oversikt over de viktigste klassene som inngår i use-cases vi har presentert ovenfor. 



```mermaid 
flowchart TD
    Start([Bruker åpner appen]) --> Map[MapScreen vises]

    Map --> Choice{Hva vil brukeren gjøre?}

    Choice -->|Se tiltak mot styrtregn| Actions[ActionsScreen]
    Choice -->|Velg lokasjon på kartet| PlacePin[Trykk på kart<br/>eller søk på adresse]

    PlacePin --> PinPlaced[Pin plasseres<br/>og stedsnavn hentes fra MET Frost]
    PinPlaced --> Confirm{Bekrefte lokasjon?}
    Confirm -->|Nei, velg nytt sted| PlacePin
    Confirm -->|Ja: «Last inn data for stedet»| Fetch[Hent klimadata fra MET Frost<br/>i bakgrunnen]

    Fetch --> Shortcuts[Snarveier åpnes:<br/>Klimastatistikk, Planter, Bier, Tiltak]

    Shortcuts --> Nav{Hvilken skjerm?}
    Nav -->|Klimastatistikk| Climate[ClimateScreen<br/>Vis sommernedbør, styrtregn<br/>og årsnedbør med trender]
    Nav -->|Anbefalte planter| Plants[PlantScreen<br/>Vis klimatrend og planteliste]
    Nav -->|Tiltak mot styrtregn| Actions

    Climate --> ToggleChart[Bytt mellom linje- og søylediagram<br/>bla mellom grafer]
    ToggleChart --> Climate

    Plants --> ExpandCard[Utvid plantekort<br/>for mer informasjon]
    ExpandCard --> Plants

    Actions --> ActionCard[Trykk på et tiltakskort]
    ActionCard --> ActionDetail[ActionsInfoScreen<br/>Vis beskrivelse og fremgangsmåte]

    Climate -. "Velg ny lokasjon" .-> Map
    Plants -. "Velg ny lokasjon" .-> Map
    Actions -. "Velg ny lokasjon" .-> Map

    classDef screen fill:#e8f4ff,stroke:#1f6feb,stroke-width:1.5px,color:#000
    classDef decision fill:#fff7e6,stroke:#d48806,stroke-width:1.5px,color:#000
    classDef terminator fill:#f5f5f5,stroke:#666,stroke-width:1.5px,color:#000
    class Map,Actions,Climate,Plants,ActionDetail,PinPlaced,PlacePin,Fetch,Shortcuts,ToggleChart,ExpandCard,ActionCard screen
    class Choice,Confirm,Nav decision
    class Start terminator

```
Vi har valgt å implementere et flytdiagram for å gi et mindre teknisk--og mer tilgjengelig--overblikk over vår applikasjons viktigste use-cases. 


