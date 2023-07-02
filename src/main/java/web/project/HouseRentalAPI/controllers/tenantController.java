package web.project.HouseRentalAPI.controllers;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.project.HouseRentalAPI.dto.LoginRequest;
import web.project.HouseRentalAPI.models.landlord;
import web.project.HouseRentalAPI.models.property;
import web.project.HouseRentalAPI.models.tenant;
import web.project.HouseRentalAPI.repositories.landlordRepository;
import web.project.HouseRentalAPI.repositories.propertyRepository;
import web.project.HouseRentalAPI.repositories.tenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tenant")
@CrossOrigin(origins = {"http://localhost:3000","https://rent-sure.vercel.app/"})
public class tenantController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private tenantRepository tenantRepository;
    @Autowired
    private landlordRepository landlordRepository;
    @Autowired
    private propertyRepository propertyRepository;
    @Autowired
    public tenantController(tenantRepository tenantRepository){
        this.tenantRepository= tenantRepository;
    }


    @PostMapping("/add")
    public ResponseEntity<?> createTenant(@RequestBody tenant user){
        if(tenantRepository.existsByEmail(user.getEmail())){
            return ResponseEntity.badRequest().body("Email already exists!");
        }
        if(tenantRepository.existsByPhone(user.getPhone())){
            return ResponseEntity.badRequest().body("Phone already exists!");
        }
        // Save the property
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        tenant newTenant = tenantRepository.save(user);

        // Update landlord's propertyId field
        landlord updateLandlord = landlordRepository.findById(newTenant.getLandlordId()).orElse(null);
        if (updateLandlord != null) {
            updateLandlord.getTenantsId().add(newTenant.getId());
            landlordRepository.save(updateLandlord);
        }

        property updateProperty = propertyRepository.findById(newTenant.getPropertyId()).orElse(null);
        if (updateProperty != null) {
            updateProperty.setTenantId(newTenant.getId());
            propertyRepository.save(updateProperty);
        }
        newTenant.setRentDue(updateProperty.getRent());
        tenantRepository.save(newTenant);
        return ResponseEntity.ok(newTenant);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginTenant(@RequestBody LoginRequest request){
        Optional<tenant> tenantOptional = tenantRepository.findByEmail(request.getEmail());
        if (tenantOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email Not Found");
        }
        else{
            boolean isMatch = passwordEncoder.matches(request.getPassword(), tenantOptional.get().getPassword());

            if(isMatch){
                return ResponseEntity.status(HttpStatus.OK).body(tenantOptional.get());
            }
            else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid Credentials");
            }
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTenantById(@PathVariable String id){
        Optional<tenant> tenantOptional = tenantRepository.findById(id);
        if (tenantOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tenant not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(tenantOptional.get());
    }

    @GetMapping("/getall")
    public ResponseEntity<List<tenant>> getAllUsers() {
        List<tenant> tenants = tenantRepository.findAll();
        if (tenants.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(tenants);
    }

    @GetMapping("/search")
    public ResponseEntity<List<tenant>> searchTenantsByName(@RequestParam("name") String name) {
        List<tenant> tenants = tenantRepository.findByNameContainingIgnoreCase(name);
        if (tenants.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(tenants);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable String id) {
        //Check if Exists
        if(!tenantRepository.existsById(id)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tenant not found");
        }

        Optional<tenant> tenantOptional = tenantRepository.findById(id);

        //Update landlord's propertyId field
        landlord updateLandlord = landlordRepository.findById(tenantOptional.get().getLandlordId()).orElse(null);
        if (updateLandlord != null) {
            updateLandlord.getTenantsId().remove(id);
            landlordRepository.save(updateLandlord);
        }

        property updateProperty = propertyRepository.findById(tenantOptional.get().getPropertyId()).orElse(null);
        if (updateProperty != null) {
            updateProperty.setTenantId("");
            propertyRepository.save(updateProperty);
        }
        tenantRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body("Tenant Deleted Successfully");
    }

    @PostMapping("/paid/{id}")
    public ResponseEntity<?> rentPaid(@PathVariable String id){
        //Check if Exists
        if(!tenantRepository.existsById(id)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tenant not found");
        }

        Optional<tenant> tenantOptional = tenantRepository.findById(id);
        Optional<property> propertyOptional=propertyRepository.findById(tenantOptional.get().getPropertyId());
        if (tenantOptional.isPresent()) {
            tenant updateTenant = tenantOptional.get();
            if(updateTenant.getRentDue()==0){
                updateTenant.setRentPaid(false);
                updateTenant.setRentDue(propertyOptional.get().getRent());
            }
            else{
                updateTenant.setRentPaid(true);
                updateTenant.setRentDue(0);
            }
            tenantRepository.save(updateTenant);
            return ResponseEntity.ok("Rent has been marked as paid for the tenant.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tenant not found");
        }
    }
}


