# Istruzioni per l'esecuzione del Progetto
## Autore: Marco Lazzaro
## Università: Università degli Studi di Napoli PARTHENOPE
## Anno: 2020/2021

**IDE utilizzato: Visual Studio Code (come amministratore)**

**OPERATIVE SYSTEM: Windows 10 Pro**

**installare node.js 14.17.0 LTS**

**Clona il progetto da github (Visual Studio Code necessita di git for windows per effettuare il clone)**

## Backend:
* aprire un terminale (CMD) da Visual studio code
* recarsi nella directory "backend" (cd backend)
* installare le dependencies tramite comando: npm install
* creare .env file nella directory backend con il seguente codice:
```
DB_URI='mongodb+srv://marco_lazzaro:marco_password@alertapp.wlpkn.mongodb.net/Alerts?retryWrites=true&w=majority'
PORT=4000***
```
* iniziare il server con il comando: npm run start

*in caso di errore relativo all'avvio del nodemon, eseguire il comando " **npm install -g --foce nodemon@2.0.7** " per installarlo globalemnte.*


## Frontend:
* aprire un terminale (CMD) da Visual studio code
*	recarsi nella directory "alertapp(cd alertapp)
* installare le dependencies tramite comando: npm install
*	Per visuallizzare correttamente il progetto è necessaria la seguente modifica:
  * *\node_modules\react-confirm-alert\src\react-confirm-alert.css* sostituire il codice di react-confirm-alert-overlay con questo:
```
.react-confirm-alert-overlay {
position: fixed;
top: 0;
left: 0;
right: 0;
bottom: 0;
z-index: 40000;
background: rgba(255, 255, 255, 0.9);
display: -webkit-flex;
display: -moz-flex;
display: -ms-flex;
display: -o-flex;
display: flex;
justify-content: center;
-ms-align-items: center;
align-items: center;
opacity: 0;
-webkit-animation: react-confirm-alert-fadeIn 0.5s 0.2s forwards;
-moz-animation: react-confirm-alert-fadeIn 0.5s 0.2s forwards;
-o-animation: react-confirm-alert-fadeIn 0.5s 0.2s forwards;
animation: react-confirm-alert-fadeIn 0.5s 0.2s forwards;
}
```
### Esecuzione in modalita Development
* iniziare il server con il comando: *npm run start*
### Esecuzione in modalita Deployment Ready
Questa modalità è necessaria per il funzionamento del service worker, per garantire il funzionamento parziale dell'applicazione in modalità offline.
Inoltre permette all'app di essere installata sul desktop
*	recarsi nella directory "alertapp"
* eseguire il comando dal terminale CMD(administartor): *npm install -g serve*
* eseguire il comando: *npm run build*
* eseguire il comando: *serve -s build* per effettuare il deployment della build precedentemente creata
  * seguire l'indirizzo riportato in console dopo il comando *serve*

# Link al Google Slides per la presentazione
https://docs.google.com/presentation/d/10oafE8tPHnoPwgduUms7-uShSpM-sB-iZ4txF4FHlcE/edit?usp=sharing
