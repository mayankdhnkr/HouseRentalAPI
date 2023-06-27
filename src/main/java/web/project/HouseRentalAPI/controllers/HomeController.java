package web.project.HouseRentalAPI.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping("/")
    public String home(){
        return "This is the Home Page for House Rental API!! The backend is Up and Running";
    }
}



