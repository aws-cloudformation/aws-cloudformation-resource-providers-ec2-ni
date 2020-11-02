package software.amazon.ec2.networkinsightspath;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.ec2.model.CreateNetworkInsightsPathRequest;
import software.amazon.awssdk.services.ec2.model.CreateTagsRequest;
import software.amazon.awssdk.services.ec2.model.DeleteNetworkInsightsPathRequest;
import software.amazon.awssdk.services.ec2.model.DeleteTagsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeNetworkInsightsPathsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeNetworkInsightsPathsResponse;
import software.amazon.awssdk.services.ec2.model.NetworkInsightsPath;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static software.amazon.ec2.networkinsightspath.PathFactory.arrangeCreatePathRequest;
import static software.amazon.ec2.networkinsightspath.PathFactory.arrangeDescribePathsRequest;
import static software.amazon.ec2.networkinsightspath.PathFactory.arrangeEc2Tag;
import static software.amazon.ec2.networkinsightspath.PathFactory.arrangeFullResourceModel;
import static software.amazon.ec2.networkinsightspath.PathFactory.arrangeNetworkInsightsPath;
import static software.amazon.ec2.networkinsightspath.PathFactory.arrangeNextToken;
import static software.amazon.ec2.networkinsightspath.PathFactory.arrangePathId;
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
    public void translateToReadRequestExpectSuccess() {
        final ResourceModel resourceModel = arrangeResourceModel();
        final ResourceHandlerRequest<ResourceModel> resourceHandlerRequest = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(resourceModel)
            .build();
        final DescribeNetworkInsightsPathsRequest expected = arrangeDescribePathsRequest(resourceHandlerRequest);

        final DescribeNetworkInsightsPathsRequest actual = Translator.translateToReadRequest(resourceModel);

        assertEquals(expected, actual);
    }

    @Test
    public void translateFromReadResponseExpectSuccess() {
        final ResourceModel expected = arrangeFullResourceModel();
        final NetworkInsightsPath path = arrangeNetworkInsightsPath(expected);
        final DescribeNetworkInsightsPathsResponse response = DescribeNetworkInsightsPathsResponse.builder()
            .networkInsightsPaths(path)
            .build();

        final ResourceModel actual = Translator.translateFromReadResponse(response);

        assertEquals(expected, actual);
    }

    @Test
    public void translateToDeleteRequestExpectSuccess() {
        final String pathId = arrangePathId();
        final DeleteNetworkInsightsPathRequest expected = DeleteNetworkInsightsPathRequest.builder()
            .networkInsightsPathId(pathId)
            .build();
        final ResourceModel model = arrangeResourceModel(pathId);

        final DeleteNetworkInsightsPathRequest actual = Translator.translateToDeleteRequest(model);

        assertEquals(expected, actual);
    }

    @Test
    public void translateToListRequestExpectSuccess() {
        final String nextToken = arrangeNextToken();
        final DescribeNetworkInsightsPathsRequest expected = DescribeNetworkInsightsPathsRequest.builder()
            .nextToken(nextToken)
            .build();

        final DescribeNetworkInsightsPathsRequest actual = Translator.translateToListRequest(nextToken);

        assertEquals(expected, actual);
    }

    @Test
    public void translateFromListRequestExpectSuccess() {
        final ResourceModel expected = arrangeFullResourceModel();
        final NetworkInsightsPath path = arrangeNetworkInsightsPath(expected);
        final DescribeNetworkInsightsPathsResponse response = DescribeNetworkInsightsPathsResponse.builder()
            .networkInsightsPaths(path)
            .build();

        final List<ResourceModel> actual = Translator.translateFromListRequest(response);

        assertEquals(ImmutableList.of(expected), actual);
    }

    @Test
    public void translateToCreateTagsRequestExpectSuccess() {
        final Tag tag = arrangeEc2Tag();
        final String pathId = arrangePathId();
        final CreateTagsRequest expected = CreateTagsRequest.builder()
            .resources(pathId)
            .tags(tag)
            .build();

        final CreateTagsRequest actual = Translator.translateToCreateTagsRequest(ImmutableList.of(tag), pathId);

        assertEquals(expected, actual);
    }

    @Test
    public void translateToDeleteTagsRequestExpectSuccess() {
        final Tag tag = arrangeEc2Tag();
        final String pathId = arrangePathId();
        final DeleteTagsRequest expected = DeleteTagsRequest.builder()
            .resources(pathId)
            .tags(tag)
            .build();

        final DeleteTagsRequest actual = Translator.translateToDeleteTagsRequest(ImmutableList.of(tag), pathId);

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
            Translator.getHandlerError("MissingParameter"));
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
