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

@DisplayName("設定権限に関するテスト")
public class FunctionAuthManagerTest extends FunctionAuthManager {

	@Nested
	@DisplayName("共通設定に関するテスト")
	public class CanManageEachConfigByEmployeeTest {

		/** デフォルト設定を返してくれるクラス */
		private CommonConfigProvider mockedDefaultConfigProvider;

		@Test
		@DisplayName("DEPEND_ON_EMPLOYEEを渡したらtrueを返すか")
		public void withCommonConfigDependOnEmployee() {
			assertTrue(canManageEachConfigByEmployee(DEPEND_ON_EMPLOYEE, mockedDefaultConfigProvider));
		}

		@ParameterizedTest
		@EnumSource(value = EnumCommonConfig.class, names = { "UNUSE", "USE" })
		@DisplayName("UNUSEまたはUSEを渡したらtrueを返すか")
		public void withCommonConfigUseOrUnuse(EnumCommonConfig commonConfig) {
			assertFalse(canManageEachConfigByEmployee(commonConfig, mockedDefaultConfigProvider));
		}

		@ParameterizedTest
		@DisplayName("UNDEFINEDを渡したらデフォルト設定を使って再判定するか")
		@CsvSource({ "UNUSE", "DEPEND_ON_EMPLOYEE", "USE", // 通常ありえないはずだが念のためテスト
				"UNDEFINED" // 通常ありえないはずだが念のためテスト
		})
		public void withCommonConfigUndefined(EnumCommonConfig defaultCommonConfig) {
			mockedDefaultConfigProvider = mock(CommonConfigProvider.class);
			when(mockedDefaultConfigProvider.getDefault()).thenReturn(defaultCommonConfig.getVal());
			// デフォルト設定が万一存在しなかった場合でもExceptionが発生しないように
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
	@DisplayName("共通設定×ユーザー設定を加味したアクセス可否テスト")
	public class CanUseServiceTest {

		@ParameterizedTest(name = "{index} ==> [期待結果：{1}] {2}")
		@MethodSource({ "authTypeUseOrUnusePatternsProvider", "authTypeDependOnBossPatternsProvider",
				"authTypeUndefinedPatternsProvider" })
		@DisplayName("canUseSerciceが共通設定とユーザー設定双方の利用設定を読んで表示制御できているか")
		public void withComAuthTypeUndefined(UseServiceConfigVo vo, boolean expected, String description) {
			assertThat(canUseService(vo), is(expected));
		}

		/**
		 * @return ・共通設定での設定値が1の場合<br>
		 *         ・共通設定での設定値が2の場合
		 */
		public static Stream<Arguments> authTypeUseOrUnusePatternsProvider() {
			return Stream.of(
					arguments(
							new UseServiceConfigVo.Builder().commonConfig(UNUSE).systemDefaultVal(USE.getVal())
									.existsUserConfig(false).userConfigVal(UserConfigConst.EMPLOYEE_CONFIG_USE).build(),
							false, "共通設定での設定値が1の場合"),
					arguments(new UseServiceConfigVo.Builder().commonConfig(USE).systemDefaultVal(UNUSE.getVal())
							.existsUserConfig(false).userConfigVal(UserConfigConst.EMPLOYEE_CONFIG_UNUSE).build(), true,
							"共通設定での設定値が2の場合"));
		}

		/**
		 * @return ・共通設定での設定値が3の場合、かつユーザー設定が0の場合<br>
		 *         ・共通設定での設定値が3の場合、かつユーザー設定が1の場合<br>
		 *         ・共通設定での設定値が3の場合、かつユーザー設定が存在しない場合
		 */
		public static Stream<Arguments> authTypeDependOnBossPatternsProvider() {
			return Stream.of(arguments(
					new UseServiceConfigVo.Builder().commonConfig(DEPEND_ON_EMPLOYEE).systemDefaultVal(USE.getVal())
							.existsUserConfig(true).userConfigVal(UserConfigConst.EMPLOYEE_CONFIG_UNUSE).build(),
					false, "共通設定での設定値が3の場合、かつユーザー設定が0の場合"),
					arguments(
							new UseServiceConfigVo.Builder().commonConfig(DEPEND_ON_EMPLOYEE)
									.systemDefaultVal(USE.getVal()).existsUserConfig(true)
									.userConfigVal(UserConfigConst.EMPLOYEE_CONFIG_USE).build(),
							true, "共通設定での設定値が3の場合、かつユーザー設定が1の場合"),
					arguments(
							new UseServiceConfigVo.Builder().commonConfig(DEPEND_ON_EMPLOYEE)
									.systemDefaultVal(USE.getVal()).existsUserConfig(false)
									.userConfigVal(UserConfigConst.EMPLOYEE_CONFIG_USE).build(),
							false, "共通設定での設定値が3の場合、かつユーザー設定が存在しない場合"));
		}

		/**
		 * 共通設定で利用設定が未設定の場合、システムデフォルト値で利用判定をできるか
		 *
		 * @return ・共通設定での設定値がされていない、かつ共通設定のシステムデフォルト値が1の場合<br>
		 *         ・共通設定での設定値がされていない、かつ共通設定のシステムデフォルト値が2の場合
		 */
		public static Stream<Arguments> authTypeUndefinedPatternsProvider() {
			return Stream.of(
					arguments(
							new UseServiceConfigVo.Builder().commonConfig(UNDEFINED).systemDefaultVal(UNUSE.getVal())
									.existsUserConfig(false).userConfigVal(UserConfigConst.EMPLOYEE_CONFIG_USE).build(),
							false, "共通設定での設定値がされていない、かつ共通設定のシステムデフォルト値が1の場合"),
					arguments(
							new UseServiceConfigVo.Builder().commonConfig(UNDEFINED).systemDefaultVal(USE.getVal())
									.existsUserConfig(false).userConfigVal(UserConfigConst.EMPLOYEE_CONFIG_USE).build(),
							true, "共通設定での設定値がされていない、かつ共通設定のシステムデフォルト値が2の場合"));
		}

	}

}
