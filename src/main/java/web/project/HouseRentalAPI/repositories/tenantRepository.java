package web.project.HouseRentalAPI.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import web.project.HouseRentalAPI.models.tenant;

import java.util.Optional;
import java.util.List;

public interface tenantRepository extends MongoRepository<tenant,String> {
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    Optional<tenant> findByEmail(String email);
    List<tenant> findByLandlordId(String id);
    List<tenant> findByNameContainingIgnoreCase(String name);
}
