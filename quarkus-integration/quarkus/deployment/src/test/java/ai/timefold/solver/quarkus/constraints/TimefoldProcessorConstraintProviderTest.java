package ai.timefold.solver.quarkus.constraints;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.inject.Inject;

import ai.timefold.solver.core.api.solver.SolverFactory;
import ai.timefold.solver.core.config.solver.SolverConfig;
import ai.timefold.solver.quarkus.deployment.api.ConstraintMetaModelBuildItem;
import ai.timefold.solver.quarkus.deployment.config.TimefoldBuildTimeConfig;
import ai.timefold.solver.quarkus.testdomain.normal.TestdataQuarkusConstraintProvider;
import ai.timefold.solver.quarkus.testdomain.normal.TestdataQuarkusEntity;
import ai.timefold.solver.quarkus.testdomain.normal.TestdataQuarkusSolution;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.test.QuarkusUnitTest;

class TimefoldProcessorConstraintProviderTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(TestdataQuarkusEntity.class,
                            TestdataQuarkusSolution.class, TestdataQuarkusConstraintProvider.class))
            .addBuildChainCustomizer(customizer -> customizer.addBuildStep(context -> {
                var constraintMetaModelsBySolverNames = context
                        .consume(ConstraintMetaModelBuildItem.class)
                        .constraintMetaModelsBySolverNames();
                assertThat(constraintMetaModelsBySolverNames.size()).isEqualTo(1);
                var constraintMetaModel = constraintMetaModelsBySolverNames.get(
                        TimefoldBuildTimeConfig.DEFAULT_SOLVER_NAME);
                assertThat(constraintMetaModel).isNotNull();
                assertThat(constraintMetaModel.getConstraints().size()).isEqualTo(1);
            })
                    .consumes(ConstraintMetaModelBuildItem.class)
                    .produces(SyntheticBeanBuildItem.class)
                    .build());

    @Inject
    SolverConfig solverConfig;
    @Inject
    SolverFactory<TestdataQuarkusSolution> solverFactory;

    @Test
    void solverConfigXml_default() {
        assertThat(solverConfig.getScoreDirectorFactoryConfig().getConstraintProviderClass())
                .isEqualTo(TestdataQuarkusConstraintProvider.class);
        assertThat(solverFactory.buildSolver()).isNotNull();
    }

}
