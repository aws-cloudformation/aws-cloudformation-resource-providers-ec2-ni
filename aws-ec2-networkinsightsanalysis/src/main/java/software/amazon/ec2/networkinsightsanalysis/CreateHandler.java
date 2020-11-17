package software.amazon.ec2.networkinsightsanalysis;

import software.amazon.awssdk.services.ec2.Ec2Client;
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

public class CreateHandler extends BaseHandlerStd {

    @Override
    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(final AmazonWebServicesClientProxy proxy,
                                                                          final ResourceHandlerRequest<ResourceModel>
                                                                                  resourceHandlerRequest,
                                                                          final Ec2Client client,
                                                                          final Logger logger) {
        final ResourceModel model = resourceHandlerRequest.getDesiredResourceState();
        logger.log("Desired tags: " + resourceHandlerRequest.getDesiredResourceTags());
        if (hasReadOnlyProperties(model)) {
            logger.log("Request is attempting to set a read-only property. Exiting early");
            throw new CfnInvalidRequestException("Attempting to set a ReadOnly Property");
        }

        final StartNetworkInsightsAnalysisRequest startNetworkInsightsAnalysisRequest =
                Translator.translateToStartRequest(resourceHandlerRequest);

        StartNetworkInsightsAnalysisResponse startNetworkInsightsAnalysisResponse;
        try {
            startNetworkInsightsAnalysisResponse = proxy.injectCredentialsAndInvokeV2(
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
            resourceHandlerRequest.setDesiredResourceState(updatedModel);
            logger.log(String.format("%s started successfully", networkInsightsAnalysis.networkInsightsAnalysisId()));

            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                    .resourceModel(updatedModel)
                    .status(OperationStatus.SUCCESS)
                    .build();
        } catch (Ec2Exception e) {
            logger.log("Exception: " + e);
            return ProgressEvent.defaultFailureHandler(e,
                    Translator.getHandlerError(e.awsErrorDetails().errorCode()));
        }
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
