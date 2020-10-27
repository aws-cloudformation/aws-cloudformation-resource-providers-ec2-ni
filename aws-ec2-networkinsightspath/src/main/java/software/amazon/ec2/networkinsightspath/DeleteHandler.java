package software.amazon.ec2.networkinsightspath;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DeleteNetworkInsightsPathRequest;
import software.amazon.awssdk.services.ec2.model.DeleteNetworkInsightsPathResponse;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class DeleteHandler extends BaseHandlerStd {

    @Override
    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(final AmazonWebServicesClientProxy proxy,
                                                                          final ResourceHandlerRequest<ResourceModel>
                                                                              resourceHandlerRequest,
                                                                          final Ec2Client ec2Client,
                                                                          final Logger logger) {
        final ResourceModel model = resourceHandlerRequest.getDesiredResourceState();
        final DeleteNetworkInsightsPathRequest deletePathRequest = Translator.translateToDeleteRequest(model);
        try {
            proxy.injectCredentialsAndInvokeV2(deletePathRequest,
                request -> {
                    logger.log("DeletePathRequest: " + deletePathRequest);
                    final DeleteNetworkInsightsPathResponse deletePathResponse =
                        ec2Client.deleteNetworkInsightsPath(request);
                    logger.log("DeletePathResponse: " + deletePathResponse);
                    return deletePathResponse;
                });
        } catch (Ec2Exception e) {
            logger.log("Exception: " + e);

            return ProgressEvent.defaultFailureHandler(e,
                Translator.getHandlerError(e.awsErrorDetails().errorCode()));
        }
        logger.log(String.format("%s deleted successfully", model.getPrimaryIdentifier()));

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
            .resourceModel(null)
            .status(OperationStatus.SUCCESS)
            .build();

    }
}
