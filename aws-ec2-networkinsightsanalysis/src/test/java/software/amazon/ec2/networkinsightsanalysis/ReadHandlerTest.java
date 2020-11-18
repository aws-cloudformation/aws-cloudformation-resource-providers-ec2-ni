package software.amazon.ec2.networkinsightsanalysis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeNetworkInsightsAnalysesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeNetworkInsightsAnalysesResponse;
import software.amazon.awssdk.services.ec2.model.NetworkInsightsAnalysis;
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
import static software.amazon.ec2.networkinsightsanalysis.AnalysisFactory.arrangeAnalysis;
import static software.amazon.ec2.networkinsightsanalysis.AnalysisFactory.arrangeAnalysisId;
import static software.amazon.ec2.networkinsightsanalysis.AnalysisFactory.arrangeResourceModel;

@ExtendWith(MockitoExtension.class)
public class ReadHandlerTest extends AbstractTestBase {

    @Mock
    LoggerProxy loggerProxy;
    @Mock
    CallbackContext callbackContext;
    @Mock
    Ec2Client client;
    @Captor
    ArgumentCaptor<DescribeNetworkInsightsAnalysesRequest> describeAnalysisCaptor;

    private AmazonWebServicesClientProxy proxy;
    private ReadHandler sut;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(loggerProxy, credentials, remainingTimeSupplier);
        sut = new ReadHandler();
    }

    @Test
    public void handleRequestExpectSuccess() {
        final NetworkInsightsAnalysis analysis = arrangeAnalysis();
        final ResourceModel expectedModel = arrangeResourceModel(analysis.networkInsightsAnalysisId(),
                analysis.networkInsightsAnalysisArn(), analysis.startDate().toString(), analysis.statusAsString());
        final DescribeNetworkInsightsAnalysesResponse describeResponse = DescribeNetworkInsightsAnalysesResponse.builder()
                .networkInsightsAnalyses(analysis)
                .build();
        final ResourceHandlerRequest<ResourceModel> request = arrangeResourceHandlerRequest(expectedModel);
        doReturn(describeResponse)
                .when(client).describeNetworkInsightsAnalyses(any(DescribeNetworkInsightsAnalysesRequest.class));

        final ProgressEvent<ResourceModel, CallbackContext> response = sut.handleRequest(proxy, request, client, callbackContext, logger);

        verify(client).describeNetworkInsightsAnalyses(describeAnalysisCaptor.capture());
        assertEquals(analysis.networkInsightsAnalysisId(),
                describeAnalysisCaptor.getValue().networkInsightsAnalysisIds().get(0));
        assertThat(response).isNotNull();
        assertThat(response.getResourceModel()).isEqualTo(expectedModel);
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
        verifyNoMoreInteractions(client);
    }

    @Test
    public void handleRequestGivenEc2ThrowsExpectSuccess() {
        final ResourceModel model = AnalysisFactory.arrangeResourceModel(arrangeAnalysisId());
        final ResourceHandlerRequest<ResourceModel> request = arrangeResourceHandlerRequest(model);
        final AwsServiceException exception = arrangeException("MissingParameter");
        doThrow(exception).when(client)
                .describeNetworkInsightsAnalyses(any(DescribeNetworkInsightsAnalysesRequest.class));

        final ProgressEvent<ResourceModel, CallbackContext> response = sut.handleRequest(proxy, request, client, callbackContext, logger);

        assertNull(response.getResourceModel());
        assertEquals(OperationStatus.FAILED, response.getStatus());
        assertEquals(HandlerErrorCode.InvalidRequest, response.getErrorCode());
        assertEquals(exception.getMessage(), response.getMessage());
    }
}
