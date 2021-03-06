package uk.nhs.digital.nhsconnect.lab.results.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.lab.results.model.edifact.message.EdifactValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessageTrailerTest {

    private static final int NUMBER_OF_SEGMENTS = 18;
    private static final long SEQUENCE_NUMBER = 3L;

    @Test
    void testValidationStatefulNonSequenceNumber() {
        MessageTrailer messageTrailer = new MessageTrailer(NUMBER_OF_SEGMENTS);

        Exception exception = assertThrows(EdifactValidationException.class, messageTrailer::validate);

        String expectedMessage = "UNT: Attribute sequenceNumber is required";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testValidationStatefulInvalidNumberOfSegments() {
        MessageTrailer messageTrailer = new MessageTrailer(-1);
        messageTrailer.setSequenceNumber(SEQUENCE_NUMBER);

        Exception exception = assertThrows(EdifactValidationException.class, messageTrailer::validate);

        String expectedMessage = "UNT: Attribute numberOfSegments must be greater than or equal to 2";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testFromString() {
        var expectedMessageTrailer = new MessageTrailer(NUMBER_OF_SEGMENTS)
                .setSequenceNumber(SEQUENCE_NUMBER);
        var edifact = "UNT+18+00000003";

        var messageTrailer = MessageTrailer.fromString(edifact);

        assertThat(messageTrailer).isExactlyInstanceOf(MessageTrailer.class);
        assertThat(messageTrailer).usingRecursiveComparison().isEqualTo(expectedMessageTrailer);
    }
}
