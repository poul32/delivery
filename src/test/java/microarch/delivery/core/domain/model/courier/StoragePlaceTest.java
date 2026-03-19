package microarch.delivery.core.domain.model.courier;

import org.junit.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


public class StoragePlaceTest {
    private static final String PLACE_NAME = "bag";
    private static final int TOTAL_VOLUME = 10;

    @Test
    public void derivedAggregate() {
        assertThat(StoragePlace.class.getSuperclass().getSimpleName()).isEqualTo("BaseEntity");
    }

    @Test
    public void shouldBeCorrectWhenParamsAreCorrectOnCreated() {
        var orderId = UUID.randomUUID();
        var result = StoragePlace.create(PLACE_NAME, orderId, TOTAL_VOLUME);

        assertThat(result.isSuccess()).isTrue();
        var storagePlace = result.getValue();
        assertThat(storagePlace.getName()).isEqualTo(PLACE_NAME);
        assertThat(storagePlace.getOrderId()).isEqualTo(orderId);
        assertThat(storagePlace.getTotalVolume()).isEqualTo(TOTAL_VOLUME);
    }

    @Test
    public void shouldPutOrderWhenOrderIsCorrect() {
        var storagePlace = StoragePlace.mustCreate(PLACE_NAME, null, TOTAL_VOLUME);
        var orderId = UUID.randomUUID();

        var putResult = storagePlace.putOrder(orderId, 7);

        assertThat(putResult.isSuccess()).isTrue();
        assertThat(storagePlace.getOrderId()).isEqualTo(orderId);
    }

    @Test
    public void shouldNotPutOrderWhenOrderAlreadyExists() {
        var oldOrderId = UUID.randomUUID();
        var storagePlace = StoragePlace.mustCreate(PLACE_NAME, oldOrderId, TOTAL_VOLUME);
        var newOrderId = UUID.randomUUID();

        var putResult = storagePlace.putOrder(newOrderId, 7);

        assertThat(putResult.isSuccess()).isFalse();
        assertThat(storagePlace.getOrderId()).isEqualTo(oldOrderId);
    }

    @Test
    public void shouldNotPutOrderWhenOrderVolumeGreaterThanTotal() {
        var storagePlace = StoragePlace.mustCreate(PLACE_NAME, null, TOTAL_VOLUME);
        var orderId = UUID.randomUUID();

        var putResult = storagePlace.putOrder(orderId, 12);

        assertThat(putResult.isSuccess()).isFalse();
        assertThat(storagePlace.getOrderId()).isNull();
    }

    @Test
    public void shouldCheckAvailableVolume() {
        var storagePlace = StoragePlace.mustCreate(PLACE_NAME, null, TOTAL_VOLUME);

        boolean result = storagePlace.checkAvailableVolume(7);

        assertThat(result).isTrue();
    }

    @Test
    public void shouldCheckAvailableVolumeAndReturnFalseWhenOrderAlreadyExists() {
        var orderId = UUID.randomUUID();
        var storagePlace = StoragePlace.mustCreate(PLACE_NAME, orderId, TOTAL_VOLUME);

        boolean result = storagePlace.checkAvailableVolume(7);

        assertThat(result).isFalse();
    }

    @Test
    public void shouldExtractOrderWhenOrderExists() {
        var orderId = UUID.randomUUID();
        var storagePlace = StoragePlace.mustCreate(PLACE_NAME, orderId, TOTAL_VOLUME);

        var result = storagePlace.extractOrder();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue()).isEqualTo(orderId);
        assertThat(storagePlace.getOrderId()).isNull();
    }

    @Test
    public void shouldNotExtractOrderWhenOrderNotExists() {
        var storagePlace = StoragePlace.mustCreate(PLACE_NAME, null, TOTAL_VOLUME);

        var result = storagePlace.extractOrder();

        assertThat(result.isSuccess()).isFalse();
    }
}
