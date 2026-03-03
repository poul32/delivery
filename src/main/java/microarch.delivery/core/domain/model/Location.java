package microarch.delivery.core.domain.model;

import jakarta.persistence.Embeddable;
import libs.ddd.ValueObject;
import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Embeddable
@Getter
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Location extends ValueObject<Location> {
    private int x;
    private int y;

    public static Result<Location, Error> create(int x, int y) {
        var err = Guard.combine(
                Guard.againstOutOfRange(x, 1, 10, "x"),
                Guard.againstOutOfRange(y, 1, 10, "y")
        );
        if (err != null) {
            return Result.failure(err);
        }

        return Result.success(new Location(x, y));
    }

    public static Location mustCreate(int x, int y) {
        return create(x, y).getValueOrThrow();
    }

    public int distanceTo(Location location) {
        return Math.abs(this.x - location.x) + Math.abs(this.y - location.y);
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(this.x, this.y);
    }
}
