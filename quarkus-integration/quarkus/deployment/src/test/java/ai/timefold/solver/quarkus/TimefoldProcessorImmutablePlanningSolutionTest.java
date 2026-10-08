package ai.timefold.solver.quarkus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import ai.timefold.solver.core.testconstraint.DummyConstraintProvider;
import ai.timefold.solver.core.testdomain.immutable.record.TestdataRecordEntity;
import ai.timefold.solver.core.testdomain.immutable.record.TestdataRecordSolution;
import ai.timefold.solver.core.testdomain.immutable.record.TestdataRecordValue;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;

class TimefoldProcessorImmutablePlanningSolutionTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(DummyConstraintProvider.class, TestdataRecordSolution.class,
                            TestdataRecordEntity.class, TestdataRecordValue.class))
            .assertException(exception -> {
                assertThat(exception.getClass()).isEqualTo(IllegalArgumentException.class);
                assertThat(exception.getMessage().contains("cannot be a record as it needs to be mutable")).isTrue();
            });

    @Test
    void solve() {
        fail("Build should fail");
    }

}
