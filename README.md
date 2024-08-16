
# SpringBootJWTIntegrationTest

## Project Description

SpringBootJWTIntegrationTest is a demonstration project that illustrates how to implement integration tests in a Spring Boot application secured with JSON Web Token (JWT). The purpose of this project is to showcase how REST services can be integrated and tested in a Spring Boot environment, using JWT-based security.
To run the application locally, prepare application.properties file in src/main/resources directory with credentials is needed.

## Technologies Used

The project is built using the following technologies:

- **Spring Boot 3.x**: A framework that simplifies the creation of Java applications, providing built-in support for many tools and libraries.
  - **Spring Security**: Used to implement authentication and authorization mechanisms, including JWT management.
  - **Spring Web**: A module that provides support for building web and RESTful applications.
- **JSON Web Token (JWT)**: A standard used to create and verify JWT tokens for user authentication and authorization.
- **JUnit 5**: A testing framework for writing unit and integration tests.
- **Test containers**: The technology that creates an independent test database based on Postgres.
- **Mock MVC**: utility that allows you to simulate HTTP requests and test your application's controllers in a controlled environment without starting a full web server.

## Project Structure

- `src/main/java`: Contains the application's source code.
- `src/test/java`: Contains the application's unit and integration tests.
- `src/main/resources`: Contains configuration files such as `application.properties`.
- `src/test/resources`: Contains configuration files and test data.

## Testing

### Integration Testing

Integration tests in this project focus on testing the interaction between different layers of the application, including controllers, services, and repositories, with the security layer (JWT) in place. **Spring Boot's** testing support makes it easy to set up a test context that closely resembles the production environment.

#### Security Integration Tests

One of the key aspects tested in this project is the JWT-based security. Tests ensure that the security configuration works as expected, verifying that endpoints are properly secured and that JWT tokens are correctly handled.

### Database Testing

The project uses **My SQL** database, but for testing purposes **Postgress** is used. This allows for fast and isolated tests that interact with the database layer. The database schema is automatically created based on the application's entities, ensuring that the tests run in an environment that closely mimics production. To create docker database container, working Docker Desktop is required.

## Contributing

If you would like to contribute to this project, please clone the repository, create a new branch, and submit a Pull Request.

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.

## Contact

For any questions or suggestions, feel free to contact me via GitHub Issues.
