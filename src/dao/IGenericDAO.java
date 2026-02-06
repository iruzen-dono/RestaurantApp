package dao;

import java.util.List;

/**
 * Interface générique pour les opérations CRUD
 */
public interface IGenericDAO<T> {
    void create(T t) throws Exception;
    T read(int id) throws Exception;
    List<T> readAll() throws Exception;
    void update(T t) throws Exception;
    void delete(int id) throws Exception;
}
