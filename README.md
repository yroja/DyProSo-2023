# DyProSo-2023 Symposium Web Application

## Overview

This project is a web application for the **DyProSo-2023 Symposium**, an international scientific conference held in Dresden.

The platform supports the full organization and administration of the symposium, including participant registration, communication, document handling, and administrative data export.

It is implemented using **Spring Boot** and follows a layered architecture.

---

## Purpose of the Application

The application provides a centralized system to manage symposium participants and related processes:

- Online registration of attendees
- Automated email communication
- Structured collection of billing and participant data
- Management of additional services such as companion tickets
- Upload and management of scientific abstracts
- Administrative export of participant data
- Publication of news and announcements by administrators

---

## Features

### User Management
- Account creation with email verification
- Secure login and logout
- Password reset via email token
- Profile editing functionality

### Registration System
- Full participant registration form
- Student and institutional participant support
- Billing address handling (EU / non-EU differentiation)
- VAT number support for EU business customers
- Optional HZDR participation

### Companion Tickets
- Ability to register additional companion participants
- Independent billing information for companions
- Optional participation in social program and excursions
- Logical linkage between attendee and companion accounts

### Email System
- Email verification after registration
- Registration confirmation emails
- Password reset emails with secure token links
- Companion ticket confirmation emails
- Abstract upload confirmation emails

### News & Notifications
- Administrators can publish news and announcements via the website
- News are displayed to all registered participants
- Used for important symposium updates (deadlines, schedule changes, general information)

### Document Handling
- Abstract upload functionality for participants
- File management per user account

### Export Features
- Excel export for billing information
- Excel export for HZDR participant data
- Name tag generation with photo permission flags

---

## Architecture

The project follows a layered Spring Boot architecture:
- Controller Layer → Handles HTTP requests and routing
- Service Layer → Contains business logic (Management classes)
- Repository Layer → Handles database access (Spring Data JPA)
- Domain Layer → Entity classes (Attendee, Companion, etc.)
- DTO Layer → Form objects used for data transfer

 ### Prerequisites

- Java 17 or higher
- Maven
- MySQL or compatible database
- SMTP mail server configuration
