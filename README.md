<p align="center"><img src="http://www.dhbw-mannheim.de/fileadmin/templates/default/img/DHBW_d_MA_46mm_4c.svg"></p>

# DHBW Mobile Apps Projekt
Projekt für Vorlesung Mobile Apps (5. & 6. Semester) mit dem Ziel, eine Java App zu entwickeln und Methoden der Projektarbeit anzuwenden.
In diesem Fall wird eine Emfpangs- & Meetingapplikation entwickelt, mit der Mitarbeiter Meetings verwalten können.

> PHP RESTful API [mobile_apps_api](https://github.com/timof1308/mobile_apps_api)

## Verwendete Komponenten
- [PostgreSQL](https://www.postgresql.org/docs/)
- [JDBC v42.2.6](https://jdbc.postgresql.org/download.html) for PostgreSQL
- [JavaMail 1.6.2](https://github.com/javaee/javamail/releases) to send mails
- [org.json](https://github.com/stleary/JSON-java) to parse JSON from string to object

## Aufbau
- Datenbank Struktur in `dump.sql` für PostgreSQL
- main.java.Models für jede Tabelle in `/src/*.java`
- `main.java.DatabaseClient.java` verwaltet alle Datenbankzugriffe (select, insert, update, delete)
- `main.java.QueryController.java` hält dabei alle SQL Statements als public variables
- Hilfe-Klasse: `main.java.PasswordController.java` zur Unterstützung bei Passwörtern
    
    Alle Passwörter werden um "alt" am Anfang des Passwortes und "salt" am Ende erweitert, bevor ein SHA256 Algorithmus angewendet wird!
- `main.java.MailController.java` ermöglicht das Versenden von E-Mails via SMTP und einen GMail Account

    Vor dem Nutzen das Passwort im Controller setzen!
- `main.java.JwtController.java` ermöglicht das Konvertieren eines JWT Tokens (String) in ein JSON (`getPayloadJwt()`) bzw. User Model (`decodeJwt()`)
 
