package software.amazon.ec2.networkinsightspath;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.CreateTagsRequest;
import software.amazon.awssdk.services.ec2.model.CreateTagsResponse;
import software.amazon.awssdk.services.ec2.model.DeleteTagsRequest;
import software.amazon.awssdk.services.ec2.model.DeleteTagsResponse;
import software.amazon.awssdk.services.ec2.model.DescribeNetworkInsightsPathsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeNetworkInsightsPathsResponse;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.LoggerProxy;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static software.amazon.ec2.networkinsightspath.PathFactory.arrangePathId;
import static software.amazon.ec2.networkinsightspath.PathFactory.arrangeResourceModel;

@ExtendWith(MockitoExtension.class)
public class UpdateHandlerTest extends AbstractTestBase {

    @Mock
    LoggerProxy loggerProxy;
    @Mock
    Ec2Client client;
    @Captor
    ArgumentCaptor<CreateTagsRequest> createTagCaptor;
    @Captor
    ArgumentCaptor<DeleteTagsRequest> deleteTagCaptor;
    @Captor
    ArgumentCaptor<DescribeNetworkInsightsPathsRequest> describePathCaptor;

    private AmazonWebServicesClientProxy proxy;
    private UpdateHandler sut;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(loggerProxy, credentials, remainingTimeSupplier);
        sut = new UpdateHandler();
    }

    @Test
    public void handleRequestExpectSuccess() {
        final String pathId = arrangePathId();
        final ResourceModel model = arrangeResourceModel(pathId);
        final Tag tagToAdd = Tag.builder()
            .key("Name")
            .value("NewName")
            .build();
        final Tag tagToRemove = Tag.builder()
            .key("KeyToRemove")
            .value("ValueToRemove")
            .build();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .previousResourceState(model)
            .desiredResourceState(model)
            .previousResourceTags(ImmutableMap.of(tagToRemove.key(), tagToRemove.value()))
            .desiredResourceTags(ImmutableMap.of(tagToAdd.key(), tagToAdd.value()))
            .build();
        final DescribeNetworkInsightsPathsResponse describeResponse = DescribeNetworkInsightsPathsResponse.builder()
            .build();
        final CreateTagsResponse createTagsResponse = CreateTagsResponse.builder().build();
        final DeleteTagsResponse deleteTagsResponse = DeleteTagsResponse.builder().build();
        doReturn(describeResponse)
            .when(client).describeNetworkInsightsPaths(any(DescribeNetworkInsightsPathsRequest.class));
        doReturn(createTagsResponse)
            .when(client).createTags(any(CreateTagsRequest.class));
        doReturn(deleteTagsResponse)
            .when(client).deleteTags(any(DeleteTagsRequest.class));

        final ProgressEvent<ResourceModel, CallbackContext> response = sut.handleRequest(proxy, request, client, logger);

        verify(client).describeNetworkInsightsPaths(describePathCaptor.capture());
        verify(client).createTags(createTagCaptor.capture());
        verify(client).deleteTags(deleteTagCaptor.capture());
        assertEquals(pathId, describePathCaptor.getValue().networkInsightsPathIds().get(0));
        assertEquals(tagToAdd, createTagCaptor.getValue().tags().get(0));
        assertEquals(tagToRemove, deleteTagCaptor.getValue().tags().get(0));
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
        verifyNoMoreInteractions(client);
    }

    @Test
    public void handleRequestGivenEc2ThrowsExpectSuccess() {
        final ResourceModel model = PathFactory.arrangeResourceModel();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .previousResourceState(model)
            .desiredResourceState(model)
            .build();
        final AwsServiceException exception = arrangeException("InvalidNetworkInsightsPathId.NotFound");
        doThrow(exception).when(client)
            .describeNetworkInsightsPaths(any(DescribeNetworkInsightsPathsRequest.class));

        final ProgressEvent<ResourceModel, CallbackContext> response = sut.handleRequest(proxy, request, client, logger);

        assertEquals(OperationStatus.FAILED, response.getStatus());
        assertEquals(HandlerErrorCode.NotFound, response.getErrorCode());
        assertEquals(exception.getMessage(), response.getMessage());
    }

    @Test
    public void addTagsGivenNoTagsExpectSuccess() {
        sut.addTags(arrangePathId(), Collections.emptyList(), client, proxy, logger);
    }

    @Test
    public void deleteTagsGivenNoTagsExpectSuccess() {
        sut.deleteTags(arrangePathId(), Collections.emptyList(), client, proxy, logger);
    }
}
