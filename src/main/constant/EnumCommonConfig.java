package main.constant;

import java.util.Arrays;
import java.util.Optional;

public enum EnumCommonConfig {
	UNDEFINED(-1), // 共通設定が未設定
	UNUSE(0), // 全社一律で利用しない
	USE(1), // 全社一律で利用する
	DEPEND_ON_EMPLOYEE(2);// 社員に設定を委ねる

	private final int val;

	private EnumCommonConfig(int val) {
		this.val = val;
	}

	public int getVal() {
		return val;
	}

	public static EnumCommonConfig find(int val) {
		Optional<EnumCommonConfig> config = Arrays.asList(EnumCommonConfig.values()).stream()
				.filter(c -> c.getVal() == val).findAny();
		if (config.isPresent()) {
			return config.get();
		}
		throw new IllegalArgumentException("Illegal val: " + val);
	}
}
