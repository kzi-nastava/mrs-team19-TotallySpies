package rs.ac.uns.ftn.asd.ProjekatSIIT2025.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.PanicNotification;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.PanicNotificationRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class PanicNotificationService {
    @Autowired
    PanicNotificationRepository panicNotificationRepository;

    public List<PanicNotification> getPanicNotifications(){
        return panicNotificationRepository.findAll();
    }
}
