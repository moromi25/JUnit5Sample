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
			// ���ݒ�̏ꍇ�A�V�X�e���f�t�H���g�l���Z�b�g���čĊm�F
			EnumCommonConfig systemCommonConfig = EnumCommonConfig
					.find(new SystemDefaultCommonConfigProvider().getDefaultVal());
			// ���蓾�Ȃ����ǔO�̂��ߖ������[�v���
			if (systemCommonConfig == EnumCommonConfig.UNDEFINED) {
				System.err.println("Please confirm system default config.");
				return false;
			}
			return canUseService(vo);
		default:
			// �������~�߂Ȃ��悤�ɂ��� 
			System.err.println("unexpected value: " + vo.getCommonConfig());
			return false;
		}
	}
}
