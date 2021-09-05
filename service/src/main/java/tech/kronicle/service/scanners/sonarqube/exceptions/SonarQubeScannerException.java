package tech.kronicle.service.scanners.sonarqube.exceptions;

public class SonarQubeScannerException extends RuntimeException {

    public SonarQubeScannerException(String message) {
        super(message);
    }
}
