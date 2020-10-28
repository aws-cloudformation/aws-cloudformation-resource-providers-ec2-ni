package software.amazon.ec2.networkinsightsanalysis;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.ec2.model.DeleteNetworkInsightsAnalysisRequest;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static software.amazon.ec2.networkinsightsanalysis.AnalysisFactory.arrangeAnalysisId;
import static software.amazon.ec2.networkinsightsanalysis.AnalysisFactory.arrangeResourceModel;

public class TranslatorTest {


    @Test
    public void translateToDeleteRequestExpectSuccess() {
        final String analysisId = arrangeAnalysisId();
        final DeleteNetworkInsightsAnalysisRequest expected = DeleteNetworkInsightsAnalysisRequest.builder()
            .networkInsightsAnalysisId(analysisId)
            .build();
        final ResourceModel model = arrangeResourceModel(analysisId);

        final DeleteNetworkInsightsAnalysisRequest actual = Translator.translateToDeleteRequest(model);

        assertEquals(expected, actual);
    }

    @Test
    public void getHandlerErrorGivenMissingParameterExpectInvalidRequestCode() {
        assertEquals(HandlerErrorCode.InvalidRequest,
            Translator.getHandlerError("MissingParameterException"));
    }

    @Test
    public void getHandlerErrorGivenInvalidParameterValueExpectInvalidRequestCode() {
        assertEquals(HandlerErrorCode.InvalidRequest,
            Translator.getHandlerError("InvalidParameterValue"));
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
    public void getHandlerErrorGivenAnalysisNotFoundExpectNotFoundCode() {
        assertEquals(HandlerErrorCode.NotFound,
            Translator.getHandlerError("InvalidNetworkInsightsAnalysisId.NotFound"));
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
