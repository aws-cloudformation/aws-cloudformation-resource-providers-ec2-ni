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
import static software.amazon.ec2.networkinsightsanalysis.AnalysisFactory.arrangeResourceModel;

@ExtendWith(MockitoExtension.class)
public class ListHandlerTest extends AbstractTestBase {

    @Mock
    LoggerProxy loggerProxy;
    @Mock
    Ec2Client client;
    @Captor
    ArgumentCaptor<DescribeNetworkInsightsAnalysesRequest> describeAnalysisCaptor;

    private AmazonWebServicesClientProxy proxy;
    private ListHandler sut;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(loggerProxy, credentials, remainingTimeSupplier);
        sut = new ListHandler();
    }

    @Test
    public void handleRequestExpectSuccess() {
        final String nextTokenInput = "someToken";
        final String nextTokenOutput = "someOtherToken";
        final NetworkInsightsAnalysis analysis = arrangeAnalysis();
        final ResourceModel model = arrangeResourceModel();
        final ResourceModel expectedModel = arrangeResourceModel(analysis.networkInsightsAnalysisId(),
                analysis.networkInsightsAnalysisArn(), analysis.startDate().toString(), analysis.statusAsString());
        final DescribeNetworkInsightsAnalysesResponse describeResponse = DescribeNetworkInsightsAnalysesResponse.builder()
                .networkInsightsAnalyses(analysis)
                .nextToken(nextTokenOutput)
                .build();
        final ResourceHandlerRequest<ResourceModel> request = arrangeResourceHandlerRequest(model);
        request.setNextToken(nextTokenInput);
        doReturn(describeResponse)
                .when(client).describeNetworkInsightsAnalyses(any(DescribeNetworkInsightsAnalysesRequest.class));

        final ProgressEvent<ResourceModel, CallbackContext> response = sut.handleRequest(proxy, request, client, logger);

        verify(client).describeNetworkInsightsAnalyses(describeAnalysisCaptor.capture());
        assertEquals(nextTokenInput, describeAnalysisCaptor.getValue().nextToken());
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
        final ResourceModel model = AnalysisFactory.arrangeResourceModel();
        final ResourceHandlerRequest<ResourceModel> request = arrangeResourceHandlerRequest(model);
        final AwsServiceException exception = arrangeException("MissingParameter");
        doThrow(exception).when(client)
                .describeNetworkInsightsAnalyses(any(DescribeNetworkInsightsAnalysesRequest.class));

        final ProgressEvent<ResourceModel, CallbackContext> response = sut.handleRequest(proxy, request, client, logger);

        assertNull(response.getResourceModel());
        assertEquals(OperationStatus.FAILED, response.getStatus());
        assertEquals(HandlerErrorCode.InvalidRequest, response.getErrorCode());
        assertEquals(exception.getMessage(), response.getMessage());
    }
}
