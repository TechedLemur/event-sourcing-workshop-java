# Del 2

I denne delen skal vi implementere funksjonaliteten for å lese ut tilstanden til handlekurven fra EventStore.
Vi skal også se på et av de unike elementene i Event Sourcing; å lage et snapshot av tilstanden på et vilkårlig tidspunkt.

## Oppgave - Lese ut tilstanden til handlekurven

Vi skal nå implementere `GET /cart/:id` endepunktet ferdig ved å gjøre ferdig `applyEvent`-metoden i [Cart](../src/main/java/com/eventsourcing/workshop/models/Cart.java).

Acceptance criteria:

- `GET /cart/:id` skal lese ut tilstanden til handlekurven fra EventStore.
- Handlekurven i Frontend skal vise nye produkter som legges til i handlekurven.
- Når man trykker på Trash-ikonet, skal produktet fjernes fra handlekurven i UI.

Din oppgave er å implementere funksjonen `applyEvent` i [Cart](../src/main/java/com/eventsourcing/workshop/models/Cart.java) som brukes av `getCart` i [CartService](../src/main/java/com/eventsourcing/workshop/services/CartService.java).

Dette er en av kjernene i Event Sourcing; å kunne lese ut tilstanden til en aggregat basert på eventene som har skjedd. Ta gjerne en kikk på `applyEvent`-metoden i [Product](../src/main/java/com/eventsourcing/workshop/models/Product.java) for inspirasjon.

## Tidsreiser

Vi skal nå se på magien i Event Sourcing; å kunne gå tilbake i tid og se hva som skjedde på et tidspunkt i fortiden.

Aktiver "Time Travel" i høyre hjørne på toppen av siden. Nå skal du kunne bruke denne for å spole frem og tilbake og se hvordan tilstanden til handlekurven har endret seg over tid.

Ta en kikk på `getCart`-metoden i [CartService](../src/main/java/com/eventsourcing/workshop/services/CartService.java) og se hvordan `maxCount`-parameteren brukes til å få denne funksjonaliteten.

Workshoppen fortsetter i [Del 3](part3.md).
