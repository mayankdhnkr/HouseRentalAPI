package web.project.HouseRentalAPI.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.project.HouseRentalAPI.models.landlord;
import web.project.HouseRentalAPI.models.property;
import web.project.HouseRentalAPI.repositories.landlordRepository;
import web.project.HouseRentalAPI.repositories.propertyRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/property")
@CrossOrigin(origins = "http://localhost:3000")
public class propertyController {

    @Autowired
    private propertyRepository propertyRepository;

    @Autowired
    private landlordRepository landlordRepository;

    @Autowired
    public propertyController(propertyRepository propertyRepository){
        this.propertyRepository= propertyRepository;
    }


    @PostMapping("/add")
    @CrossOrigin
    public ResponseEntity<?> createProperty(@RequestBody property user){
        // Save the property
        property newProperty=propertyRepository.save(user);

        // Update landlord's propertyId field
        landlord updateLandlord = landlordRepository.findById(newProperty.getOwnerId()).orElse(null);
        if (updateLandlord != null) {
            updateLandlord.getOwnedPropertyIds().add(newProperty.getId());
            landlordRepository.save(updateLandlord);
        }
        return ResponseEntity.ok(newProperty);
    }

    @GetMapping("/{id}")
    @CrossOrigin
    public ResponseEntity<?> getPropertyById(@PathVariable String id){
        Optional<property> propertyOptional = propertyRepository.findById(id);
        if (propertyOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Property not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(propertyOptional.get());
    }

    @GetMapping("/getall")
    @CrossOrigin
    public ResponseEntity<List<property>> getAllUsers() {
        List<property> properties = propertyRepository.findAll();
        if (properties.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(properties);
    }

    @DeleteMapping("/delete/{id}")
    @CrossOrigin
    public ResponseEntity<?> deletePropertyById(@PathVariable String id){
        //Check if Exists
        if(!propertyRepository.existsById(id)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Property not found");
        }

        Optional<property> propertyOptional = propertyRepository.findById(id);

        //Update landlord's propertyId field
        landlord updateLandlord = landlordRepository.findById(propertyOptional.get().getOwnerId()).orElse(null);
        if (updateLandlord != null) {
            updateLandlord.getOwnedPropertyIds().remove(id);
            landlordRepository.save(updateLandlord);
        }
        propertyRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body("Property Deleted Successfully");
    }
}

