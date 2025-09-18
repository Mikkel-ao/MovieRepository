package app.daos;

import jakarta.persistence.EntityManager;

import java.util.List;

public interface IDAO<T, I> {
    T create(T t, EntityManager em);
    T getById(I id, EntityManager em);
    List<T> getAll(EntityManager em);
    T update(T t, EntityManager em);
    boolean delete(I id, EntityManager em);
}