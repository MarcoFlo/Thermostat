package it.polito.thermostat.redis;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, String> {
    UserEntity findByCognome(String cognome);

}
