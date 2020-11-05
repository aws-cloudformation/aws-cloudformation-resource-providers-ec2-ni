package software.amazon.ec2.networkinsightsanalysis;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.ec2.model.CreateTagsRequest;
import software.amazon.awssdk.services.ec2.model.DeleteNetworkInsightsAnalysisRequest;
import software.amazon.awssdk.services.ec2.model.DeleteTagsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeNetworkInsightsAnalysesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeNetworkInsightsAnalysesResponse;
import software.amazon.awssdk.services.ec2.model.NetworkInsightsAnalysis;
import software.amazon.awssdk.services.ec2.model.StartNetworkInsightsAnalysisRequest;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static software.amazon.ec2.networkinsightsanalysis.AnalysisFactory.arrangeAnalysisId;
import static software.amazon.ec2.networkinsightsanalysis.AnalysisFactory.arrangeEc2Tag;
import static software.amazon.ec2.networkinsightsanalysis.AnalysisFactory.arrangeNextToken;
import static software.amazon.ec2.networkinsightsanalysis.AnalysisFactory.arrangeResourceModel;
import static software.amazon.ec2.networkinsightsanalysis.AnalysisFactory.arrangeDescribeAnalysesRequest;
import static software.amazon.ec2.networkinsightsanalysis.AnalysisFactory.arrangeFullResourceModel;
import static software.amazon.ec2.networkinsightsanalysis.AnalysisFactory.arrangeNetworkInsightsAnalysis;
import static software.amazon.ec2.networkinsightsanalysis.AnalysisFactory.arrangeStartAnalysisRequest;

public class TranslatorTest {

    @Test
    public void translateToStartRequestExpectSuccess() {
        final ResourceModel resourceModel = arrangeResourceModel();
        final ResourceHandlerRequest<ResourceModel> resourceHandlerRequest = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(resourceModel)
                .desiredResourceTags(ImmutableMap.of("someKey", "someValue"))
                .build();
        final StartNetworkInsightsAnalysisRequest expected = arrangeStartAnalysisRequest(resourceHandlerRequest);

        final StartNetworkInsightsAnalysisRequest actual =
                Translator.translateToStartRequest(resourceHandlerRequest);

        assertEquals(expected, actual);
    }

    @Test
    public void translateToReadRequestExpectSuccess() {
        final software.amazon.ec2.networkinsightsanalysis.ResourceModel resourceModel =
                AnalysisFactory.arrangeResourceModel();
        final ResourceHandlerRequest<software.amazon.ec2.networkinsightsanalysis.ResourceModel> resourceHandlerRequest =
                ResourceHandlerRequest.<software.amazon.ec2.networkinsightsanalysis.ResourceModel>builder().desiredResourceState(resourceModel).build();
        final DescribeNetworkInsightsAnalysesRequest expected = arrangeDescribeAnalysesRequest(resourceHandlerRequest);

        final DescribeNetworkInsightsAnalysesRequest actual = software.amazon.ec2.networkinsightsanalysis.Translator
                .translateToReadRequest(resourceModel);

        assertEquals(expected, actual);
    }

    @Test
    public void translateFromReadResponseExpectSuccess() {
        final software.amazon.ec2.networkinsightsanalysis.ResourceModel expected = arrangeFullResourceModel();
        final NetworkInsightsAnalysis analysis = arrangeNetworkInsightsAnalysis(expected);
        final DescribeNetworkInsightsAnalysesResponse response = DescribeNetworkInsightsAnalysesResponse.builder()
                .networkInsightsAnalyses(analysis)
                .build();

        final software.amazon.ec2.networkinsightsanalysis.ResourceModel actual =
                software.amazon.ec2.networkinsightsanalysis.Translator.translateFromReadResponse(response);
        System.out.println("actual: " + actual);

        assertEquals(expected, actual);
    }

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
    public void translateToListRequestExpectSuccess() {
        final String nextToken = arrangeNextToken();
        final DescribeNetworkInsightsAnalysesRequest expected = DescribeNetworkInsightsAnalysesRequest.builder()
                .nextToken(nextToken)
                .build();

        final DescribeNetworkInsightsAnalysesRequest actual = Translator.translateToListRequest(nextToken);

        assertEquals(expected, actual);
    }

    @Test
    public void translateFromListRequestExpectSuccess() {
        final ResourceModel expected = arrangeFullResourceModel();
        final NetworkInsightsAnalysis analysis = arrangeNetworkInsightsAnalysis(expected);
        final DescribeNetworkInsightsAnalysesResponse response = DescribeNetworkInsightsAnalysesResponse.builder()
                .networkInsightsAnalyses(analysis)
                .build();

        final List<ResourceModel> actual = Translator.translateFromListRequest(response);

        assertEquals(ImmutableList.of(expected), actual);
    }

    @Test
    public void translateToCreateTagsRequestExpectSuccess() {
        final software.amazon.awssdk.services.ec2.model.Tag tag = arrangeEc2Tag();
        final String analysisId = arrangeAnalysisId();
        final CreateTagsRequest expected = CreateTagsRequest.builder()
                .resources(analysisId)
                .tags(tag)
                .build();

        final CreateTagsRequest actual = Translator.translateToCreateTagsRequest(ImmutableList.of(tag), analysisId);

        assertEquals(expected, actual);
    }

    @Test
    public void translateToDeleteTagsRequestExpectSuccess() {
        final Tag tag = arrangeEc2Tag();
        final String analysisId = arrangeAnalysisId();
        final DeleteTagsRequest expected = DeleteTagsRequest.builder()
                .resources(analysisId)
                .tags(tag)
                .build();

        final DeleteTagsRequest actual = Translator.translateToDeleteTagsRequest(ImmutableList.of(tag), analysisId);

        assertEquals(expected, actual);
    }

    @Test
    public void getHandlerErrorGivenMissingParameterExpectInvalidRequestCode() {
        assertEquals(HandlerErrorCode.InvalidRequest,
            Translator.getHandlerError("MissingParameter"));
    }

    @Test
    public void getHandlerErrorGivenInvalidParameterValueExpectInvalidRequestCode() {
        assertEquals(HandlerErrorCode.InvalidRequest,
            Translator.getHandlerError("InvalidParameterValue"));
    }

    @Test
    public void getHandlerErrorGivenInvalidParameterCombinationExpectInvalidRequestCode() {
        assertEquals(HandlerErrorCode.InvalidRequest,
                Translator.getHandlerError("InvalidParameterCombination"));
    }

    @Test
    public void getHandlerErrorGivenAnalysisLimitExceededExpectInvalidRequestCode() {
        assertEquals(HandlerErrorCode.InvalidRequest,
                Translator.getHandlerError("NetworkInsightsAnalysis.LimitExceeded"));
    }

    @Test
    public void getHandlerErrorGivenIdempotentParameterMismatchExpectInvalidRequestCode() {
        assertEquals(HandlerErrorCode.InvalidRequest,
                Translator.getHandlerError("IdempotentParameterMismatch"));
    }


    @Test
    public void getHandlerErrorGivenInvalidAnalysisIdMalformedExpectInvalidRequestCode() {
        assertEquals(HandlerErrorCode.InvalidRequest,
                Translator.getHandlerError("InvalidNetworkInsightsAnalysisId.Malformed"));
    }

    @Test
    public void getHandlerErrorGivenTagPolicyViolationExpectInvalidRequestCode() {
        assertEquals(HandlerErrorCode.InvalidRequest,
                Translator.getHandlerError("TagPolicyViolation"));
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
    public void getHandlerErrorGivenAnalysisNotFoundExpectNotFoundCode() {
        assertEquals(HandlerErrorCode.NotFound,
                Translator.getHandlerError("InvalidNetworkInsightsAnalysisId.NotFound"));
    }

    @Test
    public void getHandlerErrorGivenNetworkInsightsAccessDeniedExpectAccessDeniedCode() {
        assertEquals(HandlerErrorCode.AccessDenied,
                Translator.getHandlerError("NetworkInsights.AccessDenied"));
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
