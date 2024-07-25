# Trademark Similarity Application

This application is designed to compare and analyze the similarity between trademarks. It consists of two main components: a backend service for processing and analyzing trademarks, and a frontend interface for user interaction.

## Repository Links

- Backend: [trademark-similarity-app-backend](https://github.com/malwinakochman/trademark-similarity-app-backend)
- Frontend: [trademark-similarity-app-frontend](https://github.com/malwinakochman/trademark-similarity-app-frontend)

## Overview

The Trademark Similarity Application allows users to compare trademarks and assess their similarity. This tool is useful for businesses, legal professionals, and trademark researchers who need to evaluate the uniqueness of a trademark or identify potential conflicts with existing trademarks.

## Features

- Trademark comparison and similarity analysis
- User-friendly interface for inputting and viewing trademark data
- Backend processing for accurate similarity calculations
- Integration between frontend and backend for seamless user experience

## Technical Stack

### Backend
- Language: Java, Python
- Framework: Maven, Spring Boot
- Database: PostgreSQL + EUIPO API

### Frontend
- Framework: Angular
- UI Library: Bootstrap

## Installation

### Backend Setup
1. Clone the backend repository:
   ```bash
   git clone https://github.com/malwinakochman/trademark-similarity-app-backend.git
   
2. Open the project in your IDE
3. Add SDK and Maven
4. Run the application

### Database
1. Create a PostgreSQL database
2. Configure the database connection in `application.properties`
3. Configure your EUIPO API key in `application.properties`(To get access to EUIPO API you need to register on their website: https://euipo.europa.eu/ohimportal/en/)
