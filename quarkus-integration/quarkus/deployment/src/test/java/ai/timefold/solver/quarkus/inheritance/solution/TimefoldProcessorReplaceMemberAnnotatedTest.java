package ai.timefold.solver.quarkus.inheritance.solution;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import ai.timefold.solver.core.testdomain.inheritance.solution.baseannotated.replacemember.TestdataReplaceMemberEntity;
import ai.timefold.solver.core.testdomain.inheritance.solution.baseannotated.replacemember.TestdataReplaceMemberExtendedSolution;
import ai.timefold.solver.core.testdomain.inheritance.solution.baseannotated.replacemember.TestdataReplaceMemberSolution;
import ai.timefold.solver.quarkus.testdomain.superclass.DummyConstraintProvider;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;

class TimefoldProcessorReplaceMemberAnnotatedTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(DummyConstraintProvider.class, TestdataReplaceMemberExtendedSolution.class,
                            TestdataReplaceMemberSolution.class, TestdataReplaceMemberEntity.class))
            .assertException(exception -> {
                assertThat(exception.getClass()).isEqualTo(IllegalStateException.class);
                assertThat(exception.getMessage().contains("Multiple classes")).isTrue();
                assertThat(exception.getMessage().contains("found in the classpath with a @PlanningSolution annotation."))
                        .isTrue();
            });

    /**
     * This test validates the behavior of the solver
     * when the child {@code @PlanningSolution} replaces an existing annotated member.
     */
    @Test
    void testReplaceAnnotatedMember() {
        fail("The build should fail");
    }
}
