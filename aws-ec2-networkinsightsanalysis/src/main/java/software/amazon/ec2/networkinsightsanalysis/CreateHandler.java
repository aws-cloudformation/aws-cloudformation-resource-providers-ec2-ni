package software.amazon.ec2.networkinsightsanalysis;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.AnalysisStatus;
import software.amazon.awssdk.services.ec2.model.DescribeNetworkInsightsAnalysesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeNetworkInsightsAnalysesResponse;
import software.amazon.awssdk.services.ec2.model.StartNetworkInsightsAnalysisRequest;
import software.amazon.awssdk.services.ec2.model.StartNetworkInsightsAnalysisResponse;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
import software.amazon.awssdk.services.ec2.model.NetworkInsightsAnalysis;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.time.Duration;

public class CreateHandler extends BaseHandlerStd {

    private static final Duration STABILISATION_CALLBACK = Duration.ofSeconds(20);

    @Override
    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(final AmazonWebServicesClientProxy proxy,
                                                                          final ResourceHandlerRequest<ResourceModel>
                                                                              resourceHandlerRequest,
                                                                          final Ec2Client client,
                                                                          final CallbackContext callbackContext,
                                                                          final Logger logger) {
        final ResourceModel model = resourceHandlerRequest.getDesiredResourceState();
        if (callbackContext == null || !callbackContext.isActionStarted()) {
            logger.log("Desired tags: " + resourceHandlerRequest.getDesiredResourceTags());
            try {
                final ResourceModel updatedModel = handleCreation(model, logger, resourceHandlerRequest, proxy, client);
                resourceHandlerRequest.setDesiredResourceState(updatedModel);
            } catch (Ec2Exception e) {
                logger.log("Exception: " + e);
                return ProgressEvent.defaultFailureHandler(e,
                    Translator.getHandlerError(e.awsErrorDetails().errorCode()));
            }
        }

        return handleStabilisation(resourceHandlerRequest.getDesiredResourceState(), logger, proxy, client);
    }

    private ProgressEvent<ResourceModel, CallbackContext> handleStabilisation(ResourceModel model,
                                                                              Logger logger,
                                                                              AmazonWebServicesClientProxy proxy,
                                                                              Ec2Client client) {
        logger.log("Stabilisation in progress");
        final DescribeNetworkInsightsAnalysesRequest describeNetworkInsightsAnalysesRequest =
            Translator.translateToReadRequest(model);
        final DescribeNetworkInsightsAnalysesResponse describeNetworkInsightsAnalysesResponse =
            proxy.injectCredentialsAndInvokeV2(describeNetworkInsightsAnalysesRequest, describeRequest -> {
                logger.log("DescribeAnalysisRequest: " + describeRequest);
                final DescribeNetworkInsightsAnalysesResponse describeResponse = client.describeNetworkInsightsAnalyses(
                    describeRequest);
                logger.log("DescribeAnalysisResponse: " + describeResponse);
                return describeResponse;
            });
        final NetworkInsightsAnalysis networkInsightsAnalysis =
            describeNetworkInsightsAnalysesResponse.networkInsightsAnalyses().get(0);
        final AnalysisStatus status = networkInsightsAnalysis.status();
        switch (status) {
            case RUNNING:
                return ProgressEvent.<ResourceModel, CallbackContext>builder()
                    .resourceModel(model)
                    .status(OperationStatus.IN_PROGRESS)
                    .callbackDelaySeconds((int) STABILISATION_CALLBACK.getSeconds())
                    // there is a 20 min default timeout for stabilisation : https://sage.amazon.com/posts/967595
                    .callbackContext(CallbackContext.builder().actionStarted(true).build())
                    .build();
            case FAILED:
            case SUCCEEDED:
                final ResourceModel updatedModel = updateModel(networkInsightsAnalysis);
                return ProgressEvent.<ResourceModel, CallbackContext>builder()
                    .resourceModel(updatedModel)
                    .status(OperationStatus.SUCCESS)
                    .build();
            default:
                throw new RuntimeException(String.format("Unidentified status %s found in analysis", status));
        }
    }

    private ResourceModel handleCreation(ResourceModel model,
                                         Logger logger,
                                         ResourceHandlerRequest<ResourceModel> resourceHandlerRequest,
                                         AmazonWebServicesClientProxy proxy,
                                         Ec2Client client) {
        if (hasReadOnlyProperties(model)) {
            logger.log("Request is attempting to set a read-only property. Exiting early");
            throw new CfnInvalidRequestException("Attempting to set a ReadOnly Property");
        }
        final StartNetworkInsightsAnalysisRequest startNetworkInsightsAnalysisRequest =
            Translator.translateToStartRequest(resourceHandlerRequest);

        StartNetworkInsightsAnalysisResponse startNetworkInsightsAnalysisResponse = proxy.injectCredentialsAndInvokeV2(
            startNetworkInsightsAnalysisRequest,
            startAnalysisRequest -> {
                logger.log("StartAnalysisRequest: " + startAnalysisRequest);
                final StartNetworkInsightsAnalysisResponse response = client.startNetworkInsightsAnalysis(
                    startAnalysisRequest);
                logger.log("StartAnalysisResponse: " + response);
                return response;
            });

        final NetworkInsightsAnalysis networkInsightsAnalysis = startNetworkInsightsAnalysisResponse
            .networkInsightsAnalysis();
        final ResourceModel updatedModel = updateModel(networkInsightsAnalysis);
        logger.log(String.format("%s started successfully", networkInsightsAnalysis.networkInsightsAnalysisId()));

        return updatedModel;
    }

    private ResourceModel updateModel(NetworkInsightsAnalysis networkInsightsAnalysis) {
        return Translator.translateFromAnalysisToModel(networkInsightsAnalysis);
    }

    private boolean hasReadOnlyProperties(final ResourceModel model) {
        return model.getNetworkInsightsAnalysisId() != null
            || model.getNetworkInsightsAnalysisArn() != null
            || model.getStartDate() != null
            || model.getNetworkPathFound() != null
            || model.getStatusMessage() != null
            || model.getStatus() != null
            || model.getExplanations() != null
            || model.getAlternatePathHints() != null
            || model.getForwardPathComponents() != null
            || model.getReturnPathComponents() != null;
    }
}
