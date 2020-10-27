package software.amazon.ec2.networkinsightspath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeNetworkInsightsPathsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeNetworkInsightsPathsResponse;
import software.amazon.awssdk.services.ec2.model.NetworkInsightsPath;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.LoggerProxy;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static software.amazon.ec2.networkinsightspath.PathFactory.arrangePath;
import static software.amazon.ec2.networkinsightspath.PathFactory.arrangeResourceModel;

@ExtendWith(MockitoExtension.class)
public class ListHandlerTest extends AbstractTestBase {

    @Mock
    LoggerProxy loggerProxy;
    @Mock
    Ec2Client client;
    @Captor
    ArgumentCaptor<DescribeNetworkInsightsPathsRequest> describePathCaptor;

    private AmazonWebServicesClientProxy proxy;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(loggerProxy, credentials, remainingTimeSupplier);
    }

    @Test
    public void handleRequestExpectSuccess() {
        final String nextTokenInput = "someToken";
        final String nextTokenOutput = "someOtherToken";
        final NetworkInsightsPath path = arrangePath();
        final ListHandler sut = new ListHandler();
        final ResourceModel model = arrangeResourceModel();
        final ResourceModel expectedModel = arrangeResourceModel(path.networkInsightsPathId(), path.networkInsightsPathArn(), path.createdDate().toString());
        final DescribeNetworkInsightsPathsResponse describeResponse = DescribeNetworkInsightsPathsResponse.builder()
            .networkInsightsPaths(path)
            .nextToken(nextTokenOutput)
            .build();
        final ResourceHandlerRequest<ResourceModel> request = arrangeResourceHandlerRequest(model);
        request.setNextToken(nextTokenInput);
        doReturn(describeResponse)
            .when(client).describeNetworkInsightsPaths(any(DescribeNetworkInsightsPathsRequest.class));

        final ProgressEvent<ResourceModel, CallbackContext> response = sut.handleRequest(proxy, request, client, logger);

        verify(client).describeNetworkInsightsPaths(describePathCaptor.capture());
        assertEquals(nextTokenInput, describePathCaptor.getValue().nextToken());
        assertThat(response).isNotNull();
        assertThat(response.getResourceModels().get(0)).isEqualTo(expectedModel);
        assertThat(response.getNextToken()).isEqualTo(nextTokenOutput);
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
        verifyNoMoreInteractions(client);
    }

    @Test
    public void handleRequestGivenEc2ThrowsExpectSuccess() {
        final ResourceModel model = PathFactory.arrangeResourceModel();
        final ResourceHandlerRequest<ResourceModel> request = arrangeResourceHandlerRequest(model);
        final AwsServiceException exception = arrangeException("NetworkInsightsSource.NotFound");
        doThrow(exception).when(client)
            .describeNetworkInsightsPaths(any(DescribeNetworkInsightsPathsRequest.class));
        final ListHandler sut = new ListHandler();

        final ProgressEvent<ResourceModel, CallbackContext> response = sut.handleRequest(proxy, request, client, logger);

        assertNull(response.getResourceModel());
        assertEquals(OperationStatus.FAILED, response.getStatus());
        assertEquals(HandlerErrorCode.InvalidRequest, response.getErrorCode());
        assertEquals(exception.getMessage(), response.getMessage());
    }
}
