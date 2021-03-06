package uk.nhs.digital.nhsconnect.lab.results.model.edifact.segmentgroup;

import lombok.Getter;
import lombok.NonNull;
import uk.nhs.digital.nhsconnect.lab.results.model.edifact.ClinicalInformationCode;
import uk.nhs.digital.nhsconnect.lab.results.model.edifact.FreeTextSegment;
import uk.nhs.digital.nhsconnect.lab.results.model.enums.FreeTextType;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * Provides clinical information about the patient.
 * <p>
 * Segment group 10: {@code S10-CIN-DTM-FTX-SG14}
 * <ul>
 *     <li>{@code CIN} is mandatory. Must be qualified with {@code +UN}.</li>
 *     <li>{@code DTM} is not used.</li>
 *     <li>{@code FTX} is mandatory. May have up to 99 instances. Each must be qualified with {@code +CID}.</li>
 *     <li>{@code SG14} is not used.</li>
 * </ul>
 * <p>
 * Parents: {@link uk.nhs.digital.nhsconnect.lab.results.model.edifact.Message Message}
 * &gt; {@link ServiceReportDetails}
 * &gt; {@link InvestigationSubject}
 */
public class PatientClinicalInfo extends SegmentGroup {
    public static final String INDICATOR = "S10";

    @Getter(lazy = true)
    private final ClinicalInformationCode code =
        ClinicalInformationCode.fromString(extractSegment(ClinicalInformationCode.KEY));

    @Getter(lazy = true)
    private final List<FreeTextSegment> freeTexts =
        extractSegments(FreeTextSegment.KEY).stream()
            .map(FreeTextSegment::fromString)
            .filter(segment -> FreeTextType.CLINICAL_INFO.equals(segment.getType()))
            .collect(toList());

    public static Optional<PatientClinicalInfo> createOptional(@NonNull final List<String> edifactSegments) {
        if (edifactSegments.isEmpty() || !edifactSegments.get(0).startsWith(INDICATOR)) {
            return Optional.empty();
        }
        return Optional.of(new PatientClinicalInfo(edifactSegments));
    }

    public PatientClinicalInfo(final List<String> edifactSegments) {
        super(edifactSegments);
    }
}
