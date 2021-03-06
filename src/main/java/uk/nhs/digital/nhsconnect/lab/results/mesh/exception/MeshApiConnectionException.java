package uk.nhs.digital.nhsconnect.lab.results.mesh.exception;

import org.springframework.http.HttpStatus;
import uk.nhs.digital.nhsconnect.lab.results.exception.LabResultsBaseException;

public class MeshApiConnectionException extends LabResultsBaseException {

    public MeshApiConnectionException(String description, HttpStatus expectedStatus, HttpStatus actualStatus) {
        super(description + " Expected status code: " + expectedStatus.value()
            + ", but received: " + actualStatus.value());
    }

    public MeshApiConnectionException(String description,
                                      HttpStatus expectedStatus,
                                      HttpStatus actualStatus,
                                      String content) {
        super(description + " Expected status code: " + expectedStatus.value()
            + ", but received: " + actualStatus.value() + " with response content\n" + content);
    }

    public MeshApiConnectionException(String message) {
        super(message);
    }
}
