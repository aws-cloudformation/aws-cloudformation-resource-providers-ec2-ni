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
import software.amazon.awssdk.services.ec2.model.DeleteNetworkInsightsPathRequest;
import software.amazon.awssdk.services.ec2.model.DeleteNetworkInsightsPathResponse;
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
import static software.amazon.ec2.networkinsightspath.PathFactory.arrangePathId;
import static software.amazon.ec2.networkinsightspath.PathFactory.arrangeResourceModel;

@ExtendWith(MockitoExtension.class)
public class DeleteHandlerTest extends AbstractTestBase {

    @Mock
    Ec2Client client;
    @Mock
    LoggerProxy loggerProxy;
    @Captor
    ArgumentCaptor<DeleteNetworkInsightsPathRequest> requestCaptor;

    private AmazonWebServicesClientProxy proxy;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(loggerProxy, credentials, remainingTimeSupplier);
    }

    @Test
    public void handleRequestExpectSuccess() {
        final String pathId = arrangePathId();
        final DeleteHandler sut = new DeleteHandler();
        final ResourceModel model = arrangeResourceModel(pathId);
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();
        final DeleteNetworkInsightsPathResponse deletePathResponse = DeleteNetworkInsightsPathResponse.builder()
            .networkInsightsPathId(pathId)
            .build();
        when(client.deleteNetworkInsightsPath(any(DeleteNetworkInsightsPathRequest.class))).thenReturn(deletePathResponse);

        final ProgressEvent<ResourceModel, CallbackContext> response = sut.handleRequest(proxy, request, client, logger);

        verify(client).deleteNetworkInsightsPath(requestCaptor.capture());
        final DeleteNetworkInsightsPathRequest actualRequest = requestCaptor.getValue();
        assertEquals(pathId, actualRequest.networkInsightsPathId());
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
        final ResourceModel model = PathFactory.arrangeResourceModel();
        final ResourceHandlerRequest<ResourceModel> request = arrangeResourceHandlerRequest(model);
        final AwsServiceException exception = arrangeException("InvalidNetworkInsightsPathId.NotFound");
        doThrow(exception).when(client)
            .deleteNetworkInsightsPath(any(DeleteNetworkInsightsPathRequest.class));
        final DeleteHandler sut = new DeleteHandler();

        final ProgressEvent<ResourceModel, CallbackContext> response = sut.handleRequest(proxy, request, client, logger);

        assertNull(response.getResourceModel());
        assertEquals(OperationStatus.FAILED, response.getStatus());
        assertEquals(HandlerErrorCode.NotFound, response.getErrorCode());
        assertEquals(exception.getMessage(), response.getMessage());
    }
}
