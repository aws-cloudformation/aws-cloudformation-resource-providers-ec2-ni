package software.amazon.ec2.networkinsightsanalysis;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.NetworkInsightsAnalysis;
import software.amazon.awssdk.services.ec2.model.StartNetworkInsightsAnalysisRequest;
import software.amazon.awssdk.services.ec2.model.StartNetworkInsightsAnalysisResponse;
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
import static software.amazon.ec2.networkinsightsanalysis.AnalysisFactory.arrangeAnalysis;
import static software.amazon.ec2.networkinsightsanalysis.AnalysisFactory.arrangeErrorCode;
import static software.amazon.ec2.networkinsightsanalysis.AnalysisFactory.arrangeErrorMessage;
import static software.amazon.ec2.networkinsightsanalysis.AnalysisFactory.arrangeFilterInArns;
import static software.amazon.ec2.networkinsightsanalysis.AnalysisFactory.arrangeFullAnalysis;
import static software.amazon.ec2.networkinsightsanalysis.AnalysisFactory.arrangeNetworkPathFound;
import static software.amazon.ec2.networkinsightsanalysis.AnalysisFactory.arrangeStartAnalysisResponse;
import static software.amazon.ec2.networkinsightsanalysis.AnalysisFactory.arrangeStartDate;
import static software.amazon.ec2.networkinsightsanalysis.AnalysisFactory.arrangeAnalysisArn;
import static software.amazon.ec2.networkinsightsanalysis.AnalysisFactory.arrangeAnalysisId;
import static software.amazon.ec2.networkinsightsanalysis.AnalysisFactory.arrangeStatus;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest extends AbstractTestBase {

    @Mock
    LoggerProxy loggerProxy;
    @Mock
    Ec2Client client;
    @Captor
    private ArgumentCaptor<StartNetworkInsightsAnalysisRequest> requestCaptor;

    private AmazonWebServicesClientProxy proxy;

    private CreateHandler sut;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(loggerProxy, credentials, remainingTimeSupplier);
        sut = new CreateHandler();
    }

    @Test
    public void handleRequestGivenAnalysisIdExpectThrows() {
        final ResourceModel model = AnalysisFactory.arrangeResourceModel();
        model.setNetworkInsightsAnalysisId(arrangeAnalysisId());
        final ResourceHandlerRequest<ResourceModel> request = arrangeResourceHandlerRequest(model);

        assertThrows(CfnInvalidRequestException.class, () -> sut.handleRequest(proxy, request, client, logger));
    }

    @Test
    public void handleRequestGivenAnalysisArnExpectThrows() {
        final ResourceModel model = AnalysisFactory.arrangeResourceModel();
        model.setNetworkInsightsAnalysisArn(arrangeAnalysisArn());
        final ResourceHandlerRequest<ResourceModel> request = arrangeResourceHandlerRequest(model);

        assertThrows(CfnInvalidRequestException.class, () -> sut.handleRequest(proxy, request, client, logger));
    }

    @Test
    public void handleRequestGivenStartDateExpectThrows() {
        final ResourceModel model = AnalysisFactory.arrangeResourceModel();
        model.setStartDate(arrangeStartDate().toString());
        final ResourceHandlerRequest<ResourceModel> request = arrangeResourceHandlerRequest(model);

        assertThrows(CfnInvalidRequestException.class, () -> sut.handleRequest(proxy, request, client, logger));
    }

    @Test
    public void handleRequestGivenNetworkPathFoundExpectThrows() {
        final ResourceModel model = AnalysisFactory.arrangeResourceModel();
        model.setNetworkPathFound(arrangeNetworkPathFound());
        final ResourceHandlerRequest<ResourceModel> request = arrangeResourceHandlerRequest(model);

        assertThrows(CfnInvalidRequestException.class, () -> sut.handleRequest(proxy, request, client, logger));
    }

    @Test
    public void handleRequestGivenErrorCodeExpectThrows() {
        final ResourceModel model = AnalysisFactory.arrangeResourceModel();
        model.setErrorCode(arrangeErrorCode());
        final ResourceHandlerRequest<ResourceModel> request = arrangeResourceHandlerRequest(model);

        assertThrows(CfnInvalidRequestException.class, () -> sut.handleRequest(proxy, request, client, logger));
    }

    @Test
    public void handleRequestGivenErrorMessageExpectThrows() {
        final ResourceModel model = AnalysisFactory.arrangeResourceModel();
        model.setErrorMessage(arrangeErrorMessage());
        final ResourceHandlerRequest<ResourceModel> request = arrangeResourceHandlerRequest(model);

        assertThrows(CfnInvalidRequestException.class, () -> sut.handleRequest(proxy, request, client, logger));
    }

    @Test
    public void handleRequestGivenStatusExpectThrows() {
        final ResourceModel model = AnalysisFactory.arrangeResourceModel();
        model.setStatus(arrangeStatus());
        final ResourceHandlerRequest<ResourceModel> request = arrangeResourceHandlerRequest(model);

        assertThrows(CfnInvalidRequestException.class, () -> sut.handleRequest(proxy, request, client, logger));
    }

    @Test
    public void handleRequestGivenExplanationsExpectThrows() {
        final ResourceModel model = AnalysisFactory.arrangeResourceModel();
        model.setExplanations(ImmutableList.of());
        final ResourceHandlerRequest<ResourceModel> request = arrangeResourceHandlerRequest(model);

        assertThrows(CfnInvalidRequestException.class, () -> sut.handleRequest(proxy, request, client, logger));
    }

    @Test
    public void handleRequestGivenAlternatePathHintsExpectThrows() {
        final ResourceModel model = AnalysisFactory.arrangeResourceModel();
        model.setAlternatePathHints(ImmutableList.of());
        final ResourceHandlerRequest<ResourceModel> request = arrangeResourceHandlerRequest(model);

        assertThrows(CfnInvalidRequestException.class, () -> sut.handleRequest(proxy, request, client, logger));
    }

    @Test
    public void handleRequestGivenForwardPathComponentsExpectThrows() {
        final ResourceModel model = AnalysisFactory.arrangeResourceModel();
        model.setForwardPathComponents(ImmutableList.of());
        final ResourceHandlerRequest<ResourceModel> request = arrangeResourceHandlerRequest(model);

        assertThrows(CfnInvalidRequestException.class, () -> sut.handleRequest(proxy, request, client, logger));
    }

    @Test
    public void handleRequestGivenReturnPathComponentsExpectThrows() {
        final ResourceModel model = AnalysisFactory.arrangeResourceModel();
        model.setReturnPathComponents(ImmutableList.of());
        final ResourceHandlerRequest<ResourceModel> request = arrangeResourceHandlerRequest(model);

        assertThrows(CfnInvalidRequestException.class, () -> sut.handleRequest(proxy, request, client, logger));
    }

    @Test
    public void handleRequestGivenEc2ThrowsExpectSuccess() {
        final ResourceModel model = AnalysisFactory.arrangeResourceModel();
        final ResourceHandlerRequest<ResourceModel> request = arrangeResourceHandlerRequest(model);
        final AwsServiceException exception = arrangeException("MissingParameter");
        doThrow(exception).when(client)
                .startNetworkInsightsAnalysis(any(StartNetworkInsightsAnalysisRequest.class));

        final ProgressEvent<ResourceModel, CallbackContext> response = sut.handleRequest(proxy, request,
                client, logger);

        assertNull(response.getResourceModel());
        assertEquals(OperationStatus.FAILED, response.getStatus());
        assertEquals(HandlerErrorCode.InvalidRequest, response.getErrorCode());
        assertEquals(exception.getMessage(), response.getMessage());
    }

    @Test
    public void handleRequestExpectModelUpdated() {
        final ResourceModel model = AnalysisFactory.arrangeResourceModel();
        final ResourceHandlerRequest<ResourceModel> request = arrangeResourceHandlerRequest(model);
        final NetworkInsightsAnalysis analysis = arrangeAnalysis(model.getNetworkInsightsPathId(),
                model.getFilterInArns());
        final StartNetworkInsightsAnalysisResponse startAnalysisResponse = arrangeStartAnalysisResponse(analysis);
        doReturn(startAnalysisResponse).when(client)
                .startNetworkInsightsAnalysis(any(StartNetworkInsightsAnalysisRequest.class));

        final ProgressEvent<ResourceModel, CallbackContext> response = sut.handleRequest(proxy, request, client, logger);

        // validate response fields
        validateResponse(response, request);
        final ResourceModel responseResourceModel = response.getResourceModel();
        // input fields
        assertNotNull(responseResourceModel.getNetworkInsightsPathId());
        // required read-only fields
        assertNotNull(responseResourceModel.getNetworkInsightsAnalysisId());
        assertNotNull(responseResourceModel.getNetworkInsightsAnalysisArn());
        assertNotNull(responseResourceModel.getStartDate());
        assertNotNull(responseResourceModel.getStatus());

        // request fields
        verify(client).startNetworkInsightsAnalysis(requestCaptor.capture());
        final StartNetworkInsightsAnalysisRequest actualRequest = requestCaptor.getValue();
        validateRequest(actualRequest, request);
        verifyNoMoreInteractions(client);
    }

    @Test
    public void handleRequestGivenOptionalFieldsExpectModelUpdated() {
        // set required (pathId) and optional (filterInArns) fields on model
        final ResourceModel model = AnalysisFactory.arrangeResourceModel();
        model.setFilterInArns(arrangeFilterInArns());
        final ResourceHandlerRequest<ResourceModel> request = arrangeResourceHandlerRequest(model);
        // return full analysis which includes optional fields
        final NetworkInsightsAnalysis analysis = arrangeFullAnalysis(model.getNetworkInsightsPathId(),
                model.getFilterInArns());
        final StartNetworkInsightsAnalysisResponse startAnalysisResponse = arrangeStartAnalysisResponse(analysis);
        doReturn(startAnalysisResponse).when(client)
                .startNetworkInsightsAnalysis(any(StartNetworkInsightsAnalysisRequest.class));

        final ProgressEvent<ResourceModel, CallbackContext> response = sut.handleRequest(proxy, request, client, logger);

        // validate response fields
        validateResponse(response, request);
        final ResourceModel responseResourceModel = response.getResourceModel();
        // input fields
        assertNotNull(responseResourceModel.getNetworkInsightsPathId());
        assertNotNull(responseResourceModel.getFilterInArns());
        // required read-only fields
        assertNotNull(responseResourceModel.getNetworkInsightsAnalysisId());
        assertNotNull(responseResourceModel.getNetworkInsightsAnalysisArn());
        assertNotNull(responseResourceModel.getStartDate());
        assertNotNull(responseResourceModel.getStatus());
        // optional read-only fields
        assertNotNull(responseResourceModel.getErrorCode());
        assertNotNull(responseResourceModel.getErrorMessage());
        assertNotNull(responseResourceModel.getNetworkPathFound());
        assertNotNull(responseResourceModel.getExplanations());
        assertNotNull(responseResourceModel.getForwardPathComponents());
        assertNotNull(responseResourceModel.getReturnPathComponents());
        assertNotNull(responseResourceModel.getAlternatePathHints());

        // validate request fields
        verify(client).startNetworkInsightsAnalysis(requestCaptor.capture());
        final StartNetworkInsightsAnalysisRequest actualRequest = requestCaptor.getValue();
        validateRequest(actualRequest, request);
        verifyNoMoreInteractions(client);
    }

    private void validateResponse(ProgressEvent<ResourceModel, CallbackContext> response,
                                  ResourceHandlerRequest<ResourceModel> request) {
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getResourceModel()).isEqualTo(request.getDesiredResourceState());
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    private void validateRequest(StartNetworkInsightsAnalysisRequest actualRequest,
                                 ResourceHandlerRequest<ResourceModel> request) {
        final ResourceModel model = request.getDesiredResourceState();

        assertEquals(model.getNetworkInsightsPathId(), actualRequest.networkInsightsPathId());

        if (model.getFilterInArns() == null) {
            assertTrue(actualRequest.filterInArns().isEmpty());
        } else {
            assertEquals(model.getFilterInArns(), actualRequest.filterInArns());
        }

        if (request.getDesiredResourceTags() == null) {
            assertTrue(actualRequest.tagSpecifications().isEmpty());
        } else {
            final List<Tag> tags = actualRequest.tagSpecifications()
                    .stream()
                    .flatMap(spec -> spec.tags().stream())
                    .collect(Collectors.toList());

            assertEquals(request.getDesiredResourceTags(), tags.stream().collect(Collectors.toMap(Tag::key, Tag::value)));
        }
    }
}
