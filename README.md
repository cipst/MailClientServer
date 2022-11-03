# Prog 3 Project

> #### *Barone Matteo* `951558` <a href="mailto:matteo.barone415@edu.unito.it">`matteo.barone415@edu.unito.it`</a>

> #### *Cipolletta Stefano* `948650` <a href="mailto:stefano.cipolletta@edu.unito.it">`stefano.cipolletta@edu.unito.it`</a>

## Good Practices
### Comments

### Functions / Classes
- Use camelCase syntax

### Variables / Constants
- Use snake_case syntax lowercase for variables
- Use SNAKE_CASE syntax uppercase for constants

## Roadmap
### Mail Server

*Tipo del Server:* **IMAP** 

- [ ] GUI
    - [ ] Switch acceso/spento
    - [ ] Gestione apertura file di log (per controllare un eventuale implosione)
    	- [ ] Utilizzo TreeView per visualizzazione albero cartelle/file presenti nel server

- [ ] Log messaggi
    - [ ] Suddivisione in cartelle (eg. `root/username/ricevuti/data.txt`)
    - [ ] Suddivisione in file (eg. `ID, mittente, destinatario/i, argomento, testo, data`)

- [ ] Log azioni sul server
    - [ ] apertura/chiusura di una connessione tra mail client e server
    - [ ] invio di messaggi da parte di un client
    - [ ] ricezione di messaggi da parte di un client
    - [ ] errori nella consegna di messaggi

- [ ] Tests
    - [ ] ...

### Mail Client

- [ ] Creazione e Invio messaggi [^1]
- [ ] Risposta [^1]
- [ ] Forward [^1]
- [ ] Visualizzazione email ricevute 
- [ ] Rimozione messaggi 
- [ ] Notifica nuovo messaggio
- [ ] Feedback azioni utente (eg. invio avvenuto con successo) (messaggi di errore ecc...)
- [ ] No crash se il server si disconnette
- [ ] Riconnessione automatica quanto il server torna online


[^1]: uno o più utenti




