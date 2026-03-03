package microarch.delivery.core.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class LocationTest {

    @Test
    void derivedAggregate() {
        assertThat(Location.class.getSuperclass().getSimpleName()).isEqualTo("ValueObject");
    }

    @ParameterizedTest
    @MethodSource("validCoordinates")
    void shouldBeCorrectWhenParamsAreCorrectOnCreate(int x, int y) {

        var result = Location.create(x, y);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().getX()).isEqualTo(x);
        assertThat(result.getValue().getY()).isEqualTo(y);
    }

    private static Stream<Arguments> validCoordinates() {
        return Stream.of(
                Arguments.of(1, 1),
                Arguments.of(10, 10)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidCoordinates")
    void shouldReturnErrorWhenParamsAreNotCorrectOnCreated(int x, int y) {
        var result = Location.create(x, y);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isNotNull();
    }

    private static Stream<Arguments> invalidCoordinates() {
        return Stream.of(
                Arguments.of(0, 0),
                Arguments.of(11, 10),
                Arguments.of(-1, 5)
        );
    }

    @Test
    public void shouldBeEqualWhenAllPropertiesIsEqual() {
        var first = Location.mustCreate(1, 1);
        var second = Location.mustCreate(1, 1);

        var result = first.equals(second);

        assertThat(result).isTrue();
    }

    @Test
    public void shouldBeNotEqualWhenOneOfPropertiesIsNotEqual() {
        var first = Location.mustCreate(1, 1);
        var second = Location.mustCreate(1, 2);

        var result = first.equals(second);

        assertThat(result).isFalse();
    }

    @Test
    public void shouldReturnDistanceToOtherLocation() {
        var first = Location.mustCreate(2, 6);
        var second = Location.mustCreate(4, 9);

        var result = first.distanceTo(second);

        assertThat(result).isEqualTo(5);
    }
}
