package rs.ac.uns.ftn.asd.ProjekatSIIT2025.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users.ProfileChangeRequestDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.ProfileChangeRequest;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.ProfileField;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RequestStatus;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.User;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.ProfileChangeRequestRepository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class ProfileChangeService {
    @Autowired
    ProfileChangeRequestRepository profileChangeRequestRepository;

    @Autowired
    UserRepository userRepository;

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

    public List<ProfileChangeRequestDTO> getPendingRequests(){
        List<ProfileChangeRequest> pendingRequests = profileChangeRequestRepository.findByStatus(RequestStatus.PENDING);
        List<ProfileChangeRequestDTO> dtos = new ArrayList<>();

        for (ProfileChangeRequest req : pendingRequests) {
            ProfileChangeRequestDTO dto = toDto(req);
            dtos.add(dto);
        }
        return dtos;
    }

    private ProfileChangeRequestDTO toDto(ProfileChangeRequest req) {
        ProfileChangeRequestDTO dto = new ProfileChangeRequestDTO();
        dto.setId(req.getId());
        dto.setUserEmail(req.getUser().getEmail());
        dto.setField(req.getField());
        dto.setOldValue(req.getOldValue());
        dto.setNewValue(req.getNewValue());
        dto.setCreatedAt(req.getCreatedAt());
        return dto;
    }

    @Transactional
    public void approveRequest(Long id){
        ProfileChangeRequest request = profileChangeRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile change request not found"));
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Request already processed");
        }
        User user = request.getUser();

        switch (request.getField()) {
            case NAME -> user.setName(request.getNewValue());
            case LAST_NAME -> user.setLastName(request.getNewValue());
            case PHONE -> user.setPhoneNumber(request.getNewValue());
            case ADDRESS -> user.setAddress(request.getNewValue());
            case IMAGE -> user.setProfilePicture(request.getNewValue());
        }

        request.setStatus(RequestStatus.APPROVED);
        profileChangeRequestRepository.save(request);
        userRepository.save(user);
    }

    public void rejectRequest(Long id){
        ProfileChangeRequest request = profileChangeRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile change request not found"));
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Request already processed");
        }
        request.setStatus(RequestStatus.REJECTED);
        profileChangeRequestRepository.save(request);
    }
}
