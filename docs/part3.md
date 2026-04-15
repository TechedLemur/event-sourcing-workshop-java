# Del 3 - Cart Service V2

Før du begynner på denne oppgaven, trykk på "Shop V2" i frontend navbar.

I denne delen skal vi implementere en projector (også kalt denormalizer) for å bygge tilstanden til handlekurven, og lagre denne i en database. Fordelen med denne tilnærmingen er at når vi gjør `getCart` kan vi lese ut tilstanden til handlekurven fra databasen direkte, uten å måtte lese ut hele cart-streamen fra eventstore for hver eneste request. Etter hvert som antall eventer i streamen vokser, vil det gå tregere og tregere å lese gjennom eventene.

Databasen vil kontinuerlig bli oppdatert med nye eventer som kommer inn i eventstore gjennom en `subscription`. Vi har allerede satt opp subscriptions i [EventSubscriptionRunner.java](../src/main/java/com/eventsourcing/workshop/subscription/EventSubscriptionRunner.java), så du trenger ikke å gjøre dette oppsettet selv.

Det er laget en [ProductServiceV2](../src/main/java/com/eventsourcing/workshop/services/ProductServiceV2.java) som lagrer produkter i databasen, denne kan være nyttig å se på for inspirasjon.

## Oppgave 1 - Handle Cart Event

Vi skal nå håndtere eventer som blir lest ut fra `subscription` av cart-streamen.

Acceptance criteria:

- `handleCartEvent` skal håndtere eventer som kommer inn i eventstore og oppdatere tilstanden til handlekurven i databasen (som i vårt demo-tilfelle er en json-fil). Sjekk [data/workshop-storage.json](../data/workshop-storage.json) for å se hva som er lagret.

Din oppgave er å implementere funksjonen `handleCartEvent` i [CartServiceV2](../src/main/java/com/eventsourcing/workshop/services/CartServiceV2.java). Her må vi først hente ut handlekurven fra databasen, og så oppdatere den basert på eventen. Hvis handlekurven ikke finnes i databasen, skal vi starte med en ny tom handlekurv.

Hint: I del 2 implementerte vi en metode for `Cart`-klassen som vil være nyttig her, som kan brukes for å oppdatere handlekurven.

## Oppgave 2 - Get Cart V2

Nå som vi har lagret handlekurven i databasen, kan vi implementere funksjonen for å lese ut handlekurven.

Acceptance criteria:

- `GET /cart/v2/:id` skal lese ut tilstanden til handlekurven fra databasen.
- Handlekurven i Frontend skal vise nye produkter som legges til i handlekurven.
- Når man trykker på Trash-ikonet, skal produktet fjernes fra handlekurven i UI.

Din oppgave er å implementere funksjonen `getCart` i [CartServiceV2](../src/main/java/com/eventsourcing/workshop/services/CartServiceV2.java).

Her er det bare 1 linje med kode som trenger å endres.

Du kan oppleve at du må refreshe frontend for å se nye produkter som legges til i handlekurven. Kan du tenke deg hvorfor dette skjer? Kan du komme på noen forslag til hvordan vi kan unngå dette problemet?
Prøv å legge til en query parameter i frontend url: `?delay=500` for å se om dette fikser problemet.

Workshoppen fortsetter i [Del 4](part4.md).

---

## Bonusoppgave - Checkpoints

Slik oppsettet er nå, starter `subscriptions` fra starten av streamen hver gang vi starter applikasjonen på nytt. Etter hver som det blir et par millioner eventer vil dette ta lang tid, og vi ønsker ikke at systemet skal slutte å oppdatere seg over lengre tid bare fordi en applikasjon måtte restartes.

For å unngå å starte på nytt hver gang, kan vi lagre `checkpoints` i databasen etter hvert som vi leser eventer. Når vi starter applikasjonen på nytt, kan vi lese ut siste `checkpoint` og starte `subscriptions` fra der.

I kurrentDb bruker vi `revision` som checkpoint. For å velge startpunktet for `subscriptions` kan vi bruke `SubscribeToStreamOptions.get().fromRevision(x)` som er satt opp i [EventSubscriptionRunner.java](../src/main/java/com/eventsourcing/workshop/subscription/EventSubscriptionRunner.java).

Acceptance criteria:

- `checkpoints` skal lagres i databasen etter hvert som vi leser eventer.
- `checkpoints` skal leses ut fra databasen når vi starter applikasjonen på nytt, og sette `fromRevision` parameter i `SubscribeToStreamOptions` til dette.

Her står du ganske fritt til hvordan du implementerer denne funksjonaliteten. Vi har satt opp noe kode som antar at checkpoints er en `Optional<Long>` som du kan bruke hvis du vil. Her er det lov å bruke AI hvis man sitter fast. Vi har også noen safeguards som rydder opp i databasen på startup hvis det ikke finnes noen checkpoints for å unngå feil tilstand.
