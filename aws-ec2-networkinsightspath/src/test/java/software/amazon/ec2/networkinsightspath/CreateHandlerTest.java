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
import software.amazon.awssdk.services.ec2.model.CreateNetworkInsightsPathRequest;
import software.amazon.awssdk.services.ec2.model.CreateNetworkInsightsPathResponse;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.LoggerProxy;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static software.amazon.ec2.networkinsightspath.PathFactory.arrangeCreatePathResponse;
import static software.amazon.ec2.networkinsightspath.PathFactory.arrangeCreatedDate;
import static software.amazon.ec2.networkinsightspath.PathFactory.arrangePathArn;
import static software.amazon.ec2.networkinsightspath.PathFactory.arrangePathId;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest extends AbstractTestBase {

    @Mock
    LoggerProxy loggerProxy;
    @Mock
    Ec2Client client;
    @Captor
    private ArgumentCaptor<CreateNetworkInsightsPathRequest> requestCaptor;

    private AmazonWebServicesClientProxy proxy;
    private CreateHandler sut;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(loggerProxy, credentials, remainingTimeSupplier);
        sut = new CreateHandler();
    }

    @Test
    public void handleRequestGivenPathIdExpectThrows() {
        final ResourceModel model = PathFactory.arrangeResourceModel();
        model.setNetworkInsightsPathId(arrangePathId());
        final ResourceHandlerRequest<ResourceModel> request = arrangeResourceHandlerRequest(model);

        assertThrows(CfnInvalidRequestException.class, () -> sut.handleRequest(proxy, request, client, logger));
    }

    @Test
    public void handleRequestGivenPathArnExpectThrows() {
        final ResourceModel model = PathFactory.arrangeResourceModel();
        model.setNetworkInsightsPathArn(arrangePathArn());
        final ResourceHandlerRequest<ResourceModel> request = arrangeResourceHandlerRequest(model);

        assertThrows(CfnInvalidRequestException.class, () -> sut.handleRequest(proxy, request, client, logger));
    }

    @Test
    public void handleRequestGivenCreatedDateExpectThrows() {
        final ResourceModel model = PathFactory.arrangeResourceModel();
        model.setCreatedDate(arrangeCreatedDate().toString());
        final ResourceHandlerRequest<ResourceModel> request = arrangeResourceHandlerRequest(model);

        assertThrows(CfnInvalidRequestException.class, () -> sut.handleRequest(proxy, request, client, logger));
    }

    @Test
    public void handleRequestGivenEc2ThrowsExpectSuccess() {
        final ResourceModel model = PathFactory.arrangeResourceModel();
        final ResourceHandlerRequest<ResourceModel> request = arrangeResourceHandlerRequest(model);
        final AwsServiceException exception = arrangeException("NetworkInsightsSource.NotFound");
        doThrow(exception).when(client)
            .createNetworkInsightsPath(any(CreateNetworkInsightsPathRequest.class));

        final ProgressEvent<ResourceModel, CallbackContext> response = sut.handleRequest(proxy, request, client, logger);

        assertNull(response.getResourceModel());
        assertEquals(OperationStatus.FAILED, response.getStatus());
        assertEquals(HandlerErrorCode.InvalidRequest, response.getErrorCode());
        assertEquals(exception.getMessage(), response.getMessage());
    }

    @Test
    public void handleRequestExpectModelUpdated() {
        final ResourceModel model = PathFactory.arrangeResourceModel();
        final ResourceHandlerRequest<ResourceModel> request = arrangeResourceHandlerRequest(model);
        final CreateNetworkInsightsPathResponse createPathResponse = arrangeCreatePathResponse();
        doReturn(createPathResponse).when(client)
            .createNetworkInsightsPath(any(CreateNetworkInsightsPathRequest.class));

        final ProgressEvent<ResourceModel, CallbackContext> response = sut.handleRequest(proxy, request,
            client, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getResourceModel()).isEqualTo(request.getDesiredResourceState());
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
        assertNotNull(response.getResourceModel().getNetworkInsightsPathId());
        assertNotNull(response.getResourceModel().getNetworkInsightsPathArn());
        assertNotNull(response.getResourceModel().getCreatedDate());

        verify(client).createNetworkInsightsPath(requestCaptor.capture());
        final CreateNetworkInsightsPathRequest actualRequest = requestCaptor.getValue();
        validateRequest(actualRequest, request);
        verifyNoMoreInteractions(client);
    }

    private void validateRequest(CreateNetworkInsightsPathRequest actualRequest,
                                 ResourceHandlerRequest<ResourceModel> request) {
        final ResourceModel model = request.getDesiredResourceState();
        assertEquals(model.getSource(), actualRequest.source());
        assertEquals(model.getDestination(), actualRequest.destination());
        assertEquals(model.getSourceIp(), actualRequest.sourceIp());
        assertEquals(model.getDestinationIp(), actualRequest.destinationIp());
        assertEquals(model.getDestinationPort(), actualRequest.destinationPort());
        if (request.getDesiredResourceTags() == null) {
            assertTrue(actualRequest.tagSpecifications().isEmpty());
        } else {
            final List<Tag> tags = actualRequest.tagSpecifications()
                .stream()
                .flatMap(spec -> spec.tags().stream())
                .collect(Collectors.toList());

            assertEquals(request.getDesiredResourceTags(), tags.stream().collect(Collectors.toMap(software.amazon.awssdk.services.ec2.model.Tag::key, Tag::value)));
        }
    }
}
