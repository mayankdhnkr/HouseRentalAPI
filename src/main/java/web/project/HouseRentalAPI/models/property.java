package web.project.HouseRentalAPI.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Property")
public class property {
    @Id
    private String id;
    private String name;
    private String type;
    private String city;
    private String address;
    private int rent;
    private String ownerId;
    private String tenantId;
}
