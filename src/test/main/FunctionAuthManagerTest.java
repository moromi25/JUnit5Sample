package test.main;

import static main.constant.EnumCommonConfig.DEPEND_ON_EMPLOYEE;
import static main.constant.EnumCommonConfig.UNDEFINED;
import static main.constant.EnumCommonConfig.UNUSE;
import static main.constant.EnumCommonConfig.USE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import main.FunctionAuthManager;
import main.constant.EnumCommonConfig;
import main.constant.UserConfigConst;
import main.provider.SystemDefaultCommonConfigProvider;
import main.vo.UseServiceConfigVo;

class FunctionAuthManagerTest extends FunctionAuthManager {
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	@Nested
	@DisplayName("���ʐݒ�Ɋւ���e�X�g")
	class CanManageEachConfigByEmployeeTest {

		// �V�X�e���f�t�H���g�ݒ��Ԃ��Ă����N���X
		private SystemDefaultCommonConfigProvider mockedDefaultConfigProvider;

		@BeforeAll
		public void setup() {
			mockedDefaultConfigProvider = spy(new SystemDefaultCommonConfigProvider());
		}

		@Test
		@DisplayName("�Ǘ��҂��ݒ肵�����ʐݒ肪2�̏ꍇ�ɁA���\�b�h�FcanManageEachConfigByEmployee��true�ɂȂ邩")
		void testWithCommonConfigDependOnEmployee() {
			assertTrue(canManageEachConfigByEmployee(DEPEND_ON_EMPLOYEE, mockedDefaultConfigProvider));
		}

		@ParameterizedTest
		@EnumSource(value = EnumCommonConfig.class, names = { "UNUSE", "USE" })
		@DisplayName("�Ǘ��҂��ݒ肵�����ʐݒ肪0�܂���1�̏ꍇ�ɁA���\�b�h�FcanManageEachConfigByEmployee��false�ɂȂ邩")
		void testWithCommonConfigUseOrUnuse(EnumCommonConfig commonConfig) {
			assertFalse(canManageEachConfigByEmployee(commonConfig, mockedDefaultConfigProvider));
		}

		@ParameterizedTest
		@DisplayName("�Ǘ��҂����ʐݒ�𖢐ݒ�̏ꍇ�ɁA���\�b�h�FcanManageEachConfigByEmployee���V�X�e���f�t�H���g���ʐݒ�ɉ�����boolean�l��Ԃ���")
		@CsvSource({ "UNDEFINED, UNUSE", "UNDEFINED, DEPEND_ON_EMPLOYEE", "UNDEFINED, USE", // �ʏ킠�肦�Ȃ��͂������O�̂��߃e�X�g
				"UNDEFINED, UNDEFINED" // �ʏ킠�肦�Ȃ��͂������O�̂��߃e�X�g
		})
		void testWithCommonConfigUndefined(EnumCommonConfig commonConfig, EnumCommonConfig systemDefaultCommonConfig) {
			assertTrue(commonConfig == UNDEFINED);

			when(mockedDefaultConfigProvider.getDefaultVal()).thenReturn(systemDefaultCommonConfig.getVal());

			switch (systemDefaultCommonConfig) {
			case UNUSE:
			case USE:
				assertFalse(canManageEachConfigByEmployee(commonConfig, mockedDefaultConfigProvider));
				break;
			case UNDEFINED:
				// �V�X�e���f�t�H���g�����ꑶ�݂��Ȃ������ꍇ�ł�Exception���������Ȃ��悤��
				assertDoesNotThrow(() -> canManageEachConfigByEmployee(commonConfig, mockedDefaultConfigProvider));
				break;
			default:
				break;
			}
		}
	}

	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	@Nested
	@DisplayName("�T�[�r�X�A�N�Z�X�ۂ𔻒f���邽�߂̃��\�b�h�F��canUseSercice�����������삷�邩")
	class CanUseServiceTest {

		@ParameterizedTest(name = "{index} ==> [���Ҍ��ʁF{1}] {2}")
		@MethodSource({ "authTypeUndefinedPatternsProvider", "authTypeUseOrUnusePatternsProvider",
				"authTypeDependOnBossPatternsProvider" })
		@DisplayName("canUseSercice�����ʐݒ�ƃ��[�U�[�ݒ�o���̗��p�ݒ��ǂ�ŕ\������ł��Ă��邩")
		void testWithComAuthTypeUndefined(UseServiceConfigVo vo, boolean expected, String description) {
			assertThat(canUseService(vo), is(expected));
		}

		/**
		 * ���ʐݒ�ŗ��p�ݒ肪���ݒ�̏ꍇ�A�V�X�e���f�t�H���g�l�ŗ��p������ł��邩
		 *
		 * @return �E���ʐݒ�ł̐ݒ�l������Ă��Ȃ��A�����ʐݒ�̃V�X�e���f�t�H���g�l��1�̏ꍇ<br>
		 *         �E���ʐݒ�ł̐ݒ�l������Ă��Ȃ��A�����ʐݒ�̃V�X�e���f�t�H���g�l��2�̏ꍇ
		 */
		Stream<Arguments> authTypeUndefinedPatternsProvider() {
			return Stream.of(
					arguments(
							new UseServiceConfigVo.Builder().commonConfig(UNDEFINED).systemDefaultVal(UNUSE.getVal())
									.existsUserConfig(false).userConfigVal(UserConfigConst.EMPLOYEE_CONFIG_USE).build(),
							false, "���ʐݒ�ł̐ݒ�l������Ă��Ȃ��A�����ʐݒ�̃V�X�e���f�t�H���g�l��1�̏ꍇ"),
					arguments(
							new UseServiceConfigVo.Builder().commonConfig(UNDEFINED).systemDefaultVal(USE.getVal())
									.existsUserConfig(false).userConfigVal(UserConfigConst.EMPLOYEE_CONFIG_USE).build(),
							true, "���ʐݒ�ł̐ݒ�l������Ă��Ȃ��A�����ʐݒ�̃V�X�e���f�t�H���g�l��2�̏ꍇ"));
		}

		/**
		 * @return �E���ʐݒ�ł̐ݒ�l��1�̏ꍇ<br>
		 *         �E���ʐݒ�ł̐ݒ�l��2�̏ꍇ
		 */
		Stream<Arguments> authTypeUseOrUnusePatternsProvider() {
			return Stream.of(
					arguments(
							new UseServiceConfigVo.Builder().commonConfig(UNUSE).systemDefaultVal(USE.getVal())
									.existsUserConfig(false).userConfigVal(UserConfigConst.EMPLOYEE_CONFIG_USE).build(),
							false, "���ʐݒ�ł̐ݒ�l��1�̏ꍇ"),
					arguments(new UseServiceConfigVo.Builder().commonConfig(USE).systemDefaultVal(UNUSE.getVal())
							.existsUserConfig(false).userConfigVal(UserConfigConst.EMPLOYEE_CONFIG_UNUSE).build(), true,
							"���ʐݒ�ł̐ݒ�l��2�̏ꍇ"));
		}

		/**
		 * @return �E���ʐݒ�ł̐ݒ�l��3�̏ꍇ�A�����[�U�[�ݒ肪0�̏ꍇ<br>
		 *         �E���ʐݒ�ł̐ݒ�l��3�̏ꍇ�A�����[�U�[�ݒ肪1�̏ꍇ<br>
		 *         �E���ʐݒ�ł̐ݒ�l��3�̏ꍇ�A�����[�U�[�ݒ肪���݂��Ȃ��ꍇ
		 */
		Stream<Arguments> authTypeDependOnBossPatternsProvider() {
			return Stream.of(arguments(
					new UseServiceConfigVo.Builder().commonConfig(DEPEND_ON_EMPLOYEE).systemDefaultVal(USE.getVal())
							.existsUserConfig(true).userConfigVal(UserConfigConst.EMPLOYEE_CONFIG_UNUSE).build(),
					false, "���ʐݒ�ł̐ݒ�l��3�̏ꍇ�A�����[�U�[�ݒ肪0�̏ꍇ"),
					arguments(
							new UseServiceConfigVo.Builder().commonConfig(DEPEND_ON_EMPLOYEE)
									.systemDefaultVal(USE.getVal()).existsUserConfig(true)
									.userConfigVal(UserConfigConst.EMPLOYEE_CONFIG_USE).build(),
							true, "���ʐݒ�ł̐ݒ�l��3�̏ꍇ�A�����[�U�[�ݒ肪1�̏ꍇ"),
					arguments(
							new UseServiceConfigVo.Builder().commonConfig(DEPEND_ON_EMPLOYEE)
									.systemDefaultVal(USE.getVal()).existsUserConfig(false)
									.userConfigVal(UserConfigConst.EMPLOYEE_CONFIG_USE).build(),
							false, "���ʐݒ�ł̐ݒ�l��3�̏ꍇ�A�����[�U�[�ݒ肪���݂��Ȃ��ꍇ"));
		}

	}

}
