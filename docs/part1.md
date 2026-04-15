# Del 1

I denne første delen skal vi sette opp prosjektet og lage våre første eventer.

## Oppsett

Følg instruksene i [README.md](../README.md) for å sette opp prosjektet. Hvis du har gjort alt riktig skal du nå ha en backend som kjører på [http://localhost:8080](http://localhost:8080), en frontend som kjører på [http://localhost:3000](http://localhost:3000) og noen docker-containere som kjører eventstore og mongoDB.

Gå til [http://localhost:2113/web/index.html#/dashboard](http://localhost:2113/web/index.html#/dashboard) for å se at eventstore kjører korrekt.

## Oppgave 0 - Hello Event

Først skal vi teste at backenden vår fungerer og kan kommunisere med EventStore.

Gå til Swagger UI på [http://localhost:8080/swagger-ui/index.html#/](http://localhost:8080/swagger-ui/index.html#/) for å teste backenden.

1. Kjør `POST /example` for å lage en eksempel-event.
2. Gå til [http://localhost:2113/web/index.html#/streams](http://localhost:2113/web/index.html#/streams) for å se at eventen er lagt til i eventstore. Det skal nå være en stream med navnet `hello-event-stream` som vi kan trykke på for å se eventene.
3. Kjør `GET /example` for å sjekke at vi kan lese eventen fra eventstore.

## Oppgave 1 - Legg produkt i handlekurven

Vi skal nå starte på funksjonaliteten for å legge produkter i handlekurven.

Acceptance criteria:

- `POST /cart/:id/addItem` skal legge til et produkt i en `cart-<id>` stream.
- `CartItemAdded` event skal dukke opp i dashboardet i eventstore.

Din oppgave er å implementere funksjonen `addItemToCart` i [CartService](../src/main/java/com/eventsourcing/workshop/services/CartService.java). Gjerne se på `createProduct` i [ProductService](../src/main/java/com/eventsourcing/workshop/services/ProductService.java) for inspirasjon.
Du skal i utgangspunktet bare trenge å legge til kode i blokkene der det står en `// TODO: ...` kommentar.

For å kalle endepunktet for å legge til produkt kan du klikke på "Add to Cart" på et produkt i frontenden. Du vil ikke se noen endringer i frontenden (denne oppgaven kommer snart), men du kan se at eventen er lagt til i eventstore.

Hint: Se på definisjonen av `CartItemAddedEvent` i [CartItemAddedEvent.java](../src/main/java/com/eventsourcing/workshop/events/CartItemAddedEvent.java) for å se hvilke felter som må fylles ut. Har `productService` en metode vi kan bruke for å hente ut info om produktet?

## Oppgave 2 - Slett produkt fra handlekurven

Vi skal nå implementere funksjonen `removeItemFromCart` i [CartService](../src/main/java/com/eventsourcing/workshop/services/CartService.java).

Acceptance criteria:

- `DELETE /cart/:id/removeItem/:itemId` skal fjerne et produkt fra en `cart-<id>` stream.
- `CartItemRemoved` event skal dukke opp i dashboardet i eventstore.

Din oppgave er å implementere funksjonen `removeItemFromCart` i [CartService](../src/main/java/com/eventsourcing/workshop/services/CartService.java). Sjekk TODO-kommentaren for mer informasjon.

Siden `getCart` ikke er ferdig implementert enda, kan du ikke bruke frontend for å se at eventene blir produsert korrekt. Du kan kjøre http-requests i Swagger UI i stedet. For å kalle riktig endepunkt må du ha både `cartId` og `itemId` som parametre. Du kan finne disse ved å se i EventStore dashboardet for en cart-stream og undersøke `CartItemAdded` eventen.
