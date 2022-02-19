package tech.kronicle.service.utils;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class EnumUtilsTest {

    @Test
    public void getEnumListFromJsonValuesShouldThrowAnExceptionForANullEnumType() {
        // When
        Throwable thrown = catchThrowable(() -> EnumUtils.getEnumListFromJsonValues(null, List.of("ANYTHING")));

        // Then
        assertThat(thrown).isInstanceOf(NullPointerException.class);
        assertThat(thrown).hasMessage("enumType");
    }

    @Test
    public void getEnumListFromJsonValuesShouldHandleANullEnumList() {
        // When
        List<TestEnum> returnValue = EnumUtils.getEnumListFromJsonValues(TestEnum.class, null);

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void getEnumListFromJsonValuesShouldHandleOneEnumValueString() {
        // When
        List<TestEnum> returnValue = EnumUtils.getEnumListFromJsonValues(TestEnum.class, List.of("VALUE_1"));

        // Then
        assertThat(returnValue).containsExactly(TestEnum.VALUE_1);
    }

    @Test
    public void getEnumListFromJsonValuesShouldHandleMultipleEnumValueStrings() {
        // When
        List<TestEnum> returnValue = EnumUtils.getEnumListFromJsonValues(TestEnum.class, List.of("VALUE_1", "VALUE_2", "VALUE_3"));

        // Then
        assertThat(returnValue).containsExactly(TestEnum.VALUE_1, TestEnum.VALUE_2, TestEnum.VALUE_3);
    }

    @Test
    public void getEnumListFromJsonValuesShouldThrowAnExceptionForAnUnknownEnumValueString() {
        // When
        Throwable thrown = catchThrowable(() -> EnumUtils.getEnumListFromJsonValues(TestEnum.class, List.of("UNEXPECTED")));

        // Then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        assertThat(thrown).hasMessage("No enum constant tech.kronicle.service.utils.EnumUtilsTest.TestEnum.UNEXPECTED");
    }

    @Test
    public void getEnumFromJsonValueShouldThrowAnExceptionForANullEnumType() {
        // When
        Throwable thrown = catchThrowable(() -> EnumUtils.getEnumFromJsonValue(null, "ANYTHING"));

        // Then
        assertThat(thrown).isInstanceOf(NullPointerException.class);
        assertThat(thrown).hasMessage("enumType");
    }

    @Test
    public void getEnumFromJsonValueShouldHandleAEnumValueString() {
        // When
        TestEnum returnValue = EnumUtils.getEnumFromJsonValue(TestEnum.class, "VALUE_1");

        // Then
        assertThat(returnValue).isEqualTo(TestEnum.VALUE_1);
    }

    @Test
    public void getEnumFromJsonValueShouldThrowAnExceptionForAnUnknownEnumValueString() {
        // When
        Throwable thrown = catchThrowable(() -> EnumUtils.getEnumFromJsonValue(TestEnum.class, "UNEXPECTED"));

        // Then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        assertThat(thrown).hasMessage("No enum constant tech.kronicle.service.utils.EnumUtilsTest.TestEnum.UNEXPECTED");
    }

    @Test
    public void getEnumFromJsonValueShouldThrowAnExceptionForANullEnumValueString() {
        // When
        Throwable thrown = catchThrowable(() -> EnumUtils.getEnumFromJsonValue(TestEnum.class, null));

        // Then
        assertThat(thrown).isInstanceOf(NullPointerException.class);
        assertThat(thrown).hasMessage("Name is null");
    }

    private enum TestEnum {

        VALUE_1,
        VALUE_2,
        VALUE_3
    }
}