package software.amazon.ec2.networkinsightsanalysis;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.CreateTagsRequest;
import software.amazon.awssdk.services.ec2.model.CreateTagsResponse;
import software.amazon.awssdk.services.ec2.model.DeleteTagsRequest;
import software.amazon.awssdk.services.ec2.model.DeleteTagsResponse;
import software.amazon.awssdk.services.ec2.model.DescribeNetworkInsightsAnalysesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeNetworkInsightsAnalysesResponse;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UpdateHandler extends BaseHandlerStd {

    @Override
    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(final AmazonWebServicesClientProxy proxy,
                                                                          final ResourceHandlerRequest<ResourceModel>
                                                                                  resourceHandlerRequest,
                                                                          final Ec2Client client,
                                                                          final CallbackContext callbackContext,
                                                                          final Logger logger) {
        try {
            verifyResourceExists(resourceHandlerRequest, client, proxy, logger);
            handleTagUpdates(resourceHandlerRequest, client, proxy, logger);

            final ResourceModel updatedModel = resourceHandlerRequest.getDesiredResourceState();
            logger.log(String.format("%s updated successfully", updatedModel));

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

    private void verifyResourceExists(ResourceHandlerRequest<ResourceModel> resourceHandlerRequest,
                                      Ec2Client client,
                                      AmazonWebServicesClientProxy proxy,
                                      Logger logger) {
        final ResourceModel model = resourceHandlerRequest.getDesiredResourceState();
        final DescribeNetworkInsightsAnalysesRequest describeRequest = Translator.translateToReadRequest(model);

        proxy.injectCredentialsAndInvokeV2(describeRequest, request -> {
            logger.log("DescribeAnalysesRequest: " + request);
            final DescribeNetworkInsightsAnalysesResponse response = client.describeNetworkInsightsAnalyses(request);
            logger.log("DescribeAnalysesResponse:" + response);

            return response;
        });
    }

    private void handleTagUpdates(ResourceHandlerRequest<ResourceModel> resourceHandlerRequest,
                                  Ec2Client client,
                                  AmazonWebServicesClientProxy proxy,
                                  Logger logger) {
        final String analysisId = resourceHandlerRequest.getPreviousResourceState().getNetworkInsightsAnalysisId();
        final Map<String, String> previousTags = resourceHandlerRequest.getPreviousResourceTags();
        final Map<String, String> desiredTags = resourceHandlerRequest.getDesiredResourceTags();
        logger.log("previous tags: " + previousTags);
        logger.log("desired tags: " + desiredTags);

        final List<String> tagsToDelete = previousTags.keySet().stream()
                .filter(key -> !desiredTags.containsKey(key))
                .collect(Collectors.toList());
        final List<String> tagsToUpdate = previousTags.keySet().stream()
                .filter(key -> desiredTags.containsKey(key) && !previousTags.get(key).equals(desiredTags.get(key)))
                .collect(Collectors.toList());
        final List<String> tagsToAdd = desiredTags.keySet().stream()
                .filter(key -> !previousTags.containsKey(key))
                .collect(Collectors.toList());
        final List<Tag> tagsToBeDeleted = TagUtils.getTagsToDelete(previousTags, tagsToDelete);
        final List<Tag> tagsToBeAdded = TagUtils.getTagsToAdd(desiredTags, tagsToUpdate, tagsToAdd);

        addTags(analysisId, tagsToBeAdded, client, proxy, logger);
        deleteTags(analysisId, tagsToBeDeleted, client, proxy, logger);
    }

    private void addTags(String analysisId,
                         List<Tag> tagsToBeAdded,
                         Ec2Client client,
                         AmazonWebServicesClientProxy proxy,
                         Logger logger) {
        if (!tagsToBeAdded.isEmpty()) {
            final CreateTagsRequest createTagsRequest = Translator.translateToCreateTagsRequest(tagsToBeAdded, analysisId);
            logger.log("CreateTags request" + createTagsRequest);
            final CreateTagsResponse createTagsResponse = proxy.injectCredentialsAndInvokeV2(createTagsRequest, client::createTags);
            logger.log("CreateTags response: " + createTagsResponse);
        } else {
            logger.log("No tags to add");
        }
    }

    private void deleteTags(String analysisId,
                            List<Tag> tagsToBeDeleted,
                            Ec2Client client,
                            AmazonWebServicesClientProxy proxy,
                            Logger logger) {
        if (!tagsToBeDeleted.isEmpty()) {
            DeleteTagsRequest deleteTagsRequest = Translator.translateToDeleteTagsRequest(tagsToBeDeleted, analysisId);
            logger.log("DeleteTags request: " + deleteTagsRequest);
            final DeleteTagsResponse deleteTagsResponse = proxy.injectCredentialsAndInvokeV2(deleteTagsRequest, client::deleteTags);
            logger.log("DeleteTags response: " + deleteTagsResponse);
        } else {
            logger.log("No tags to delete");
        }
    }
}
