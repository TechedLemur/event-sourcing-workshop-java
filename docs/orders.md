# Ordre

Workhoppen har også et sett med avanserte oppgaver som innebærer å produsere et ordre. Tanken bak disse oppgavene er at du skal selv måtte ta valg som omhandler event sourcing og føle litt på hva som funker og hva som ikke funker. Disse oppgavene har ikke et løsningsforslag i java, men du kan se på [denne Node.js-koden](https://github.com/TechedLemur/Event-Sourcing-Workshop/blob/solution/backend/src/routes/order.ts) og tilpasse den til java.

## Oppgave 1 - Lage ordre-eventer

I denne oppgaven skal du lage helt egne eventer for ordre basert på det som blir bestilt fra en handlekurv. I butikken så kan en bruker klikke "Checkout", dette vil kalle endepunktet `POST /order/checkout/:cartId`. Du skal implementere denne routen slik at en ordre-event blir produsert til EventStore.

Når du lager ordre-eventen, tenk gjerne gjennom hvor det gir mening at informasjonen lever mtp. business caset. E.g. gir det mening å kopiere produkt navnet eller burde dette leve i ordren?

Acceptance criteria:

- `POST /orders/checkout/:cartId` skal opprette en ny ordre
- En event relatert til ordre skal dukke opp i dashboardet i eventstore

Optional criteria:

- Tømme handlekurven når en ordre blir opprettet

### Ordre oppgave 2 - Nye ordrer

I grensesnittet har vi en fane som heter Admin. Dette er et eksempel på et administrasjonsgrensesnitt og viser litt diverse informasjon vi er interessert i. I denne oppgaven skal vi se nærmere på å hente ut nylige ordrer.

Acceptance criteria:

- `GET /orders?limit=X` skal returnere de nyeste X ordrene sortert fra nyest til eldst.
- De nyeste ordrene skal dukke opp på "Recent Orders" i admin grensesnittet

Reponsen skal ligne på denne:

```json
[
  {
    "orderId": "SomeID",
    "totalAmount": 19.99,
    "createdAt": "2023-11-03T12:34:56.789Z",
    "items": [
      {
        "productName": "Kaffi"
      },
      {
        "productName": "Bolle"
      }
    ]
  }
]
```

### Ordre oppgave 3 - Populære produkter

I samme grensesnittet finner vi også "Popular Items". I denne oppgaven skal du hente ut de X mest populære produktene sortert fra mest populær til minst populær.

Acceptance criteria:

- `GET /orders/popular?limit=X` skal returnere de nyeste X produktene sortert fra mest populær til minst populær.
- De mest populære produktene skal dukke opp i admin grensesnittet

Responsen skal ligne på denne:

```json
[
  {
    "productName": "Kaffi",
    "totalQuantity": 1
  },
  {
    "productName": "Bolle",
    "totalQuantity": 1
  }
]
```

## Oppgave 4 - Orders over time graf

Den siste oversikten i admin grensesnittet er ordre over tid. Her ønsker vi å kunne se flyten av ordrer. Vi ønsker at denne grafen returnerer datapunkter for hvert 15. minutt og hvor mange ordrer som ble produsert i det tidsrommet. Om der ikke er noen orderer trenger du ikke returnere et datapunkt.

Acceptance criteria:

- `GET /orders/graph` skal returnere en liste med datapunkter. Hvert datapunkt inneholder et tidspunkt og et antall ordre for det tidspunktet.
- En graf skal vise i admin grensesnittet

Responsen skal ligne på denne:

```json
{
  "points": [
    {
      "timestamp": "2023-11-03T12:34:00.000Z",
      "orderCount": 2
    },
    {
      "timestamp": "2023-11-03T12:35:00.000Z",
      "orderCount": 1
    }
  ]
}
```

## Oppgave 5 - Order projectors

I denne oppgaven skal vi forbedre løsningen fra `ordre oppgave 2-4` ved å lage en projector som produserer en read model i mongodb. Vi ønsker at denne read modellen skal hentes direkte i api-et.

Tips: Det er potensielt enklere å lage flere projectors :)

NB: Du kan måtte gjøre endringer på [storageClient](../src/main/java/com/eventsourcing/workshop/clients/FileStorageClient.java).

Acceptance criteria:

- Endepunktene skal fungere som før refaktoreringen
- Det skal ikke trenge å gå noe trafikk til EventStore ved forespørsel fra api-et

Optional criteria:

- Lage checkpoints slik at applikasjonen ikke trenger spole gjennom alle eventene hver oppstart

## Oppgave 6 - Order reactor

En stor fordel med Event Sourcing er at vi enkelt kan bryte opp systemet i isolerte biter som jobber isolert, litt som et samlebånd. Vi kan produsere en event og ha en tjeneste som lytter på denne eventen og basert på den utfører en side effekt og produserer en ny event. I utgangspunktet funker den veldig likt en projector, men en viktig distinksjon er at den har en sideeffekt. Basert på hvor viktig sideeffekten er så kan vi ha strenge krav til å levere den minst en gang eller i e.g bank, levere bare en gang. Derfor er det ekstra viktig i denne sammenheng å holde checkpoints eller id på enkelt eventer slik at man ikke ender opp med å lage duplikater. (I denne oppgaven er det ikke så krise, men verdt å tenke på!)

Event Sourcing går ofte veldig hånd i hånd med bruk av orkestreringspatterns som saga og workflow på det som er veldig sentrale biter av applikasjonen.

I denne oppgaven skal du produsere en invoice, men om du har en egen ide er det bare å følge denne. (e.g sende bekreftelses-epost, etc.)

Acceptance criteria:

- Du skal opprette en subscription som lytter på `OrderCreated` eventen
- Når en `OrderCreated` event blir produsert, skal en `InvoiceCreated` event produseres kort tid etter

Eventen må inneholde følgende:

```json
{
  "invoiceId": "someId",
  "orderId": "orderId",
  "dueDate": "2026-11-03T12:34:56.789Z",
  "amount": 123.45,
  "status": "Created"
}
```

Optional criteria:

- Tenk gjennom / lag en reactor som purrer 2 dager før fristen

## Oppgave 7 - Server sent events

En effekt av at hele systemet er sourcet fra events er at vi i teorien kan eksponere eventene direkte til en klient eller integrasjon. Dette gjør at vi kan få sanntidsoppdatering. I denne oppgaven skal du implementere sanntidsoppdatering av grafen. Generelt vil vi aldri eksponere eventene rå, men gjennom et derivat som vi har mer kontroll på. På denne måten gjør vi det enklere å introdusere nye versjoner av en event eller funksjonalitet uten at det fører til breaking changes.

Server Sent Events lar deg i grunn holde en get connection mot en server (sett fra klienten sitt perspektiv). Dette lar deg sende oppdateringer på en måte som er enklere enn web sockets. Admin siden er satt opp til å kunne koble seg mot et SSE endepunkt på `GET /orders/live` og forventer følgende datastruktur på eventene.

NB: Denne kan ikke lett testes med requests.http da det er en persistert connection.

Acceptance criteria:

- `GET /orders/live` responderer som et SSE endepunkt
- Admin grensesnittet oppdateres live når du gjør en checkout. (Åpne to tabs for å verifisere)

Eksempel payload:

```json
{
  "orderId": "123",
  "items": [{ "productId": "123123", "productName": "Kaffi", "price": 100 }],
  "totalAmount": 100,
  "createdAt": "2024-05-04T13:45:00.000Z"
}
```

Her er et kort eksempel på hvordan du kan eksponere server-sent events (SSE) i Express, på endepunktet `/orders/live`:

```js
router.get("/orders/live", (req, res) => {
  res.setHeader("Content-Type", "text/event-stream");
  res.setHeader("Cache-Control", "no-cache");
  res.setHeader("Connection", "keep-alive");
  res.flushHeaders();

  // Her kan du sende nye ordre når de kommer inn:
  const orderEvent = {
    orderId: "123",
    items: [{ productId: "123123", productName: "Kaffi", price: 100 }],
    totalAmount: 100,
    createdAt: new Date().toISOString(),
  };
  res.write(`data: ${JSON.stringify(orderEvent)}\n\n`);

  // Lukk forbindelsen når klienten kobler fra
  req.on("close", () => res.end());
});
```
