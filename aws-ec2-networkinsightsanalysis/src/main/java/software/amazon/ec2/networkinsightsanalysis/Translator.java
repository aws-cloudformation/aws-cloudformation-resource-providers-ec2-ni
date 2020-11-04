package software.amazon.ec2.networkinsightsanalysis;

import software.amazon.awssdk.services.ec2.model.CreateTagsRequest;
import software.amazon.awssdk.services.ec2.model.DeleteNetworkInsightsAnalysisRequest;
import software.amazon.awssdk.services.ec2.model.DeleteTagsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeNetworkInsightsAnalysesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeNetworkInsightsAnalysesResponse;

import software.amazon.awssdk.services.ec2.model.NetworkInsightsAnalysis;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.cloudformation.proxy.HandlerErrorCode;

import java.util.List;
import java.util.stream.Collectors;

import static software.amazon.ec2.networkinsightsanalysis.TagUtils.convertTags;

/**
 * This class is a centralized placeholder for
 * - api request construction
 * - object translation to/from aws sdk
 * - resource model construction for read/list handlers
 */

public class Translator {

  static DescribeNetworkInsightsAnalysesRequest translateToReadRequest(
          final software.amazon.ec2.networkinsightsanalysis.ResourceModel model) {
    return DescribeNetworkInsightsAnalysesRequest.builder()
            .networkInsightsAnalysisIds(model.getNetworkInsightsAnalysisId())
            .build();
  }

  static ResourceModel translateFromReadResponse(final DescribeNetworkInsightsAnalysesResponse response) {
    return Translator.translateFromAnalysisToModel(response.networkInsightsAnalyses().get(0));
  }

  static DeleteNetworkInsightsAnalysisRequest translateToDeleteRequest(final ResourceModel model) {
    return DeleteNetworkInsightsAnalysisRequest.builder()
            .networkInsightsAnalysisId(model.getNetworkInsightsAnalysisId())
            .build();
  }

  static DescribeNetworkInsightsAnalysesRequest translateToListRequest(String nextToken) {
    return DescribeNetworkInsightsAnalysesRequest.builder()
            .nextToken(nextToken)
            .build();
  }

  static List<ResourceModel> translateFromListRequest(DescribeNetworkInsightsAnalysesResponse response) {
    return response.networkInsightsAnalyses()
            .stream()
            .map(Translator::translateFromAnalysisToModel)
            .collect(Collectors.toList());
  }

  static CreateTagsRequest translateToCreateTagsRequest(List<software.amazon.awssdk.services.ec2.model.Tag> tags, String analysisId) {
    return CreateTagsRequest.builder()
            .resources(analysisId)
            .tags(tags)
            .build();
  }

  static DeleteTagsRequest translateToDeleteTagsRequest(List<Tag> tags, String analysisId) {
    return DeleteTagsRequest.builder()
            .resources(analysisId)
            .tags(tags)
            .build();
  }

  //TODO: Add exceptions for other start API, when implementing that handler
  static HandlerErrorCode getHandlerError(final String errorCode) {
    switch (errorCode) {
      // just adding the exceptions thrown by the Delete, Describe, and Tagging APIs for now
      case "MissingParameter":
      case "InvalidParameterValue":
      case "InvalidParameterCombination":
          // including this error as a safety net; we don't expect this to occur as analysis Read/List handlers
          // should only get invoked for valid analysis resources
      case "InvalidNetworkInsightsAnalysisId.Malformed":
      case "TagPolicyViolation":
        return HandlerErrorCode.InvalidRequest;
      case "RequestLimitExceeded":
      case "Client.RequestLimitExceeded":
        return HandlerErrorCode.Throttling;
      case "InvalidNetworkInsightsAnalysisId.NotFound":
        return HandlerErrorCode.NotFound;
      case "UnauthorizedOperation":
      case "Client.UnauthorizedOperation":
        return HandlerErrorCode.AccessDenied;
      case "Unavailable":
      case "InternalError":
        return HandlerErrorCode.ServiceInternalError;
      default:
        return HandlerErrorCode.GeneralServiceException;
    }
  }

  private static ResourceModel translateFromAnalysisToModel(NetworkInsightsAnalysis analysis) {
    return ResourceModel.builder()
            .networkInsightsAnalysisId(analysis.networkInsightsAnalysisId())
            .networkInsightsAnalysisArn(analysis.networkInsightsAnalysisArn())
            .networkInsightsPathId(analysis.networkInsightsPathId())
            .startDate(translateToString(analysis.startDate()))
            .status(translateToString(analysis.statusAsString()))
            .filterInArns(translateFilterInArns(analysis.filterInArns()))
            .errorCode(analysis.errorCode())
            .errorMessage(analysis.errorMessage())
            .networkPathFound(analysis.networkPathFound())
            .forwardPathComponents(translatePathComponents(analysis.forwardPathComponents()))
            .returnPathComponents(translatePathComponents(analysis.returnPathComponents()))
            .explanations(translateExplanations((analysis.explanations())))
            .alternatePathHints(translateAlternatePathHints(analysis.alternatePathHints()))
            .tags(convertTags(analysis.tags()))
            .build();
  }

  private static String translateToString(Object value) {
    if (value == null) {
      return null;
    } else {
      return value.toString();
    }
  }

  private static List<String> translateFilterInArns(List<String> arns) {
    return TranslatorHelper.translateStringList(arns);
  }

  private static List<software.amazon.ec2.networkinsightsanalysis.PathComponent> translatePathComponents(
          List<software.amazon.awssdk.services.ec2.model.PathComponent> pathComponents) {
    if (pathComponents == null || pathComponents.isEmpty()) {
      return null;
    }
    return pathComponents.stream().map(TranslatorHelper::translatePathComponent).collect(Collectors.toList());
  }

  private static List<Explanation> translateExplanations(
          List<software.amazon.awssdk.services.ec2.model.Explanation> explanations) {
    if (explanations == null || explanations.isEmpty()) {
      return null;
    }
    return explanations.stream().map(TranslatorHelper::translateExplanation).collect(Collectors.toList());
  }

  private static List<AlternatePathHint> translateAlternatePathHints(
          List<software.amazon.awssdk.services.ec2.model.AlternatePathHint> alternatePathHints) {
    if (alternatePathHints == null || alternatePathHints.isEmpty()) {
      return null;
    }
    return alternatePathHints.stream().map(TranslatorHelper::translateAlternatePathHint).collect(Collectors.toList());
  }
}
