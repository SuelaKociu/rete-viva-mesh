# RETE VIVA — Mesh Civica
**Ideazione e direzione: Suela Kociu**  
**Copyright © 2026 Suela Kociu**  
**Licenza del codice originale: Mozilla Public License 2.0 (MPL-2.0)**

> ⚠️ **Prototipo Android sperimentale 0.1. Non validato per le emergenze.** Non usare per richiedere soccorso né per inviare dati sensibili. Non sostituisce il 112, la Protezione Civile o i canali ufficiali.

## Missione
RETE VIVA studia la comunicazione tra smartphone vicini **senza SIM, rete cellulare o connessione Internet**. Il progetto nasce per sperimentare reti di messaggi civiche, aperte, misurabili e replicabili, utilizzando telefoni comuni.

## Che cosa include oggi
- Un esperimento **solo Android**, basato su Google Nearby Connections e strategia `P2P_CLUSTER`.
- Scoperta di nodi vicini, collegamenti da confermare confrontando i codici sui telefoni, invio di brevi messaggi, inoltro con massimo sei salti, filtro dei duplicati e cache locale.
- Configurazione Gradle, test unitari delle regole di inoltro, workflow GitHub Actions per provare a generare un APK di debug.
- Guide di sperimentazione, documentazione privacy/sicurezza e protocollo di misurazione.

**Non sono ancora stati dimostrati:** compilazione dell'APK su GitHub, comunicazione tra tre telefoni reali, funzionamento affidabile a schermo spento, cifratura end-to-end e interoperabilità con iPhone o Meshtastic. I nodi intermedi possono leggere i messaggi. Non dichiarare questo software sicuro per comunicazioni sensibili.

## Esperimento iniziale
Servono tre smartphone Android compatibili con Google Play Services. Installare la demo, attivare Bluetooth e Wi‑Fi e concedere i permessi richiesti, mantenendo aperta l'app. Confrontare **sui due dispositivi** i codici di autenticazione prima di accettare il collegamento. Inviare solo stringhe di prova e misurare la consegna A → B → C.

Per compilare: Android Studio con JDK 17, SDK Android 35, oppure **Actions → Compila prototipo Android → Run workflow**. La compilazione richiede Internet per scaricare dipendenze, ma i collegamenti Nearby possono operare localmente offline sui telefoni compatibili.

## Limitazioni
Il raggio d'azione dipende da dispositivi e ambiente; la copertura non è garantita. Nearby richiede servizi Google Play compatibili. La demo funziona in primo piano, non implementa crittografia end-to-end del contenuto, non autentica l'identità degli autori oltre al collegamento tra peer e non ha una sicurezza verificata. Non sostituisce alcun sistema di comunicazione di emergenza certificato.

## Documentazione
- [Guida volontari](docs/GUIDA_VOLONTARI.md)
- [Protocollo di test 001](docs/PROTOCOLLO_TEST_001.md)
- [Sicurezza](SECURITY.md) · [Privacy](PRIVACY.md) · [Roadmap](ROADMAP.md)
- [Licenza MPL-2.0](LICENSE) · [Contributi](CONTRIBUTING.md)

## Diritti e contributi
**Suela Kociu è la fondatrice del progetto e la titolare dichiarata del copyright sui materiali originali pubblicati a suo nome**, salvo diritti di terzi o contributori. La MPL-2.0 permette a tutti di leggere, utilizzare, modificare, distribuire e anche utilizzare commercialmente il codice, secondo le sue condizioni. Non trasferisce automaticamente a Suela il copyright dei contributi di altri. Nome, logo ed eventuali marchi sono questioni distinte dalla licenza del codice.

Il progetto è pubblico per una finalità civica: la sicurezza si costruisce con prove, revisioni e trasparenza, non con promesse di infallibilità.