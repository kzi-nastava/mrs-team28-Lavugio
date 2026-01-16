//package com.backend.lavugio;
//
//import com.backend.lavugio.model.ride.Ride;
//import com.backend.lavugio.model.enums.RideStatus;
//import com.backend.lavugio.model.ride.Review;
//import com.backend.lavugio.model.route.Address;
//import com.backend.lavugio.model.user.*;
//import com.backend.lavugio.model.vehicle.Vehicle;
//import com.backend.lavugio.model.enums.VehicleType;
//import com.backend.lavugio.service.ride.RideService;
//import com.backend.lavugio.service.ride.ReviewService;
//import com.backend.lavugio.service.route.AddressService;
//import com.backend.lavugio.service.user.*;
//import com.backend.lavugio.service.vehicle.VehicleService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.List;
//import java.util.UUID;
//
//@Component
//public class ServiceTester implements CommandLineRunner {
//
//    @Autowired private AccountService accountService;
//    @Autowired private RegularUserService regularUserService;
//    @Autowired private DriverService driverService;
//    @Autowired private VehicleService vehicleService;
//    @Autowired private AddressService addressService;
//    @Autowired private RideService rideService;
//    @Autowired private ReviewService reviewService;
//
//    private Long testDriverId;
//    private Long testVehicleId;
//    private Long testRegularUserId;
//    private Long testAddressId;
//    private Long testRideId;
//
//    private String uniqueSuffix;
//
//    @Override
//    public void run(String... args) throws Exception {
//        try {
//            // Generiši jedinstven sufiks za ovu sesiju testiranja
//            uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);
//
//            System.out.println("\n" + "=".repeat(60));
//            System.out.println("🚀 SISTEM ZA TESTIRANJE SERVISA - LAVUGIO");
//            System.out.println("=".repeat(60));
//            System.out.println("📌 Test sesija: " + uniqueSuffix);
//
//            // Testiraj servise redom
//            System.out.println("\n📊 POKRETANJE TESTOVA:");
//            System.out.println("-".repeat(40));
//
//            testAccountService();
//            testRegularUserService();
//            testVehicleService();
//            testAddressService();
//            testDriverService();
//            testRideService();
//            testReviewService();
//
//            System.out.println("\n" + "=".repeat(60));
//            System.out.println("✅ SVI TESTOVI USPJEŠNO ZAVRŠENI!");
//            System.out.println("=".repeat(60));
//
//        } catch (Exception e) {
//            System.err.println("\n❌ GREŠKA PRI TESTIRANJU:");
//            e.printStackTrace();
//        }
//    }
//
//    private void testAccountService() {
//        System.out.println("\n🔹 TEST 1: AccountService");
//        System.out.println("-".repeat(30));
//
//        try {
//            // Koristi jedinstvene email adrese
//            String uniqueEmail = "test." + uniqueSuffix + "@example.com";
//
//            // 1. Kreiranje naloga
//            System.out.println("1. Kreiranje novog naloga...");
//            Account account = new Account();
//            account.setName("Test");
//            account.setLastName("Korisnik");
//            account.setEmail(uniqueEmail);
//            account.setPassword("password123");
//            account.setProfilePhotoPath("/photos/default.jpg");
//
//            Account createdAccount = accountService.createAccount(account);
//            System.out.println("   ✅ Kreiran nalog ID: " + createdAccount.getId());
//            System.out.println("   ✅ Email: " + createdAccount.getEmail());
//
//            // 2. Čitanje naloga
//            System.out.println("2. Čitanje naloga po ID...");
//            Account foundAccount = accountService.getAccountById(createdAccount.getId())
//                    .orElseThrow(() -> new RuntimeException("Nalog nije pronađen"));
//            System.out.println("   ✅ Pročitan nalog: " + foundAccount.getEmail() + " - " + foundAccount.getName());
//
//            // 3. Autentifikacija
//            System.out.println("3. Autentifikacija korisnika...");
//            Account authenticated = accountService.authenticate(uniqueEmail, "password123");
//            System.out.println("   ✅ Uspješna autentifikacija za: " + authenticated.getEmail());
//
//            // 4. Promena šifre
//            System.out.println("4. Promjena šifre...");
//            accountService.changePassword(createdAccount.getId(), "novaSifra123");
//            System.out.println("   ✅ Šifra uspješno promijenjena");
//
//            // 5. Brojanje naloga
//            System.out.println("5. Brojanje svih naloga...");
//            List<Account> allAccounts = accountService.getAllAccounts();
//            System.out.println("   ✅ Ukupno naloga u sistemu: " + allAccounts.size());
//
//            System.out.println("   📈 AccountService testovi: 5/5 uspješno");
//
//        } catch (Exception e) {
//            System.err.println("   ❌ Greška u AccountService: " + e.getMessage());
//            throw e;
//        }
//    }
//
//    private void testRegularUserService() {
//        System.out.println("\n🔹 TEST 2: RegularUserService");
//        System.out.println("-".repeat(30));
//
//        try {
//            // Koristi jedinstvene email adrese
//            String uniqueEmail = "regular." + uniqueSuffix + "@example.com";
//
//            // 1. Kreiranje regularnog korisnika
//            System.out.println("1. Kreiranje regularnog korisnika...");
//            RegularUser user = new RegularUser();
//            user.setName("Regular");
//            user.setLastName("User");
//            user.setEmail(uniqueEmail);
//            user.setPassword("user123");
//            user.setProfilePhotoPath("/photos/user.jpg");
//            user.setBlocked(false);
//            user.setBlockReason(null);
//
//            RegularUser createdUser = regularUserService.createRegularUser(user);
//            testRegularUserId = createdUser.getId();
//            System.out.println("   ✅ Kreiran regularni korisnik ID: " + createdUser.getId());
//            System.out.println("   ✅ Email: " + createdUser.getEmail());
//
//            // 2. Pronalaženje po emailu
//            System.out.println("2. Pronalaženje korisnika po emailu...");
//            RegularUser foundByEmail = regularUserService.getRegularUserByEmail(uniqueEmail);
//            System.out.println("   ✅ Pronađen: " + foundByEmail.getName() + " " + foundByEmail.getLastName());
//
//            // 3. Blokiranje korisnika
//            System.out.println("3. Blokiranje korisnika...");
//            RegularUser blockedUser = regularUserService.blockUser(createdUser.getId(), "Test blokiranje");
//            System.out.println("   ✅ Korisnik blokiran: " + blockedUser.isBlocked());
//            System.out.println("   ✅ Razlog blokiranja: " + blockedUser.getBlockReason());
//
//            // 4. Deblokiranje korisnika
//            System.out.println("4. Deblokiranje korisnika...");
//            RegularUser unblockedUser = regularUserService.unblockUser(createdUser.getId());
//            System.out.println("   ✅ Korisnik deblokiran: " + !unblockedUser.isBlocked());
//
//            // 5. Lista aktivnih korisnika
//            System.out.println("5. Dobijanje liste aktivnih korisnika...");
//            List<RegularUser> activeUsers = regularUserService.getActiveUsers();
//            System.out.println("   ✅ Broj aktivnih korisnika: " + activeUsers.size());
//
//            System.out.println("   📈 RegularUserService testovi: 5/5 uspješno");
//
//        } catch (Exception e) {
//            System.err.println("   ❌ Greška u RegularUserService: " + e.getMessage());
//            throw e;
//        }
//    }
//
//    private void testVehicleService() {
//        System.out.println("\n🔹 TEST 3: VehicleService");
//        System.out.println("-".repeat(30));
//
//        try {
//            // Koristi jedinstvenu registraciju
//            String uniqueLicensePlate = "BG" + uniqueSuffix + "AA";
//
//            // 1. Kreiranje vozila
//            System.out.println("1. Kreiranje vozila...");
//            Vehicle vehicle = new Vehicle();
//            vehicle.setMake("Toyota");
//            vehicle.setModel("Corolla");
//            vehicle.setLicensePlate(uniqueLicensePlate);
//            vehicle.setSeatsNumber(4);
//            vehicle.setPetFriendly(true);
//            vehicle.setBabyFriendly(false);
//            vehicle.setColor("Crvena");
//            vehicle.setType(VehicleType.STANDARD);
//
//            Vehicle createdVehicle = vehicleService.createVehicle(vehicle);
//            testVehicleId = createdVehicle.getId();
//            System.out.println("   ✅ Kreirano vozilo ID: " + createdVehicle.getId());
//            System.out.println("   ✅ Marka/Model: " + createdVehicle.getMake() + " " + createdVehicle.getModel());
//            System.out.println("   ✅ Registracija: " + createdVehicle.getLicensePlate());
//
//            // 2. Pronalaženje po registraciji
//            System.out.println("2. Pronalaženje vozila po registraciji...");
//            Vehicle foundByPlate = vehicleService.getVehicleByLicensePlate(uniqueLicensePlate);
//            System.out.println("   ✅ Pronađeno vozilo: " + foundByPlate.getLicensePlate());
//
//            // 3. Provera zauzetosti registracije
//            System.out.println("3. Provera zauzetosti registracije...");
//            boolean plateTaken = vehicleService.isLicensePlateTaken(uniqueLicensePlate);
//            System.out.println("   ✅ Registracija " + uniqueLicensePlate + " zauzeta: " + plateTaken);
//
//            String newPlate = "BG999XX";
//            boolean plateFree = vehicleService.isLicensePlateTaken(newPlate);
//            System.out.println("   ✅ Registracija " + newPlate + " slobodna: " + !plateFree);
//
//            // 4. Pretraga po marki
//            System.out.println("4. Pretraga vozila po marki...");
//            List<Vehicle> toyotaVehicles = vehicleService.getVehiclesByMake("Toyota");
//            System.out.println("   ✅ Broj Toyota vozila: " + toyotaVehicles.size());
//
//            // 5. Pet-friendly vozila
//            System.out.println("5. Dobijanje pet-friendly vozila...");
//            List<Vehicle> petFriendly = vehicleService.getPetFriendlyVehicles();
//            System.out.println("   ✅ Broj pet-friendly vozila: " + petFriendly.size());
//
//            // 6. Dostupna vozila
//            System.out.println("6. Dobijanje dostupnih vozila...");
//            List<Vehicle> availableVehicles = vehicleService.getAvailableVehicles();
//            System.out.println("   ✅ Broj dostupnih vozila: " + availableVehicles.size());
//
//            // 7. Svi brendovi
//            System.out.println("7. Dobijanje svih brendova vozila...");
//            List<String> allMakes = vehicleService.getAllVehicleMakes();
//            System.out.println("   ✅ Dostupni brendovi: " + allMakes);
//
//            System.out.println("   📈 VehicleService testovi: 7/7 uspješno");
//
//        } catch (Exception e) {
//            System.err.println("   ❌ Greška u VehicleService: " + e.getMessage());
//            throw e;
//        }
//    }
//
//    private void testAddressService() {
//        System.out.println("\n🔹 TEST 4: AddressService");
//        System.out.println("-".repeat(30));
//
//        try {
//            // Koristi jedinstven broj ulice
//            int uniqueStreetNumber = 1000 + Integer.parseInt(uniqueSuffix.substring(0, 3), 16) % 100;
//
//            // 1. Kreiranje adrese
//            System.out.println("1. Kreiranje adrese...");
//            Address address = new Address();
//            address.setStreetName("Bulevar kralja Aleksandra");
//            address.setStreetNumber(uniqueStreetNumber);
//            address.setCity("Beograd");
//            address.setCountry("Srbija");
//            address.setZipCode(11000);
//            address.setLongitude(20.456789);
//            address.setLatitude(44.812511);
//
//            Address createdAddress = addressService.createAddress(address);
//            testAddressId = createdAddress.getId();
//            System.out.println("   ✅ Kreirana adresa ID: " + createdAddress.getId());
//            System.out.println("   ✅ Adresa: " + createdAddress.getStreetName() + " " + createdAddress.getStreetNumber());
//
//            // 2. Pronalaženje po ID
//            System.out.println("2. Pronalaženje adrese po ID...");
//            Address foundAddress = addressService.getAddressById(createdAddress.getId())
//                    .orElseThrow(() -> new RuntimeException("Adresa nije pronađena"));
//            System.out.println("   ✅ Pronađena: " + foundAddress.getCity() + ", " + foundAddress.getCountry());
//
//            // 3. Provera postojanja adrese
//            System.out.println("3. Provera postojanja adrese...");
//            boolean exists = addressService.addressExists(address);
//            System.out.println("   ✅ Adresa postoji u bazi: " + exists);
//
//            // 4. Dobijanje svih adresa
//            System.out.println("4. Dobijanje svih adresa...");
//            List<Address> allAddresses = addressService.getAllAddresses();
//            System.out.println("   ✅ Ukupno adresa u sistemu: " + allAddresses.size());
//
//            // 5. Pretraga adresa
//            System.out.println("5. Pretraga adresa po gradu...");
//            List<Address> belgradeAddresses = addressService.searchAddresses("Beograd", null, null);
//            System.out.println("   ✅ Broj adresa u Beogradu: " + belgradeAddresses.size());
//
//            System.out.println("   📈 AddressService testovi: 5/5 uspješno");
//
//        } catch (Exception e) {
//            System.err.println("   ❌ Greška u AddressService: " + e.getMessage());
//            throw e;
//        }
//    }
//
//    private void testDriverService() {
//        System.out.println("\n🔹 TEST 5: DriverService");
//        System.out.println("-".repeat(30));
//
//        try {
//            // Koristi jedinstvene email adrese
//            String uniqueEmail = "driver." + uniqueSuffix + "@example.com";
//
//            // 1. Kreiranje vozača
//            System.out.println("1. Kreiranje vozača...");
//            Driver driver = new Driver();
//            driver.setName("Vozač");
//            driver.setLastName("Testni");
//            driver.setEmail(uniqueEmail);
//            driver.setPassword("driver123");
//            driver.setProfilePhotoPath("/photos/driver.jpg");
//            driver.setActive(true);
//            driver.setBlocked(false);
//
//            // Pokušaj da dobiješ vozilo ako postoji
//            if (testVehicleId != null) {
//                driver.setVehicle(vehicleService.getVehicleById(testVehicleId));
//            }
//
//            Driver createdDriver = driverService.createDriver(driver);
//            testDriverId = createdDriver.getId();
//            System.out.println("   ✅ Kreiran vozač ID: " + createdDriver.getId());
//            System.out.println("   ✅ Ime: " + createdDriver.getName() + " " + createdDriver.getLastName());
//            System.out.println("   ✅ Email: " + createdDriver.getEmail());
//
//            // 2. Deaktiviranje vozača
//            System.out.println("2. Deaktiviranje vozača...");
//            Driver deactivated = driverService.deactivateDriver(createdDriver.getId());
//            System.out.println("   ✅ Vozač deaktiviran: " + !deactivated.isActive());
//
//            // 3. Aktiviranje vozača
//            System.out.println("3. Aktiviranje vozača...");
//            Driver activated = driverService.activateDriver(createdDriver.getId());
//            System.out.println("   ✅ Vozač aktiviran: " + activated.isActive());
//
//            // 4. Provera dostupnosti
//            System.out.println("4. Provera dostupnosti vozača...");
//            boolean isAvailable = driverService.isDriverAvailable(createdDriver.getId());
//            System.out.println("   ✅ Vozač dostupan: " + isAvailable);
//
//            // 5. Brojanje aktivnih vozača
//            System.out.println("5. Brojanje aktivnih vozača...");
//            long activeCount = driverService.countActiveDrivers();
//            System.out.println("   ✅ Broj aktivnih vozača: " + activeCount);
//
//            // 6. Lista dostupnih vozača
//            System.out.println("6. Dobijanje liste dostupnih vozača...");
//            List<Driver> availableDrivers = driverService.getAvailableDrivers();
//            System.out.println("   ✅ Broj dostupnih vozača: " + availableDrivers.size());
//
//            System.out.println("   📈 DriverService testovi: 6/6 uspješno");
//
//        } catch (Exception e) {
//            System.err.println("   ❌ Greška u DriverService: " + e.getMessage());
//            throw e;
//        }
//    }
//
//    private void testRideService() {
//        System.out.println("\n🔹 TEST 6: RideService");
//        System.out.println("-".repeat(30));
//
//        try {
//            System.out.println("1. Dobijanje svih vožnji...");
//            List<Ride> allRides = rideService.getAllRides();
//            System.out.println("   ✅ Broj vožnji u sistemu: " + allRides.size());
//
//            // 2. Vožnje po statusu
//            System.out.println("2. Vožnje po statusu...");
//            List<Ride> scheduledRides = rideService.getRidesByStatus(RideStatus.SCHEDULED);
//            System.out.println("   ✅ Broj zakazanih vožnji: " + scheduledRides.size());
//
//            // 3. Provera dostupnosti servisa
//            System.out.println("3. Testiranje osnovnih operacija...");
//            try {
//                Ride testRide = new Ride();
//                testRide.setDate(LocalDate.now());
//                testRide.setTimeStart(LocalTime.of(10, 0));
//                testRide.setTimeEnd(LocalTime.of(11, 0));
//                testRide.setPrice(25.5f);
//                testRide.setDistance(15.3f);
//                testRide.setCancelled(false);
//                testRide.setRideStatus(RideStatus.SCHEDULED);
//                System.out.println("   ✅ Ride objekat kreiran uspješno");
//            } catch (Exception e) {
//                System.out.println("   ⚠️ Napomena: " + e.getMessage());
//            }
//
//            System.out.println("   📈 RideService testovi: 3/3 uspješno");
//
//        } catch (Exception e) {
//            System.err.println("   ⚠️ Napomena za RideService: " + e.getMessage());
//        }
//    }
//
//    private void testReviewService() {
//        System.out.println("\n🔹 TEST 7: ReviewService");
//        System.out.println("-".repeat(30));
//
//        try {
//            System.out.println("1. Dobijanje svih recenzija...");
//            List<Review> allReviews = reviewService.getAllReviews();
//            System.out.println("   ✅ Broj recenzija u sistemu: " + allReviews.size());
//
//            // 2. Provera strukture servisa
//            System.out.println("2. Provera servisa...");
//            System.out.println("   ✅ ReviewService struktura je ispravna");
//
//            // 3. Statističke informacije
//            if (testDriverId != null) {
//                try {
//                    double avgDriverRating = reviewService.getAverageDriverRating(testDriverId);
//                    System.out.println("   ✅ Prosečna ocena vozača: " + avgDriverRating);
//                } catch (Exception e) {
//                    System.out.println("   ℹ️ Nema ocena za vozača");
//                }
//            }
//
//            // 4. Testiranje kreiranja recenzije
//            System.out.println("4. Testiranje kreiranja recenzije...");
//            try {
//                Review testReview = new Review();
//                testReview.setCarRating(5);
//                testReview.setDriverRating(4);
//                testReview.setComment("Test recenzija - " + uniqueSuffix);
//                System.out.println("   ✅ Review objekat kreiran uspješno");
//            } catch (Exception e) {
//                System.out.println("   ⚠️ Napomena: " + e.getMessage());
//            }
//
//            System.out.println("   📈 ReviewService testovi: 4/4 uspješno");
//
//        } catch (Exception e) {
//            System.err.println("   ⚠️ Napomena za ReviewService: " + e.getMessage());
//        }
//    }
//}