package software.amazon.ec2.networkinsightsanalysis;

import com.google.common.collect.ImmutableList;
import software.amazon.awssdk.services.ec2.model.AnalysisComponent;
import software.amazon.awssdk.services.ec2.model.AnalysisStatus;
import software.amazon.awssdk.services.ec2.model.DescribeNetworkInsightsAnalysesResponse;
import software.amazon.awssdk.services.ec2.model.Explanation;
import software.amazon.awssdk.services.ec2.model.StartNetworkInsightsAnalysisRequest;
import software.amazon.awssdk.services.ec2.model.StartNetworkInsightsAnalysisResponse;
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

import static software.amazon.ec2.networkinsightsanalysis.TagUtils.getTagSpecification;

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
        return "running";
    }

    public static int arrangeErrorCode() {
        return 0;
    }

    public static String arrangeStatusMessage() {
        return "status message";
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

    public static AnalysisComponent arrangeComponent() {
        return AnalysisComponent.builder().id(arrangeId("component")).build();
    }

    public static List<Explanation> arrangeExplanations() {
        return ImmutableList.of(
                software.amazon.awssdk.services.ec2.model.Explanation.builder().vpc(arrangeComponent()).build());
    }

    public static List<software.amazon.awssdk.services.ec2.model.PathComponent> arrangePathComponents() {
        return ImmutableList.of(
                software.amazon.awssdk.services.ec2.model.PathComponent.builder()
                        .sequenceNumber(1)
                        .component(arrangeComponent()).build());
    }

    public static List<software.amazon.awssdk.services.ec2.model.AlternatePathHint> arrangeAlternatePathHints() {
        return ImmutableList.of(
                software.amazon.awssdk.services.ec2.model.AlternatePathHint.builder()
                        .componentId(arrangeId("hint")).build());
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
                .statusMessage(arrangeStatusMessage())
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

    public static NetworkInsightsAnalysis arrangeAnalysis(final String pathId, final List<String> filterInArns) {
        return NetworkInsightsAnalysis.builder()
                .networkInsightsPathId(pathId)
                .filterInArns(filterInArns)
                .networkInsightsAnalysisId(arrangeAnalysisId())
                .networkInsightsAnalysisArn(arrangeAnalysisArn())
                .startDate(arrangeStartDate())
                .status(arrangeStatus())
                .build();
    }

    public static NetworkInsightsAnalysis arrangeFullAnalysis(final String pathId, final List<String> filterInArns) {
        return NetworkInsightsAnalysis.builder()
                .networkInsightsPathId(pathId)
                .filterInArns(filterInArns)
                .networkInsightsAnalysisId(arrangeAnalysisId())
                .networkInsightsAnalysisArn(arrangeAnalysisArn())
                .startDate(arrangeStartDate())
                .status(arrangeStatus())
                .networkPathFound(arrangeNetworkPathFound())
                .statusMessage(arrangeStatusMessage())
                .explanations(arrangeExplanations())
                .forwardPathComponents(arrangePathComponents())
                .returnPathComponents(arrangePathComponents())
                .alternatePathHints(arrangeAlternatePathHints())
                .build();
    }

    public static StartNetworkInsightsAnalysisRequest arrangeStartAnalysisRequest(
            ResourceHandlerRequest<ResourceModel> request) {
        final ResourceModel startAnalysisModel = request.getDesiredResourceState();
        final Map<String, String> desiredTags = request.getDesiredResourceTags();

        final StartNetworkInsightsAnalysisRequest.Builder startAnalysisRequestBuilder =
                StartNetworkInsightsAnalysisRequest.builder()
                        .networkInsightsPathId(startAnalysisModel.getNetworkInsightsPathId())
                        .filterInArns(startAnalysisModel.getFilterInArns());
        if (desiredTags != null && !desiredTags.isEmpty()) {
            startAnalysisRequestBuilder.tagSpecifications(getTagSpecification(desiredTags));
        }

        return startAnalysisRequestBuilder.build();
    }

    public static DescribeNetworkInsightsAnalysesRequest arrangeDescribeAnalysesRequest(
            ResourceHandlerRequest<software.amazon.ec2.networkinsightsanalysis.ResourceModel> request) {
        final software.amazon.ec2.networkinsightsanalysis.ResourceModel model = request.getDesiredResourceState();

        return DescribeNetworkInsightsAnalysesRequest.builder()
                .networkInsightsAnalysisIds(model.getNetworkInsightsAnalysisId())
                .build();
    }

    public static StartNetworkInsightsAnalysisResponse arrangeStartAnalysisResponse(NetworkInsightsAnalysis analysis) {
        return StartNetworkInsightsAnalysisResponse.builder()
                .networkInsightsAnalysis(analysis)
                .build();
    }

    public static DescribeNetworkInsightsAnalysesResponse arrangeDescribeAnalysisResponse(NetworkInsightsAnalysis analysis) {
        return DescribeNetworkInsightsAnalysesResponse.builder()
            .networkInsightsAnalyses(analysis)
            .build();
    }

    public static DescribeNetworkInsightsAnalysesResponse arrangeSucceededDescribeAnalysisResponse(NetworkInsightsAnalysis analysis) {
        final NetworkInsightsAnalysis succeededAnalysis = analysis.toBuilder().status(AnalysisStatus.SUCCEEDED).build();
        return arrangeDescribeResponseBuilder(succeededAnalysis).build();
    }

    public static DescribeNetworkInsightsAnalysesResponse arrangeFailedDescribeAnalysisResponse(NetworkInsightsAnalysis analysis) {
        final NetworkInsightsAnalysis failedAnalysis = analysis.toBuilder().status(AnalysisStatus.FAILED).build();
        return arrangeDescribeResponseBuilder(failedAnalysis).build();
    }

    public static DescribeNetworkInsightsAnalysesResponse.Builder arrangeDescribeResponseBuilder(NetworkInsightsAnalysis analysis) {
        return DescribeNetworkInsightsAnalysesResponse.builder()
            .networkInsightsAnalyses(analysis);
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
                .statusMessage(model.getStatusMessage());
        if (model.getTags() != null) {
            builder.tags(model.getTags().stream().map(tag -> Tag.builder()
                    .key(tag.getKey())
                    .value(tag.getValue())
                    .build()).collect(Collectors.toList()));
        }
        return builder.build();
    }

}
