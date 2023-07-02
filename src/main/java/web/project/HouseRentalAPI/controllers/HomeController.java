package web.project.HouseRentalAPI.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping("/")
    public String home(){
        String code="<h1>RestSure Backend</h1>" +
                "<p>This is the hosted backend for the project RentSure- House Rental Management website</p>" +
                "<p>Check out the live frontend for the project here - <a href='https://rent-sure.vercel.app/' target='_blank'>RentSure - Frontend</a></p>" +
                "<p>Or checkout the Github Repo - <a href='https://github.com/mayankdhnkr/HouseRentalAPI' target='_blank'>Repo Link</a></p>";
        return code;
    }
}



