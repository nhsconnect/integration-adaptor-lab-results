package uk.nhs.digital.nhsconnect.lab.results.inbound;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.Bundle;
import uk.nhs.digital.nhsconnect.lab.results.model.edifact.Message;

@Getter
@RequiredArgsConstructor
public abstract class MessageProcessingResult {
    private final Message message;

    @Getter
    public static final class Success extends MessageProcessingResult {
        private final Bundle bundle;

        public Success(Message message, Bundle bundle) {
            super(message);
            this.bundle = bundle;
        }
    }

    @Getter
    public static final class Error extends MessageProcessingResult {
        private final Exception exception;

        public Error(Message message, Exception exception) {
            super(message);
            this.exception = exception;
        }
    }
}
