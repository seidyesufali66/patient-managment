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
        
        git hub workflow for maven build and docker build and push to docker hub
     5. Create a repository on Docker Hub to store the Docker images.
     6. Update the application.properties file with your PostgreSQL database credentials.
     7. Build the project using Maven:
     8. mvn clean install
     9. Run the application:
     10. mvn spring-boot:run
     11. Dockerize the application:
     12. docker build -t your-dockerhub-username/patient-microservice
     13. Push the Docker image to Docker Hub:
     14. docker push your-dockerhub-username/patient-microservice
  15. 