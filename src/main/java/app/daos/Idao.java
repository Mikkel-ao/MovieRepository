package app.daos;

import java.util.List;

public interface Idao<T, I> {
    T create(T t);

    boolean update(T t);

    boolean delete(T t);

    T find(I id);

    List<T> getAll();
}
