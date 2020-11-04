package software.amazon.ec2.networkinsightsanalysis;

import com.google.common.collect.Streams;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TagUtils {

    public static List<Tag> convertTags(List<software.amazon.awssdk.services.ec2.model.Tag> tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        } else {
            return tags.stream()
                    .map(tag -> software.amazon.ec2.networkinsightsanalysis.Tag.builder()
                            .key(tag.key())
                            .value(tag.value())
                            .build())
                    .collect(Collectors.toList());
        }
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
