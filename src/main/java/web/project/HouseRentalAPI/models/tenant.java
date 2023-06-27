package web.project.HouseRentalAPI.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Tenant")
public class tenant {
    @Id
    private String id;
    private String name;
    @Indexed(unique = true)
    private String email;
    @Indexed(unique = true)
    private String phone;
    private String password;

    private boolean rentPaid= false;
    private int rentDue;

    private String landlordId;
    private String propertyId;
}
