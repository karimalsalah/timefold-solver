package ai.timefold.solver.service.quarkus.deployment;

import static ai.timefold.solver.service.quarkus.deployment.TimefoldModelDescriptorProcessor.getResourceTypeFromPath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import ai.timefold.solver.service.definition.api.ResourceType;

import org.junit.jupiter.api.Test;

class ModelResourceTypeParsingTest {

    @Test
    void versionedPath() {
        assertThat(getResourceTypeFromPath("/v1/schedules")).isEqualTo(new ResourceType("schedules"));
    }

    @Test
    void nonVersionedPath() {
        assertThat(getResourceTypeFromPath("/schedules")).isEqualTo(new ResourceType("schedules"));
    }

    @Test
    void emptyPath() {
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> getResourceTypeFromPath(""))
                .withMessage("Could not derive model resource type: ModelRest @Path value is empty.");
    }

    @Test
    void missingPathSegment() {
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> getResourceTypeFromPath("/"))
                .withMessage("Could not derive model resource type: ModelRest @Path does not contain path segments.");
    }

    @Test
    void versionOnlyPath() {
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> getResourceTypeFromPath("/v1")).withMessage(
                "Could not derive model resource type: ModelRest @Path only contains API version but no resource segment.");
    }
}
