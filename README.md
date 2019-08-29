<p align="center"><img src="http://www.dhbw-mannheim.de/fileadmin/templates/default/img/DHBW_d_MA_46mm_4c.svg"></p>

# DHBW Mobile Apps Projekt
Projekt für Vorlesung Mobile Apps (5. & 6. Semester) mit dem Ziel, eine Java App zu entwickeln und Mehtoden der Projektarbeit anzuwenden.
In diesem Fall wird eine Emfpangs- & Meetingapplikation entwickelt, mit der Mitarbeiter deren Meetings verwalten können.

## Verwendete Komponenten
- [PostgreSQL](https://www.postgresql.org/docs/)
- [JDBC v42.2.6](https://jdbc.postgresql.org/download.html) for PostgreSQL

## Aufbau
- Datenbank Struktur in `dump.sql` für PostgreSQL
- Models für jede Tabelle in `/src/*.java`
- `DatabaseClient.java` verwaltet alle Datenbankzugriffe (select, insert, update, delete)
- `QueryController.java` hält dabei alle SQL Statements als public variables
- Hilfe-Klasse: `PasswordController.java` zur Unterstützung bei Passwörtern
    
    Alle Passwörter werden um "alt" am Anfang des Passwortes und "salt" am Ende erweitert, bevor ein SHA256 Algorithmus angewendet wird!
