package uk.nhs.digital.nhsconnect.lab.results.sequence;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SequenceService {
    private static final String INTERCHANGE_FORMAT = "SIS-%s-%s";
    private static final String INTERCHANGE_MESSAGE_FORMAT = "SMS-%s-%s";

    @Autowired
    private SequenceRepository sequenceRepository;

    public Long generateInterchangeSequence(final String sender, final String recipient) {
        validateSenderAndRecipient(sender, recipient);
        return getNextSequence(String.format(INTERCHANGE_FORMAT, sender, recipient));
    }

    public Long generateMessageSequence(final String sender, final String recipient) {
        validateSenderAndRecipient(sender, recipient);
        return getNextSequence(String.format(INTERCHANGE_MESSAGE_FORMAT, sender, recipient));
    }

    private void validateSenderAndRecipient(final String sender, final String recipient) {
        if (StringUtils.isBlank(sender) || StringUtils.isBlank(recipient)) {
            throw new SequenceException(
                String.format("Sender or recipient not valid. Sender: %s, recipient: %s", sender, recipient)
            );
        }
    }

    private Long getNextSequence(final String key) {
        return sequenceRepository.getNext(key);
    }
}
