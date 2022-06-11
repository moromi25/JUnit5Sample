package test.main;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import main.CanUseService;
import static main.constant.EnumCommonConfig.*;
import  main.constant.EnumCommonConfig;
import main.provider.SystemDefaultCommonConfigProvider;

class canUseServiceTest extends CanUseService {
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("共通設定に関するテスト")
    class CanManageEachConfigByEmployeeTest{

        // システムデフォルト設定を返してくれるクラス
        private SystemDefaultCommonConfigProvider mockedDefaultConfigProvider;

        @BeforeAll
        public void setup() {
//            mockedDefaultConfigProvider= spy(new SystemDefaultCommonConfigProvider());
        }

        @Test
        @DisplayName("管理者が設定した共通設定が2の場合に、メソッド：canManageEachConfigByEmployeeがtrueになるか")
        void testWithCommonConfigUseOrOnuse() {
            assertTrue(canManageEachConfigByEmployee(DEPEND_ON_EMPLOYEE, mockedDefaultConfigProvider));
        }

        @ParameterizedTest
        @EnumSource(value = EnumCommonConfig.class, names = { "UNUSE", "USE" })
        @DisplayName("管理者が設定した共通設定が0または1の場合に、メソッド：canManageEachConfigByEmployeeがfalseになるか")
        void testWithCommonConfigUseOrOnuse(EnumCommonConfig commonConfig) {
            assertFalse(canManageEachConfigByEmployee(commonConfig, mockedDefaultConfigProvider));
        }

        @ParameterizedTest
        @DisplayName("管理者が共通設定を未設定の場合に、メソッド：canManageEachConfigByEmployeeがシステムデフォルト共通設定に応じたboolean値を返すか")
        @CsvSource({
                "UNDEFINED, UNUSE",
                "UNDEFINED, DEPEND_ON_EMPLOYEE",
                "UNDEFINED, USE", // 通常ありえないはずだが念のためテスト
                "UNDEFINED, UNDEFINED" // 通常ありえないはずだが念のためテスト
        })
        void testWithCommonConfigUndefined(EnumCommonConfig commonConfig, EnumCommonConfig systemDefaultCommonConfig) {
            assertTrue(commonConfig== UNDEFINED);

//            when(mockedDefaultConfigProvider.getDefaultVal()).thenReturn(systemDefaultCommonConfig.getVal());

            switch (systemDefaultCommonConfig) {
            case UNUSE:
            case USE:
                assertFalse(canManageEachConfigByEmployee(commonConfig, mockedDefaultConfigProvider));
                break;
            case UNDEFINED:
                // システムデフォルトが万一存在しなかった場合、ExceptionではなくsysErrが返ってくるかを確認
                assertDoesNotThrow(() -> canManageEachConfigByEmployee(commonConfig, mockedDefaultConfigProvider));
                break;
            default:
                break;
            }
        }
    }
}
