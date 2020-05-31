package app.service.configuration;

public interface IGenericConfiguration {

    void updateApp();

    void wipeData();

    boolean changePassword(String currentPassword, String newPassword, String repeatPassword);

    void resetUser();

}
