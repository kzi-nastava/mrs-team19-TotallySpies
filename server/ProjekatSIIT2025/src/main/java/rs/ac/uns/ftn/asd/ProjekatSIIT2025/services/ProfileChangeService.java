package rs.ac.uns.ftn.asd.ProjekatSIIT2025.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.ProfileChangeRequest;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.ProfileField;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RequestStatus;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.User;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.ProfileChangeRequestRepository;

import java.time.LocalDateTime;

@Service
public class ProfileChangeService {
    @Autowired
    ProfileChangeRequestRepository profileChangeRequestRepository;

    public void createChangeRequest(User user, ProfileField field, String oldValue, String newValue){
        ProfileChangeRequest request = new ProfileChangeRequest();
        request.setUser(user);
        request.setField(field);
        request.setOldValue(oldValue);
        request.setNewValue(newValue);
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());

        profileChangeRequestRepository.save(request);
    }
}
