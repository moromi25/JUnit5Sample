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
    @DisplayName("���ʐݒ�Ɋւ���e�X�g")
    class CanManageEachConfigByEmployeeTest{

        // �V�X�e���f�t�H���g�ݒ��Ԃ��Ă����N���X
        private SystemDefaultCommonConfigProvider mockedDefaultConfigProvider;

        @BeforeAll
        public void setup() {
//            mockedDefaultConfigProvider= spy(new SystemDefaultCommonConfigProvider());
        }

        @Test
        @DisplayName("�Ǘ��҂��ݒ肵�����ʐݒ肪2�̏ꍇ�ɁA���\�b�h�FcanManageEachConfigByEmployee��true�ɂȂ邩")
        void testWithCommonConfigUseOrOnuse() {
            assertTrue(canManageEachConfigByEmployee(DEPEND_ON_EMPLOYEE, mockedDefaultConfigProvider));
        }

        @ParameterizedTest
        @EnumSource(value = EnumCommonConfig.class, names = { "UNUSE", "USE" })
        @DisplayName("�Ǘ��҂��ݒ肵�����ʐݒ肪0�܂���1�̏ꍇ�ɁA���\�b�h�FcanManageEachConfigByEmployee��false�ɂȂ邩")
        void testWithCommonConfigUseOrOnuse(EnumCommonConfig commonConfig) {
            assertFalse(canManageEachConfigByEmployee(commonConfig, mockedDefaultConfigProvider));
        }

        @ParameterizedTest
        @DisplayName("�Ǘ��҂����ʐݒ�𖢐ݒ�̏ꍇ�ɁA���\�b�h�FcanManageEachConfigByEmployee���V�X�e���f�t�H���g���ʐݒ�ɉ�����boolean�l��Ԃ���")
        @CsvSource({
                "UNDEFINED, UNUSE",
                "UNDEFINED, DEPEND_ON_EMPLOYEE",
                "UNDEFINED, USE", // �ʏ킠�肦�Ȃ��͂������O�̂��߃e�X�g
                "UNDEFINED, UNDEFINED" // �ʏ킠�肦�Ȃ��͂������O�̂��߃e�X�g
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
                // �V�X�e���f�t�H���g�����ꑶ�݂��Ȃ������ꍇ�AException�ł͂Ȃ�sysErr���Ԃ��Ă��邩���m�F
                assertDoesNotThrow(() -> canManageEachConfigByEmployee(commonConfig, mockedDefaultConfigProvider));
                break;
            default:
                break;
            }
        }
    }
}
