package main;

import main.constant.EnumCommonConfig;
import main.provider.SystemDefaultCommonConfigProvider;
import main.vo.UseServiceConfigVo;

public class FunctionAuthManager {

	public boolean canManageEachConfigByEmployee(EnumCommonConfig config, SystemDefaultCommonConfigProvider provider) {
		return false;
	}

	public boolean canUseService(UseServiceConfigVo vo) {
		switch (vo.getCommonConfig()) {
		case UNUSE:
			return false;
		case USE:
			return true;
		case UNDEFINED:
			// 未設定の場合、システムデフォルト値をセットして再確認
			EnumCommonConfig systemCommonConfig = EnumCommonConfig
					.find(new SystemDefaultCommonConfigProvider().getDefaultVal());
			// あり得ないけど念のため無限ループ回避
			if (systemCommonConfig == EnumCommonConfig.UNDEFINED) {
				System.err.println("Please confirm system default config.");
				return false;
			}
			return canUseService(vo);
		default:
			// 処理を止めないようにする 
			System.err.println("unexpected value: " + vo.getCommonConfig());
			return false;
		}
	}
}
