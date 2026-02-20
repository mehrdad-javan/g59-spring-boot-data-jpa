package se.lexicon.g59springbootdatajpa.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import se.lexicon.g59springbootdatajpa.entity.User;
import se.lexicon.g59springbootdatajpa.entity.UserProfile;
import se.lexicon.g59springbootdatajpa.repository.UserProfileRepository;
import se.lexicon.g59springbootdatajpa.repository.UserRepository;

@Component
public class MyCommandLineRunner implements CommandLineRunner {

    private UserRepository userRepository;
    private UserProfileRepository userProfileRepository;

    @Autowired
    public MyCommandLineRunner(UserRepository userRepository, UserProfileRepository userProfileRepository) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("--------------------------------------------------");
        System.out.println("Starting CommandLineRunner - Seeding Data...");
        System.out.println("--------------------------------------------------");

        // Create User 1
        User userData1 = new User();
        userData1.setEmail("sven.svensson@example.se");
        userData1.setFullName("Sven Svensson");

        // Create Profile 1
        UserProfile profileData1 = new UserProfile();
        profileData1.setNickname("Svenne");
        profileData1.setBio("A proud Swede who loves Fika.");
        profileData1.setAddress("Storgatan 1, Stockholm");


        User savedUser1 = userRepository.save(userData1);
        System.out.println("Saved User: " + savedUser1);

        // Link User and Profile (Manual linking)
        profileData1.setUser(savedUser1);

        UserProfile savedUserProfile1 = userProfileRepository.save(profileData1);
        System.out.println("Saved UserProfile: " + savedUserProfile1);

        System.out.println("User 1 and Profile 1 saved successfully.");

        // Create User 2
        User userData2 = new User();
        userData2.setEmail("anna.andersson@example.se");
        userData2.setFullName("Anna Andersson");

        // Create Profile 2
        UserProfile profileData2 = new UserProfile();
        profileData2.setNickname("Ankan");
        profileData2.setBio("Loves nature and Swedish meatballs.");
        profileData2.setAddress("Hamngatan 5, Gothenburg");

        // Save User 2
        User savedUser2 = userRepository.save(userData2);
        System.out.println("Saved User: " + savedUser2);

        // Link User 2 and Profile 2
        profileData2.setUser(savedUser2);

        // Save Profile 2
        UserProfile savedUserProfile2 = userProfileRepository.save(profileData2);
        System.out.println("Saved UserProfile: " + savedUserProfile2);

        System.out.println("User 2 and Profile 2 saved successfully.");

    }
}
