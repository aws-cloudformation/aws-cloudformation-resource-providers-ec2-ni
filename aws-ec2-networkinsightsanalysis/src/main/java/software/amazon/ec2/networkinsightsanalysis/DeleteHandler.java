package software.amazon.ec2.networkinsightsanalysis;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DeleteNetworkInsightsAnalysisRequest;
import software.amazon.awssdk.services.ec2.model.DeleteNetworkInsightsAnalysisResponse;
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
        final DeleteNetworkInsightsAnalysisRequest deleteAnalysisRequest = Translator.translateToDeleteRequest(model);
        try {
            proxy.injectCredentialsAndInvokeV2(deleteAnalysisRequest,
                    request -> {
                        logger.log("DeleteAnalysisRequest: " + deleteAnalysisRequest);
                        final DeleteNetworkInsightsAnalysisResponse deleteAnalysisResponse =
                                ec2Client.deleteNetworkInsightsAnalysis(request);
                        logger.log("DeleteAnalysisResponse: " + deleteAnalysisResponse);
                        return deleteAnalysisResponse;
                    });
            logger.log(String.format("%s deleted successfully", model.getPrimaryIdentifier()));

            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                    .resourceModel(null)
                    .status(OperationStatus.SUCCESS)
                    .build();
        } catch (Ec2Exception e) {
            logger.log("Exception: " + e);

            return ProgressEvent.defaultFailureHandler(e,
                    Translator.getHandlerError(e.awsErrorDetails().errorCode()));
        }
    }
}
