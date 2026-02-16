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
        System.out.println("#######################");
        User userData = User.builder()
                .email("test1@test.se")
                .fullName("Test User 1")
                .build();


        UserProfile userProfileData = UserProfile.builder().nickname("URL").user(userData).build();
        userRepository.save(userData);
        userProfileRepository.save(userProfileData);


    }
}
