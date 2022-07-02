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
		@Nested
		@DisplayName("パターン1：assertTrue/Falseを使ったシンプルなテスト")
		public class CanManageEachConfigByEmployeeTest_pattern1 {

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
				when(mockedDefaultConfigProvider.getDefaultVal()).thenReturn(defaultCommonConfig.getVal());
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
		@DisplayName("パターン2：UNDEFINEDとそれ以外を分けてテスト")
		public class CanManageEachConfigByEmployeeTest_pattern2 {

			private CommonConfigProvider mockedDefaultConfigProvider;

			@ParameterizedTest
			@DisplayName("EnumCommonConfigのパターンテスト(UNDEFINED以外)")
			@CsvSource({ "USE, false", "UNUSE, false", "DEPEND_ON_EMPLOYEE, true" })
			public void withCommonConfig(EnumCommonConfig config, boolean expected) {
				assertThat(canManageEachConfigByEmployee(config, null), is(expected));
			}

			@ParameterizedTest
			@DisplayName("UNDEFINED時にシステムデフォルト値を使って判定できるかテスト")
			@MethodSource("defaultConfigPatternsProvider")
			public void withCommonConfigUndefined(EnumCommonConfig def, boolean expected) {
				mockedDefaultConfigProvider = mock(CommonConfigProvider.class);
				when(mockedDefaultConfigProvider.getDefaultVal()).thenReturn(def.getVal());
				assertThat(canManageEachConfigByEmployee(UNDEFINED, mockedDefaultConfigProvider), is(expected));
			}

			public static Stream<Arguments> defaultConfigPatternsProvider() {
				return Stream.of(arguments(USE, false), arguments(UNUSE, false), arguments(DEPEND_ON_EMPLOYEE, true),
						arguments(UNDEFINED, false)// 異常値テスト
				);
			}

		}

		@Nested
		@DisplayName("パターン3：全部まとめて1つのメソッドでテスト")
		public class CanManageEachConfigByEmployeeTest_pattern3 {
			@ParameterizedTest
			@MethodSource({ "exceptUndefinedProvider", "undefinedProvider" })
			public void withCanManageEachConfigByEmployee(String description, EnumCommonConfig config,
					CommonConfigProvider provider, boolean expected) {
				assertThat(canManageEachConfigByEmployee(config, provider), is(expected));
			}

			static Stream<Arguments> exceptUndefinedProvider() {
				return Stream.of(arguments("USE：ユーザー個別設定【不可】か", USE, null, false),
						arguments("UNUSE：ユーザー個別設定【不可】か", UNUSE, null, false),
						arguments("DEPEND_ON_EMPLOYEE：ユーザー個別設定【可能】か", DEPEND_ON_EMPLOYEE, null, true));
			}

			static Stream<Arguments> undefinedProvider() {
				return Stream.of(
						arguments("UNDEFINED×デフォルトUSE：ユーザー個別設定【不可】か", UNDEFINED, getMockedProvider(USE), false),
						arguments("UNDEFINED×デフォルトUNUSE：ユーザー個別設定【不可】か", UNDEFINED, getMockedProvider(UNUSE), false),
						arguments("UNDEFINED×デフォルトDEPEND_ON_EMPLOYEE：ユーザー個別設定【可能】か", UNDEFINED,
								getMockedProvider(DEPEND_ON_EMPLOYEE), true),
						arguments("UNDEFINED×デフォルト未設定：ユーザー個別設定【不可】か（Exceptionが吐かれないかも確認）", UNDEFINED,
								getMockedProvider(UNDEFINED), false) // 異常系
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
	@DisplayName("共通設定×ユーザー設定を加味したサービスアクセス可否テスト")
	public class CanUseServiceTest {

		@ParameterizedTest(name = "{index} ==> [期待結果：{1}] {2}")
		@MethodSource({ "authTypeUseOrUnusePatternsProvider", "authTypeDependOnBossPatternsProvider" })
		@DisplayName("canUseSerciceが共通設定とユーザー設定双方の利用設定を読んで表示制御できているか")
		public void withComAuthTypeUndefined(UseServiceConfigVo vo, boolean expected, String description) {
			assertThat(canUseService(vo), is(expected));
		}

		/**
		 * @return ・共通設定がUNUSEの場合<br>
		 *         ・共通設定がUSEの場合
		 */
		public static Stream<Arguments> authTypeUseOrUnusePatternsProvider() {
			return Stream.of(
					arguments(
							new UseServiceConfigVo.Builder().commonConfig(UNUSE).systemDefaultVal(USE.getVal())
									.existsUserConfig(false).userConfigVal(UserConfigConst.EMPLOYEE_CONFIG_USE).build(),
							false, "共通設定がUNUSEの場合"),
					arguments(new UseServiceConfigVo.Builder().commonConfig(USE).systemDefaultVal(UNUSE.getVal())
							.existsUserConfig(false).userConfigVal(UserConfigConst.EMPLOYEE_CONFIG_UNUSE).build(), true,
							"共通設定がUSEの場合"));
		}

		/**
		 * @return ・共通設定がDEPEND_ON_EMPLOYEE×ユーザー設定がUNUSEの場合<br>
		 *         ・共通設定がDEPEND_ON_EMPLOYEE×ユーザー設定がUSEの場合<br>
		 *         ・共通設定がDEPEND_ON_EMPLOYEE×ユーザー設定が存在しない場合
		 */
		public static Stream<Arguments> authTypeDependOnBossPatternsProvider() {
			return Stream.of(arguments(
					new UseServiceConfigVo.Builder().commonConfig(DEPEND_ON_EMPLOYEE).systemDefaultVal(USE.getVal())
							.existsUserConfig(true).userConfigVal(UserConfigConst.EMPLOYEE_CONFIG_UNUSE).build(),
					false, "通設定がDEPEND_ON_EMPLOYEE×ユーザー設定がUNUSEの場合"),
					arguments(
							new UseServiceConfigVo.Builder().commonConfig(DEPEND_ON_EMPLOYEE)
									.systemDefaultVal(USE.getVal()).existsUserConfig(true)
									.userConfigVal(UserConfigConst.EMPLOYEE_CONFIG_USE).build(),
							true, "通設定がDEPEND_ON_EMPLOYEE×ユーザー設定がUSEの場合"),
					arguments(
							new UseServiceConfigVo.Builder().commonConfig(DEPEND_ON_EMPLOYEE)
									.systemDefaultVal(USE.getVal()).existsUserConfig(false)
									.userConfigVal(UserConfigConst.EMPLOYEE_CONFIG_USE).build(),
							false, "共通設定がDEPEND_ON_EMPLOYEE×ユーザー設定が存在しない場合"));
		}

	}

}
