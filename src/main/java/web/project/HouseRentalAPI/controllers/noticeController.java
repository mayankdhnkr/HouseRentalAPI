package web.project.HouseRentalAPI.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.project.HouseRentalAPI.models.notice;
import web.project.HouseRentalAPI.repositories.noticeRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notice")
@CrossOrigin(origins = {"http://localhost:3000","https://rent-sure.vercel.app/"})
public class noticeController {
    @Autowired
    private web.project.HouseRentalAPI.repositories.noticeRepository noticeRepository;
    @Autowired
    public noticeController(noticeRepository noticeRepository){
        this.noticeRepository= noticeRepository;
    }

    @PostMapping("/add")
    public ResponseEntity<?> createNotice(@RequestBody notice user){
        // Save the property
        notice newNotice= noticeRepository.save(user);
        return ResponseEntity.ok(newNotice);

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getNoticeById(@PathVariable String id){
        Optional<notice> noticeOptional = noticeRepository.findById(id);
        if (noticeOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notice not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(noticeOptional.get());
    }

    @GetMapping("/getall")
    public ResponseEntity<List<notice>> getAllUsers() {
        List<notice> notices = noticeRepository.findAll();
        if (notices.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(notices);
    }

    @GetMapping("/inactive/{id}")
    public ResponseEntity<?> inactiveUserById(@PathVariable String id) {
        if(!noticeRepository.existsById(id)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notice not found");
        }
        Optional<notice> noticeOptional = noticeRepository.findById(id);
        if (noticeOptional.isPresent()) {
            notice updateNotice = noticeOptional.get();
            updateNotice.setActive(false);
            noticeRepository.save(updateNotice);
            return ResponseEntity.ok("Notice Inactivated.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tenant not found");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable String id) {
        if(!noticeRepository.existsById(id)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notice not found");
        }

        noticeRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}
