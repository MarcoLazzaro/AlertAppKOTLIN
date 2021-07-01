# Istruzioni per l'esecuzione del Progetto
## Autore: Marco Lazzaro
## Università: Università degli Studi di Napoli PARTHENOPE
## Anno: 2020/2021

**IDE utilizzato: Visual Studio Code (come amministratore) per la backend**

**IDE utilizzato: AndoidStudio per l'applicazione andoid nativa**

**OPERATIVE SYSTEM: Windows 10 Pro**

**installare node.js 14.17.0 LTS**

**Scaricare il progetto in formato .zip**
# Estrarre il file zip e portare la cartella "backend" fuori dalla directory

## Backend:
* aprire Visual Studio Code
* aprire la cartella Backend nell'IDE
* aprire un terminale (CMD) da Visual studio code
* Aassicurasi di trovarsi nella cartella "/backend"
* installare le dependencies tramite comando: npm install
* creare .env file nella directory backend con il seguente codice:
```
DB_URI='mongodb+srv://marco_lazzaro:marco_password@alertapp.wlpkn.mongodb.net/Alerts?retryWrites=true&w=majority'
PORT=4000
```
* iniziare il server con il comando: npm run start

*in caso di errore relativo all'avvio del nodemon, eseguire il comando " **npm install -g --foce nodemon@2.0.7** " per installarlo globalemnte.*


## App nativa:
* aprire la cartella estratta dallo zip in android studio"
*	eseguire la build del progetto
* mandare in esecuzione l'applicazione
 *in caso di errore durante la fase di build, disabilitare l'antivirus momentaneamente ha risolto il problema. Errore: "AAPT: error failed writing to R.txt: the data is invalid"*

# Link al Google Slides per la presentazione
https://docs.google.com/presentation/d/10oafE8tPHnoPwgduUms7-uShSpM-sB-iZ4txF4FHlcE/edit?usp=sharing
