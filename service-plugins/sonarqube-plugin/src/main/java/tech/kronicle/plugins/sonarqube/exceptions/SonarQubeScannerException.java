package tech.kronicle.plugins.sonarqube.exceptions;

public class SonarQubeScannerException extends RuntimeException {

    public SonarQubeScannerException(String message) {
        super(message);
    }
}
