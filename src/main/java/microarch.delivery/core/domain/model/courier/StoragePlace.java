package microarch.delivery.core.domain.model.courier;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import libs.ddd.BaseEntity;
import libs.errs.*;
import libs.errs.Error;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "storage_place")
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public class StoragePlace extends BaseEntity<UUID> {
    @Column(name = "name")
    @Getter
    private final String name;
    @Column(name = "order_id")
    @Getter
    private UUID orderId;
    @Getter
    @Column(name = "total_volume")
    private final int totalVolume;

    private StoragePlace(String name, UUID orderId, int totalVolume) {
        super(UUID.randomUUID());
        this.name = name;
        this.orderId = orderId;
        this.totalVolume = totalVolume;
    }

    public static Result<StoragePlace, Error> create(String name, UUID orderId, int totalVolume) {
        var err = Guard.combine(
                Guard.againstNullOrEmpty(name, "name"),
                Guard.againstLessOrEqual(totalVolume, 0, "totalVolume")
                );
        if (err != null) {
            return Result.failure(err);
        }

        var storagePlace = new StoragePlace(name, orderId, totalVolume);
        return Result.success(storagePlace);
    }

    public static StoragePlace mustCreate(String name, UUID orderId, int totalVolume) {
        return create(name, orderId, totalVolume).getValueOrThrow();
    }

    public boolean checkAvailableVolume(int volume) {
        if (orderId != null) {
            return false;
        }
        return volume <= totalVolume;
    }

    public UnitResult<Error> putOrder(UUID orderId, int volume) {
        if (this.orderId != null) {
            return UnitResult.failure(Errors.orderMustBeEmpty());
        }
        var err = Guard.againstGreaterThan(volume, totalVolume, "volume");
        if (err != null) {
            return UnitResult.failure(err);
        }
        this.orderId = orderId;
        return UnitResult.success();
    }

    public Result<UUID, Error> extractOrder() {
        var err = Guard.againstNullOrEmpty(orderId, "orderId");
        if (err != null) {
            return Result.failure(err);
        }
        var result = Result.success(orderId);
        orderId = null;
        return result;
    }

    public static class Errors  {
        public static Error orderMustBeEmpty() {
            return Error.of("order.must.be.empty", "Storage place already has order!");
        }
    }
}
