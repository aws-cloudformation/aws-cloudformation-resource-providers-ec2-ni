package software.amazon.ec2.networkinsightspath;

import com.google.common.collect.Streams;
import software.amazon.awssdk.services.ec2.model.ResourceType;
import software.amazon.awssdk.services.ec2.model.TagSpecification;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TagUtils {

    public static TagSpecification getTagSpecification(Map<String, String> desiredTags) {
        return TagSpecification.builder()
            .resourceType(ResourceType.NETWORK_INSIGHTS_PATH)
            .tags(desiredTags.keySet().stream()
                .map(key -> software.amazon.awssdk.services.ec2.model.Tag.builder()
                    .key(key)
                    .value(desiredTags.get(key))
                    .build())
                .collect(Collectors.toList()))
            .build();
    }

    public static List<Tag> convertTags(List<software.amazon.awssdk.services.ec2.model.Tag> tags) {
        return tags.stream()
            .map(tag -> software.amazon.ec2.networkinsightspath.Tag.builder()
                .key(tag.key())
                .value(tag.value())
                .build())
            .collect(Collectors.toList());
    }

    public static List<software.amazon.awssdk.services.ec2.model.Tag> getTagsToAdd(Map<String, String> tags,
                                                                                         List<String> tagsToUpdate,
                                                                                         List<String> tagsToAdd) {
        return Streams.concat(tagsToAdd.stream(), tagsToUpdate.stream())
            .map(tag -> software.amazon.awssdk.services.ec2.model.Tag.builder()
                .key(tag)
                .value(tags.get(tag))
                .build())
            .collect(Collectors.toList());
    }

    public static List<software.amazon.awssdk.services.ec2.model.Tag> getTagsToDelete(Map<String, String> tags,
                                                                                            List<String> tagsToDelete) {
        return tagsToDelete.stream()
            .map(tag -> software.amazon.awssdk.services.ec2.model.Tag.builder()
                .key(tag)
                .value(tags.get(tag))
                .build())
            .collect(Collectors.toList());
    }
}
