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
import software.amazon.awssdk.services.ec2.model.DeleteNetworkInsightsAnalysisRequest;
import software.amazon.awssdk.services.ec2.model.DeleteNetworkInsightsAnalysisResponse;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static software.amazon.ec2.networkinsightsanalysis.AnalysisFactory.arrangeAnalysisId;
import static software.amazon.ec2.networkinsightsanalysis.AnalysisFactory.arrangeResourceModel;

@ExtendWith(MockitoExtension.class)
public class DeleteHandlerTest extends AbstractTestBase {

    @Mock
    Ec2Client client;
    @Mock
    LoggerProxy loggerProxy;
    @Captor
    ArgumentCaptor<DeleteNetworkInsightsAnalysisRequest> requestCaptor;

    private AmazonWebServicesClientProxy proxy;
    private DeleteHandler sut;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(loggerProxy, credentials, remainingTimeSupplier);
        sut = new DeleteHandler();
    }

    @Test
    public void handleRequestExpectSuccess() {
        final String analysisId = arrangeAnalysisId();
        final ResourceModel model = arrangeResourceModel(analysisId);
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        final DeleteNetworkInsightsAnalysisResponse deleteAnalysisResponse = DeleteNetworkInsightsAnalysisResponse.builder()
                .networkInsightsAnalysisId(analysisId)
                .build();
        when(client.deleteNetworkInsightsAnalysis(any(DeleteNetworkInsightsAnalysisRequest.class))).thenReturn(deleteAnalysisResponse);

        final ProgressEvent<ResourceModel, CallbackContext> response = sut.handleRequest(proxy, request, client, logger);

        verify(client).deleteNetworkInsightsAnalysis(requestCaptor.capture());
        final DeleteNetworkInsightsAnalysisRequest actualRequest = requestCaptor.getValue();
        assertEquals(analysisId, actualRequest.networkInsightsAnalysisId());
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
        verifyNoMoreInteractions(client);
    }

    @Test
    public void handleRequestGivenEc2ThrowsExpectSuccess() {
        final ResourceModel model = AnalysisFactory.arrangeResourceModel();
        final ResourceHandlerRequest<ResourceModel> request = arrangeResourceHandlerRequest(model);
        final AwsServiceException exception = arrangeException("InvalidNetworkInsightsAnalysisId.NotFound");
        doThrow(exception).when(client)
                .deleteNetworkInsightsAnalysis(any(DeleteNetworkInsightsAnalysisRequest.class));

        final ProgressEvent<ResourceModel, CallbackContext> response = sut.handleRequest(proxy, request, client, logger);

        assertNull(response.getResourceModel());
        assertEquals(OperationStatus.FAILED, response.getStatus());
        assertEquals(HandlerErrorCode.NotFound, response.getErrorCode());
        assertEquals(exception.getMessage(), response.getMessage());
    }
}
