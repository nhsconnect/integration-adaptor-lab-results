package uk.nhs.digital.nhsconnect.lab.results.model.edifact;

import lombok.Getter;
import lombok.Setter;
import uk.nhs.digital.nhsconnect.lab.results.model.edifact.segmentgroup.InvolvedParty;
import uk.nhs.digital.nhsconnect.lab.results.model.edifact.segmentgroup.ServiceReportDetails;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class Message extends Section {
    private static final String DEFAULT_GP_CODE = "9999";

    @Getter(lazy = true)
    private final MessageHeader messageHeader =
        MessageHeader.fromString(extractSegment(MessageHeader.KEY));

    @Getter(lazy = true)
    private final HealthAuthorityNameAndAddress healthAuthorityNameAndAddress =
        HealthAuthorityNameAndAddress.fromString(extractSegment(HealthAuthorityNameAndAddress.KEY_QUALIFIER));

    @Getter(lazy = true)
    private final List<InvolvedParty> involvedParties = InvolvedParty.createMultiple(getEdifactSegments().stream()
        .dropWhile(segment -> !segment.startsWith(InvolvedParty.INDICATOR))
        .takeWhile(segment -> !segment.startsWith(ServiceReportDetails.INDICATOR))
        .collect(toList()));

    @Getter(lazy = true)
    private final ServiceReportDetails serviceReportDetails = new ServiceReportDetails(getEdifactSegments().stream()
        .dropWhile(segment -> !segment.startsWith(ServiceReportDetails.INDICATOR))
        .takeWhile(segment -> !segment.startsWith(MessageTrailer.KEY))
        .collect(toList()));

    @Getter(lazy = true)
    private final MessageTrailer messageTrailer =
        MessageTrailer.fromString(extractSegment(MessageTrailer.KEY));

    @Getter
    @Setter
    private Interchange interchange;

    public Message(final List<String> edifactSegments) {
        super(edifactSegments);
    }

    public String findFirstGpCode() {
        return extractOptionalSegment(GpNameAndAddress.KEY_QUALIFIER)
            .stream()
            .map(GpNameAndAddress::fromString)
            .map(GpNameAndAddress::getIdentifier)
            .findFirst()
            .orElse(DEFAULT_GP_CODE);
    }

    @Override
    public String toString() {
        return String.format("Message{SIS: %s, SMS: %s}",
            getInterchange().getInterchangeHeader().getSequenceNumber(),
            getMessageHeader().getSequenceNumber());
    }
}
