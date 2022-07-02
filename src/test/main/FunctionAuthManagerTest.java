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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import main.FunctionAuthManager;
import main.constant.EnumCommonConfig;
import main.constant.UserConfigConst;
import main.provider.CommonConfigProvider;
import main.vo.UseServiceConfigVo;

@DisplayName("�ݒ茠���Ɋւ���e�X�g")
public class FunctionAuthManagerTest extends FunctionAuthManager {

	@Nested
	@DisplayName("���ʐݒ�Ɋւ���e�X�g")
	public class CanManageEachConfigByEmployeeTest {
		@Nested
		@DisplayName("�p�^�[��1�FassertTrue/False���g�����V���v���ȃe�X�g")
		public class CanManageEachConfigByEmployeeTest_pattern1 {

			/** �f�t�H���g�ݒ��Ԃ��Ă����N���X */
			private CommonConfigProvider mockedDefaultConfigProvider;

			@Test
			@DisplayName("DEPEND_ON_EMPLOYEE��n������true��Ԃ���")
			public void withCommonConfigDependOnEmployee() {
				assertTrue(canManageEachConfigByEmployee(DEPEND_ON_EMPLOYEE, mockedDefaultConfigProvider));
			}

			@ParameterizedTest
			@EnumSource(value = EnumCommonConfig.class, names = { "UNUSE", "USE" })
			@DisplayName("UNUSE�܂���USE��n������true��Ԃ���")
			public void withCommonConfigUseOrUnuse(EnumCommonConfig commonConfig) {
				assertFalse(canManageEachConfigByEmployee(commonConfig, mockedDefaultConfigProvider));
			}

			@ParameterizedTest
			@DisplayName("UNDEFINED��n������f�t�H���g�ݒ���g���čĔ��肷�邩")
			@CsvSource({ "UNUSE", "DEPEND_ON_EMPLOYEE", "USE", // �ʏ킠�肦�Ȃ��͂������O�̂��߃e�X�g
					"UNDEFINED" // �ʏ킠�肦�Ȃ��͂������O�̂��߃e�X�g
			})
			public void withCommonConfigUndefined(EnumCommonConfig defaultCommonConfig) {
				mockedDefaultConfigProvider = mock(CommonConfigProvider.class);
				when(mockedDefaultConfigProvider.getDefaultVal()).thenReturn(defaultCommonConfig.getVal());
				// �f�t�H���g�ݒ肪���ꑶ�݂��Ȃ������ꍇ�ł�Exception���������Ȃ��悤��
				boolean canManageByEmployee = assertDoesNotThrow(
						() -> canManageEachConfigByEmployee(UNDEFINED, mockedDefaultConfigProvider));

				switch (defaultCommonConfig) {
				case UNUSE, USE -> assertFalse(canManageByEmployee);
				case UNDEFINED -> assertFalse(canManageByEmployee);
				case DEPEND_ON_EMPLOYEE -> assertTrue(canManageByEmployee);
				default -> throw new IllegalArgumentException("Unexpected value: " + defaultCommonConfig);
				}
			}
		}

		@Nested
		@DisplayName("�p�^�[��2�FUNDEFINED�Ƃ���ȊO�𕪂��ăe�X�g")
		public class CanManageEachConfigByEmployeeTest_pattern2 {

			private CommonConfigProvider mockedDefaultConfigProvider;

			@ParameterizedTest
			@DisplayName("EnumCommonConfig�̃p�^�[���e�X�g(UNDEFINED�ȊO)")
			@CsvSource({ "USE, false", "UNUSE, false", "DEPEND_ON_EMPLOYEE, true" })
			public void withCommonConfig(EnumCommonConfig config, boolean expected) {
				assertThat(canManageEachConfigByEmployee(config, null), is(expected));
			}

			@ParameterizedTest
			@DisplayName("UNDEFINED���ɃV�X�e���f�t�H���g�l���g���Ĕ���ł��邩�e�X�g")
			@MethodSource("defaultConfigPatternsProvider")
			public void withCommonConfigUndefined(EnumCommonConfig def, boolean expected) {
				mockedDefaultConfigProvider = mock(CommonConfigProvider.class);
				when(mockedDefaultConfigProvider.getDefaultVal()).thenReturn(def.getVal());
				assertThat(canManageEachConfigByEmployee(UNDEFINED, mockedDefaultConfigProvider), is(expected));
			}

			public static Stream<Arguments> defaultConfigPatternsProvider() {
				return Stream.of(arguments(USE, false), arguments(UNUSE, false), arguments(DEPEND_ON_EMPLOYEE, true),
						arguments(UNDEFINED, false)// �ُ�l�e�X�g
				);
			}

		}

		@Nested
		@DisplayName("�p�^�[��3�F�S���܂Ƃ߂�1�̃��\�b�h�Ńe�X�g")
		public class CanManageEachConfigByEmployeeTest_pattern3 {
			@ParameterizedTest
			@MethodSource({ "exceptUndefinedProvider", "undefinedProvider" })
			public void withCanManageEachConfigByEmployee(String description, EnumCommonConfig config,
					CommonConfigProvider provider, boolean expected) {
				assertThat(canManageEachConfigByEmployee(config, provider), is(expected));
			}

			static Stream<Arguments> exceptUndefinedProvider() {
				return Stream.of(arguments("USE�F���[�U�[�ʐݒ�y�s�z��", USE, null, false),
						arguments("UNUSE�F���[�U�[�ʐݒ�y�s�z��", UNUSE, null, false),
						arguments("DEPEND_ON_EMPLOYEE�F���[�U�[�ʐݒ�y�\�z��", DEPEND_ON_EMPLOYEE, null, true));
			}

			static Stream<Arguments> undefinedProvider() {
				return Stream.of(
						arguments("UNDEFINED�~�f�t�H���gUSE�F���[�U�[�ʐݒ�y�s�z��", UNDEFINED, getMockedProvider(USE), false),
						arguments("UNDEFINED�~�f�t�H���gUNUSE�F���[�U�[�ʐݒ�y�s�z��", UNDEFINED, getMockedProvider(UNUSE), false),
						arguments("UNDEFINED�~�f�t�H���gDEPEND_ON_EMPLOYEE�F���[�U�[�ʐݒ�y�\�z��", UNDEFINED,
								getMockedProvider(DEPEND_ON_EMPLOYEE), true),
						arguments("UNDEFINED�~�f�t�H���g���ݒ�F���[�U�[�ʐݒ�y�s�z���iException���f����Ȃ������m�F�j", UNDEFINED,
								getMockedProvider(UNDEFINED), false) // �ُ�n
				);
			}

			static CommonConfigProvider getMockedProvider(EnumCommonConfig def) {
				CommonConfigProvider mockedDefaultConfigProvider = mock(CommonConfigProvider.class);
				when(mockedDefaultConfigProvider.getDefaultVal()).thenReturn(def.getVal());
				return mockedDefaultConfigProvider;
			}

		}
	}

	@Nested
	@DisplayName("���ʐݒ�~���[�U�[�ݒ�����������T�[�r�X�A�N�Z�X�ۃe�X�g")
	public class CanUseServiceTest {

		@ParameterizedTest(name = "{index} ==> [���Ҍ��ʁF{1}] {2}")
		@MethodSource({ "authTypeUseOrUnusePatternsProvider", "authTypeDependOnBossPatternsProvider" })
		@DisplayName("canUseSercice�����ʐݒ�ƃ��[�U�[�ݒ�o���̗��p�ݒ��ǂ�ŕ\������ł��Ă��邩")
		public void withComAuthTypeUndefined(UseServiceConfigVo vo, boolean expected, String description) {
			assertThat(canUseService(vo), is(expected));
		}

		/**
		 * @return �E���ʐݒ肪UNUSE�̏ꍇ<br>
		 *         �E���ʐݒ肪USE�̏ꍇ
		 */
		public static Stream<Arguments> authTypeUseOrUnusePatternsProvider() {
			return Stream.of(
					arguments(
							new UseServiceConfigVo.Builder().commonConfig(UNUSE).systemDefaultVal(USE.getVal())
									.existsUserConfig(false).userConfigVal(UserConfigConst.EMPLOYEE_CONFIG_USE).build(),
							false, "���ʐݒ肪UNUSE�̏ꍇ"),
					arguments(new UseServiceConfigVo.Builder().commonConfig(USE).systemDefaultVal(UNUSE.getVal())
							.existsUserConfig(false).userConfigVal(UserConfigConst.EMPLOYEE_CONFIG_UNUSE).build(), true,
							"���ʐݒ肪USE�̏ꍇ"));
		}

		/**
		 * @return �E���ʐݒ肪DEPEND_ON_EMPLOYEE�~���[�U�[�ݒ肪UNUSE�̏ꍇ<br>
		 *         �E���ʐݒ肪DEPEND_ON_EMPLOYEE�~���[�U�[�ݒ肪USE�̏ꍇ<br>
		 *         �E���ʐݒ肪DEPEND_ON_EMPLOYEE�~���[�U�[�ݒ肪���݂��Ȃ��ꍇ
		 */
		public static Stream<Arguments> authTypeDependOnBossPatternsProvider() {
			return Stream.of(arguments(
					new UseServiceConfigVo.Builder().commonConfig(DEPEND_ON_EMPLOYEE).systemDefaultVal(USE.getVal())
							.existsUserConfig(true).userConfigVal(UserConfigConst.EMPLOYEE_CONFIG_UNUSE).build(),
					false, "�ʐݒ肪DEPEND_ON_EMPLOYEE�~���[�U�[�ݒ肪UNUSE�̏ꍇ"),
					arguments(
							new UseServiceConfigVo.Builder().commonConfig(DEPEND_ON_EMPLOYEE)
									.systemDefaultVal(USE.getVal()).existsUserConfig(true)
									.userConfigVal(UserConfigConst.EMPLOYEE_CONFIG_USE).build(),
							true, "�ʐݒ肪DEPEND_ON_EMPLOYEE�~���[�U�[�ݒ肪USE�̏ꍇ"),
					arguments(
							new UseServiceConfigVo.Builder().commonConfig(DEPEND_ON_EMPLOYEE)
									.systemDefaultVal(USE.getVal()).existsUserConfig(false)
									.userConfigVal(UserConfigConst.EMPLOYEE_CONFIG_USE).build(),
							false, "���ʐݒ肪DEPEND_ON_EMPLOYEE�~���[�U�[�ݒ肪���݂��Ȃ��ꍇ"));
		}

	}

}
