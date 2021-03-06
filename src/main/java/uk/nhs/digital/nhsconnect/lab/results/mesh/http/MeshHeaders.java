package uk.nhs.digital.nhsconnect.lab.results.mesh.http;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.SystemUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.lab.results.model.enums.WorkflowId;
import uk.nhs.digital.nhsconnect.lab.results.mesh.token.MeshAuthorizationToken;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MeshHeaders {

    private static final List<BasicHeader> OS_HEADERS = List.of(
            new BasicHeader("Mex-ClientVersion", "1.0"),
            new BasicHeader("Mex-OSVersion", Optional.ofNullable(SystemUtils.OS_VERSION).orElse("1.0")),
            new BasicHeader("Mex-OSName", Optional.ofNullable(SystemUtils.OS_NAME).orElse("Unix"))
    );
    private final MeshConfig meshConfig;

    protected Header[] createSendHeaders(String recipient, WorkflowId workflowId) {
        final List<BasicHeader> sendHeaders = List.of(
                new BasicHeader("Mex-From", meshConfig.getMailboxId()),
                new BasicHeader("Mex-To", recipient),
                new BasicHeader("Mex-WorkflowID", workflowId.getWorkflowId()),
                new BasicHeader("Mex-FileName", "edifact.dat"),
                new BasicHeader("Mex-MessageType", "DATA"),
                new BasicHeader("Mex-Content-Compressed", "N"),
                new BasicHeader("Content-Type", "application/octet-stream"));
        return Stream.concat(Arrays.stream(createMinimalHeaders()), sendHeaders.stream())
                .toArray(Header[]::new);
    }

    public Header[] createMinimalHeaders() {
        final List<BasicHeader> authorization = List.of(
            new BasicHeader("Authorization", new MeshAuthorizationToken(meshConfig).getValue()));
        return Stream.concat(OS_HEADERS.stream(), authorization.stream())
                .toArray(Header[]::new);
    }

    protected Header[] createAuthenticateHeaders() {
        final List<BasicHeader> authHeaders = List.of(
                new BasicHeader("Mex-JavaVersion", Runtime.version().toString()),
                new BasicHeader("Mex-OSArchitecture", Optional.ofNullable(SystemUtils.OS_ARCH).orElse("Unix")));
        return Stream.concat(Arrays.stream(createMinimalHeaders()), authHeaders.stream())
                .toArray(Header[]::new);
    }

}
