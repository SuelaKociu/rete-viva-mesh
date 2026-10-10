# Protocollo RV-001 — Verifica sperimentale multi-hop
**Scopo:** verificare se un messaggio originato su A viene ricevuto su C attraverso B quando A e C non hanno un collegamento diretto.

## Materiale
Tre dispositivi Android compatibili, stessa versione RETE VIVA, orologi approssimativamente sincronizzati, possibilità di escludere SIM/dati mobili e Internet, un osservatore.

## Procedura
1. Registrare modelli, versioni Android, build dell'app, livello batteria e permessi concessi.
2. Stabilire tre collegamenti a breve distanza, verificando i codici sui due schermi.
3. Verificare scambio A → B e B → C.
4. Separare A e C fino a impedire collegamento diretto, mantenendo B come possibile ponte.
5. Spedire un messaggio con identificatore univoco da A.
6. Osservare se e quando arriva su C; ripetere almeno 10 volte e registrare anche i fallimenti.
7. Disconnettere B e verificare se la consegna fallisce o viene rinviata.
8. Ripetere con schermo spento e poi modalità aereo, riattivando Bluetooth/Wi-Fi se necessario; documentare le differenze.

## Risultati
Per ogni prova: ora, mittente, ricevente, dispositivi collegati, esito, secondi trascorsi, note. Non registrare nominativi reali, indirizzi precisi o dati sensibili.

**Criterio di successo:** ricezione su C senza collegamento diretto A–C, documentata e ripetibile. Un singolo messaggio riuscito non dimostra affidabilità in una catastrofe.
