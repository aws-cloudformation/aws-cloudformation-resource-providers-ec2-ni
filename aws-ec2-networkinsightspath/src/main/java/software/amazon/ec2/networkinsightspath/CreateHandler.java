package software.amazon.ec2.networkinsightspath;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.CreateNetworkInsightsPathRequest;
import software.amazon.awssdk.services.ec2.model.CreateNetworkInsightsPathResponse;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
import software.amazon.awssdk.services.ec2.model.NetworkInsightsPath;
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
        final CreateNetworkInsightsPathRequest createNetworkInsightsPathRequest =
            Translator.translateToCreateRequest(resourceHandlerRequest);
        CreateNetworkInsightsPathResponse createNetworkInsightsPathResponse;
        try {
            createNetworkInsightsPathResponse = proxy.injectCredentialsAndInvokeV2(createNetworkInsightsPathRequest,
                createPathRequest -> {
                    logger.log("CreatePathRequest: " + createPathRequest);
                    final CreateNetworkInsightsPathResponse response = client.createNetworkInsightsPath(createPathRequest);
                    logger.log("CreatePathResponse: " + response);

                    return response;
                });

            final NetworkInsightsPath networkInsightsPath = createNetworkInsightsPathResponse.networkInsightsPath();
            updateModel(model, networkInsightsPath);
            logger.log(String.format("%s created successfully", networkInsightsPath.networkInsightsPathId()));

            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(model)
                .status(OperationStatus.SUCCESS)
                .build();
        } catch (Ec2Exception e) {
            logger.log("Exception: " + e);
            return ProgressEvent.defaultFailureHandler(e,
                    Translator.getHandlerError(e.awsErrorDetails().errorCode()));
        }
    }

    private void updateModel(ResourceModel desiredModel,
                             NetworkInsightsPath networkInsightsPath) {
        desiredModel.setNetworkInsightsPathId(networkInsightsPath.networkInsightsPathId());
        desiredModel.setNetworkInsightsPathArn(networkInsightsPath.networkInsightsPathArn());
        desiredModel.setCreatedDate(networkInsightsPath.createdDate().toString());
    }

    private boolean hasReadOnlyProperties(final ResourceModel model) {
        return model.getNetworkInsightsPathId() != null
            || model.getNetworkInsightsPathArn() != null
            || model.getCreatedDate() != null;
    }
}