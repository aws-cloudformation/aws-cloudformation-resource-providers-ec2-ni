package software.amazon.ec2.networkinsightspath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static junit.framework.Assert.assertEquals;
import static software.amazon.ec2.networkinsightspath.PathFactory.arrangeFullResourceModel;
import static software.amazon.ec2.networkinsightspath.PathFactory.arrangeResourceModel;

@ExtendWith(MockitoExtension.class)
public class ConfigurationTest {
    private Configuration sut;

    @BeforeEach
    public void setup() {
        sut = new Configuration();
    }

    @Test
    public void resourceDefinedTagsGivenResourceModelWithTagsExpectMap() {
        final ResourceModel resourceModel = arrangeFullResourceModel();
        final Tag tag = resourceModel.getTags().get(0);
        assertEquals(sut.resourceDefinedTags(resourceModel), Collections.singletonMap(tag.getKey(), tag.getValue()));
    }

    @Test
    public void resourceDefinedTagsGivenResourceModelWithNoTagsExpectNull() {
        assertEquals(sut.resourceDefinedTags(arrangeResourceModel()), null);
    }
}
