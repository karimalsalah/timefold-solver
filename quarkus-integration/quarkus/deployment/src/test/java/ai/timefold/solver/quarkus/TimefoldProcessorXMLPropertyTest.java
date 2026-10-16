package ai.timefold.solver.quarkus;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import jakarta.inject.Inject;

import ai.timefold.solver.core.api.solver.SolverFactory;
import ai.timefold.solver.core.config.solver.SolverConfig;
import ai.timefold.solver.quarkus.testdomain.normal.TestdataQuarkusConstraintProvider;
import ai.timefold.solver.quarkus.testdomain.normal.TestdataQuarkusEntity;
import ai.timefold.solver.quarkus.testdomain.normal.TestdataQuarkusSolution;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;

class TimefoldProcessorXMLPropertyTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .overrideConfigKey("quarkus.timefold.solver-config-xml", "ai/timefold/solver/quarkus/customSolverConfig.xml")
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(TestdataQuarkusEntity.class,
                            TestdataQuarkusSolution.class, TestdataQuarkusConstraintProvider.class)
                    .addAsResource("ai/timefold/solver/quarkus/customSolverConfig.xml"));

    @Inject
    SolverConfig solverConfig;
    @Inject
    SolverFactory<TestdataQuarkusSolution> solverFactory;

    @Test
    void solverConfigXml_property() {
        assertThat(solverConfig).isNotNull();
        assertThat(solverConfig.getSolutionClass()).isEqualTo(TestdataQuarkusSolution.class);
        assertThat(solverConfig.getEntityClassList()).isEqualTo(Collections.singletonList(TestdataQuarkusEntity.class));
        assertThat(solverConfig.getScoreDirectorFactoryConfig().getConstraintProviderClass())
                .isEqualTo(TestdataQuarkusConstraintProvider.class);
        // Properties defined in solverConfig.xml
        assertThat(solverConfig.getTerminationConfig().getSecondsSpentLimit().longValue()).isEqualTo(3L);
        assertThat(solverFactory).isNotNull();
        assertThat(solverFactory.buildSolver()).isNotNull();
    }

}
