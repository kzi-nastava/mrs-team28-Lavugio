package com.backend.lavugio.service.user;

import com.backend.lavugio.model.user.RegularUser;
import com.backend.lavugio.model.ride.Ride;

import java.util.List;
import java.util.Set;

public interface RegularUserService {
    RegularUser createRegularUser(RegularUser user);
    RegularUser updateRegularUser(Long id, RegularUser user);
    void deleteRegularUser(Long id);
    RegularUser getRegularUserById(Long id);
    RegularUser getRegularUserByEmail(String email);
    List<RegularUser> getAllRegularUsers();
    RegularUser blockUser(Long userId, String reason);
    RegularUser unblockUser(Long userId);
    List<RegularUser> getBlockedUsers();
    List<RegularUser> getActiveUsers();
    Set<Ride> getUserRides(Long userId);
    void addRideToUser(Long userId, Ride ride);
    void removeRideFromUser(Long userId, Ride ride);
    void enableUserOrdering(Long userId);
    void disableUserOrdering(Long userId);
}