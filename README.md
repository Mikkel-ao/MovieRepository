# MovieRepository Backend Assignment - Key Points

## Project Scope
- Build a small backend to store/retrieve information about **movies, actors, directors, genres**.
- Fetch movie data from **TMDb API** (Danish movies from the last 5 years, ~1146 movies).
- Store data in a **database** using **JPA** and **entities**.
- Implement **DAO layer** (CRUD) and **service layer** (DTO ↔ Entity conversions).
- Write **JUnit tests** with **Testcontainers**.
- Use Java concepts: **Streams, Lambdas, Generics, JPA, Lombok**.

---

## Project Structure Suggestions
- **DTOs**: For converting JSON from TMDb.
- **Entities**: For database storage.
- **DAO**: CRUD operations with Entities.
- **Service Layer**: Convert DTOs ↔ Entities, call DAO methods.
- **Main Method**: Once-only fetch & store operation.
- **Tests**: Cover DAO and Service functionality.

---

## Required Backend Functionality
- Store/retrieve **movies, actors, directors, genres**.
- **List all movies, actors, directors, genres**.
- **List movies by genre**.
- **Add, update, delete movies** (at least title & release date).
- **Search movies by title** (case-insensitive, partial match).
- **Statistics**:
    - Total average rating
    - Top-10 highest/lowest rated
    - Top-10 most popular movies

---

## Bonus Functionality (Optional)
- List all movies by a specific actor or director.
- Fetch and sync new movies from TMDb API.
- Fetch movies in **parallel** using `ExecutorService`/Futures.

---

## Hints & Best Practices
1. **API Key**:
    - Use environment variable or `config.properties`.
    - Never push API key to GitHub.

2. **Entity Relationships**:
    - Decide between `Many-to-Many` or `One-to-Many`.
    - Avoid unnecessary complexity, duplicates are acceptable.

3. **JSON Structure**:
    - Understand TMDb API responses to design DTOs.

4. **Database Constraints**:
    - Use `unique`, `not null`, `length` for integrity.

5. **Error Handling**:
    - Graceful handling, throw custom exceptions if needed.

6. **DTO vs Entity**:
    - DTOs: for communication with external systems.
    - Entities: for database persistence.

---

## Key Technologies
- **Java 17**
- **Maven**
- **JPA / Hibernate**
- **PostgreSQL**
- **Lombok**
- **Jackson**
- **JUnit 5**
- **Testcontainers**
