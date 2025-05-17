package co.g3a.springbootclientrop.shared.errorhandling;

public interface AppErrorDefinition {
    String detail();
    String code();
    int status();
    void logErrorMessage();
}