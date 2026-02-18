package rs.ac.uns.ftn.asd.ProjekatSIIT2025.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.reports.DailyReportDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.reports.ReportResponseDTO;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users.*;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.*;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.RideRepository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.UserRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProfileChangeService profileChangeService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    RideRepository rideRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null){
            throw new UsernameNotFoundException("User with that email not found");
        }
        return user;
    }

    public void updatePassword(String email, String password){
        //System.out.println("Nova lozinka " + password);
        String encodedPassword = encoder.encode(password);
        User user = userRepository.findByEmail(email);
        user.setPassword(encodedPassword);
        //userRepository.updatePassword(email, encodedPassword);
        //needed for jwt validation
        user.setLastPasswordResetDate(Date.from(Instant.now()));
        userRepository.save(user);

    }
    public UserRole getRoleByEmail(String email){
        return userRepository.getRoleByEmail(email);
    }

    public UserProfileResponseDTO getProfileByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if(user == null){
            throw new UsernameNotFoundException("User not found");
        }

        UserProfileResponseDTO dto = new UserProfileResponseDTO();
        dto.setName(user.getName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddress(user.getAddress());
        dto.setProfilePicture(user.getProfilePicture() != null ? user.getProfilePicture() : "default-profile-image.png");

        return dto;
    }

    public void updateProfile(String email, UserProfileUpdateRequestDTO request){
        User user = userRepository.findByEmail(email);
        if(user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        if (user.getRole() == UserRole.DRIVER) {
            updateProfileDriver(user, request);
        } else {
            if (request.getName() != null){
                user.setName(request.getName());
            }
            if (request.getLastName() != null){
                user.setLastName(request.getLastName());
            }
            if (request.getPhoneNumber() != null) {
                user.setPhoneNumber(request.getPhoneNumber());
            }
            if (request.getAddress() != null) {
                user.setAddress(request.getAddress());
            }
            userRepository.save(user);
        }
    }

    public void updateProfileDriver(User user, UserProfileUpdateRequestDTO requestDTO){
        if (!requestDTO.getName().equals(user.getName())){
            profileChangeService.createChangeRequest(
                    user,
                    ProfileField.NAME,
                    user.getName(),
                    requestDTO.getName()
            );
        }
        if (!requestDTO.getLastName().equals(user.getLastName())){
            profileChangeService.createChangeRequest(
                    user,
                    ProfileField.LAST_NAME,
                    user.getLastName(),
                    requestDTO.getLastName()
            );
        }
        if (!requestDTO.getPhoneNumber().equals(user.getPhoneNumber())) {
            profileChangeService.createChangeRequest(
                    user,
                    ProfileField.PHONE,
                    user.getPhoneNumber(),
                    requestDTO.getPhoneNumber()
            );
        }
        if (!requestDTO.getAddress().equals(user.getAddress())) {
            profileChangeService.createChangeRequest(
                    user,
                    ProfileField.ADDRESS,
                    user.getAddress(),
                    requestDTO.getAddress()
            );
        }
    }

    public void updateProfilePicture(String email, UserImageUpdateDTO request){
        User user = userRepository.findByEmail(email);
        if(user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        user.setProfilePicture(request.getImageUrl());
        userRepository.save(user);
    }

    public void updateProfileImage(String email, MultipartFile image) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        if (image == null || image.isEmpty()) {
            return;
        }

        String imagePath = fileStorageService.storeProfileImage(image, user.getId());

        if (user.getRole() != UserRole.DRIVER) {
            user.setProfilePicture(imagePath);
            userRepository.save(user);
        } else {
            profileChangeService.createChangeRequest(user, ProfileField.IMAGE, user.getProfilePicture(), imagePath);
        }
    }

    public ReportResponseDTO generateReport(String email, LocalDateTime from, LocalDateTime to, String targetEmail) {
        User user = userRepository.findByEmail(email);
        if (user == null){
            throw new UsernameNotFoundException("User not found");
        }

        List<Ride> rides = new ArrayList<>();
        // lista validnih voznji koje ce se racunati u statistici
        List<RideStatus> validReportStatuses = List.of(RideStatus.COMPLETED, RideStatus.STOPPED);

        if (user.getRole() == UserRole.ADMIN) {
            if (targetEmail != null && !targetEmail.isEmpty()) {
                //admin gleda specificnog putnika provjeravam njegovu rolu
                User targetUser = userRepository.findByEmail(targetEmail);
                if (targetUser != null){
                    if(targetUser.getRole() == UserRole.DRIVER){
                        rides = rideRepository.findAllByDriverEmailAndStatusInAndStartedAtBetween(targetEmail, validReportStatuses, from, to);
                    } else if (targetUser.getRole() == UserRole.PASSENGER) {
                        rides = rideRepository.findAllByCreatorEmailAndStatusInAndStartedAtBetween(targetEmail, validReportStatuses, from, to);
                    }
                }
            } else {
                // admin gleda sve
                rides = rideRepository.findAllByStatusInAndStartedAtBetween(validReportStatuses, from, to);
            }
        } else if (user.getRole() == UserRole.DRIVER) {
            rides = rideRepository.findAllByDriverEmailAndStatusInAndStartedAtBetween(email, validReportStatuses, from, to);
        } else {
            // ROLE_PASSENGER - gleda samo one kojima je on kreator
            rides = rideRepository.findAllByCreatorEmailAndStatusInAndStartedAtBetween(email, validReportStatuses, from, to);
        }

        return processRidesToReport(rides, from, to);
    }

    private ReportResponseDTO processRidesToReport(List<Ride> rides, LocalDateTime from, LocalDateTime to) {
        // Grupisanje po danu
        Map<LocalDate, List<Ride>> groupedByDay = rides.stream()
                .collect(Collectors.groupingBy(r -> r.getStartedAt().toLocalDate()));

        List<DailyReportDTO> dailyData = new ArrayList<>();
        double totalSum = 0;
        double totalDistance = 0;
        long totalCount = rides.size();

        // prolazimo kroz svaki dan u opsegu
        for (LocalDate date = from.toLocalDate(); !date.isAfter(to.toLocalDate()); date = date.plusDays(1)) {
            List<Ride> dayRides = groupedByDay.getOrDefault(date, new ArrayList<>());

            long count = dayRides.size();
            double distance = dayRides.stream().mapToDouble(Ride::getDistanceKm).sum();
            double money = dayRides.stream().mapToDouble(Ride::getTotalPrice).sum();

            dailyData.add(new DailyReportDTO(date, count, distance, money));

            totalSum += money;
            totalDistance += distance;
        }

        ReportResponseDTO response = new ReportResponseDTO();
        response.setDailyData(dailyData);
        response.setTotalSum(totalSum);
        response.setTotalDistance(totalDistance);
        response.setTotalCount(totalCount);

        // prosjecna zarada po danu u izabranom periodu
        long daysBetween = ChronoUnit.DAYS.between(from.toLocalDate(), to.toLocalDate()) + 1;
        response.setAverageMoney(daysBetween > 0 ? totalSum / daysBetween : 0);

        return response;
    }

    public List<AdminUserDTO> findAllUsersByRole(UserRole role){
        List<User> users = userRepository.findAllByRole(role);
        List<AdminUserDTO> usersDTOList = new ArrayList<>();

        for (User user : users){
            AdminUserDTO dto = new AdminUserDTO();
            dto.setId(user.getId());
            dto.setEmail(user.getEmail());
            dto.setName(user.getName());
            dto.setRole(user.getRole());
            dto.setBlocked(user.isBlocked());
            dto.setBlockReason(user.getBlockReason());
            usersDTOList.add(dto);
        }

        return usersDTOList;
    }

    public void blockUser(Long userId, String blockReason){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (user.isBlocked()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This user is already blocked!");
        }

        user.setBlocked(true);
        user.setBlockReason(blockReason);
        userRepository.save(user);
    }

    public void unblockUser(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!user.isBlocked()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This user is not blocked!");
        }

        user.setBlocked(false);
        user.setBlockReason(null);
        userRepository.save(user);
    }

    public User findById(Long id) throws Exception {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new Exception("User with id " + id + " not found."));
        return user;
    }

    public List<User> findAllById(List<Long> userIds) {
        return userRepository.findAllById(userIds);
    }

    
    public List<UserDTO> getAllUsers(){
        List<User> users = userRepository.findAll();
        List<UserDTO> allUsers = new ArrayList<>();
        for (User user: users){
            if(user.getRole()!= UserRole.ADMIN){
                allUsers.add(new UserDTO(user.getId(),user.getRole().toString(),user.getName(),user.getLastName(),user.getEmail()));
            }
        }
        return allUsers;
    }
}
