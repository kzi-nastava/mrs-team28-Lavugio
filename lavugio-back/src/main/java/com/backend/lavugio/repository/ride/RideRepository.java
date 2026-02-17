package com.backend.lavugio.repository.ride;

import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.enums.RideStatus;
import com.backend.lavugio.model.user.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {

    // Find by driver
    List<Ride> findByDriver(Driver driver);

    List<Ride> findByDriverId(Long driverId);

    // Find by status
    List<Ride> findByRideStatus(RideStatus status);

    // Combined queries
    List<Ride> findByDriverIdAndRideStatus(Long driverId, RideStatus status);

    // Find by price
    List<Ride> findByPriceGreaterThan(float minPrice);

    List<Ride> findByPriceBetween(float minPrice, float maxPrice);

    // Find by passengers (ManyToMany relationship)

    @Query("SELECT r FROM Ride r JOIN r.passengers p WHERE p.id = :passengerId")
    List<Ride> findByPassengerId(@Param("passengerId") Long passengerId);

    // Count queries
    long countByRideStatus(RideStatus status);

    long countByDriverId(Long driverId);

    // Custom queries
    @Query("SELECT r FROM Ride r WHERE r.driver.id = :driverId " +
            "AND r.startDateTime >= :fromDate " +
            "AND r.rideStatus = 'SCHEDULED' " +
            "ORDER BY r.startDateTime ASC")
    List<Ride> findUpcomingRidesByDriver(@Param("driverId") Long driverId,
                                         @Param("fromDate") LocalDateTime fromDate);

    @Query("SELECT r FROM Ride r WHERE r.rideStatus IN ('SCHEDULED', 'ACTIVE')")
    List<Ride> findAllActiveOrScheduledRides();

    @Query("SELECT r FROM Ride r WHERE r.rideStatus IN ('ACTIVE') ORDER BY r.startDateTime ASC")
    List<Ride> findAllActiveRides();

    @Query("""
        SELECT r
        FROM Ride r
        WHERE r.rideStatus = :status
          AND r.driver.id = :driverId
    """)
    List<Ride> findAllRidesForDriverByStatus(
            @Param("driverId") Long driverId,
            @Param("status") RideStatus status
    );

    @Query("SELECT r FROM Ride r JOIN r.passengers p " +
            "WHERE p.id = :passengerId " +
            "AND r.startDateTime BETWEEN :startDate AND :endDate " +
            "AND r.endDateTime BETWEEN :startDate AND :endDate " +
            "ORDER BY r.startDateTime DESC")
    List<Ride> findRidesForPassengerInDateRange(@Param("passengerId") Long passengerId,
                                                @Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);


    @Query("""
        SELECT DISTINCT r
        FROM Ride r
        LEFT JOIN FETCH r.driver
        WHERE r.driver.id = :driverId
        AND r.rideStatus = :status
    """)
    List<Ride> findByDriverIdAndRideStatusWithDriver(
            @Param("driverId") Long driverId,
            @Param("status") RideStatus status
    );

    // Aggregation queries
    @Query("SELECT SUM(r.price) FROM Ride r " +
            "WHERE r.driver.id = :driverId " +
            "AND r.rideStatus = 'FINISHED' ")
    Optional<Float> calculateTotalEarningsForDriver(@Param("driverId") Long driverId);

    @Query("SELECT SUM(r.distance) FROM Ride r " +
            "WHERE r.driver.id = :driverId " +
            "AND r.rideStatus = 'FINISHED'")
    Optional<Float> calculateTotalDistanceForDriver(@Param("driverId") Long driverId);

    @Query("SELECT AVG(r.price) FROM Ride r " +
            "WHERE r.driver.id = :driverId " +
            "AND r.rideStatus = 'FINISHED'")
    Optional<Float> calculateAverageFareForDriver(@Param("driverId") Long driverId);

    // Find rides with multiple passengers
    @Query("SELECT r FROM Ride r WHERE SIZE(r.passengers) > 1")
    List<Ride> findRidesWithMultiplePassengers();

    // Find available rides for a date
    @Query("SELECT r FROM Ride r " +
            "WHERE r.startDateTime = :date " +
            "AND r.rideStatus = 'SCHEDULED' ")
    List<Ride> findAvailableRidesForDate(@Param("date") LocalDateTime date);

    List<Ride> findByStartDateTimeBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Ride> findByStartDateTime(LocalDateTime date);

        @Query("SELECT r FROM Ride r WHERE r.rideStatus = 'FINISHED' " +
            "AND r.startDateTime BETWEEN :startDate AND :endDate")
        List<Ride> findFinishedRidesInDateRange(@Param("startDate") LocalDateTime startDate,
                            @Param("endDate") LocalDateTime endDate);

    @Query(value = """
        SELECT r.*
        FROM rides r
        JOIN ride_destinations cStart ON cStart.ride_id = r.id\s
            AND cStart.destination_order = (
                SELECT MIN(c1.destination_order)\s
                FROM ride_destinations c1\s
                WHERE c1.ride_id = r.id
            )
        JOIN ride_destinations cEnd ON cEnd.ride_id = r.id\s
            AND cEnd.destination_order = (
                SELECT MAX(c2.destination_order)\s
                FROM ride_destinations c2\s
                WHERE c2.ride_id = r.id
            )
        JOIN addresses aStart ON aStart.id = cStart.address_id
        JOIN addresses aEnd ON aEnd.id = cEnd.address_id
        WHERE r.driver_id = :driverId\s
            AND r.start_date_time BETWEEN :startDate AND :endDate
            AND r.ride_status IN ('FINISHED', 'CANCELLED', 'DENIED')
        ORDER BY\s
            CASE WHEN :sorting = 'ASC' THEN
                CASE\s
                    WHEN :sortBy = 'START' THEN TO_CHAR(r.start_date_time, 'YYYY-MM-DD HH24:MI:SS')
                    WHEN :sortBy = 'DEPARTURE' THEN CONCAT(aStart.street_name, ' ', aStart.street_number, ' ', aStart.city)
                    WHEN :sortBy = 'DESTINATION' THEN CONCAT(aEnd.street_name, ' ', aEnd.street_number, ' ', aEnd.city)
                END
            END  NULLS LAST,
            CASE WHEN :sorting = 'DESC' THEN
                CASE\s
                    WHEN :sortBy = 'START' THEN TO_CHAR(r.start_date_time, 'YYYY-MM-DD HH24:MI:SS')
                    WHEN :sortBy = 'DEPARTURE' THEN CONCAT(aStart.street_name, ' ', aStart.street_number, ' ', aStart.city)
                    WHEN :sortBy = 'DESTINATION' THEN CONCAT(aEnd.street_name, ' ', aEnd.street_number, ' ', aEnd.city)
                END
            END DESC NULLS LAST
        """,
                    countQuery = """
            SELECT COUNT(DISTINCT r.id)
            FROM rides r
            WHERE r.driver_id = :driverId\s
                AND r.start_date_time BETWEEN :startDate AND :endDate
                AND r.ride_status IN ('FINISHED', 'CANCELLED', 'DENIED')
        """,
            nativeQuery = true)
    Page<Ride> findRidesForDriver(
            @Param("driverId") Long driverId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("sortBy") String sortBy,
            @Param("sorting") String sorting,
            Pageable pageable
    );

    Ride findFirstByPassengers_IdOrderByStartDateTimeDesc(Long userId);
    @Query("SELECT r FROM Ride r WHERE r.creator.id = :creatorId")
    List<Ride> findByCreatorId(@Param("creatorId") Long creatorId);

    @Query("SELECT DISTINCT r FROM Ride r WHERE (r.creator.id = :userId OR EXISTS (SELECT p FROM r.passengers p WHERE p.id = :userId)) AND r.rideStatus = :status")
    List<Ride> findByCreatorIdAndStatus(@Param("userId") Long userId, @Param("status") RideStatus status);

        @Query("SELECT r FROM Ride r WHERE r.driver.id = :driverId " +
            "AND r.rideStatus = 'FINISHED' " +
            "AND r.startDateTime BETWEEN :startDate AND :endDate")
        List<Ride> findFinishedRidesForDriverInDateRange(@Param("driverId") Long driverId,
                                 @Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate);

        @Query("SELECT r FROM Ride r WHERE r.creator.id = :creatorId " +
            "AND r.rideStatus = 'FINISHED' " +
            "AND r.startDateTime BETWEEN :startDate AND :endDate")
        List<Ride> findFinishedRidesForCreatorInDateRange(@Param("creatorId") Long creatorId,
                                  @Param("startDate") LocalDateTime startDate,
                                  @Param("endDate") LocalDateTime endDate);

    // User (passenger) ride history with pagination and sorting
    @Query(value = """
        SELECT r.*
        FROM rides r
        JOIN ride_passengers rp ON rp.ride_id = r.id
        JOIN ride_destinations cStart ON cStart.ride_id = r.id 
            AND cStart.destination_order = (
                SELECT MIN(c1.destination_order) 
                FROM ride_destinations c1 
                WHERE c1.ride_id = r.id
            )
        JOIN ride_destinations cEnd ON cEnd.ride_id = r.id 
            AND cEnd.destination_order = (
                SELECT MAX(c2.destination_order) 
                FROM ride_destinations c2 
                WHERE c2.ride_id = r.id
            )
        JOIN addresses aStart ON aStart.id = cStart.address_id
        JOIN addresses aEnd ON aEnd.id = cEnd.address_id
        WHERE rp.user_id = :userId 
            AND r.start_date_time BETWEEN :startDate AND :endDate
            AND r.ride_status IN ('FINISHED', 'CANCELLED', 'DENIED')
        ORDER BY 
            CASE WHEN :sorting = 'ASC' THEN
                CASE 
                    WHEN :sortBy = 'START' THEN TO_CHAR(r.start_date_time, 'YYYY-MM-DD HH24:MI:SS')
                    WHEN :sortBy = 'DEPARTURE' THEN CONCAT(aStart.street_name, ' ', aStart.street_number, ' ', aStart.city)
                    WHEN :sortBy = 'DESTINATION' THEN CONCAT(aEnd.street_name, ' ', aEnd.street_number, ' ', aEnd.city)
                END
            END NULLS LAST,
            CASE WHEN :sorting = 'DESC' THEN
                CASE 
                    WHEN :sortBy = 'START' THEN TO_CHAR(r.start_date_time, 'YYYY-MM-DD HH24:MI:SS')
                    WHEN :sortBy = 'DEPARTURE' THEN CONCAT(aStart.street_name, ' ', aStart.street_number, ' ', aStart.city)
                    WHEN :sortBy = 'DESTINATION' THEN CONCAT(aEnd.street_name, ' ', aEnd.street_number, ' ', aEnd.city)
                END
            END DESC NULLS LAST
        """,
                    countQuery = """
            SELECT COUNT(DISTINCT r.id)
            FROM rides r
            JOIN ride_passengers rp ON rp.ride_id = r.id
            WHERE rp.user_id = :userId 
                AND r.start_date_time BETWEEN :startDate AND :endDate
                AND r.ride_status IN ('FINISHED', 'CANCELLED', 'DENIED')
        """,
            nativeQuery = true)
    Page<Ride> findRidesForUser(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("sortBy") String sortBy,
            @Param("sorting") String sorting,
            Pageable pageable
    );

    @Query("SELECT r FROM Ride r JOIN r.passengers p WHERE p.id = :userId AND r.id = :rideId")
    Optional<Ride> findByIdAndPassengerId(@Param("rideId") Long rideId, @Param("userId") Long userId);

    // Admin: Find rides by email (as passenger or driver)
    @Query(value = """
        SELECT DISTINCT r.*
        FROM rides r
        LEFT JOIN ride_passengers rp ON rp.ride_id = r.id
        LEFT JOIN regular_users ru ON ru.id = rp.user_id
        LEFT JOIN accounts a_passenger ON a_passenger.id = ru.id
        LEFT JOIN drivers d ON d.id = r.driver_id
        LEFT JOIN accounts a_driver ON a_driver.id = d.id
        WHERE (a_passenger.email = :email OR a_driver.email = :email)
            AND r.start_date_time BETWEEN :startDate AND :endDate
            AND r.ride_status IN ('FINISHED', 'CANCELLED', 'DENIED')
        ORDER BY r.start_date_time DESC
        """,
            countQuery = """
        SELECT COUNT(DISTINCT r.id)
        FROM rides r
        LEFT JOIN ride_passengers rp ON rp.ride_id = r.id
        LEFT JOIN regular_users ru ON ru.id = rp.user_id
        LEFT JOIN accounts a_passenger ON a_passenger.id = ru.id
        LEFT JOIN drivers d ON d.id = r.driver_id
        LEFT JOIN accounts a_driver ON a_driver.id = d.id
        WHERE (a_passenger.email = :email OR a_driver.email = :email)
            AND r.start_date_time BETWEEN :startDate AND :endDate
            AND r.ride_status IN ('FINISHED', 'CANCELLED', 'DENIED')
        """,
            nativeQuery = true)
    Page<Ride> findRidesForUserByEmail(
            @Param("email") String email,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    Optional<Ride> findById(Long rideId);

}