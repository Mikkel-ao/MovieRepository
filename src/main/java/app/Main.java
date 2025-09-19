package app;

import app.configs.HibernateConfig;

import app.services.TaskManagerService;
import jakarta.persistence.EntityManagerFactory;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        // Task Manager
        TaskManagerService taskManagerService = new TaskManagerService();
        //taskManagerService.fetchAllAndStoreMovies();

        try {

            //CRUD calls
            // Task 2:
            //taskManagerService.getAllMovies();

            // Task 3:
            //taskManagerService.getDirectorAndActors(10);

            // Task 4
            //taskManagerService.getMoviesByGenre("Drama");

            // Task 5
            //taskManagerService.getAllMoviesByTitle("New Title");

            // Task 6:
            //taskManagerService.getTotalAvgRatingForAllMovies();

            // Task 7:
            //taskManagerService.topTenHighestRatedMovies();

            // Task 7.1
            //taskManagerService.topTenLowestRatedMovies();

            // Task 7.2
            //taskManagerService.topTenPopularMovies();

            taskManagerService.updateMovieTitle(2, "New Title: The Sequel");

            ///// Optional CRUDS ///
            //taskManagerService.allMoviesWithActor("Mads Mikkelsen");

            //taskManagerService.allMoviesWithDirector("Diba khalaj");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        emf.close();

    }
}
