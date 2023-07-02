package web.project.HouseRentalAPI.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.project.HouseRentalAPI.dto.LoginRequest;
import web.project.HouseRentalAPI.models.property;
import web.project.HouseRentalAPI.models.tenant;
import web.project.HouseRentalAPI.repositories.landlordRepository;
import web.project.HouseRentalAPI.models.landlord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import web.project.HouseRentalAPI.repositories.propertyRepository;
import web.project.HouseRentalAPI.repositories.tenantRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/landlord")
@CrossOrigin(origins = {"http://localhost:3000","https://rent-sure.vercel.app/"})
public class landlordController {
    @Autowired
    private landlordRepository landlordRepository;
    @Autowired
    private tenantRepository tenantRepository;
    @Autowired
    private propertyRepository propertyRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    public landlordController(landlordRepository landRepository) {
        this.landlordRepository = landRepository;
    }

    @PostMapping("/add")
    public ResponseEntity<?> createLandlord(@RequestBody landlord user){
        if(landlordRepository.existsByEmail(user.getEmail())){
            return ResponseEntity.badRequest().body("Email already exists!");
        }
        if(landlordRepository.existsByPhone(user.getPhone())){
            return ResponseEntity.badRequest().body("Phone already exists!");
        }
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        landlordRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginLandlord(@RequestBody LoginRequest request){
        Optional<landlord> landlordOptional = landlordRepository.findByEmail(request.getEmail());
        if (landlordOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email Not Found");
        }
        else{
            boolean isMatch = passwordEncoder.matches(request.getPassword(), landlordOptional.get().getPassword());

            if(isMatch){
                return ResponseEntity.status(HttpStatus.OK).body(landlordOptional.get());
            }
            else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid Credentials");
            }
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLandlordById(@PathVariable String id){
        Optional<landlord> landlordOptional = landlordRepository.findById(id);
        if (landlordOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Landlord not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(landlordOptional.get());
    }

    @GetMapping("/getall")
    public ResponseEntity<List<landlord>> getAllUsers(){
        List<landlord> landlords = landlordRepository.findAll();
        if (landlords.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(landlords);
    }

    @GetMapping("get/tenants/{id}")
    public ResponseEntity<List<tenant>> getAllTenants(@PathVariable String id){
        if(!landlordRepository.existsById(id)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        List<tenant> tenants = tenantRepository.findByLandlordId(id);
        return ResponseEntity.status(HttpStatus.OK).body(tenants);
    }

    @GetMapping("get/properties/{id}")
    public ResponseEntity<List<property>> getAllProperty(@PathVariable String id){
//        if(!landlordRepository.existsById(id)){
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        }
        List<property> properties = propertyRepository.findByOwnerId(id);
        return ResponseEntity.status(HttpStatus.OK).body(properties);
    }

    @GetMapping("/totalrent/{id}")
    public ResponseEntity<?> getTotalRent(@PathVariable String id){
//        if(!landlordRepository.existsById(id)){
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        }
        List<tenant> tenants = tenantRepository.findByLandlordId(id);
        int totalRent = 0;
        for (tenant singleTenant : tenants) {
            totalRent += singleTenant.getRentDue();
        }
        return ResponseEntity.status(HttpStatus.OK).body(totalRent);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable String id) {
        if(!landlordRepository.existsById(id)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Landlord not found");
        }

        landlordRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body("Landlord deleted successfully");
    }
}
