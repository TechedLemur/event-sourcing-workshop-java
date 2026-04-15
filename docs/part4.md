# Del 4 - Nye krav

En produktansvarlig har bestemt at vi skal ha med valuta i handlekurven. Din teamlead har bestemt at vi løser dette ved å lage en ny versjon av `CartItemAdded` som inneholder valuta som en del av `price`-feltet.

`CartItemAddedV2Event` er allerede definert i [CartItemAddedV2Event.java](../src/main/java/com/eventsourcing/workshop/events/CartItemAddedV2Event.java).

## Oppgave 1 - CartItemAddedV2Event

Når man legger til et produkt i handlekurven, skal vi nå opprette denne nye eventen.

Din oppgave er å endre funksjonen `addItemToCart` i [CartService](../src/main/java/com/eventsourcing/workshop/services/CartService.java) slik at den oppretter `CartItemAddedV2Event` i stedet for `CartItemAddedEvent`.
Valuta er allerede en del av `Product`-objektet, så du kan bruke denne for å få alle verdiene du trenger.

## Oppgave 2 - Håndtere CartItemAddedV2Event

En av karakteristikkene i Event Sourcing er at vi ikke kan endre eller slette eventer. Dette betyr at vi må fremdeles håndtere den gamle eventen, `CartItemAddedEvent`, samtidig som vi håndterer den nye eventen, `CartItemAddedV2Event`.

Din oppgave er å endre funksjonen `applyEvent` i [Cart](../src/main/java/com/eventsourcing/workshop/models/Cart.java) slik at den håndterer begge eventene. Hvis du får V2 av eventen skal du sette `productCurrency`-feltet på `CartItem`-objektet til `currency`-feltet fra eventen sin `price`.

Acceptance criteria:

- `applyEvent` skal håndtere både V1 og V2 av eventen.
- Frontend skal vise valutta i handlekurven for nye elementer som legges til i handlekurven.

## Videre oppgaver

Ble du ferdig med alle oppgavene og har lyst på en ny utfordring? Prøv gjerne å se på oppgavene i [orders.md](orders.md) som er et sett med avanserte oppgaver som handler om ordre og bestillingsflyt.
