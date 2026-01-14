package rs.ac.uns.ftn.asd.ProjekatSIIT2025.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Driver;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.DriverRepository;

@Service
public class DriverService {

    @Autowired
    DriverRepository driverRepository;

    public void setActiveDriver(String email){
        Driver driver = driverRepository.findByEmail(email);
        driver.setActive(true);
        driverRepository.save(driver);
    }
}
