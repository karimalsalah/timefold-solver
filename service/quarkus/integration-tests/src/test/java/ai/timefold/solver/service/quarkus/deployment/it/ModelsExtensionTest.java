package ai.timefold.solver.service.quarkus.deployment.it;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.inject.Inject;

import ai.timefold.solver.core.api.score.HardMediumSoftScore;
import ai.timefold.solver.service.definition.impl.storage.inmemory.InMemoryStorage;
import ai.timefold.solver.service.definition.internal.storage.AbstractStorageService;
import ai.timefold.solver.service.definition.internal.storage.Storage;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ModelsExtensionTest {

    @Inject
    Storage<TestdataSolution> storage;

    @Inject
    AbstractStorageService<TestdataSolution, TestdataModelConfig, TestdataModelInputMetrics, TestdataModelOutputMetrics, TestdataSolution, HardMediumSoftScore, TestdataModelConstraintJustification> storageService;

    @Test
    void testStorageClassGeneratedForModel() {
        assertThat(storage).isNotNull();
        assertThat(storage).isInstanceOf(InMemoryStorage.class);

        assertThat(storageService).isNotNull();
    }
}
