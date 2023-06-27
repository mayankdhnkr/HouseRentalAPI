package web.project.HouseRentalAPI.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import web.project.HouseRentalAPI.models.property;
import java.util.List;

@Repository
public interface propertyRepository extends MongoRepository<property,String> {
    List<property> findByOwnerId(String id);
    List<property> findByNameContainingIgnoreCaseOrCityContainingIgnoreCaseOrTypeContainingIgnoreCase(String name, String city, String type);
}
