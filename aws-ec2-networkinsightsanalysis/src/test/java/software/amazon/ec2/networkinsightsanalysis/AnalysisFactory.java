package software.amazon.ec2.networkinsightsanalysis;

import com.google.common.collect.ImmutableList;
import software.amazon.awssdk.services.ec2.model.StartNetworkInsightsAnalysisRequest;
import software.amazon.awssdk.services.ec2.model.StartNetworkInsightsAnalysisResponse;
import software.amazon.awssdk.services.ec2.model.DeleteNetworkInsightsAnalysisRequest;
import software.amazon.awssdk.services.ec2.model.DescribeNetworkInsightsAnalysesRequest;
import software.amazon.awssdk.services.ec2.model.NetworkInsightsAnalysis;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class AnalysisFactory {

    private static Map<String, Integer> idMap = new HashMap<>();

    public static int increment(int id) {
        return (id + 1) % 10000;
    }

    public static int nextId(String prefix) {
        return idMap.compute(prefix, (k, v) -> increment(Optional.ofNullable(v).orElse(0)));
    }

    public static String arrangeId(String prefix) {
        return prefix + "-" + nextId(prefix);
    }

    public static String arrangeNextToken() {
        return arrangeId("nextToken");
    }

    public static String arrangeAnalysisId() {
        return arrangeId("nia");
    }

    public static String arrangeAnalysisArn() {
        return arrangeId("arn");
    }

    public static String arrangePathId() {
        return arrangeId("nip");
    }

    public static Instant arrangeStartDate() {
        return Instant.MAX;
    }

    public static List<String> arrangeFilterInArns() {
        return ImmutableList.of("arn-1", "arn-2");
    }

    public static String arrangeStatus() {
        return "RUNNING";
    }

    public static int arrangeErrorCode() {
        return 0;
    }

    public static String arrangeErrorMessage() {
        return "error message";
    }

    public static boolean arrangeNetworkPathFound() {
        return false;
    }

    public static String arrangeKey() {
        return arrangeId("key");
    }

    public static String arrangeValue() {
        return arrangeId("value");
    }

    public static  software.amazon.awssdk.services.ec2.model.Tag arrangeEc2Tag() {
        return software.amazon.awssdk.services.ec2.model.Tag.builder()
                .key(arrangeKey())
                .value(arrangeValue())
                .build();
    }

    public static ResourceModel arrangeResourceModel() {
        return ResourceModel.builder()
                .networkInsightsPathId(arrangePathId())
                .build();
    }

    public static ResourceModel arrangeFullResourceModel() {
        return ResourceModel.builder()
                .networkInsightsAnalysisId(arrangeAnalysisId())
                .networkInsightsAnalysisArn(arrangeAnalysisArn())
                .networkInsightsPathId(arrangePathId())
                .filterInArns(arrangeFilterInArns())
                .startDate(arrangeStartDate().toString())
                .status(arrangeStatus())
                .networkPathFound(arrangeNetworkPathFound())
                .errorCode(arrangeErrorCode())
                .errorMessage(arrangeErrorMessage())
                .build();
    }

    public static ResourceModel arrangeResourceModel(final String analysisId,
                                                     final String analysisArn,
                                                     final String startDate,
                                                     final String status) {
        return ResourceModel.builder()
                .networkInsightsAnalysisId(analysisId)
                .networkInsightsAnalysisArn(analysisArn)
                .startDate(startDate)
                .status(status)
                .build();
    }

    public static ResourceModel arrangeResourceModel(final String analysisId) {
        return ResourceModel.builder()
            .networkInsightsAnalysisId(analysisId)
            .build();
    }

    public static NetworkInsightsAnalysis arrangeAnalysis() {
        return NetworkInsightsAnalysis.builder()
                .networkInsightsAnalysisId(arrangeAnalysisId())
                .networkInsightsAnalysisArn(arrangeAnalysisArn())
                .startDate(arrangeStartDate())
                .status(arrangeStatus())
                .build();
    }

    public static DescribeNetworkInsightsAnalysesRequest arrangeDescribeAnalysesRequest(
            ResourceHandlerRequest<software.amazon.ec2.networkinsightsanalysis.ResourceModel> request) {
        final software.amazon.ec2.networkinsightsanalysis.ResourceModel model = request.getDesiredResourceState();

        return DescribeNetworkInsightsAnalysesRequest.builder()
                .networkInsightsAnalysisIds(model.getNetworkInsightsAnalysisId())
                .build();
    }

    public static NetworkInsightsAnalysis arrangeNetworkInsightsAnalysis(software.amazon.ec2.networkinsightsanalysis.ResourceModel model) {
        final NetworkInsightsAnalysis.Builder builder = NetworkInsightsAnalysis.builder()
                .networkInsightsAnalysisId(model.getNetworkInsightsAnalysisId())
                .networkInsightsAnalysisArn(model.getNetworkInsightsAnalysisArn())
                .startDate(Instant.parse(model.getStartDate()))
                .status(model.getStatus())
                .networkInsightsPathId(model.getNetworkInsightsPathId())
                .filterInArns(model.getFilterInArns())
                .networkPathFound(model.getNetworkPathFound())
                .errorCode(model.getErrorCode())
                .errorMessage(model.getErrorMessage());
        if (model.getTags() != null) {
            builder.tags(model.getTags().stream().map(tag -> Tag.builder()
                    .key(tag.getKey())
                    .value(tag.getValue())
                    .build()).collect(Collectors.toList()));
        }
        return builder.build();
    }

}
