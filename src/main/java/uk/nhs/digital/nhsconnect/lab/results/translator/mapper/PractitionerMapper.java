package uk.nhs.digital.nhsconnect.lab.results.translator.mapper;

import java.util.Optional;

import org.hl7.fhir.dstu3.model.Practitioner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.nhs.digital.nhsconnect.lab.results.model.edifact.Message;
import uk.nhs.digital.nhsconnect.lab.results.model.edifact.segmentgroup.InvolvedParty;
import uk.nhs.digital.nhsconnect.lab.results.utils.UUIDGenerator;

import static uk.nhs.digital.nhsconnect.lab.results.model.edifact.ServiceProviderCode.PROFESSIONAL;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PractitionerMapper {
    private final UUIDGenerator uuidGenerator;

    private static final String SDS_USER_SYSTEM = "https://fhir.nhs.uk/Id/sds-user-id";

    public Optional<Practitioner> mapToRequestingPractitioner(final Message message) {
        return message.getInvolvedParties().stream()
            .filter(party -> party.getServiceProvider().getServiceProviderCode().equals(PROFESSIONAL))
            .map(InvolvedParty::getRequesterNameAndAddress)
            .flatMap(Optional::stream)
            .map(r -> mapToPractitioner(r.getIdentifier(), r.getPractitionerName()))
            .findFirst();
    }

    public Optional<Practitioner> mapToPerformingPractitioner(final Message message) {
        return message.getInvolvedParties().stream()
            .filter(party -> party.getServiceProvider().getServiceProviderCode().equals(PROFESSIONAL))
            .map(InvolvedParty::getPerformerNameAndAddress)
            .flatMap(Optional::stream)
            .map(p -> mapToPractitioner(p.getIdentifier(), p.getPractitionerName()))
            .findFirst();
    }

    private Practitioner mapToPractitioner(final String identifier, final String name) {
        final var result = new Practitioner();

        result.addIdentifier()
            .setValue(identifier)
            .setSystem(SDS_USER_SYSTEM);
        Optional.ofNullable(name)
            .ifPresent(n -> result.addName().setText(n));
        result.setId(uuidGenerator.generateUUID());

        return result;
    }
}
