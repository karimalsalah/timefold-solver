package ai.timefold.solver.service.quarkus.deployment;

import static ai.timefold.solver.service.quarkus.deployment.TimefoldModelDescriptorProcessor.validateModelId;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;

public class ModelNameValidationTest {

    public static final String EXPECTED = "Model name can only contain letters (a-z, A-Z) and numbers (0-9), and hyphens (-).";

    @Test
    void testValidModelId() {
        assertThatCode(() -> validateModelId("employee-scheduling_v1")).doesNotThrowAnyException();
    }

    @Test
    void testEmptyModelId() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> validateModelId(""))
                .withMessage("Model name cannot be null or empty.");
    }

    @Test
    void testModelIdWithMultipleUnderscores() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> validateModelId("invalid_model_name"))
                .withMessage("Model name cannot contain underscore (_).");
    }

    @Test
    void testWhitespaceModelId() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> validateModelId("model id with space"))
                .withMessage(EXPECTED);
    }

    @Test
    void testModelIdWithEmoji() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> validateModelId("\uD83D\uDE80Rocket_123"))
                .withMessage(EXPECTED);
    }

    @Test
    void testSystemModelId() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> validateModelId("system.users"))
                .withMessage(EXPECTED);
    }

    @Test
    void testModelIdWithDotSystem() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> validateModelId("example.system.model"))
                .withMessage(EXPECTED);
    }

    @Test
    void testModelIdWithNullCharacter() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> validateModelId("invalid\0id"))
                .withMessage(EXPECTED);
    }

    @Test
    void testModelIdWithDollarNotAllowed() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> validateModelId("oplog.$main"))
                .withMessage(EXPECTED);
    }

    @Test
    void testTooLongModelId() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> validateModelId("a".repeat(255) + "_"))
                .withMessage("Model name and version is too long.");
    }

}
