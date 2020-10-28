package software.amazon.ec2.networkinsightsanalysis;

import software.amazon.awssdk.services.ec2.model.DeleteNetworkInsightsAnalysisRequest;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class is a centralized placeholder for
 * - api request construction
 * - object translation to/from aws sdk
 * - resource model construction for read/list handlers
 */

public class Translator {


  static DeleteNetworkInsightsAnalysisRequest translateToDeleteRequest(final ResourceModel model) {
    return DeleteNetworkInsightsAnalysisRequest.builder()
            .networkInsightsAnalysisId(model.getNetworkInsightsAnalysisId())
            .build();
  }

  //TODO: Add exceptions for other APIs, and tagging, when implementing those handlers
  static HandlerErrorCode getHandlerError(final String errorCode) {
    switch (errorCode) {
      // just adding the exceptions thrown by the Delete API for now
      case "MissingParameterException":
      case "InvalidParameterValue":
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

}
