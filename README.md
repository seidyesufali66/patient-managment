Patient Managment microservice 
This microservice is responsible for managing patient information in a healthcare application. It provides functionalities to create, read, update, and delete patient records.
Technologies Used
- java 21
- Spring Boot
- Spring Data JPA
- Hibernate
  posytgresql
- Maven
- Docker
- JUnit and Mockito for testing
Features
- Patient-microservice provides RESTful APIs for managing patient records.
  - Create Patient: Add new patient records to the database.
  - Read Patient: Retrieve patient information by ID or list all patients.
  - Update Patient: Modify existing patient records.
  - Delete Patient: Remove patient records from the database.
  - Validation: Ensure data integrity with input validation.
- Error Handling: Graceful handling of errors and exceptions.
- Logging: Implement logging for monitoring and debugging.
Getting Started
Prerequisites
- Java 21
- Maven
- Docker (for containerization)
- PostgreSQL database
Installation
  1. Clone the repository:
     git clone    
  2. navigate to the project directory:
  3. create the patient-microservice modulels
  cd patient-microservice
     4. create an entity modele nmed 
        5. Patient with fields id, name, age, address, and .
        Table name Patients
|  column name  |   descriptions | 
|  patient Id   |   UUID |
|  name         |   String |
|  age          |   Integer |
|  address      |   String |
|  dateOfBirth  |   LocalDate |
|  dateOfRegistered  |   LocalDate |
        
        