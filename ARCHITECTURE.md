Blomster og Bier er utviklet i tråd med MVVM og UDF. Mappestrukturen i prosjektet underbygger dette med packages: ui, domain og data. Under ui ligger både skjermer og viewmodels, mens data inneholder repositories og datasources. Nedenfor er disse beskrevet i større detalj.

På øverste nivå har vi UI-laget. Her ligger kode for hva brukergrensesnittet viser. Dette inkluderer skjermer, composables, kart, addressebar og tema for utseende. Skjermene henter data ved å observere uiState i tilhørende ViewModels. 

Viewmodels ligger i samme pakke som skjermene de tilbyr data til. De har en intern dataklasse som inneholder data i tillegg til variabler for loading og error. Disse dataklassene blir oppdatert når funksjonen for henting av data kjøres. Innlastingsfunksjonene starter korutiner for å kommunisere med repositories. Når korutinen er ferdig oppdateres en privat stateFlow, som igjen blir eksponert til en offentlig uiState som skjermene kan observere.

Domenelaget har en klasse ClimateProfile. Denne kaller kobler sammen repository for klima, planter og utfører regresjon.

Repositories ligger under datalaget. De har funksjoner som view-modelene kan benytte for å snakke med datapunkter, som Frost API og cache. 

Nederst finner vi datasources. Disse henter ut informasjonen fra blant annet Frost, cache, og tiltak. 

På toppem av arkitekturen ligger det også klasser for navigasjon og initialisering av objekter. 

For å oppnå høy kohesjon har vi gjennom å følge en klar lagdelt arkitektur gitt hver klasse ett bestemt ansvarsområde, og funksjoner som i størst mulig grad utfører én oppgave, og benytter seg av andre funksjoner. Videre har vi arbeidet for å oppnå lav kobling ved å la avhengigheter gå stegvis i koden, for eksempel vil objekter på ui-laget aldri få kontakt med objekter under datasource. Når det gjelder dependency injection oppretter vi de aller fleste instansene av objekter på MainActivity-nivå, og sender de nedover i de aktuelle klassene. På denne måten har vi kun én instans av et objekt å forholde oss til. 

Vi har valgt Android API-nivå 36, ettersom denne er anbefalt for MapBox og bruk av Material3. Dette er også den nyeste stabile versjonen.
