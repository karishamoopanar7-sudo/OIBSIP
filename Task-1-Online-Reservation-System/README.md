# Task 1 - Online Reservation System - OIBSIP Java

## Objective
Build GUI-based train reservation system where users can log in, book tickets, cancel using PNR.

## Tech Stack
- Java Swing (GUI)
- JDBC
- SQLite (reservations.db)

## Login Credentials
Username: admin
Password: 1234

## Features Implemented (as per OIBSIP checklist)
- [x] Login Form with validation - Access Denied on wrong password
- [x] Reservation Form - Name, Train No, Train Name auto-fills, Class, Date, From, To
- [x] Book Button - Generates unique PNR and saves to DB
- [x] Confirmation Dialog with PNR
- [x] Cancellation Form - Enter PNR + Fetch booking details
- [x] Confirm Cancellation - "Are you sure?" dialog + delete from DB
- [x] Validation - No empty fields, Train No numeric, Date DD-MM-YYYY
- [x] JDBC SQLite connectivity

## How to Run
1. Download sqlite-jdbc jar
2. javac -cp .:sqlite-jdbc.jar ReservationSystem.java
3. java -cp .:sqlite-jdbc.jar ReservationSystem

## Demo Flow
1. Login admin/1234
2. Book ticket with Train No 12345 (auto shows Rajdhani Express) -> Get PNR
3. Go to Cancellation tab -> Enter PNR -> Fetch -> Confirm Cancel

Author: karishamoopanar7-sudo
Internship: OIBSIP Java Development
