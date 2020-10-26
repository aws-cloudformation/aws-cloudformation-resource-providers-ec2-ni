package software.amazon.ec2.networkinsightspath;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.ec2.model.CreateNetworkInsightsPathRequest;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static software.amazon.ec2.networkinsightspath.PathFactory.arrangeCreatePathRequest;
import static software.amazon.ec2.networkinsightspath.PathFactory.arrangeResourceModel;

public class TranslatorTest {

    @Test
    public void translateToCreateRequestExpectSuccess() {
        final ResourceModel resourceModel = arrangeResourceModel();
        final ResourceHandlerRequest<ResourceModel> resourceHandlerRequest = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(resourceModel)
            .desiredResourceTags(ImmutableMap.of("someKey", "someValue"))
            .build();
        final CreateNetworkInsightsPathRequest expected = arrangeCreatePathRequest(resourceHandlerRequest);

        final CreateNetworkInsightsPathRequest actual =
            Translator.translateToCreateRequest(resourceHandlerRequest);

        assertEquals(expected, actual);
    }

    @Test
    public void getHandlerErrorGivenSourceNotFoundExpectInvalidRequestCode() {
        assertEquals(HandlerErrorCode.InvalidRequest,
            Translator.getHandlerError("NetworkInsightsSource.NotFound"));
    }

    @Test
    public void getHandlerErrorGivenDestinationNotFoundExpectInvalidRequestCode() {
        assertEquals(HandlerErrorCode.InvalidRequest,
            Translator.getHandlerError("NetworkInsightsDestination.NotFound"));
    }

    @Test
    public void getHandlerErrorGivenMissingParameterExpectInvalidRequestCode() {
        assertEquals(HandlerErrorCode.InvalidRequest,
            Translator.getHandlerError("MissingParameterException"));
    }

    @Test
    public void getHandlerErrorGivenInvalidParameterCombinationExpectInvalidRequestCode() {
        assertEquals(HandlerErrorCode.InvalidRequest,
            Translator.getHandlerError("InvalidParameterCombination"));
    }

    @Test
    public void getHandlerErrorGivenInvalidParameterValueExpectInvalidRequestCode() {
        assertEquals(HandlerErrorCode.InvalidRequest,
            Translator.getHandlerError("InvalidParameterValue"));
    }

    @Test
    public void getHandlerErrorGivenTagPolicyViolationExpectInvalidRequestCode() {
        assertEquals(HandlerErrorCode.InvalidRequest,
            Translator.getHandlerError("TagPolicyViolation"));
    }

    @Test
    public void getHandlerErrorGivenIdempotentParameterMismatchExpectInvalidRequestCode() {
        assertEquals(HandlerErrorCode.InvalidRequest,
            Translator.getHandlerError("IdempotentParameterMismatch"));
    }

    @Test
    public void getHandlerErrorGivenAnalysisExistsForPathExpectInvalidRequestCode() {
        assertEquals(HandlerErrorCode.InvalidRequest,
            Translator.getHandlerError("AnalysisExistsForNetworkInsightsPath"));
    }

    @Test
    public void getHandlerErrorGivenPathLimitExceededExpectThrottlingCode() {
        assertEquals(HandlerErrorCode.Throttling,
            Translator.getHandlerError("NetworkInsightsPath.LimitExceeded"));
    }

    @Test
    public void getHandlerErrorGivenLimitExceededExpectThrottlingCode() {
        assertEquals(HandlerErrorCode.Throttling,
            Translator.getHandlerError("RequestLimitExceeded"));
    }

    @Test
    public void getHandlerErrorGivenClientLimitExceededExpectThrottlingCode() {
        assertEquals(HandlerErrorCode.Throttling,
            Translator.getHandlerError("Client.RequestLimitExceeded"));
    }

    @Test
    public void getHandlerErrorGivenPathNotFoundExpectNotFoundCode() {
        assertEquals(HandlerErrorCode.NotFound,
            Translator.getHandlerError("InvalidNetworkInsightsPathId.NotFound"));
    }

    @Test
    public void getHandlerErrorGivenUnauthorizedOperationExpectAccessDeniedCode() {
        assertEquals(HandlerErrorCode.AccessDenied,
            Translator.getHandlerError("UnauthorizedOperation"));
    }

    @Test
    public void getHandlerErrorGivenClientUnauthorizedOperationExpectAccessDeniedCode() {
        assertEquals(HandlerErrorCode.AccessDenied,
            Translator.getHandlerError("Client.UnauthorizedOperation"));
    }

    @Test
    public void getHandlerErrorGivenUnavailableExpectServiceInternalErrorCode() {
        assertEquals(HandlerErrorCode.ServiceInternalError,
            Translator.getHandlerError("Unavailable"));
    }

    @Test
    public void getHandlerErrorGivenInternalErrorExpectServiceInternalErrorCode() {
        assertEquals(HandlerErrorCode.ServiceInternalError,
            Translator.getHandlerError("InternalError"));
    }

    @Test
    public void getHandlerErrorGivenUnknownErrorExpectGeneralServiceExceptionCode() {
        assertEquals(HandlerErrorCode.GeneralServiceException,
            Translator.getHandlerError("Something"));
    }
}
