package web.project.HouseRentalAPI.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import web.project.HouseRentalAPI.models.notice;

public interface noticeRepository extends MongoRepository<notice,String> {
}
