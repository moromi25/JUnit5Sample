package main;

import main.constant.EnumCommonConfig;
import main.constant.UserConfigConst;
import main.provider.SystemDefaultCommonConfigProvider;
import main.vo.UseServiceConfigVo;

public class FunctionAuthManager {

	public boolean canManageEachConfigByEmployee(EnumCommonConfig config, SystemDefaultCommonConfigProvider provider) {
		return switch (config) {
		case USE, UNUSE -> false;
		case DEPEND_ON_EMPLOYEE -> true;
		case UNDEFINED -> {
			EnumCommonConfig def = config.find(provider.getDefaultVal());
			if (def == EnumCommonConfig.UNDEFINED) {
				System.err.println("Illegal default config value: " + def);
				yield false;
			}
			yield canManageEachConfigByEmployee(def, provider);
		}
		default -> {
			System.err.println("Unexpected value: " + config);
			yield false;
		}
		};
	}

	public boolean canUseService(UseServiceConfigVo vo) {
		return switch (vo.getCommonConfig()) {
		case UNUSE:
			yield false;
		case USE:
			yield true;
		case DEPEND_ON_EMPLOYEE:
			if (vo.isExistsUserConfig()) {
				yield vo.getUserConfigVal() == UserConfigConst.EMPLOYEE_CONFIG_USE;
			}
			yield false;
		case UNDEFINED:
			// 未設定の場合、システムデフォルト値をセットして再確認
			EnumCommonConfig systemCommonConfig = vo.getCommonConfig().find(vo.getSystemDefaultVal());
			// あり得ないけど念のため無限ループ回避
			if (systemCommonConfig == EnumCommonConfig.UNDEFINED) {
				System.err.println("Please confirm system default config.");
				yield false;
			}
			yield canUseService(new UseServiceConfigVo.Builder().commonConfig(systemCommonConfig)
					.systemDefaultVal(systemCommonConfig.getVal()).existsUserConfig(vo.isExistsUserConfig())
					.userConfigVal(vo.getUserConfigVal()).build());
		default:
			throw new IllegalArgumentException("Unexpected value: " + vo.getCommonConfig());
		};
	}
}
